package com.example.madey.easynotes.NoteFragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.madey.easynotes.AsyncTasks.CreateThumbsTask;
import com.example.madey.easynotes.AsyncTasks.WriteFileTask;
import com.example.madey.easynotes.AsyncTasks.WriteSimpleNoteFilesTask;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.data.SimpleNoteDataObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class NewNoteFragment extends NoteFragment {


    private LinearLayout imageHolderLayout;

    private ArrayList<String> fileNames = new ArrayList<>();

    private boolean imageWrittenFlag = false;

    public NewNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        MainActivity.CURRENT_FRAGMENT = MainActivity.FRAGMENTS.NEWNOTE;
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.fragment_new_note, container, false);
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbar.setTitle("Create Note");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().remove(NewNoteFragment.this).commit();

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
                    startActivityForResult(cameraIntent, Utils.CAMERA_REQUEST);
                } else
                    Snackbar.make(getView(), "Image Limit Reached", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.action_pictures:
                if (imageHolderLayout.getChildCount() < 4) {
                    Utils.verifyStoragePermissions(getActivity());
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), Utils.PICTURE_REQUEST);
                } else
                    Snackbar.make(getView(), "Image Limit Reached", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.action_done:
                saveNote();


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void saveNote() {

        Utils.verifyStoragePermissions(getActivity());
        EditText title= (EditText) getView().findViewById(R.id.editText);
        EditText content= (EditText) getView().findViewById(R.id.editText2);
        if (title.getText().toString().length() == 0 && content.getText().toString().length() == 0 && fileNames.size() == 0) {
            Snackbar.make(getActivity().getCurrentFocus(), "Nothing to Save. Empty Note :(", Snackbar.LENGTH_SHORT).show();
            return;
        }
        final SimpleNoteDataObject sndo=new SimpleNoteDataObject(title.getText().toString(),content.getText().toString());
        Calendar c = Calendar.getInstance();
        if (sndo.getCreationDate() == 0) {
            sndo.setCreationDate(System.currentTimeMillis());
        }
        sndo.setLastModifiedDate(System.currentTimeMillis());
        sndo.setImagePath(fileNames);
        ((MainActivity)getActivity()).getNotes().add(0,sndo);

        //write note asynchronously to SQLite
        WriteSimpleNoteFilesTask wft = new WriteSimpleNoteFilesTask(getActivity()) {
            @Override
            public void onSaved(Boolean success) {
                //Let the activity know the current fragment
                if (success) {
                    Snackbar.make(getActivity().getCurrentFocus(), "Note Saved :)", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getActivity().getCurrentFocus(), "Error Saving Note :(", Snackbar.LENGTH_SHORT).show();
                }

                getActivity().getFragmentManager().popBackStack();
                getActivity().getFragmentManager().beginTransaction().remove(NewNoteFragment.this).commit();
            }
        };
        wft.execute(sndo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Bitmap> bitmaps = new ArrayList<>();
        List<Bitmap> thumbs = new ArrayList<>();
        imageWrittenFlag = false;
        if (resultCode == Activity.RESULT_OK) {
            int THUMBSIZE = Utils.DEVICE_WIDTH / 4;
            if (requestCode == Utils.CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                bitmaps.add(photo);
                Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                thumbs.add(thumb);
                imageHolderLayout.addView(createImageView(thumb));
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getData() != null) {
                Bitmap photo = null;
                try {
                    photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
                } catch (FileNotFoundException e) {
                    System.out.println("FileNotFoundException: " + e.getMessage());
                }
                bitmaps.add(photo);
                Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                thumbs.add(thumb);
                imageHolderLayout.addView(createImageView(thumb));
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getClipData() != null) {
                System.out.println("Clip Data:" + data.getClipData());
                for (int i = 0; bitmaps.size() <= 4 && i < data.getClipData().getItemCount(); i++) {
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
                    imageHolderLayout.addView(createImageView(thumb));
                }
            }
            //Write any images to disk asynchronously
            WriteFileTask eft = new WriteFileTask(this.getActivity()) {
                @Override
                public void onResponseReceived(Object obj) {
                    fileNames = (ArrayList<String>) obj;
                    System.out.println("File Names: " + fileNames.toString());
                    imageWrittenFlag = true;
                }
            };
            //execute the task
            Bitmap[] params = new Bitmap[bitmaps.size()];
            params = bitmaps.toArray(params);
            eft.execute(params);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("bitmap_files", fileNames);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            fileNames = (ArrayList<String>) savedInstanceState.getSerializable("bitmap_files");
            for (String name : fileNames) {
                new CreateThumbsTask(getActivity(), new Point(Utils.DEVICE_WIDTH / 4, Utils.DEVICE_WIDTH / 4)) {
                    @Override
                    public void onCompleted(Bitmap bmp) {
                        imageHolderLayout.addView(createImageView(bmp));
                    }
                }.execute(name);
            }
        }
    }

    /*private static abstract class PreviewGeneratorTask extends AsyncTask<List<Object>,Integer, Boolean>{

        @Override
        protected Boolean doInBackground(List<Object>... params) {
            boolean success = false;


            return success;
        }

        public abstract void onComplete(Boolean aBoolean);

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            onComplete(aBoolean);
        }
    }*/

    private Bitmap decodeBitmap(InputStream is, Size size) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        //decode only withing bounds
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeStream(is);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;

        return bmp;
    }
}
