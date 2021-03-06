package com.example.madey.easynotes;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.example.madey.easynotes.models.AudioClipModel;
import com.example.madey.easynotes.uicomponents.NewNoteFragment;

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
public class BarAudioPlayer implements TextWatcher {

    public static final String LOG_TAG = "BarAudioPlayer.java";
    public static long id = System.currentTimeMillis();
    //A timer
    private Timer timer;
    //A Timer Task
    private TimerTask timerTask;
    private AudioClipModel audioClipModel;
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
            mp.start();
            NewNoteFragment.CURR_MEDIA_STATE = NewNoteFragment.MEDIA_STATE.PLAYING;
            final ProgressBar progressBar = ((ProgressBar) getBarAudioPlayerUI().findViewById(R.id.progress_bar_media_progress));
            progressBar.setMax(mp.getDuration());
            //start a timer to update the progress bar, after 200 milliseconds
            timer = new java.util.Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    progressBar.setProgress(mp.getCurrentPosition());
                }
            };
            timer.scheduleAtFixedRate(timerTask, 10, 200);
            System.out.println("Media Player prepared and started playback.");

        }
    };
    //Media Player error event listener
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            ((ProgressBar) getBarAudioPlayerUI().findViewById(R.id.progress_bar_media_progress)).setProgress(0);
            //Toast.makeText(context, "Error in MediaPlayer: " + what + "| Extra: " + extra, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error in MediaPlayer: " + what + "| Extra: " + extra);
            return false;
        }
    };
    //media player playback completion listener
    private MediaPlayer.OnCompletionListener onCompletionListener =
            //media player on complete listener, invoked when media player completes playback
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    System.out.println("Going to reset MediaPlayer: " + mp);
                    mp.reset();
                    resetTimer();
                    ((ProgressBar) getBarAudioPlayerUI().findViewById(R.id.progress_bar_media_progress)).setProgress(0);
                    //Change State of media player before invoking setChecked
                    NewNoteFragment.CURR_MEDIA_STATE = NewNoteFragment.MEDIA_STATE.STOPPED;
                    //toggle the button state to not playing
                    ((ToggleButton) getBarAudioPlayerUI().findViewById(R.id.toggle_audio_media_state)).setChecked(false);

                }
            };

    public BarAudioPlayer(AudioClipModel audioClipModel, Context ctx) {
        super();
        //reference to context
        this.context = ctx;
        //reference to media file
        this.audioClipModel = audioClipModel;
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
            descriptionEditText.addTextChangedListener(this);
            descriptionEditText.setText(this.audioClipModel.getAudioDescription());
            //found a genius way to attach data to views
            barAudioPlayerUI.setTag(audioClipModel.getAudioFileName());
        }
    }

    public ToggleButton getMediaStateToggle() {
        return mediaStateToggle;
    }

    public ProgressBar getMediaPlayProgress() {
        return mediaPlayProgress;
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

    public AudioClipModel getAudioClipModel() {
        return audioClipModel;
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

    public void resetTimer() {
        if (timerTask != null)
            timerTask.cancel();
        if (timer != null)
            timer.cancel();
        timerTask = null;
        timer = null;
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        this.audioClipModel.setAudioDescription(s.toString());
    }
}
