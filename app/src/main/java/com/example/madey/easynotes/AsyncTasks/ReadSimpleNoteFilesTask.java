package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.View;

import com.example.madey.easynotes.DataObject.SimpleNoteDataObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

/**
 * Created by madey on 8/9/2016.
 */
public class ReadSimpleNoteFilesTask extends AsyncTask<String,Integer,SimpleNoteDataObject>{

    private SimpleNoteDataObject sndo;
    private Activity ctx;
    private OnNoteLoadedListener onNoteLoadedListener;

    public ReadSimpleNoteFilesTask(SimpleNoteDataObject sndo, Activity ctx){
        this.sndo=sndo;
        this.ctx=ctx;
    }

    public void setOnNoteLoadedListener(OnNoteLoadedListener onNoteLoadedListener) {
        this.onNoteLoadedListener = onNoteLoadedListener;
    }

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
    protected SimpleNoteDataObject doInBackground(String... params) {

        String fileName=params[0];
        SimpleNoteDataObject read=null;
        ObjectInputStream ois=null;
            try {
                FileInputStream fis=ctx.openFileInput(fileName);
                ois=new ObjectInputStream(fis);
                read=(SimpleNoteDataObject) ois.readObject();
                ois.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        finally {
                if(read!=null){
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sndo.setImagePath(read.getImagePath());
                    sndo.setLastModifiedDate(read.getLastModifiedDate());
                    sndo.setCreationDate(read.getCreationDate());
                    sndo.setTitle(read.getTitle());
                    sndo.setContent(read.getContent());
                }
            }

        for(String path:sndo.getImagePath()){
            Bitmap bmp= null;
            Bitmap thumb = null;
            try {
                bmp = BitmapFactory.decodeStream(ctx.openFileInput(path));
                int id = ctx.getResources().getIdentifier("simple_note_card_view", "id", ctx.getPackageName());
                View view = ctx.findViewById(id);
                thumb = ThumbnailUtils.extractThumbnail(bmp, 200, 200);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("Path:" + bmp);
            sndo.getImageList().add(bmp);
            sndo.getImageThumbs().add(thumb);
        }

        return sndo;
    }

    @Override
    protected void onPostExecute(SimpleNoteDataObject simpleNoteDataObject) {
        super.onPostExecute(simpleNoteDataObject);
        onNoteLoadedListener.onNoteLoaded(simpleNoteDataObject);
    }

    public interface OnNoteLoadedListener{
        void onNoteLoaded(SimpleNoteDataObject sndo);
    }


}
