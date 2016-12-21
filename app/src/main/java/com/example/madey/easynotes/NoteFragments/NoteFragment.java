package com.example.madey.easynotes.NoteFragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.madey.easynotes.AsyncTasks.CreateThumbsTask;
import com.example.madey.easynotes.AsyncTasks.WriteFileTask;
import com.example.madey.easynotes.AsyncTasks.WriteUriToFileTask;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class NoteFragment extends android.app.Fragment {

    protected LinearLayout imageHolderLayout;
    protected ProgressBar imageHolderProgressBar;


    protected ArrayList<String> fileNames = new ArrayList<>();
    protected ArrayList<Bitmap> thumbs = new ArrayList<>();


    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                            //add the fileName to the fileNames List
                            fileNames.addAll(obj);
                            //queue a create thumb task with the retrieved fileName
                            String[] fileNames = new String[obj.size()];
                            new CreateThumbsTask(getActivity(), new Point(THUMBSIZE, THUMBSIZE)) {
                                @Override
                                public void onCompleted(ArrayList<Bitmap> bmp) {
                                    for (Bitmap b : bmp) {
                                        //set the bitmap generated into the holder layout
                                        imageHolderLayout.addView(createImageView(b));
                                    }
                                }
                            }.execute(obj.toArray(fileNames));
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
                            Snackbar.make(getActivity().getCurrentFocus(), "Images saved!", Snackbar.LENGTH_SHORT).show();
                            //add the fileName to the fileNames List
                            fileNames.addAll(obj);
                            //queue a create thumb task with the retrieved fileName
                            String[] fileNames = new String[obj.size()];
                            new CreateThumbsTask(getActivity(), new Point(THUMBSIZE, THUMBSIZE)) {
                                @Override
                                public void onCompleted(ArrayList<Bitmap> bmp) {
                                    for (Bitmap b : bmp) {
                                        //set the bitmap generated into the holder layout
                                        imageHolderLayout.addView(createImageView(b));
                                    }
                                }
                            }.execute(obj.toArray(fileNames));
                        } else
                            Snackbar.make(getActivity().getCurrentFocus(), "Captured image not saved", Snackbar.LENGTH_SHORT).show();
                    }
                }.execute(data.getData());
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getClipData() != null) {
                //multiple images from gallery
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    ClipData.Item item = data.getClipData().getItemAt(i);
                    //generate thumb asynchronously
                    new WriteUriToFileTask(getActivity()) {
                        @Override
                        protected void onCompleted(List<String> obj) {
                            if (obj.size() > 0) {
                                Snackbar.make(getActivity().getCurrentFocus(), "Images saved!", Snackbar.LENGTH_SHORT).show();
                                //add the fileName to the fileNames List
                                fileNames.addAll(obj);
                                //queue a create thumb task with the retrieved fileName
                                String[] fileNames = new String[obj.size()];
                                new CreateThumbsTask(getActivity(), new Point(THUMBSIZE, THUMBSIZE)) {
                                    @Override
                                    public void onCompleted(ArrayList<Bitmap> bmp) {
                                        for (Bitmap b : bmp) {
                                            //set the bitmap generated into the holder layout
                                            imageHolderLayout.addView(createImageView(b));
                                        }
                                    }
                                }.execute(obj.toArray(fileNames));
                            } else
                                Snackbar.make(getActivity().getCurrentFocus(), "Captured image not saved", Snackbar.LENGTH_SHORT).show();
                        }
                    }.execute(item.getUri());
                }
            }
        }
    }
    protected abstract void saveNote();

}
