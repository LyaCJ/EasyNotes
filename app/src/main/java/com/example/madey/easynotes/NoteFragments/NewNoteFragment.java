package com.example.madey.easynotes.NoteFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.madey.easynotes.R;

import java.util.ArrayList;
import java.util.List;

public class NewNoteFragment extends android.app.Fragment {

    private static final int CAMERA_REQUEST = 1088;
    private static final int PICTURE_REQUEST = 1188;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private LinearLayout imageHolderLayout;
    private List<Bitmap> bitmaps = new ArrayList<>();

    NoteOnSaveListener nosl;

    public NewNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.fragment_new_note, container, false);
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbar.setTitle("Create Note");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        imageHolderLayout = (LinearLayout) v.findViewById(R.id.pictures_holder);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu != null) {
            menu.setGroupVisible(R.id.create_note_menu_group, true);
            menu.setGroupVisible(R.id.main_menu_group, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                // do s.th.
                return true;
            case R.id.action_camera:
                if (imageHolderLayout.getChildCount() < 4) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else
                    Snackbar.make(getView(), "Image Limit Reached", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.action_pictures:
                if (imageHolderLayout.getChildCount() < 4) {
                    verifyStoragePermissions(getActivity());
                    Intent pictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pictureIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    pictureIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), PICTURE_REQUEST);
                } else
                    Snackbar.make(getView(), "Image Limit Reached", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.action_done:

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int width = imageHolderLayout.getWidth();
        imageHolderLayout.setMinimumHeight(width/4);
        ImageView imageView = new ImageView(getActivity());
        imageView.setAdjustViewBounds(false);
        imageView.setMaxWidth(width / 4);
        imageView.setMaxHeight(width / 4);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        int THUMBSIZE = width / 4;
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            bitmaps.add(photo);
            Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
            imageView.setImageBitmap(thumb);
            imageHolderLayout.addView(imageView);
        }
        if (requestCode == PICTURE_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if(cursor.getCount()+imageHolderLayout.getChildCount()>4){
                Snackbar.make(getView(),"Max 4 Images Allowed", Snackbar.LENGTH_SHORT).show();
            }
            cursor.moveToFirst();
            int count=0;
            do{
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                System.out.println(picturePath);

                Bitmap photo = BitmapFactory.decodeFile(picturePath);
                bitmaps.add(photo);
                Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                imageView.setImageBitmap(thumb);
                imageHolderLayout.addView(imageView);
                count++;
                cursor.moveToNext();
            }
            while(count<4 && !cursor.isAfterLast());
            cursor.close();

        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            nosl = (NoteOnSaveListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * Interface to send Data back to MainActivity
     */
    public interface NoteOnSaveListener{
        void onNoteSaved();
    }
}
