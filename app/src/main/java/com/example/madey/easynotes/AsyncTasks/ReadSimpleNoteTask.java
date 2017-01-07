package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.madey.easynotes.contract.NoteReaderContract;
import com.example.madey.easynotes.contract.sqlite.NoteReaderDbHelper;
import com.example.madey.easynotes.models.AudioClipModel;
import com.example.madey.easynotes.models.CoarseAddress;
import com.example.madey.easynotes.models.Coordinates;
import com.example.madey.easynotes.models.ImageModel;
import com.example.madey.easynotes.models.ListItemModel;
import com.example.madey.easynotes.models.SimpleNoteModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madey on 8/9/2016.
 */
public abstract class ReadSimpleNoteTask extends AsyncTask<String, Integer, List<Object>> {

    //private SimpleNoteModel sndo;
    private Activity ctx;

    public ReadSimpleNoteTask(Activity ctx) {
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
                NoteReaderContract.NoteEntry.COLUMN_NAME_IMG_DATA
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
        Gson gson = new Gson();
        if (cursor.moveToFirst()) {
            do {
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_CONTENT));
                long created = cursor.getLong(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_CREATED));
                long modified = cursor.getLong(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_MODIFIED));
                Boolean hasImages = cursor.getInt(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_IMG)) == 1;
                String imageModelJson = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_IMG_DATA));
                ArrayList<ImageModel> imageModels = gson.fromJson(imageModelJson, new TypeToken<ArrayList<ImageModel>>() {
                }.getType());
                Boolean hasAudio = cursor.getInt(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_AUDIO)) == 1;
                String audioModelJson = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_AUDIO_DATA));
                ArrayList<AudioClipModel> audioModels = gson.fromJson(audioModelJson, new TypeToken<ArrayList<AudioClipModel>>() {
                }.getType());
                Boolean hasLocation = cursor.getInt(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_LOCATION)) == 1;
                String coordinatesJson = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_COORDINATES));
                Coordinates coordinates = gson.fromJson(coordinatesJson, Coordinates.class);
                String coarseAddressJson = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_LOCATION));
                CoarseAddress coarseAddress = gson.fromJson(coarseAddressJson, CoarseAddress.class);
                Boolean hasList = cursor.getInt(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_HAS_LIST)) == 1;
                String listModelsJson = cursor.getString(cursor.getColumnIndexOrThrow(NoteReaderContract.NoteEntry.COLUMN_NAME_LIST_JSON));
                ArrayList<ListItemModel> listItemModels = gson.fromJson(listModelsJson, new TypeToken<ArrayList<ListItemModel>>() {
                }.getType());

                //log id
                //System.out.println("Str SIze: " + fileName.get(0).length());

                //create object
                SimpleNoteModel sndo = new SimpleNoteModel(title, content);
                sndo.setId(itemId);
                sndo.setCreationDate(created);
                sndo.setLastModifiedDate(modified);
                sndo.setHasImages(hasImages);
                sndo.setImageModels(imageModels);
                sndo.setHasAudioRecording(hasAudio);
                sndo.setAudioClipModels(audioModels);
                sndo.setLocationEnabled(hasLocation);
                sndo.setCoordinates(coordinates);
                sndo.setCoarseAddress(coarseAddress);
                sndo.setHasList(hasList);
                sndo.setListItems(listItemModels);

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
