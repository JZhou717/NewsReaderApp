package com.jakezhou.newsreaderapp;

import android.provider.BaseColumns;

/**
 * Although a separate class to define a db schema is not fully necessary for such a small app, this is good practice for managing a SQLite table if we were to use the same tools on a larger project
 */

public class NewsReaderContract {
    // We do not want anything instantiating the contract class
    private NewsReaderContract() {}

    // Defining schema of DB. On a large project, we would not have to worry about changing column names since they are all stored in one location
    public static class NewsEntry implements BaseColumns {
        public static final String TABLE_NAME = "hacker_news";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
    }

}
