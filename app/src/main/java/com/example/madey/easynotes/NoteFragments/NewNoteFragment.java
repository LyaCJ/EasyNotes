package com.example.madey.easynotes.NoteFragments;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.madey.easynotes.AsyncTasks.WriteSimpleNoteFilesTask;
import com.example.madey.easynotes.DataObject.SimpleNoteDataObject;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class NewNoteFragment extends android.app.Fragment {

    private static final int CAMERA_REQUEST = 1088;
    private static final int PICTURE_REQUEST = 1188;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private LinearLayout imageHolderLayout;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<Bitmap> thumbs = new ArrayList<>();


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
        System.out.println("Fragment Note Create");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(NewNoteFragment.this).commit();
                getFragmentManager().popBackStack();

            }
        });
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
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST);
                } else
                    Snackbar.make(getView(), "Image Limit Reached", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.action_done:
                saveNote();

                getActivity().getFragmentManager().popBackStack();
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        verifyStoragePermissions(getActivity());
        EditText title= (EditText) getView().findViewById(R.id.editText);
        EditText content= (EditText) getView().findViewById(R.id.editText2);
        final SimpleNoteDataObject sndo=new SimpleNoteDataObject(title.getText().toString(),content.getText().toString());
        sndo.setImageURI(bitmaps);
        Calendar c = Calendar.getInstance();
        if(sndo.getCreationDate()== null){
            sndo.setCreationDate(c.getTime());
        }
        sndo.setLastModifiedDate(c.getTime());
        ((MainActivity)getActivity()).getNotes().add(0,sndo);
        //write images asynchronously

        WriteSimpleNoteFilesTask wft=new WriteSimpleNoteFilesTask(getActivity(), new WriteSimpleNoteFilesTask.AsyncResponse() {
            @Override
            public void processFinish(Boolean success) {
            }
        });
        wft.execute(sndo);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int width = imageHolderLayout.getWidth();
        imageHolderLayout.setMinimumHeight(width / 4);
        ImageView imageView = new ImageView(getActivity());
        imageView.setAdjustViewBounds(false);
        imageView.setMaxWidth(width / 4);
        imageView.setMaxHeight(width / 4);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(getResources().getColor(R.color.accent_dark));
        int THUMBSIZE = width / 4;
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            bitmaps.add(photo);
            Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
            thumbs.add(thumb);
            imageView.setImageBitmap(thumb);
            imageHolderLayout.addView(imageView);
        }
        if (requestCode == PICTURE_REQUEST && resultCode == Activity.RESULT_OK && null != data && data.getData() != null) {
            Bitmap photo = null;
            try {
                photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            }
            bitmaps.add(photo);
            Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
            thumbs.add(thumb);
            imageView.setImageBitmap(thumb);
            imageHolderLayout.addView(imageView);
        }
        if (requestCode == PICTURE_REQUEST && resultCode == Activity.RESULT_OK && null != data && data.getClipData() != null) {
            System.out.println("Clip Data:" + data.getClipData());
            for (int i = 0; bitmaps.size() <= 4 && i<data.getClipData().getItemCount(); i++) {
                imageView = new ImageView(getActivity());
                imageView.setAdjustViewBounds(false);
                imageView.setMaxWidth(width / 4);
                imageView.setMaxHeight(width / 4);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                ClipData.Item item = data.getClipData().getItemAt(i);
                InputStream is = null;
                try {
                    is = getActivity().getContentResolver().openInputStream(item.getUri());
                } catch (FileNotFoundException e) {
                    System.out.println("Exception: " + e.getMessage());
                }
                Bitmap photo = BitmapFactory.decodeStream(is);
                bitmaps.add(photo);
                Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                thumbs.add(thumb);
                imageView.setImageBitmap(thumb);
                imageHolderLayout.addView(imageView);
            }


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
    public interface NoteOnSaveListener {
        void onNoteSaved();
    }
}
