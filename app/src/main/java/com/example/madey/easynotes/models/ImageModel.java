package com.example.madey.easynotes.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by Madeyedexter on 23-12-2016.
 */

public class ImageModel implements Parcelable {

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    private String imageFileName;

    private transient Bitmap thumbBitmap;
    private transient Bitmap originalBitmap;

    private String caption;

    protected ImageModel(Parcel in) {
        imageFileName = in.readString();
        //thumbBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        //originalBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        caption = in.readString();
    }

    public ImageModel() {

    }

    public ImageModel(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public ImageModel(String imageFileName, Bitmap thumbBitmap) {
        this.imageFileName = imageFileName;
        this.thumbBitmap = thumbBitmap;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public void setOriginalBitmap(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageFileName);
        //dest.writeParcelable(thumbBitmap, flags);
        //dest.writeParcelable(originalBitmap, flags);
        dest.writeString(caption);
    }
}
