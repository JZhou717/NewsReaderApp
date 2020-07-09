package com.jakezhou.newsreaderapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jakezhou.newsreaderapp.NewsReaderContract.NewsEntry;

/**
 * A helper class that manages our database creation and such
 */
public class NewsReaderDbHelper extends SQLiteOpenHelper {
    //Change the version if we need to change the schema for the db
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NewsReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + NewsEntry.TABLE_NAME + " (" +
            NewsEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
            NewsEntry.COLUMN_NAME_TITLE + " VARCHAR, " +
            NewsEntry.COLUMN_NAME_CONTENT + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME;


    public NewsReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We won't need this for this project since we don't expect to be updating the schema anytime
        // However, this would be a point to study more about SQLite in Android should we need it in the future
        // For now, we'll follow the Android doc to discard the existing table and create a new one
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
