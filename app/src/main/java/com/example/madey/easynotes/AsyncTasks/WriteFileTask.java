package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madeyedexter on 26-11-2016.
 */

/**
 * Writes Bitmaps to internal storage and return the Uri of the files written
 */

public abstract class WriteFileTask extends AsyncTask<Bitmap, Integer, List<Uri>> {
    Context ctx;

    public WriteFileTask(Context ctx) {
        super();
        this.ctx = ctx;
    }

    public abstract void onResponseReceived(Object obj);

    @Override
    protected List<Uri> doInBackground(Bitmap... params) {
        FileOutputStream fos = null;
        ArrayList<Uri> fileNames = new ArrayList<>();
        try {
            for (Bitmap bmp : params) {
                String fileName = "Image_" + System.currentTimeMillis() + ".png";
                fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                fileNames.add(Uri.fromFile(ctx.getFileStreamPath(fileName)));
            }
        } catch (FileNotFoundException e) {
            Snackbar.make(((Activity) ctx).getCurrentFocus(), "Error Saving Images", Snackbar.LENGTH_SHORT);
        } catch (IOException ioe) {
            Snackbar.make(((Activity) ctx).getCurrentFocus(), "I/O Error Saving Images", Snackbar.LENGTH_SHORT);
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
        return fileNames;
    }

    @Override
    protected void onPostExecute(List<Uri> strings) {
        onResponseReceived(strings);
    }
}
