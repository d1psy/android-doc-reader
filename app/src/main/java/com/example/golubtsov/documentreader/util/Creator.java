package com.example.golubtsov.documentreader.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.SparseArray;
import android.widget.EditText;

import com.example.golubtsov.documentreader.database.DocumentDBContract;
import com.example.golubtsov.documentreader.database.DocumentDBHelper;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Даня on 13.05.2018.
 */

public class Creator {

    public void createDocument(EditText edit, ImageAdapter imageAdapter, Context context) {
        String filename = edit.getText().toString() + ".doc";
        try {
            File dir = context.getExternalFilesDir("documents");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String text = getText(imageAdapter, context);
            writer.write(text);
            insert(filename, context);
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private String getText(ImageAdapter imageAdapter, Context context) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < imageAdapter.getCount(); i++) {
            int x = 61;
            Bitmap photo = imageAdapter.getItem(i);
            TextRecognizer ocrFrame = new TextRecognizer.Builder(context).build();
            Frame frame = new Frame.Builder().setBitmap(photo).build();
            SparseArray<TextBlock> textBlocks = ocrFrame.detect(frame);
            for (int j = 0; j < textBlocks.size(); j++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(j));
                sb.append(textBlock.getValue());
                sb.append("\n");
                x--;
            }
            for (int j = 0; j < x; j++) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void insert(String name, Context context) {
        DocumentDBHelper mDbHelper = new DocumentDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DocumentDBContract.FeedEntry.COLUMN_NAME, name);
        db.insert(DocumentDBContract.FeedEntry.TABLE_NAME, null, values);
    }

    public File createImageFile(File storageDir) throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }
}
