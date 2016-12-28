package com.example.madey.easynotes;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.madey.easynotes.models.AudioClipDataObject;

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
 */
public class BarAudioPlayer {

    //a static to have a count of ids for audio description
    private static int EDIT_TEXT_DESCRIPTION_ID_COUNTER = 1;

    private AudioClipDataObject audioClipDataObject;
    private Context context;
    //the UI component of Audio Player
    private CardView barAudioPlayerUI;
    //reference to toggle button
    private ToggleButton mediaStateToggle = null;
    //a listener for delete confirm dialog confirm/reject for this audio player
    private DialogInterface.OnClickListener deleteDialogClickListener;
    //a listener for play/pause UI interaction with audio player
    private CompoundButton.OnCheckedChangeListener playCheckedChangeListener;

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
            ImageView mediaPlayProgress = (ImageView) barAudioPlayerUI.findViewById(R.id.image_view_media_progress);
            mediaStateToggle.setOnCheckedChangeListener(playCheckedChangeListener);
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
        }

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


}
