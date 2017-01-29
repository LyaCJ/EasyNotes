package com.example.madey.easynotes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by madey on 8/13/2016.
 */
public final class Utils {

    public static final int CAMERA_REQUEST = 1088;
    public static final int PICTURE_REQUEST = 1188;
    private static final int REQUEST_COARSE_LOCATION = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_MANAGE_DOCUMENTS = 2;
    private static final int REQUEST_RECORD_AUDIO = 3;
    public static Point dimension;
    public static int DEVICE_WIDTH;
    public static int COUNTER = 0;

    //FRAGMENT TAGS
    public static String FRAGMENT_TAG_MAIN = "main_fragment";
    public static String FRAGMENT_TAG_NEWNOTE = "newnote_fragment";
    public static String FRAGMENT_TAG_NEWLIST = "newlist_fragment";
    public static String FRAGMENT_TAG_NEWAUDIO = "newaudio_fragment";
    public static String FRAGMENT_TAG_SEARCH = "search_fragment";
    public static String FRAGMENT_TAG_PAGER = "pager_fragment";


    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String[] PERMISSIONS_COARSE_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    public static void initDimensions(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dimension = new Point(width, height);
        DEVICE_WIDTH = width < height ? width : height;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // Check if we have write permission
        if (currentapiVersion >= 23) {
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    public static void verifyManageDocumentsPermissions(Activity activity) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {

            int manageDocumentsPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_DOCUMENTS);
            if (manageDocumentsPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, REQUEST_MANAGE_DOCUMENTS);
            }

        }
    }

    public static void verifyRecordAudioPermissions(Activity activity) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {

            int manageDocumentsPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
            if (manageDocumentsPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
            }

        }
    }

    public static void initStoragePreference(MainActivity mainActivity) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        String storagePreferenceKey = mainActivity.getResources().getString(R.string.key_storage_preference);
        String storageToUse = sp.getString(storagePreferenceKey, null);
        if (storageToUse == null) {
            Utils.verifyStoragePermissions(mainActivity);
            //running app for the first time, decide which storage to use
            boolean mExternalStorageWriteable = false;
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // We can read and write the media/sdcard
                sp.edit().putString(storagePreferenceKey, "EXTERNAL").commit();
                //create directory for storing media files.
                File file = Environment.getExternalStorageDirectory();
                System.out.println("External: " + file.getAbsolutePath());

            } else {
                // Something else is wrong. It may be one of many other states, but all we need
                //  to know is we can neither read nor write, hence use internal storage
                sp.edit().putString(storagePreferenceKey, "INTERNAL").commit();
                File file = Environment.getExternalStorageDirectory();
                System.out.println("INTERNAL: " + file.getAbsolutePath());
            }

        }


    }

    public static String getStoragePath(Context context) {
        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean storage = sp.getBoolean("storage_checkbox_preference", false);
        */
        String path;
        System.out.println("getExternalFilesDir(): " + context.getExternalFilesDir(null).getAbsolutePath());
        path = context.getExternalFilesDir(null).getAbsolutePath();
        /*
        if (storage) {
            path = context.getFilesDir().getAbsolutePath();
            System.out.println("getFilesDir(): " + path);
        } else {

        }*/
        return path;
    }

    public static FileOutputStream getFileOutputStream(Context ctx, String fileName) {
        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean storage = sp.getBoolean("storage_checkbox_preference", false);*/
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(ctx.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName));
            System.out.println("External: " + ctx.getExternalFilesDir(null).getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        /*
        String path;
        if (storage) {
            try {
                fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
                System.out.println("Internal: " + fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {

        }*/
        return fos;
    }

    public static FileInputStream getFileInputStream(Context ctx, String fileName) {
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        //boolean storage = sp.getBoolean("storage_checkbox_preference", false);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(ctx.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName));
        } catch (FileNotFoundException e) {
            Toast.makeText(ctx, "File Not Found: " + fileName, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        /*
        if (storage) {
            try {
                fis = ctx.openFileInput(fileName);
            } catch (FileNotFoundException e) {
                Toast.makeText(ctx, "File Not Found: " + fileName, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {

            }
        } else {

        }*/
        return fis;
    }

    public static void verifyLocationPermission(Activity activity) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // Check if we have write permission
        if (currentapiVersion >= 23) {
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_COARSE_LOCATION,
                        REQUEST_COARSE_LOCATION
                );
            }
        }
    }
}
