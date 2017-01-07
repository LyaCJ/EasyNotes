package com.example.madey.easynotes.uicomponents;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.madey.easynotes.AsyncTasks.WriteFileTask;
import com.example.madey.easynotes.AsyncTasks.WriteUriToFileTask;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.AudioClipModel;
import com.example.madey.easynotes.models.ImageModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class NoteFragment extends android.app.Fragment {

    protected LinearLayout imageHolderLayout;
    protected ProgressBar imageHolderProgressBar;
    protected RecyclerView thumbsRecyclerView;

    protected ArrayList<ImageModel> imageModels = new ArrayList<>();
    protected Set<AudioClipModel> audioClipModels = new HashSet<>();
    protected ArrayList<Bitmap> thumbs = new ArrayList<>();


    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.verifyLocationPermission(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * creates a square ImageView with dimensions equal to device width/4
     *
     * @param bmp Bitmap to set for tis image view
     * @return ImageView with the Bitmap set to it.
     */
    protected ImageView createImageView(Bitmap bmp) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setAdjustViewBounds(false);
        int width = Utils.dimension.x < Utils.dimension.y ? Utils.dimension.x : Utils.dimension.y;
        imageView.setMaxWidth(width / 4);
        imageView.setMaxHeight(width / 4);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(getResources().getColor(R.color.accent_dark));
        imageView.setImageBitmap(bmp);
        return imageView;
    }

    /**
     * creates a square ImageView with dimensions equal to device width/4
     *
     * @param uri Uri to set for tis image view
     * @return ImageView with the Uri set to it.
     */
    protected ImageView createImageView(Uri uri) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setAdjustViewBounds(false);
        int width = Utils.DEVICE_WIDTH;
        imageView.setMaxWidth(width / 4);
        imageView.setMaxHeight(width / 4);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(getResources().getColor(R.color.accent_dark));
        imageView.setImageURI(uri);
        return imageView;
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
                            imageModels.add(0, new ImageModel(obj.get(0)));
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
                            imageModels.add(0, new ImageModel(obj.get(0)));
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
                        imageModels.addAll(mapFileNamesToModel(obj));
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

    /**
     * This method must be implemented by the sub-classes to appropriately save the note one the user is
     * finished making changes to the note.
     */
    protected abstract void saveNote();

    /**
     * This method must be implemented by the sub classes to restore the state variables after rotation
     * to the appropriate views.
     */
    protected abstract void setViewState();

    private List<ImageModel> mapFileNamesToModel(List<String> fileNames) {
        List<ImageModel> imageModels = new ArrayList<>();
        for (String fileName : fileNames) {
            imageModels.add(new ImageModel(fileName));
        }
        return imageModels;
    }

}
