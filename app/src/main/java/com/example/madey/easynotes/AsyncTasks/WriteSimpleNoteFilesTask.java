package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.example.madey.easynotes.DataObject.SimpleNoteDataObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by madey on 8/9/2016.
 */
public class WriteSimpleNoteFilesTask extends AsyncTask<SimpleNoteDataObject,Integer,Boolean>{

    private Context ctx;
    public WriteSimpleNoteFilesTask(Context ctx, AsyncResponse delegate){
        this.ctx=ctx;
        //this.delegate=delegate;
    }

    //public AsyncResponse delegate;

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Boolean doInBackground(SimpleNoteDataObject... params) {

        for(SimpleNoteDataObject sndo:params) {
            FileOutputStream fos=null;
            try {
                ArrayList<String> fileNames=new ArrayList<>();
                for (Bitmap bmp : sndo.getImageList()) {
                    String fileName = "Image_" + System.currentTimeMillis()+".png";
                    fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    fileNames.add(fileName);
                }
                sndo.setImagePath(fileNames);
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

            fos = null;
            ObjectOutputStream oos = null;
            String fileName="Sndo_"+System.currentTimeMillis()+".snote";
            try {
                fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
                oos =new ObjectOutputStream(fos);
                oos.writeObject(sndo);
                oos.flush();


            } catch (FileNotFoundException e) {
                Snackbar.make(((Activity) ctx).getCurrentFocus(),"Unable to Save Note",Snackbar.LENGTH_SHORT).show();
            } catch (IOException e) {
                Snackbar.make(((Activity) ctx).getCurrentFocus(),"Error Saving Note",Snackbar.LENGTH_SHORT).show();
            }
            finally {
                try {
                    if(oos != null)
                        oos.close();
                    if(fos != null)
                        fos.close();
                } catch (IOException e) {
                    Snackbar.make(((Activity) ctx).getCurrentFocus(),"An I/O Error Occurred",Snackbar.LENGTH_SHORT).show();
                }
            }
        }
        return true;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param success The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        //delegate.processFinish(success);
    }

    public interface AsyncResponse {
        void processFinish(Boolean success);
    }


}
