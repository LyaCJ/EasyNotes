package com.example.madey.easynotes.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Madeyedexter on 26-11-2016.
 */

public abstract class CreateThumbsTask extends AsyncTask<String, Integer, Bitmap> {
    Context ctx;
    Point dim;

    public CreateThumbsTask(Context ctx, Point dim) {
        super();
        this.ctx = ctx;
        this.dim = dim;
    }

    public abstract void onCompleted(Bitmap bmp);

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bmp = null;
        try {
            FileInputStream fis = ctx.openFileInput(params[0]);
            System.out.println("Image Name: " + params[0]);
            bmp = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp = ThumbnailUtils.extractThumbnail(bmp, dim.x, dim.y);
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap o) {
        super.onPostExecute(o);
        onCompleted(o);

    }
}
