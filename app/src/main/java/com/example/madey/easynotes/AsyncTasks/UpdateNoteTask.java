package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.madey.easynotes.contract.NoteReaderContract;
import com.example.madey.easynotes.contract.sqlite.NoteReaderDbHelper;
import com.example.madey.easynotes.models.SimpleNoteModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Madeyedexter on 14-01-2017.
 */

public class UpdateNoteTask extends AsyncTask<SimpleNoteModel, Integer, Integer> {

    private Activity activity;

    public UpdateNoteTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(SimpleNoteModel... params) {
        SimpleNoteModel simpleNoteModel = params[0];
        Integer affected = 0;
        NoteReaderDbHelper mDbHelper = new NoteReaderDbHelper(activity);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Gson gson = new GsonBuilder().create();
        System.out.println(gson.toJson(params[0]));
        for (SimpleNoteModel sndo : params) {
// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE, sndo.getTitle());
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_CONTENT, sndo.getContent());
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_CREATED, sndo.getCreationDate());
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_MODIFIED, sndo.getLastModifiedDate());
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_IMG, sndo.getHasImages() ? 1 : 0);
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_IMG_DATA, gson.toJson(sndo.getImageModels()));
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_AUDIO, sndo.getHasAudioRecording() ? 1 : 0);
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_AUDIO_DATA, gson.toJson(sndo.getAudioClipModels()));
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_LOCATION, sndo.getLocationEnabled() ? 1 : 0);
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_COORDINATES, gson.toJson(sndo.getCoordinates()));
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_LOCATION, gson.toJson(sndo.getCoarseAddress()));
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_LIST, sndo.getHasList() ? 1 : 0);
            values.put(NoteReaderContract.NoteEntry.COLUMN_NAME_LIST_JSON, gson.toJson(sndo.getListItems()));

// Insert the new row, returning the primary key value of the new row
            affected = db.update(NoteReaderContract.NoteEntry.TABLE_NAME, values, NoteReaderContract.NoteEntry.SQL_UPDATE_WHERE_CLAUSE, new String[]{String.valueOf(sndo.getId())});
        }
        db.close();
        return affected;
    }
}
