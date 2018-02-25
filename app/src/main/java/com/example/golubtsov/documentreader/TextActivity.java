package com.example.golubtsov.documentreader;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.golubtsov.documentreader.DocumentDBContract.FeedEntry;

/**
 * Created by Даня on 26.01.2018.
 */

public class TextActivity extends AppCompatActivity {

    EditText edit;
    Button takePhoto;
    Button createFile;
    Button takeFromGallery;
    List<Bitmap> photoList;
    String mCurrentPhotoPath;
    GridView gridView;
    Uri photoURI;
    ArrayAdapter<Bitmap> adapter;
    ImageAdapter imageAdapter;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PHOTO_GALLERY_REQUEST = 2000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        edit = findViewById(R.id.message);
        takePhoto = findViewById(R.id.takeAnother);
        createFile = findViewById(R.id.createFile);
        takeFromGallery = findViewById(R.id.takeFromGallery);
        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(3);
        photoList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.item, R.id.tvText);
        imageAdapter = new ImageAdapter(this);
        Intent intent = getIntent();
        Uri photoURIFromMain = intent.getParcelableExtra("photoURI");
        try {
            imageAdapter.addItem(MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURIFromMain));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gridView.setAdapter(imageAdapter);
    }

    public void takeAnother(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    public void takeFromGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PHOTO_GALLERY_REQUEST);
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                imageAdapter.addItem(MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI));
                setImagesInGridView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PHOTO_GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                imageAdapter.addItem(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                setImagesInGridView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setImagesInGridView() {
        gridView.setAdapter(imageAdapter);
    }

    public void createDocument(View view) {
        String filename = edit.getText().toString() + ".doc";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/DocCreator";
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            Context context = getApplicationContext();
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < imageAdapter.getCount(); i++) {
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
            writer.write(sb.toString());
            insert(filename);
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void insert(String name) {
        DocumentDBHelper mDbHelper = new DocumentDBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME, name);

        long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
    }
}
