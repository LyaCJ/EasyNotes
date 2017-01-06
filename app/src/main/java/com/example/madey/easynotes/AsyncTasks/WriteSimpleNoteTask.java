package com.example.madey.easynotes.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.example.madey.easynotes.contract.NoteReaderContract;
import com.example.madey.easynotes.contract.sqlite.NoteReaderDbHelper;
import com.example.madey.easynotes.models.SimpleNoteModel;

/**
 * Created by madey on 8/9/2016.
 */
public abstract class WriteSimpleNoteTask extends AsyncTask<SimpleNoteModel, Integer, Boolean> {

    private Context ctx;

    public WriteSimpleNoteTask(Context ctx) {
        this.ctx = ctx;
        //this.delegate=delegate;
    }

    //public AsyncResponse delegate;
    public abstract void onSaved(Boolean success);

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
    protected Boolean doInBackground(SimpleNoteModel... params) {
        NoteReaderDbHelper mDbHelper = new NoteReaderDbHelper(ctx);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        for (SimpleNoteModel sndo : params) {


// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE, sndo.getTitle());
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_CONTENT, sndo.getContent());
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_CREATED, sndo.getCreationDate());
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_MODIFIED, sndo.getLastModifiedDate());
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_IMGPATH, TextUtils.join(",", sndo.getImageFileNames()));

// Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(NoteReaderContract.NoteEntry.TABLE_NAME, null, values);
            //we have the row id for this note from SQLite
            sndo.setId(newRowId);
        }
        db.close();
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
        onSaved(success);

        //delegate.processFinish(success);
    }


}
