package com.example.madey.easynotes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by madey on 8/13/2016.
 */
public final class Utils {

    public static final int CAMERA_REQUEST = 1088;
    public static final int PICTURE_REQUEST = 1188;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_MANAGE_DOCUMENTS = 2;
    public static Point dimension;
    public static int DEVICE_WIDTH;
    public static int COUNTER = 0;

    //FRAGMENT TAGS
    public static String FRAGMENT_TAG_MAIN = "main_fragment";
    public static String FRAGMENT_TAG_NEWNOTE = "newnote_fragment";
    public static String FRAGMENT_TAG_NEWLIST = "newlist_fragment";
    public static String FRAGMENT_TAG_NEWAUDIO = "newaudio_fragment";
    public static String FRAGMENT_TAG_SEARCH = "search_fragment";

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static void init(Context ctx) {
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
            int manageDocumentsPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_DOCUMENTS);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, REQUEST_MANAGE_DOCUMENTS);
            }

        }
    }
}
