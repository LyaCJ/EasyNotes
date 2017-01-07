package com.example.madey.easynotes.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

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
    @Expose
    private String fileName;
    private Bitmap thumbBitmap;
    private Bitmap originalBitmap;
    @Expose
    private String caption;

    protected ImageModel(Parcel in) {
        fileName = in.readString();
        //thumbBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        //originalBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        caption = in.readString();
    }

    public ImageModel() {

    }

    public ImageModel(String fileName) {
        this.fileName = fileName;
    }

    public ImageModel(String fileName, Bitmap thumbBitmap) {
        this.fileName = fileName;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        //dest.writeParcelable(thumbBitmap, flags);
        //dest.writeParcelable(originalBitmap, flags);
        dest.writeString(caption);
    }
}
