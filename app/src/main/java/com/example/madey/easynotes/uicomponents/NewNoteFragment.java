package com.example.madey.easynotes.uicomponents;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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

import com.example.madey.easynotes.AsyncTasks.WriteFileTask;
import com.example.madey.easynotes.AsyncTasks.WriteSimpleNoteTask;
import com.example.madey.easynotes.AsyncTasks.WriteUriToFileTask;
import com.example.madey.easynotes.BarAudioPlayer;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.AudioClipModel;
import com.example.madey.easynotes.models.CoarseAddress;
import com.example.madey.easynotes.models.Coordinates;
import com.example.madey.easynotes.models.ImageModel;
import com.example.madey.easynotes.models.SimpleNoteModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class NewNoteFragment extends NoteFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String LOG_TAG = "NewNoteFragment:";
    public static MEDIA_STATE CURR_MEDIA_STATE;
    //views and view groups
    private TextView dateTimeLocationView;
    private ToggleButton locationToggleButton;
    private FloatingActionMenu menuGreen;
    private FloatingActionButton fabAudio;
    private FloatingActionButton fabPhotos;
    private FloatingActionButton fabPictures;
    private View rootView;
    //For coarse location access
    private GoogleApiClient mGoogleApiClient;
    //Media Player and recorder
    private MediaRecorder mRecorder;
    private MediaPlayer mediaPlayer;
    //Formatting timestamp as date
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy, hh:mm a");
    //A map of BarAuioPlayers
    private Map<String, BarAudioPlayer> barAudioPlayers = new HashMap<>();
    //Instance variables used as state variables
    private Boolean isRecording = false;
    private String currentlyPlaying;
    private String audioCaptureFileName;
    //Following instance variables are persisted in the sqlite database
    /*private Long timeStamp = System.currentTimeMillis();
    private Coordinates coordinates;
    private Boolean isLocationEnabled = false;
    private Boolean hasAudioRecording = false;
    private CoarseAddress coarseAddress;
*/
    //A model to save all the data in this note
    private SimpleNoteModel simpleNoteModel;

    //Required empty constructor
    public NewNoteFragment() {
    }

    public static NewNoteFragment newInstance(SimpleNoteModel simpleNoteModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("simpleNote", simpleNoteModel);
        NewNoteFragment newNoteFragment = new NewNoteFragment();
        newNoteFragment.setArguments(bundle);
        return newNoteFragment;
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
        if (getArguments() != null) {
            //lets set everything in the SimpleNoteModel
            this.simpleNoteModel = getArguments().getParcelable("simpleNote");
        } else {
            this.simpleNoteModel = new SimpleNoteModel();
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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save note here.
                saveNote();
            }
        });
        thumbsRecyclerView = (RecyclerView) rootView.findViewById(R.id.pictures_holder);
        LinearLayoutManager thumbsRecyclerViewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        thumbsRecyclerView.setLayoutManager(thumbsRecyclerViewLayoutManager);
        ThumbsRecyclerViewAdapter thumbsRecyclerViewAdapter = new ThumbsRecyclerViewAdapter(new ArrayList<ImageModel>());
        thumbsRecyclerView.setAdapter(thumbsRecyclerViewAdapter);
        imageHolderProgressBar = (ProgressBar) rootView.findViewById(R.id.pictures_holder_progressbar);
        dateTimeLocationView = (TextView) rootView.findViewById(R.id.date_time_location_view);
        dateTimeLocationView.setText(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        locationToggleButton = (ToggleButton) rootView.findViewById(R.id.location_image_button);

        locationToggleButton.setOnCheckedChangeListener(this);

        // Inflate the layout for this fragment
        menuGreen = (FloatingActionMenu) rootView.findViewById(R.id.menu_red);

        fabAudio = (FloatingActionButton) rootView.findViewById(R.id.fab_add_audio);
        fabPhotos = (FloatingActionButton) rootView.findViewById(R.id.fab_add_photos);
        fabPictures = (FloatingActionButton) rootView.findViewById(R.id.fab_add_picture);

        fabAudio.setOnClickListener(this);
        fabPhotos.setOnClickListener(this);
        fabPictures.setOnClickListener(this);
        menuGreen.setClosedOnTouchOutside(true);
        //additionally initialize the view of this fragment if there is a bundled argument associated with it.
        if (getArguments() != null) {
            Bundle args = getArguments();
            SimpleNoteModel simpleNoteModel = args.getParcelable("simpleNote");
            ((EditText) rootView.findViewById(R.id.editText)).setText(simpleNoteModel.getTitle());
            ((EditText) rootView.findViewById(R.id.editText2)).setText(simpleNoteModel.getContent());
            if (simpleNoteModel.getLocationEnabled() && simpleNoteModel.getCoarseAddress() != null) {
                dateTimeLocationView.setText(simpleDateFormat.format(new Date(simpleNoteModel.getLastModifiedDate())) + " in " + simpleNoteModel.getCoarseAddress().toAddressString());
                locationToggleButton.setChecked(true);
            } else
                dateTimeLocationView.setText(simpleDateFormat.format(new Date(simpleNoteModel.getLastModifiedDate())));
            //add items to image recycler view
            thumbsRecyclerViewAdapter.getDataSet().addAll(simpleNoteModel.getImageModels());
            thumbsRecyclerViewAdapter.notifyDataSetChanged();
            //add Bar Audio Players
            setViewState();
        }

        //additionally resetting the media player
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
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
        if (title.getText().toString().length() == 0 && content.getText().toString().length() == 0 && ((ThumbsRecyclerViewAdapter) thumbsRecyclerView.getAdapter()).getDataSet().size() == 0 && barAudioPlayers.size() == 0) {
            Toast.makeText(getActivity(), "Nothing to Save. Empty Note :(", Toast.LENGTH_SHORT).show();
            return;
        }
        //create a data object holding this note.
        final SimpleNoteModel sndo = new SimpleNoteModel(title.getText().toString(), content.getText().toString());
        Calendar c = Calendar.getInstance();
        //date of creation as long milli seconds.
        if (sndo.getCreationDate() == 0) {
            sndo.setCreationDate(System.currentTimeMillis());
        }
        sndo.setLastModifiedDate(System.currentTimeMillis());

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
/*
        outState.putParcelableArrayList("bitmap_files", imageModels);
        outState.putParcelable("coordinates", coordinates);
        outState.putBoolean("isLocationEnabled", isLocationEnabled);
        outState.putLong("timeStamp", timeStamp);
        AudioClipModel[] audioArray = new AudioClipModel[audioClipModels.size()];
        outState.putParcelableArray("audio_files", audioClipModels.toArray(audioArray));
        outState.putBoolean("has_audio", hasAudioRecording);
        outState.putParcelable("coarse_address", coarseAddress);
        */
        outState.putString("curr_audio_recording_filename", audioCaptureFileName);
        outState.putParcelable("simpleNote", simpleNoteModel);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            /*
            imageModels = savedInstanceState.getParcelableArrayList("bitmap_files");
            for (ImageModel imageModel : imageModels) {
                ThumbsRecyclerViewAdapter thumbsRecyclerViewAdapter = (ThumbsRecyclerViewAdapter) thumbsRecyclerView.getAdapter();
                thumbsRecyclerViewAdapter.getDataSet().add(0, new ImageModel(imageModel.getFileName()));
                thumbsRecyclerViewAdapter.notifyItemInserted(thumbsRecyclerViewAdapter.getDataSet().size() - 1);
            }
            coordinates = savedInstanceState.getParcelable("coordinates");
            isLocationEnabled = savedInstanceState.getBoolean("isLocationEnabled");
            timeStamp = savedInstanceState.getLong("timeStamp");
            audioClipModels = new HashSet<>(Arrays.asList((AudioClipModel[]) savedInstanceState.getParcelableArray("audio_files")));
            hasAudioRecording = savedInstanceState.getBoolean("has_audio");
            coarseAddress = savedInstanceState.getParcelable("coarse_address");

            */

            simpleNoteModel = savedInstanceState.getParcelable("simpleNote");
            ThumbsRecyclerViewAdapter thumbsRecyclerViewAdapter = ((ThumbsRecyclerViewAdapter) thumbsRecyclerView.getAdapter());
            thumbsRecyclerViewAdapter.getDataSet().addAll(simpleNoteModel.getImageModels());
            thumbsRecyclerViewAdapter.notifyDataSetChanged();
            audioCaptureFileName = savedInstanceState.getString("curr_audio_recording_filename");
            //state variables restored. Now set them into the appropriate views.
            setViewState();
        }
    }

    @Override
    protected void setViewState() {
        for (AudioClipModel audioClipModel : simpleNoteModel.getAudioClipModels())
            if (new File(Utils.getStoragePath(getActivity()) + "/" + audioClipModel.getFileName()).exists())
                addAudioRecording(audioClipModel);
        locationToggleButton.setChecked(simpleNoteModel.getLocationEnabled());
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
        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        if (simpleNoteModel.getCoordinates() != null) {
            final Coordinates coordinates = simpleNoteModel.getCoordinates();
            final List<Address> addresses = new ArrayList<>();
            //run an async task for Geocoder.getFromLocation as the call is blocking.
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        addresses.addAll(geocoder.getFromLocation(coordinates.getX(), coordinates.getY(), 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    dateTimeLocationView.setText(simpleDateFormat.format(new Date(simpleNoteModel.getLastModifiedDate())) + " in " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
                }
            }.execute();
        } else {
            final Location location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (location != null) {
                final List<Address> addresses = new ArrayList<>();
                //run an async task for Geocoder.getFromLocation as the call is blocking.
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            addresses.addAll(geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        dateTimeLocationView.setText(simpleDateFormat.format(new Date(simpleNoteModel.getLastModifiedDate())) + " in " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
                        simpleNoteModel.setCoordinates(new Coordinates(location.getLatitude(), location.getLongitude()));
                        simpleNoteModel.setCoarseAddress(new CoarseAddress(addresses.get(0).getLocality(), addresses.get(0).getCountryName()));
                    }
                }.execute();
            } else {
                dateTimeLocationView.setText(simpleDateFormat.format(new Date(simpleNoteModel.getLastModifiedDate())) + " in <location error>");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.isSuccess()) {
            dateTimeLocationView.setText(simpleDateFormat.format(new Date(simpleNoteModel.getLastModifiedDate())));
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
        //only one clip must be recorded per user interaction with Dialog
        //so decide a single filename for the audio clip
        audioCaptureFileName = "Audio_" + System.currentTimeMillis() + ".3gp";
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
                File file = new File(Utils.getStoragePath(getActivity()) + "/" + audioCaptureFileName);
                System.out.println("File exists: " + file.exists());
                if (file.exists()) {
                    //This file name will be used after rotation to re populate the UI with an audio player widget
                    addAudioRecording(new AudioClipModel(audioCaptureFileName, null));
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, stop recording if recording is in progress
                if (isRecording)
                    stopRecording();
                //delete the recording file
                File file = new File(Utils.getStoragePath(getActivity()) + "/" + audioCaptureFileName);
                if (file.delete())
                    Toast.makeText(getActivity(), "Recorded File Deleted", Toast.LENGTH_SHORT).show();
                audioCaptureFileName = null;
            }
        });
        // Set other dialog properties
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        final ToggleButton recordAudioButton = (ToggleButton) v.findViewById(R.id.toggle_button_record_audio);
        recordAudioButton.setOnCheckedChangeListener(this);
        dialog.show();
    }

    /**
     * @param audioClipModel a data object representing the fileName and description of the Audio Player
     *                            Invocation Scenarios:
     *                            1. from inside onActivityCreated(), when restoring the audio player widgets after screen rotation
     *                            2. After the record audio dialog is dismissed by the user after finishhing the audio recording.
     */
    private void addAudioRecording(final AudioClipModel audioClipModel) {
        //set boolean audio flag to true
        simpleNoteModel.setHasAudioRecording(true);
        final BarAudioPlayer barAudioPlayer = new BarAudioPlayer(audioClipModel, getActivity());
        barAudioPlayer.setDeleteDialogClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        removeAudioRecording(barAudioPlayer);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked, simply dismiss the dialog
                        break;
                }
            }
        });
        simpleNoteModel.getAudioClipModels().add(audioClipModel);
        barAudioPlayer.setPlayCheckedChangeListener(this);
        ((LinearLayout) rootView.findViewById(R.id.new_note_main_content_layout)).addView(barAudioPlayer.getBarAudioPlayerUI(), 1);
        //save a reference to the bar audio player
        barAudioPlayers.put(barAudioPlayer.getAudioClipModel().getFileName(), barAudioPlayer);
    }

    private void removeAudioRecording(BarAudioPlayer barAudioPlayer) {
        String fileName = barAudioPlayer.getAudioClipModel().getFileName();
        File file = new File(Utils.getStoragePath(getActivity()) + "/" + fileName);
        //release the MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            currentlyPlaying = null;
        }
        //clean up the audio file associated with the MediaPlayer
        if (file.exists() && file.delete()) {
            //remove ui component of audio player
            ((LinearLayout) rootView.findViewById(R.id.new_note_main_content_layout)).removeView(barAudioPlayer.getBarAudioPlayerUI());
            //remove fileName
            simpleNoteModel.getAudioClipModels().remove(barAudioPlayer.getAudioClipModel());
            //remove from HashMap
            barAudioPlayers.remove(fileName);
            if (simpleNoteModel.getAudioClipModels().size() == 0)
                simpleNoteModel.setHasAudioRecording(false);
            //show success toast
            Toast.makeText(getActivity(), "Audio Clip Deleted", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "Unable to delete Audio Clip", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        try {
            if (mRecorder != null && isRecording)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final int THUMBSIZE = Utils.DEVICE_WIDTH / 4;
            //Showprogress overlay.
            //write images to internal storage
            //on complete, retrieve file namse and store them in list
            //generate thumbs from filenames
            //populate imageviews.
            //hide progress overlay

            // Show the Progress Bar
            imageHolderProgressBar.setVisibility(View.VISIBLE);
            if (requestCode == Utils.CAMERA_REQUEST) {
                //received image from camera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                //write photo to disk, and rerieve fileName
                new WriteFileTask(getActivity()) {
                    @Override
                    public void onResponseReceived(List<String> obj) {
                        if (obj.size() > 0) {
                            Snackbar.make(getActivity().getCurrentFocus(), "Captured Image saved!", Snackbar.LENGTH_SHORT).show();
                            //add the fileName to the imageModels List
                            simpleNoteModel.getImageModels().add(0, new ImageModel(obj.get(0)));
                            ThumbsRecyclerViewAdapter adapter = ((ThumbsRecyclerViewAdapter) thumbsRecyclerView.getAdapter());
                            adapter.getDataSet().add(0, mapFileNamesToModel(obj).get(0));
                            adapter.notifyItemInserted(0);
                            imageHolderProgressBar.setVisibility(View.GONE);
                        } else
                            Snackbar.make(getActivity().getCurrentFocus(), "Captured image not saved", Snackbar.LENGTH_SHORT).show();
                    }
                }.execute(photo);
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getData() != null) {
                //write photo to disk, and retrieve fileName
                new WriteUriToFileTask(getActivity()) {
                    @Override
                    protected void onCompleted(List<String> obj) {
                        if (obj.size() > 0) {
                            //add the fileName to the imageModels List
                            simpleNoteModel.getImageModels().add(0, new ImageModel(obj.get(0)));
                            ThumbsRecyclerViewAdapter adapter = ((ThumbsRecyclerViewAdapter) thumbsRecyclerView.getAdapter());
                            adapter.getDataSet().add(0, mapFileNamesToModel(obj).get(0));
                            adapter.notifyItemInserted(0);
                            imageHolderProgressBar.setVisibility(View.GONE);
                        } else
                            Snackbar.make(getActivity().getCurrentFocus(), "Captured image not saved", Snackbar.LENGTH_SHORT).show();
                    }
                }.execute(data.getData());
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getClipData() != null) {
                //multiple images from gallery
                Uri[] uris = new Uri[data.getClipData().getItemCount()];
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    uris[i] = data.getClipData().getItemAt(i).getUri();
                }
                new WriteUriToFileTask(getActivity()) {
                    @Override
                    protected void onCompleted(List<String> obj) {
                        simpleNoteModel.getImageModels().addAll(mapFileNamesToModel(obj));
                        ThumbsRecyclerViewAdapter adapter = ((ThumbsRecyclerViewAdapter) thumbsRecyclerView.getAdapter());
                        int position = adapter.getItemCount();
                        adapter.getDataSet().addAll(0, mapFileNamesToModel(obj));
                        adapter.notifyItemRangeInserted(0, obj.size());
                        imageHolderProgressBar.setVisibility(View.GONE);
                    }
                }.execute(uris);
            }
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(Utils.getStoragePath(getActivity()) + "/" + audioCaptureFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
        isRecording = true;
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.location_image_button:
                if (isChecked) {
                    simpleNoteModel.setLocationEnabled(true);
                    if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                        dateTimeLocationView.setText(simpleDateFormat.format(new Date(simpleNoteModel.getLastModifiedDate())) + " in <retrieving location...>");
                    } else
                        retrieveLocation();
                } else {
                    simpleNoteModel.setLocationEnabled(false);
                    simpleNoteModel.setCoordinates(null);
                    mGoogleApiClient.disconnect();
                    dateTimeLocationView.setText(simpleDateFormat.format(new Date(simpleNoteModel.getLastModifiedDate())));
                }
                break;
            case R.id.toggle_button_record_audio:
                ToggleButton recordAudioButton = (ToggleButton) buttonView;
                if (isChecked) {
                    //change status to started recording
                    recordAudioButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mic_green_24dp));
                    startRecording();
                    simpleNoteModel.getAudioClipModels().add(new AudioClipModel(audioCaptureFileName, null));
                } else {
                    Log.e(LOG_TAG, "Stopping Audio Recording");
                    //change status to stop recording
                    recordAudioButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mic_black_24dp));
                    //release resource
                    stopRecording();
                    Log.e(LOG_TAG, "Stopped Audio Recording");
                }
                break;
            case R.id.toggle_audio_media_state:
                ToggleButton playToggle = (ToggleButton) buttonView;
                //get the card player which was clicked
                CardView playerCard = (CardView)playToggle.getParent().getParent().getParent();
                String clickedPlayerFileName = playerCard.getTag().toString();
                //check if an audio player is playing or not, if not simply play and save a reference
                if (isChecked) {
                    if (mediaPlayer != null && clickedPlayerFileName.equals(currentlyPlaying) && CURR_MEDIA_STATE == MEDIA_STATE.PAUSED) {
                        mediaPlayer.start();
                        CURR_MEDIA_STATE = MEDIA_STATE.PLAYING;
                        currentlyPlaying = clickedPlayerFileName;
                    }
                    if (mediaPlayer != null && !clickedPlayerFileName.equals(currentlyPlaying)) {
                        //need to reset the timers and state of previous card player
                        if (currentlyPlaying != null) {
                            BarAudioPlayer prevBarAudioPlayer = barAudioPlayers.get(currentlyPlaying);
                            prevBarAudioPlayer.getMediaPlayProgress().setProgress(0);
                            prevBarAudioPlayer.getMediaStateToggle().setChecked(false);
                            prevBarAudioPlayer.resetTimer();
                        }
                        //reset and restart
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(Utils.getStoragePath(getActivity()) + "/" + playerCard.getTag().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        BarAudioPlayer barAudioPlayer = barAudioPlayers.get(clickedPlayerFileName);
                        System.out.println("Attaching listeners...");
                        mediaPlayer.setOnPreparedListener(barAudioPlayer.getOnPreparedListener());
                        mediaPlayer.setOnCompletionListener(barAudioPlayer.getOnCompletionListener());
                        mediaPlayer.setOnErrorListener(barAudioPlayer.getOnErrorListener());
                        mediaPlayer.prepareAsync();
                        CURR_MEDIA_STATE = MEDIA_STATE.PREPARING;
                        currentlyPlaying = clickedPlayerFileName;
                    }
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(Utils.getStoragePath(getActivity()) + "/" + playerCard.getTag().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        BarAudioPlayer barAudioPlayer = barAudioPlayers.get(clickedPlayerFileName);
                        System.out.println("Attaching listeners...");
                        mediaPlayer.setOnPreparedListener(barAudioPlayer.getOnPreparedListener());
                        mediaPlayer.setOnCompletionListener(barAudioPlayer.getOnCompletionListener());
                        mediaPlayer.setOnErrorListener(barAudioPlayer.getOnErrorListener());
                        mediaPlayer.prepareAsync();
                        CURR_MEDIA_STATE = MEDIA_STATE.PREPARING;
                        currentlyPlaying = clickedPlayerFileName;
                    }
                } else {
                    //simply pause the media player
                    if (mediaPlayer != null && CURR_MEDIA_STATE == MEDIA_STATE.PLAYING) {
                        System.out.println("Pausing Media Player...");
                        mediaPlayer.pause();
                        CURR_MEDIA_STATE = MEDIA_STATE.PAUSED;
                    } else {
                        //loose reference to the currently playing
                        currentlyPlaying = null;
                    }

                }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
        try {
            if (mediaPlayer != null) {
                System.out.println("OnPause Reset");
                mediaPlayer.reset();
                System.out.println("OnPause Reset Complete");
                //reset state variables
                currentlyPlaying = null;
                CURR_MEDIA_STATE = null;
            }
        } catch (Exception e) {
            System.out.println("OnPause Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public enum MEDIA_STATE {PREPARING, PLAYING, PAUSED, STOPPED}
}
