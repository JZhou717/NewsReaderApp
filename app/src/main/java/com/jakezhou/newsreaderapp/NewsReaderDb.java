package com.jakezhou.newsreaderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jakezhou.newsreaderapp.NewsReaderContract.NewsEntry;

/**
 * A class to encapsulate and handle our database and relevant tasks
 */
public class NewsReaderDb {

    private NewsReaderDbHelper dbHelper;
    private SQLiteDatabase db;

    public NewsReaderDb(Context context) {
        // Accessing database in writable format
        dbHelper = new NewsReaderDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Inserts an entry into the database, returns the primary key of the entry
     */
    public long addEntry(int id, String title, String content) {
        // Map for db entries, key is column name
        ContentValues values = new ContentValues();
        values.put(NewsEntry.COLUMN_NAME_ID, id);
        values.put(NewsEntry.COLUMN_NAME_TITLE, title);
        values.put(NewsEntry.COLUMN_NAME_CONTENT, content);

        return db.insert(NewsEntry.TABLE_NAME, null, values);
    }

    public void dropTable() {
        db.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
    }

    /**
     * Returns a cursor the the return of this query
     * @param columns columns that we want to read from db
     * @param selection where clause column
     * @param selectionArgs where clause values
     * @return
     */
    public Cursor getEntry(String[] columns, String selection, String[] selectionArgs) {
        Cursor c = db.query(
                NewsEntry.TABLE_NAME,       // Table to query
                columns,                    // Columns to return
                selection,                  // WHERE clause columns
                selectionArgs,              // WHERE clause values
                null,              // GroupBy value
                null,               // Filter by groups
                null);              // Sort order

        if(c != null) c.moveToFirst();
        return c;
    }

}
