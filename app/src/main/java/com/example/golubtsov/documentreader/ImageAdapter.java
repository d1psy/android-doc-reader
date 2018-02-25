package com.example.golubtsov.documentreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Даня on 15.02.2018.
 */

public class ImageAdapter extends BaseAdapter {

    private Bitmap[] photo;
    private List<Bitmap> bitmapList;
    Context context;

    public ImageAdapter(Context c) {//Bitmap c)
        bitmapList = new ArrayList<>();
        context = c;
    }

    //---returns the number of images---
    public int getCount() {
        return bitmapList.size();
//        return imageIDs.length;
    }

    //---returns the ID of an item---
    public Bitmap getItem(int position) {
        return bitmapList.get(position);
//        return position;
    }

    public void addItem(Bitmap bitmap) {
        bitmapList.add(bitmap);
    }

    public long getItemId(int position) {
        return position;
    }

    //---returns an ImageView view---
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);//(context);
            imageView.setLayoutParams(new GridView.LayoutParams(185, 185));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }
//        imageView.setImageResource(imageIDs[position]);
        imageView.setImageBitmap(bitmapList.get(position));
        return imageView;
    }
}