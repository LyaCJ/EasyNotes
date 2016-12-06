package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.madey.easynotes.contract.NoteReaderContract;
import com.example.madey.easynotes.contract.sqlite.NoteReaderDbHelper;
import com.example.madey.easynotes.data.SimpleNoteDataObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by madey on 8/9/2016.
 */
public abstract class ReadSimpleNoteFilesTask extends AsyncTask<String, Integer, List<Object>> {

    //private SimpleNoteDataObject sndo;
    private Activity ctx;

    public ReadSimpleNoteFilesTask(Activity ctx) {
        //this.sndo=sndo;
        this.ctx = ctx;
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
    protected List<Object> doInBackground(String... params) {
        NoteReaderDbHelper nrdh = new NoteReaderDbHelper(ctx);
        SQLiteDatabase db = nrdh.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                NoteReaderContract.NoteEntry._ID,
                NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE,
                NoteReaderContract.NoteEntry.COLUMN_NAME_CONTENT,
                NoteReaderContract.NoteEntry.COLUMN_NAME_CREATED,
                NoteReaderContract.NoteEntry.COLUMN_NAME_MODIFIED,
                NoteReaderContract.NoteEntry.COLUMN_NAME_IMGURI
        };

// Filter results WHERE "title" = 'My Title'
        String selection = NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {"%"};

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                NoteReaderContract.NoteEntry.COLUMN_NAME_CREATED + " DESC";

        Cursor cursor = db.query(
                NoteReaderContract.NoteEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        //list to store read notes
        List<Object> notes = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_CONTENT));
                long created = cursor.getLong(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_CREATED));
                long modified = cursor.getLong(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_MODIFIED));
                List<String> fileName = new ArrayList<String>(Arrays.asList(cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_IMGURI)).split(",")));

                //log id
                //System.out.println("Str SIze: " + fileName.get(0).length());

                //create object
                SimpleNoteDataObject sndo = new SimpleNoteDataObject(title, content);
                sndo.setId(itemId);
                sndo.setCreationDate(created);
                sndo.setLastModifiedDate(modified);
                sndo.setImagePath((ArrayList<String>) fileName);
                notes.add(sndo);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        nrdh.close();
        return notes;
    }

    public abstract void onResponseReceived(List<Object> obj);

    @Override
    protected void onPostExecute(List<Object> simpleNoteDataObject) {
        super.onPostExecute(simpleNoteDataObject);
        onResponseReceived(simpleNoteDataObject);
    }


}
