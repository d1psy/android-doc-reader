package com.example.golubtsov.documentreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.golubtsov.documentreader.database.DocumentDBHelper;
import com.example.golubtsov.documentreader.util.Creator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button takePhoto;
    private static final int CAMERA_REQUEST = 1888;
    Uri photoURI;
    String mCurrentPhotoPath;
    private Creator creator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creator = new Creator();
        DocumentDBHelper mDbHelper = new DocumentDBHelper(getApplicationContext());
        setContentView(R.layout.activity_main);
        takePhoto = findViewById(R.id.take_photo);
    }

    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},23
                );
            }
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = creator.createImageFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                mCurrentPhotoPath = photoFile.getAbsolutePath();
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

    public void checkDocuments(View view) {
        Intent intent = new Intent(getApplicationContext(), DocumentsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Intent intent = new Intent(getApplicationContext(), TextActivity.class);
            intent.putExtra("photoURI", photoURI);
            startActivity(intent);
        }
    }
}
