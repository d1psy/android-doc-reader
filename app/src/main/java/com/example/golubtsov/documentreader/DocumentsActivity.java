package com.example.golubtsov.documentreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.golubtsov.documentreader.DocumentDBContract.FeedEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Даня on 22.02.2018.
 */

public class DocumentsActivity extends Activity {

    private List<String> list;
    ListView lvMain;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        getDocuments();
        setContentView(R.layout.activity_documents);

        lvMain = findViewById(R.id.lvMain);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);

        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                BufferedReader br = null;
                FileReader fr = null;
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/DocCreator/" + list.get(position);
                StringBuilder text = new StringBuilder();
                try {
                    fr = new FileReader(path);
                    br = new BufferedReader(fr);
                    String sCurrentLine;
                    while ((sCurrentLine = br.readLine()) != null) {
                        text.append(sCurrentLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null)
                            br.close();
                        if (fr != null)
                            fr.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentsActivity.this);
                builder.setTitle("Text")
                        .setMessage(text.toString())
                        .setCancelable(false)
                        .setNegativeButton("Done",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void getDocuments() {
        String selectQuery = "SELECT " + FeedEntry.COLUMN_NAME + " FROM " + FeedEntry.TABLE_NAME;
        DocumentDBHelper mDbHelper = new DocumentDBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String str;
                        str = cursor.getString(0);
                        list.add(str);
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
    }
}