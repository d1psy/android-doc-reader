package com.example.golubtsov.documentreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Даня on 22.02.2018.
 */

public class DocumentDBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DocumentDBContract.FeedEntry.TABLE_NAME + " (" +
                    DocumentDBContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    DocumentDBContract.FeedEntry.COLUMN_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DocumentDBContract.FeedEntry.TABLE_NAME;

    private static final String SQL_SELECT_ENTRIES =
            "SELECT " + DocumentDBContract.FeedEntry.COLUMN_NAME + " FROM " + DocumentDBContract.FeedEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DocumentDBHelper.db";

    public DocumentDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public String getSqlSelectEntries() {
        return SQL_SELECT_ENTRIES;
    }
}