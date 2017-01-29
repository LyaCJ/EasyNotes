package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.madey.easynotes.contract.NoteReaderContract;
import com.example.madey.easynotes.contract.sqlite.NoteReaderDbHelper;
import com.example.madey.easynotes.models.SimpleNoteModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Madeyedexter on 22-01-2017.
 */

public class UpdateImageListTask extends AsyncTask<SimpleNoteModel, Void, Integer> {

    private static final String LOG_TAG = "UpdateImageListTask";
    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     */
    private Activity activity;

    public UpdateImageListTask(Activity activity) {
        super();
        this.activity = activity;
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
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
    protected Integer doInBackground(SimpleNoteModel... params) {
        int affected = 0;
        SimpleNoteModel simpleNoteModel = params[0];
        long id = simpleNoteModel.getId();
        NoteReaderDbHelper mDbHelper = new NoteReaderDbHelper(activity);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Gson gson = new GsonBuilder().create();
        Log.d(LOG_TAG, "Updating ImageList for note with id " + id);
        ContentValues values = new ContentValues();
        values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_IMG, simpleNoteModel.getHasImages() ? 1 : 0);
        values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_IMG_DATA, gson.toJson(simpleNoteModel.getImageModels()));
        affected = db.update(NoteReaderContract.NoteEntry.TABLE_NAME, values, NoteReaderContract.NoteEntry.SQL_UPDATE_WHERE_CLAUSE, new String[]{String.valueOf(id)});
        db.close();
        return affected;
    }
}
