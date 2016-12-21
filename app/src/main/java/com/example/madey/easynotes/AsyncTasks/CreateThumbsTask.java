package com.example.madey.easynotes.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.madey.easynotes.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Madeyedexter on 26-11-2016.
 */

public abstract class CreateThumbsTask extends AsyncTask<String, Integer, ArrayList<Bitmap>> {
    Context ctx;
    Point dim;

    public CreateThumbsTask(Context ctx, Point dim) {
        super();
        this.ctx = ctx;
        this.dim = dim;
    }

    public abstract void onCompleted(ArrayList<Bitmap> bmp);

    @Override
    protected ArrayList<Bitmap> doInBackground(String... params) {
        ArrayList<Bitmap> thumbs = new ArrayList<>(5);
        for (String fileName : params)
            thumbs.add(createThumbFromFileName(fileName));
        return thumbs;
    }


    private Bitmap createThumbFromUri(Uri uri) {
        System.out.println("Uri is : " + uri);
        InputStream is = null;

        try {
            is = ctx.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory


            BitmapFactory.decodeStream(is, null, bitmapOptions);

            // find the best scaling factor for the desired dimensions
            int desiredWidth = Utils.DEVICE_WIDTH / 4;
            int desiredHeight = Utils.DEVICE_WIDTH / 4;
            float widthScale = (float) bitmapOptions.outWidth / desiredWidth;
            float heightScale = (float) bitmapOptions.outHeight / desiredHeight;
            float scale = Math.min(widthScale, heightScale);
            int sampleSize = 1;
            while (sampleSize < scale) {
                sampleSize *= 2;
            }
            bitmapOptions.inSampleSize = sampleSize; // this value must be a power of 2,
            // this is why you can not have an image scaled as you would like to have
            bitmapOptions.inJustDecodeBounds = false; // now we want to load the image

            // Let's load just the part of the image necessary for creating the thumbnail, not the whole image
            is.close();
            is = ctx.getContentResolver().openInputStream(uri);
            Bitmap thumbnail = BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bitmapOptions);
            thumbnail = ThumbnailUtils.extractThumbnail(thumbnail, Utils.DEVICE_WIDTH / 4, Utils.DEVICE_WIDTH / 4);
            return thumbnail;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    private Bitmap createThumbFromFileName(String fileName) {
        FileInputStream fis = null;
        try {
            fis = ctx.openFileInput(fileName);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory

            BitmapFactory.decodeStream(fis, null, bitmapOptions);

            // find the best scaling factor for the desired dimensions
            int desiredWidth = Utils.DEVICE_WIDTH / 4;
            int desiredHeight = Utils.DEVICE_WIDTH / 4;
            float widthScale = (float) bitmapOptions.outWidth / desiredWidth;
            float heightScale = (float) bitmapOptions.outHeight / desiredHeight;
            float scale = Math.min(widthScale, heightScale);
            int sampleSize = 1;
            while (sampleSize < scale) {
                sampleSize *= 2;
            }
            bitmapOptions.inSampleSize = sampleSize; // this value must be a power of 2,
            // this is why you can not have an image scaled as you would like to have
            bitmapOptions.inJustDecodeBounds = false; // now we want to load the image

            // Let's load just the part of the image necessary for creating the thumbnail, not the whole image
            fis.close();
            fis = ctx.openFileInput(fileName);
            Bitmap thumbnail = BitmapFactory.decodeStream(fis, null, bitmapOptions);
            thumbnail = ThumbnailUtils.extractThumbnail(thumbnail, Utils.DEVICE_WIDTH / 4, Utils.DEVICE_WIDTH / 4);
            return thumbnail;
        } catch (FileNotFoundException ex) {
        } catch (IOException ioex) {
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }


    @Override
    protected void onPostExecute(ArrayList<Bitmap> o) {
        super.onPostExecute(o);
        onCompleted(o);
    }
}
