package com.example.madey.easynotes.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.madey.easynotes.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Madeyedexter on 26-11-2016.
 */

public abstract class CreateThumbsTask extends AsyncTask<Uri, Integer, ArrayList<Bitmap>> {
    Context ctx;
    Point dim;

    public CreateThumbsTask(Context ctx, Point dim) {
        super();
        this.ctx = ctx;
        this.dim = dim;
    }

    public abstract void onCompleted(ArrayList<Bitmap> bmp);

    @Override
    protected ArrayList<Bitmap> doInBackground(Uri... params) {
        ArrayList<Bitmap> thumbs=new ArrayList<>(5);
        for(Uri uri : params)
            thumbs.add(createThumbFromUri(uri));
        return thumbs;
    }



    private Bitmap createThumbFromUri(Uri uri){
        File file=new File(uri.getPath());
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory
        BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);

        // find the best scaling factor for the desired dimensions
        int desiredWidth = Utils.DEVICE_WIDTH/4;
        int desiredHeight = Utils.DEVICE_WIDTH/4;
        float widthScale = (float)bitmapOptions.outWidth/desiredWidth;
        float heightScale = (float)bitmapOptions.outHeight/desiredHeight;
        float scale = Math.min(widthScale, heightScale);
        int sampleSize = 1;
        while (sampleSize < scale) {
            sampleSize *= 2;
        }
        bitmapOptions.inSampleSize = sampleSize; // this value must be a power of 2,
        // this is why you can not have an image scaled as you would like to have
        bitmapOptions.inJustDecodeBounds = false; // now we want to load the image

        // Let's load just the part of the image necessary for creating the thumbnail, not the whole image
        Bitmap thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);
        thumbnail=ThumbnailUtils.extractThumbnail(thumbnail,Utils.DEVICE_WIDTH/4,Utils.DEVICE_WIDTH/4);
        return thumbnail;
    }

    @Override
    protected void onPostExecute(ArrayList<Bitmap> o) {
        super.onPostExecute(o);
        onCompleted(o);
    }
}
