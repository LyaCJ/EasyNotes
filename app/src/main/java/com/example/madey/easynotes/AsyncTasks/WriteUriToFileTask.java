package com.example.madey.easynotes.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madeyedexter on 12/21/2016.
 */

public abstract class WriteUriToFileTask extends AsyncTask<Uri, Void, List<String>> {
    private Context context;

    public WriteUriToFileTask(Context ctx) {
        super();
        this.context = ctx;
    }

    protected abstract void onCompleted(List<String> fileNames);

    @Override
    protected List<String> doInBackground(Uri... uri) {
        List<String> fileNames = new ArrayList<>();
        for (Uri u : uri) {
            try {
                //retrieve Bitmap from Uri
                Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(u));
                //write Bitmap image to internal storage.
                String fileName = "Image_" + System.currentTimeMillis() + ".png";
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                fileNames.add(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
            }
        }
        return fileNames;
    }

    @Override
    protected void onPostExecute(List<String> fileNames) {
        onCompleted(fileNames);
    }
}
