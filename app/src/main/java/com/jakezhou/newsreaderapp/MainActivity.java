package com.jakezhou.newsreaderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public static NewsReaderDb db;
    private RecyclerView headlinesRecyclerView;
    private RecyclerView.Adapter headlinesAdapter;
    private LinearLayoutManager headlinesManager;
    List<String> headlinesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up connection to SQLite database
        db = new NewsReaderDb(this);

        // Creating empty ArrayList for headlines
        headlinesList = new ArrayList<>();

        //Connecting list of news headlines
        headlinesRecyclerView = findViewById(R.id.headlinesRecyclerView);
        // This line improves performance if the recyclerview does not have to be invalidated when children size or adapter changes
        headlinesRecyclerView.setHasFixedSize(true);
        // Linear layout
        headlinesManager = new LinearLayoutManager(this);
        headlinesRecyclerView.setLayoutManager(headlinesManager);
        // Setting the content adapter
        headlinesAdapter = new HeadlinesAdapter(headlinesList);
        headlinesRecyclerView.setAdapter(headlinesAdapter);
        headlinesRecyclerView.addItemDecoration(new DividerItemDecoration(headlinesRecyclerView.getContext(),
                headlinesManager.getOrientation()));


        // Grabbing content of hottest Hacker News Items
        // Giving time for db to finalize creation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new RetrieveNewsItems().execute("https://hacker-news.firebaseio.com/v0/topstories.json");
            }
        }, 1000);

    }

    @Override
    protected void onDestroy() {

        db.dropTable();
        super.onDestroy();
    }

    // TODO: AsyncTask was deprecated in Feb, 2020. Update this to use a single thread executor in future versions
    // Takes url that list ids of current hottest stories. After retrieving ids, gets the titles and content of each story
    private class RetrieveNewsItems extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {

            int id;
            String title;
            String content;

            StringBuilder sb = new StringBuilder();
            URL url;
            HttpsURLConnection urlConnection = null;
            HttpURLConnection httpConnection = null;
            JSONArray ids = null;
            JSONObject newsItem = null;

            try {
                url = new URL(args[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1) {
                    char current = (char) data;
                    sb.append(current);
                    data = reader.read();
                }
                ids = new JSONArray(sb.toString());

                for(int i = 0; i < Math.min(ids.length(), 20); i++) {

                    id = ids.getInt(i);

                    sb.setLength(0);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + id + ".json");
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    in = urlConnection.getInputStream();
                    reader = new InputStreamReader(in);

                    data = reader.read();
                    while(data != -1) {
                        char current = (char) data;
                        sb.append(current);
                        data = reader.read();
                    }

                    newsItem = new JSONObject(sb.toString());

                    title = newsItem.getString("title");
                    if(newsItem.has("text")) {
                        content = newsItem.getString("text");
                    }
                    else if(newsItem.has("url")) {

                        content = newsItem.getString("url");

//                        String contentURL = newsItem.getString("url");
//
//                        sb.setLength(0);
//                        url = new URL(contentURL);
//                        httpConnection = (HttpURLConnection) url.openConnection();
//                        in = httpConnection.getInputStream();
//                        reader = new InputStreamReader(in);
//
//                        data = reader.read();
//                        while(data != -1) {
//                            char current = (char) data;
//                            sb.append(current);
//                            data = reader.read();
//                        }
//
//                        content = sb.toString();
                    }
                    else {
                        content = "";
                    }
                    db.addEntry(id, title, content);

                }

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(httpConnection != null) {
                    httpConnection.disconnect();
                }
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {

            Cursor headline = db.getEntry(null, null, null);

            while(!headline.isAfterLast()) {

                headlinesList.add(headline.getString(headline.getColumnIndex("title")));
                headline.moveToNext();
                headlinesAdapter.notifyDataSetChanged();
            }
            headline.close();

        }
    }

    /**
     *  Adapter class to handle headlines for main app list
     *  Bringing over comments from the Android developer site I find helpful to understand this
     */
    public class HeadlinesAdapter extends RecyclerView.Adapter<HeadlinesAdapter.HeadlinesViewHolder> {
        private List<String> headlinesData;

        public HeadlinesAdapter(List<String> headlines) {
            headlinesData = headlines;
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class HeadlinesViewHolder extends RecyclerView.ViewHolder {
            //Data items are just strings
            public TextView textView;
            public HeadlinesViewHolder(TextView v) {
                super(v);
                textView = v;
            }
        }

        // Create new views (called by layout manager)
        @Override
        public HeadlinesAdapter.HeadlinesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Create a new view
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.headline_item, parent, false);
            HeadlinesViewHolder vh = new HeadlinesViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(HeadlinesViewHolder holder, int position) {

            final int p = position;

            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.textView.setText(headlinesData.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("CLICKED", "WE ARE CLICKED");
                    Intent articleIntent = new Intent(getApplicationContext(), ArticleActivity.class);
                    articleIntent.putExtra("title", headlinesList.get(p));
                    startActivity(articleIntent);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return headlinesData.size();
        }
    }
}