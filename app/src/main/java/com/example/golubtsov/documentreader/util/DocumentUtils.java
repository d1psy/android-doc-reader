package com.example.golubtsov.documentreader.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.golubtsov.documentreader.database.DocumentDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Даня on 14.05.2018.
 */

public class DocumentUtils {

    public List<String> getDocuments(Context context) {
        List<String> documents = new ArrayList<>();
        DocumentDBHelper mDbHelper = new DocumentDBHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(mDbHelper.getSqlSelectEntries(), null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String str;
                        str = cursor.getString(0);
                        documents.add(str);
                    } while (cursor.moveToNext());
                }
            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {

                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {

            }
        }
        return documents;
    }
}
