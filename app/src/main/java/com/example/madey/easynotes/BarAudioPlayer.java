package com.example.madey.easynotes;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.madey.easynotes.models.AudioClipDataObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Madeyedexter on 25-12-2016.
 */

/*
 * When I started coding this class, it was supposed to be a wrapper around MediaPlayer and CardView for the UI part.
 * But I realised that multiple instances of this class will have multiple copies of MediaPlayer object which is not
 * desirable. Not desirable because it would be a lot of code handling each MediaPlayer instance, stopping one instance and playing another
 * if the user decides to have multiple audio recordings in their note.
 * Hence, I decided to keep a single MediaPlayer object in the NewNoteFragment and use that MediaPlayer object, every time user
 * wants to play their recorded audio clip.
 *
 *
 * Note to self: Now using a private static media player instance to share across all BarAudioPlayer instances.
 *
 */
public class BarAudioPlayer implements CompoundButton.OnCheckedChangeListener {

    public static long id = System.currentTimeMillis();

    //A static shared media player to play the audio for instances of these player
    public static MediaPlayer mediaPlayer;
    //A static timer to share across MediaPlayer UI instances to update Media progress.
    private static Timer timer;
    private AudioClipDataObject audioClipDataObject;
    private Context context;
    //maintain a list of all the AudioPlayers created till now
    private CardView barAudioPlayerUI;
    //reference to toggle button
    private ToggleButton mediaStateToggle = null;
    //A progress bar that displays media play progress
    private ProgressBar mediaPlayProgress;
    //a listener for delete confirm dialog confirm/reject for this audio player
    private DialogInterface.OnClickListener deleteDialogClickListener;
    //a listener for play/pause UI interaction with audio player
    private CompoundButton.OnCheckedChangeListener playCheckedChangeListener;
    //Media Player state listeners
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mp) {
            mediaPlayer.start();
            final ProgressBar progressBar = ((ProgressBar) getBarAudioPlayerUI().findViewById(R.id.progress_bar_media_progress));
            progressBar.setMax(mediaPlayer.getDuration());
            //start a timer to update the progress bar, after 200 milliseconds
            timer = new java.util.Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    progressBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }, 10, 200);
        }
    };
    //Media Player error event listener
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(context, "Error in MediaPlayer: " + what + "| Extra: " + extra, Toast.LENGTH_SHORT).show();
            return false;
        }
    };
    //media player playback completion listener
    private MediaPlayer.OnCompletionListener onCompletionListener =
            //media player on complete listener, invoked when media player completes playback
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    System.out.println("Going to release MediaPlayer: " + mediaPlayer);
                    mediaPlayer.reset();
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    ((ProgressBar) getBarAudioPlayerUI().findViewById(R.id.progress_bar_media_progress)).setProgress(0);
                    //toggle the button state to not playing
                    ((ToggleButton) getBarAudioPlayerUI().findViewById(R.id.toggle_audio_media_state)).setChecked(false);
                }
            };

    public BarAudioPlayer(AudioClipDataObject audioClipDataObject, Context ctx) {
        super();
        //reference to context
        this.context = ctx;
        //reference to media file
        this.audioClipDataObject = audioClipDataObject;
        //save a reference to CardView
        this.barAudioPlayerUI = (CardView) inflateCircularAudioPlayerUI(ctx);
        //bind events to card view
        if (barAudioPlayerUI != null) {
            //initialize the media player components if inflation was successful
            mediaStateToggle = (ToggleButton) barAudioPlayerUI.findViewById(R.id.toggle_audio_media_state);
            mediaPlayProgress = (ProgressBar) barAudioPlayerUI.findViewById(R.id.progress_bar_media_progress);
            ImageView mediaDelete = (ImageView) barAudioPlayerUI.findViewById(R.id.image_view_media_delete);
            mediaDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Delete this audio clip?").setPositiveButton("Yes", deleteDialogClickListener)
                            .setNegativeButton("No", deleteDialogClickListener).show();
                }
            });
            EditText descriptionEditText = ((EditText) barAudioPlayerUI.findViewById(R.id.edit_text_audio_description));

            //found a genius way to attach data to views
            barAudioPlayerUI.setTag(audioClipDataObject.getFileName());
        }
    }

    public MediaPlayer.OnPreparedListener getOnPreparedListener() {
        return onPreparedListener;
    }

    public MediaPlayer.OnErrorListener getOnErrorListener() {
        return onErrorListener;
    }

    public MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return onCompletionListener;
    }

    public AudioClipDataObject getAudioClipDataObject() {
        return audioClipDataObject;
    }

    public CardView getBarAudioPlayerUI() {
        return barAudioPlayerUI;
    }

    public void setDeleteDialogClickListener(DialogInterface.OnClickListener deleteDialogClickListener) {
        this.deleteDialogClickListener = deleteDialogClickListener;
    }

    public void setPlayCheckedChangeListener(CompoundButton.OnCheckedChangeListener playCheckedChangeListener) {
        this.playCheckedChangeListener = playCheckedChangeListener;
        mediaStateToggle.setOnCheckedChangeListener(playCheckedChangeListener);
    }

    private View inflateCircularAudioPlayerUI(Context ctx) {
        CardView view = (CardView) LayoutInflater.from(ctx).inflate(R.layout.card_view_audio_player, null);
        return view;
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.toggle_audio_media_state:
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
        }
    }
}
