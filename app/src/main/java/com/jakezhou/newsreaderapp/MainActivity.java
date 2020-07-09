package com.jakezhou.newsreaderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView headlinesRecyclerView;
    private RecyclerView.Adapter headlinesAdapter;
    private RecyclerView.LayoutManager headlinesManager;
    List<String> headlinesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        // TODO: We will need an SQL table that stores ids, titles, and either text or html from the url
        // Accessing our database


        // TODO: When we click on a list item, we should direct to a webview activity that displays either the text or html from url

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
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.textView.setText(headlinesData.get(position));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return headlinesData.size();
        }
    }
}