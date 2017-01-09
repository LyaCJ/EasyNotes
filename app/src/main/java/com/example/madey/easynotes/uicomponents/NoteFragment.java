package com.example.madey.easynotes.uicomponents;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.ImageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class NoteFragment extends android.app.Fragment {

    protected LinearLayout imageHolderLayout;
    protected ProgressBar imageHolderProgressBar;
    protected RecyclerView thumbsRecyclerView;

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

    protected List<ImageModel> mapFileNamesToModel(List<String> fileNames) {
        List<ImageModel> imageModels = new ArrayList<>();
        for (String fileName : fileNames) {
            imageModels.add(new ImageModel(fileName));
        }
        return imageModels;
    }

}
