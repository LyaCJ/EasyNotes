package com.example.madey.easynotes.NoteFragments;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class NoteFragment extends android.app.Fragment {


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

    protected abstract void saveNote();

}
