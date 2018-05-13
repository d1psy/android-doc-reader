package com.example.golubtsov.documentreader.database;

import android.provider.BaseColumns;

/**
 * Created by Даня on 22.02.2018.
 */

public final class DocumentDBContract {

    private DocumentDBContract() {}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "documents";
        public static final String COLUMN_NAME = "name";
    }
}