package com.jakezhou.newsreaderapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ArticleActivity extends AppCompatActivity {

    WebView webView;
    Intent data;
    NewsReaderDb database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        // Setting up webview
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new HelloWebViewClient());

        data = getIntent();
        // OFFENDING LINE:
        database = MainActivity.db;

        // Load content into webview
        loadContent();
    }

    private void loadContent() {
        // Getting article content
        String title = data.getStringExtra("title");
        Cursor contentC = database.getEntry(new String[]{NewsReaderContract.NewsEntry.COLUMN_NAME_CONTENT}, "title = ?", new String[]{title});
        String content = contentC.getString(contentC.getColumnIndex(NewsReaderContract.NewsEntry.COLUMN_NAME_CONTENT));
        webView.loadUrl(content);
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}


