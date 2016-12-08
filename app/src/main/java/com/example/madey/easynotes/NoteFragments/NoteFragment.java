package com.example.madey.easynotes.NoteFragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.madey.easynotes.AsyncTasks.CreateThumbsTask;
import com.example.madey.easynotes.AsyncTasks.WriteFileTask;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class NoteFragment extends android.app.Fragment {

    protected LinearLayout imageHolderLayout;

    protected ArrayList<Uri> fileUris = new ArrayList<>();
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
            int THUMBSIZE = Utils.DEVICE_WIDTH / 4;
            //images from camera should be written to external/internal storage, and a Uri should be retrieved.
            //As soon as an image is captured, write it to disk asynchronously. Generate the thumb asap.
            //image uri won't be available until the bitmap is written to the internal storage. For that, temporarily store
            //the thumbnail in a list and show it post rotation. When the Uri is available, we will remove the thumb from
            //the list and generate thumbnails for subsequent rotations using the Uri available in the fileUris list.
            //PS1: Need to make Thumbnail generation asynchronous? Maybe??.

            if (requestCode == Utils.CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                final Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                imageHolderLayout.addView(createImageView(thumb));
                //write photo to disk, and rerieve URI
                new WriteFileTask(getActivity()) {
                    @Override
                    public void onResponseReceived(Object obj) {
                        if (((List<Uri>) obj).size() > 0) {
                            Snackbar.make(getActivity().getCurrentFocus(), "Captured Image saved!", Snackbar.LENGTH_SHORT).show();
                            //add the Uri to the existing Uri list
                            fileUris.addAll(((List<Uri>) obj));
                            //remove the thumbnail from the bitmaps list
                            thumbs.remove(thumb);
                        } else
                            Snackbar.make(getActivity().getCurrentFocus(), "Captured image not saved", Snackbar.LENGTH_SHORT).show();
                    }
                }.execute(photo);
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getData() != null) {
                //generate thumb asynchronously
                new CreateThumbsTask(getActivity(), new Point(THUMBSIZE, THUMBSIZE)) {
                    @Override
                    public void onCompleted(ArrayList<Bitmap> bitmaps) {
                        System.out.println("Holder Layoput is: " + imageHolderLayout);
                        for (Bitmap bmp : bitmaps)
                            imageHolderLayout.addView(createImageView(bmp));
                    }
                }.execute(data.getData());
                //save the Uri from data.getData into list of Uris
                fileUris.add(data.getData());
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getClipData() != null) {
                System.out.println("Clip Data:" + data.getClipData());
                //multiple images from gallery
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    ClipData.Item item = data.getClipData().getItemAt(i);
                    //generate thumb asynchronously
                    new CreateThumbsTask(getActivity(), new Point(THUMBSIZE, THUMBSIZE)) {
                        @Override
                        public void onCompleted(ArrayList<Bitmap> bitmaps) {
                            for (Bitmap bmp : bitmaps)
                                imageHolderLayout.addView(createImageView(bmp));
                        }
                    }.execute(item.getUri());
                    //save the Uri from data.getData into list of Uris
                    fileUris.add(item.getUri());
                }
            }
        }
    }
    protected abstract void saveNote();

}
