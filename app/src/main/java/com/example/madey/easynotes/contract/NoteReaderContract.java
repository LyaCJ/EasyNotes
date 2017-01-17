package com.example.madey.easynotes.contract;

import android.provider.BaseColumns;

/**
 * Created by Madeyedexter on 26-11-2016.
 */

public class NoteReaderContract {

    private NoteReaderContract() {

    }

    public static class NoteEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "note";
        //table columns
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_CREATED = "created";
        public static final String COLUMN_NAME_MODIFIED = "modified";
        public static final String COLUMN_NAME_HAS_IMG = "has_img";
        public static final String COLUMN_NAME_IMG_DATA = "img_data";
        public static final String COLUMN_NAME_HAS_AUDIO = "has_audio";
        public static final String COLUMN_NAME_AUDIO_DATA = "audio_data";
        public static final String COLUMN_NAME_HAS_LOCATION = "has_location";
        public static final String COLUMN_NAME_COORDINATES = "coordinates";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_HAS_LIST = "has_list";
        public static final String COLUMN_NAME_LIST_JSON = "list_json";

        //sql drop statement
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME;
        //sql where clause
        public static final String SQL_UPDATE_WHERE_CLAUSE = " _id= ?";
        //constants
        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE = " INT";
        private static final String COMMA_SEP = ",";
        //sql create statement
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                        NoteEntry._ID + " INTEGER PRIMARY KEY," +
                        NoteEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_CREATED + INT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_MODIFIED + INT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_HAS_IMG + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_IMG_DATA + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_HAS_AUDIO + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_AUDIO_DATA + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_HAS_LOCATION + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_COORDINATES + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_HAS_LIST + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                        NoteEntry.COLUMN_NAME_LIST_JSON + TEXT_TYPE +
                        " )";
    }
}
