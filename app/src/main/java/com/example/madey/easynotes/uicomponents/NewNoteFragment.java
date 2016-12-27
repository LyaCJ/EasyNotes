package com.example.madey.easynotes.uicomponents;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.madey.easynotes.AsyncTasks.WriteSimpleNoteTask;
import com.example.madey.easynotes.BarAudioPlayer;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.Coordinates;
import com.example.madey.easynotes.models.SimpleNoteDataObject;
import com.example.madey.easynotes.models.ThumbnailModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewNoteFragment extends NoteFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String LOG_TAG = "NewNoteFragment:";
    private TextView dateTimeLocationView;
    private ToggleButton locationToggleButton;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy, hh:mm a");
    private Long timeStamp = System.currentTimeMillis();
    private Coordinates coordinates;
    private Boolean isLocationEnabled = false;
    private Boolean isRecording = false;
    private Boolean isPlaying = false;
    private Boolean hasAudioRecording = false;

    private GoogleApiClient mGoogleApiClient;
    private MediaRecorder mRecorder;
    private MediaPlayer mediaPlayer;

    //media player listeners
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(getActivity(), "Error in MediaPlayer: " + what + "| Extra: " + extra, Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    private FloatingActionMenu menuGreen;
    private FloatingActionButton fabAudio;
    private FloatingActionButton fabPhotos;
    private FloatingActionButton fabPictures;
    private View rootView;

    public NewNoteFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        MainActivity.CURRENT_FRAGMENT = MainActivity.FRAGMENTS.NEWNOTE;
        setRetainInstance(true);
        rootView = inflater.inflate(R.layout.fragment_new_note, container, false);
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        getActivity().setTitle("Create Note");
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
        thumbsRecyclerView = (RecyclerView) rootView.findViewById(R.id.pictures_holder);
        LinearLayoutManager thumbsRecyclerViewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        thumbsRecyclerView.setLayoutManager(thumbsRecyclerViewLayoutManager);
        ThumbsRecyclerViewAdapter thumbsRecyclerViewAdapter = new ThumbsRecyclerViewAdapter(new ArrayList<ThumbnailModel>());
        thumbsRecyclerView.setAdapter(thumbsRecyclerViewAdapter);
        imageHolderProgressBar = (ProgressBar) rootView.findViewById(R.id.pictures_holder_progressbar);
        dateTimeLocationView = (TextView) rootView.findViewById(R.id.date_time_location_view);
        dateTimeLocationView.setText(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        locationToggleButton = (ToggleButton) rootView.findViewById(R.id.location_image_button);

        locationToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isLocationEnabled = true;
                    if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                        dateTimeLocationView.setText(simpleDateFormat.format(new Date(timeStamp)) + " in retrieving location...");
                    } else
                        retrieveLocation();
                } else {
                    isLocationEnabled = false;
                    coordinates = null;
                    mGoogleApiClient.disconnect();
                    dateTimeLocationView.setText(simpleDateFormat.format(new Date(timeStamp)));
                }

            }
        });

        // Inflate the layout for this fragment
        menuGreen = (FloatingActionMenu) rootView.findViewById(R.id.menu_red);

        fabAudio = (FloatingActionButton) rootView.findViewById(R.id.fab_add_audio);
        fabPhotos = (FloatingActionButton) rootView.findViewById(R.id.fab_add_photos);
        fabPictures = (FloatingActionButton) rootView.findViewById(R.id.fab_add_picture);

        fabAudio.setOnClickListener(this);
        fabPhotos.setOnClickListener(this);
        fabPictures.setOnClickListener(this);
        menuGreen.setClosedOnTouchOutside(true);
        return rootView;
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
            Toast.makeText(getActivity(), "Nothing to Save. Empty Note :(", Toast.LENGTH_SHORT).show();
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
        WriteSimpleNoteTask wft = new WriteSimpleNoteTask(getActivity()) {
            @Override
            public void onSaved(Boolean success) {
                if (success) {
                    ((MainActivity) NewNoteFragment.this.getActivity()).getNotes().add(0, sndo);
                    Toast.makeText(NewNoteFragment.this.getActivity(), "Note Saved :)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewNoteFragment.this.getActivity(), "Error Saving Note :(", Toast.LENGTH_SHORT).show();
                }

                NewNoteFragment.this.getActivity().getFragmentManager().popBackStack();
                NewNoteFragment.this.getActivity().getFragmentManager().beginTransaction().remove(NewNoteFragment.this).commit();
            }
        };
        wft.execute(sndo);
    }


    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("bitmap_files", fileNames);
        outState.putParcelable("coordinates", coordinates);
        outState.putBoolean("isLocationEnabled", isLocationEnabled);
        outState.putLong("timeStamp", timeStamp);
        outState.putStringArrayList("audio_files", audioFileNames);
        outState.putBoolean("has_audio", hasAudioRecording);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            fileNames = savedInstanceState.getStringArrayList("bitmap_files");
            for (String fileName : fileNames) {
                ThumbsRecyclerViewAdapter thumbsRecyclerViewAdapter = (ThumbsRecyclerViewAdapter) thumbsRecyclerView.getAdapter();
                thumbsRecyclerViewAdapter.getDataSet().add(0, new ThumbnailModel(fileName));
                thumbsRecyclerViewAdapter.notifyItemInserted(thumbsRecyclerViewAdapter.getDataSet().size() - 1);
            }
            coordinates = savedInstanceState.getParcelable("coordinates");
            isLocationEnabled = savedInstanceState.getBoolean("isLocationEnabled");
            timeStamp = savedInstanceState.getLong("timeStamp");
            audioFileNames = savedInstanceState.getStringArrayList("audio_files");
            hasAudioRecording = savedInstanceState.getBoolean("has_audio");
            //state variables restored. Now set them into the appropriate views.
            setViewState();
        }
    }

    @Override
    protected void setViewState() {
        if (isLocationEnabled) {
            retrieveLocation();
        } else
            dateTimeLocationView.setText(simpleDateFormat.format(new Date(timeStamp)));
        locationToggleButton.setChecked(isLocationEnabled);
    }


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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        retrieveLocation();
    }

    private void retrieveLocation() {
        //if we already have the coordinates, we will use them instead of using the location
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        if (coordinates != null) {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(coordinates.getX(), coordinates.getY(), 1);
                dateTimeLocationView.setText(simpleDateFormat.format(new Date(timeStamp)) + " in " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (location != null) {
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    dateTimeLocationView.setText(simpleDateFormat.format(new Date(timeStamp)) + " in " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
                    coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                dateTimeLocationView.setText(simpleDateFormat.format(new Date(timeStamp)) + " in <location error>");
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.isSuccess()) {
            dateTimeLocationView.setText(simpleDateFormat.format(new Date(timeStamp)));
            Toast.makeText(getActivity(), "Unable to Connect to Play Services: " + connectionResult.getErrorCode() + " " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            locationToggleButton.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        //close the menu
        menuGreen.toggle(true);
        //perform operations.
        switch (v.getId()) {
            case R.id.fab_add_picture:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, Utils.CAMERA_REQUEST);
                return;
            case R.id.fab_add_photos:
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
                return;
            case R.id.fab_add_audio:
                openDialogForAudioCapture();
                return;
            default:
                return;
        }
    }

    private void openDialogForAudioCapture() {
        //only once clip must be recorded per user interaction with Dialog
        //so decide a single filename for the auido clip
        final String fileName = "Audio_" + System.currentTimeMillis() + ".3gp";
        //verify audio permissions on api>23
        Utils.verifyRecordAudioPermissions(getActivity());
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //inflate dialog layout
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_record_audio, null);
// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.audio_dialog_message)
                .setTitle(R.string.audio_dialog_title)
                .setView(v);

        // Add the buttons
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button, if recording, stop recording
                if (isRecording)
                    stopRecording();
                //save the file name as an audio file, if the file was created
                File file = new File(Utils.getStoragePath(getActivity()) + "/" + fileName);
                if (file.exists()) {
                    //This file name will be used after rotation to re populate the UI with an audio player widget
                    addAudioRecording(fileName);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, stop recording if recording is in progress
                if (isRecording)
                    stopRecording();
                //delete the recording file
                File file = new File(Utils.getStoragePath(getActivity()) + "/" + fileName);
                if (file.delete())
                    Toast.makeText(getActivity(), "Recorded File Deleted", Toast.LENGTH_SHORT).show();
            }
        });
// Set other dialog properties
// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        final ToggleButton recordAudioButton = (ToggleButton) v.findViewById(R.id.toggle_button_record_audio);
        recordAudioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //change status to started recording
                    recordAudioButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mic_green_24dp));
                    startRecording(fileName);
                } else {
                    Log.e(LOG_TAG, "Stopping Audio Recording");
                    //change status to stop recording
                    recordAudioButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mic_black_24dp));
                    //release resource
                    stopRecording();
                    Log.e(LOG_TAG, "Stopped Audio Recording");
                }

            }
        });
        dialog.show();
    }

    private void addAudioRecording(final String fileName) {
        //set boolean audio flag to true
        hasAudioRecording = true;
        audioFileNames.add(fileName);
        final BarAudioPlayer barAudioPlayer = new BarAudioPlayer(fileName, getActivity());
        barAudioPlayer.setDeleteDialogClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        removeAudioRecording(barAudioPlayer, fileName);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked, simply dismiss the dialog
                        break;
                }
            }
        });
        barAudioPlayer.setPlayCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setOnPreparedListener(onPreparedListener);
                        mediaPlayer.setOnErrorListener(onErrorListener);
                        mediaPlayer.setDataSource(Utils.getStoragePath(getActivity()) + "/" + fileName);
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "Invalid Audio Source", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mediaPlayer.pause();
                }
            }
        });
        ((LinearLayout) rootView.findViewById(R.id.new_note_main_content_layout)).addView(barAudioPlayer.getCircularAudioPlayerUI(), 1);
    }

    private void removeAudioRecording(BarAudioPlayer barAudioPlayer, final String fileName) {
        File file = new File(Utils.getStoragePath(getActivity()) + "/" + fileName);
        //TODO stop playing audio

        //TODO remove audio data source

        if (file.exists() && file.delete()) {
            //remove ui component of audio player
            ((LinearLayout) rootView.findViewById(R.id.new_note_main_content_layout)).removeView(barAudioPlayer.getCircularAudioPlayerUI());
            //at this point, barAudioPlayer should be gc'd by the GC, but what if it does not?
            //TODO ensure garbage collection of circularAudioPlayerUI
            //remove fileName
            audioFileNames.remove(fileName);
            if (audioFileNames.size() == 0)
                hasAudioRecording = false;
            //show success toast
            Toast.makeText(getActivity(), "Audio Clip Deleted", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "Unable to delete Audio Clip", Toast.LENGTH_SHORT).show();

    }


    private void stopRecording() {
        try {
            if (mRecorder != null)
                mRecorder.stop();
        } catch (RuntimeException re) {
            Toast.makeText(getActivity(), "Exception while Stopping Recording: " + re.getMessage(), Toast.LENGTH_SHORT).show();
            re.printStackTrace();
            Log.e(LOG_TAG, "Exception while stopping: " + re.getMessage());
        } finally {
            if (mRecorder != null)
                mRecorder.release();
            mRecorder = null;
            isRecording = false;
        }
    }

    private void startRecording(String fileName) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(Utils.getStoragePath(getActivity()) + "/" + fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
        isRecording = true;
    }
}
