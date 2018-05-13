package com.example.golubtsov.documentreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.golubtsov.documentreader.util.Creator;
import com.example.golubtsov.documentreader.util.ImageAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Даня on 26.01.2018.
 */

public class TextActivity extends AppCompatActivity {

    EditText edit;
    Button takePhoto;
    Button createFile;
    Button takeFromGallery;
    List<Bitmap> photoList;
    GridView gridView;
    Uri photoURI;
    ArrayAdapter<Bitmap> adapter;
    ImageAdapter imageAdapter;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PHOTO_GALLERY_REQUEST = 2000;
    private Creator creator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        creator = new Creator();
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
                photoFile = creator.createImageFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23
                );
            }
        }
        creator.createDocument(edit, imageAdapter, getApplicationContext());
    }
}
