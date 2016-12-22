package com.example.madey.easynotes.contract;

import android.provider.BaseColumns;

/**
 * Created by Madeyedexter on 26-11-2016.
 */

public class NoteReaderContract {

    private NoteReaderContract() {

    }

    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_CREATED = "created";
        public static final String COLUMN_NAME_MODIFIED = "modified";


        public static final String COLUMN_NAME_IMGPATH = "imgpath";
        public static final String COLUMN_NAME_AUDIOPATH = "audiopath";
        public static final String COLUMN_NAME_NOTETYPE = "notetype";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME;
        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE = " INT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                        NoteEntry._ID + " INTEGER PRIMARY KEY," +
                        NoteEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_CREATED + INT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_MODIFIED + INT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_IMGPATH + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_AUDIOPATH + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_NOTETYPE + TEXT_TYPE +
                        " )";
    }
}
