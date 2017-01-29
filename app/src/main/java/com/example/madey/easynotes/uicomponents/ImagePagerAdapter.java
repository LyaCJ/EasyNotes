package com.example.madey.easynotes.uicomponents;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.madey.easynotes.models.ImageModel;

import java.util.List;


/**
 * Created by Madeyedexter on 21-01-2017.
 */

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    // This holds all the currently displayable views, in order from left to right.
    private List<ImageModel> imageModels;

    public ImagePagerAdapter(FragmentManager fm, List<ImageModel> imageModels) {
        super(fm);
        this.imageModels = imageModels;
    }

    //-----------------------------------------------------------------------------
    // Used by ViewPager.  "Object" represents the page; tell the ViewPager where the
    // page should be displayed, from left-to-right.  If the page no longer exists,
    // return POSITION_NONE.
    @Override
    public int getItemPosition(Object object) {
        int index = imageModels.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }


    //-----------------------------------------------------------------------------
    // Used by ViewPager; can be used by app as well.
    // Returns the total number of pages that the ViewPage can display.  This must
    // never be 0.
    @Override
    public int getCount() {
        return imageModels.size();
    }

    //-----------------------------------------------------------------------------
    // Add "view" at "position" to "views".
    // Returns position of new view.
    // The app should call this to add pages; not used by ViewPager.
    public int addImageModel(ImageModel imageModel, int position) {
        imageModels.add(position, imageModel);
        return position;
    }


    //-----------------------------------------------------------------------------
    // Removes the "view" at "position" from "views".
    // Retuns position of removed view.
    // The app should call this to remove pages; not used by ViewPager.
    public void removeImageModel(int position) {
        imageModels.remove(position);
        this.notifyDataSetChanged();
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return ImageDetailFragment.newInstance(position);
    }
}
