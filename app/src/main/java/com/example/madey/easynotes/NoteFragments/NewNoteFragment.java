package com.example.madey.easynotes.NoteFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
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
import android.widget.ProgressBar;

import com.example.madey.easynotes.AsyncTasks.CreateThumbsTask;
import com.example.madey.easynotes.AsyncTasks.WriteSimpleNoteFilesTask;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.data.SimpleNoteDataObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class NewNoteFragment extends NoteFragment {

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
                //save note here.
                saveNote();
            }
        });
        imageHolderLayout = (LinearLayout) v.findViewById(R.id.pictures_holder);
        imageHolderProgressBar = (ProgressBar) v.findViewById(R.id.pictures_holder_progressbar);
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
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Utils.CAMERA_REQUEST);
                return true;
            case R.id.action_pictures:
                Utils.verifyStoragePermissions(getActivity());
                Utils.verifyManageDocumentsPermissions(getActivity());
                Intent intent = null;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Utils.PICTURE_REQUEST);
                return true;
            case R.id.action_done:
                saveNote();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void saveNote() {
        //check storage permissions on the main thread.
        Utils.verifyStoragePermissions(getActivity());
        EditText title = (EditText) getView().findViewById(R.id.editText);
        EditText content = (EditText) getView().findViewById(R.id.editText2);
        //Validate note if it's worth saving.
        if (title.getText().toString().length() == 0 && content.getText().toString().length() == 0 && fileNames.size() == 0) {
            Snackbar.make(getActivity().getCurrentFocus(), "Nothing to Save. Empty Note :(", Snackbar.LENGTH_SHORT).show();
            return;
        }
        //create a data object holding this note.
        final SimpleNoteDataObject sndo = new SimpleNoteDataObject(title.getText().toString(), content.getText().toString());
        Calendar c = Calendar.getInstance();
        //date of creation as long millsiseconds.
        if (sndo.getCreationDate() == 0) {
            sndo.setCreationDate(System.currentTimeMillis());
        }
        sndo.setLastModifiedDate(System.currentTimeMillis());
        sndo.setImagePath(fileNames);
        // we will add the note once it is written successfully in SQLIte.
        //((MainActivity) getActivity()).getNotes().add(0, sndo);
        //write note asynchronously to SQLite
        WriteSimpleNoteFilesTask wft = new WriteSimpleNoteFilesTask(getActivity()) {
            @Override
            public void onSaved(Boolean success) {
                //Let the activity know the current fragment
                if (success) {
                    ((MainActivity) getActivity()).getNotes().add(0, sndo);
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
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("bitmap_files", fileNames);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            fileNames = savedInstanceState.getStringArrayList("bitmap_files");
            for (String file : fileNames) {
                new CreateThumbsTask(getActivity(), new Point(Utils.DEVICE_WIDTH / 4, Utils.DEVICE_WIDTH / 4)) {
                    @Override
                    public void onCompleted(ArrayList<Bitmap> bitmaps) {
                        for (Bitmap bmp : bitmaps)
                            imageHolderLayout.addView(createImageView(bmp));
                    }
                }.execute(file);
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
