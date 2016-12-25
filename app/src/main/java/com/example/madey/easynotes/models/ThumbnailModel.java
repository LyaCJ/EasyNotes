package com.example.madey.easynotes.models;

import android.graphics.Bitmap;

/**
 * Created by Madeyedexter on 23-12-2016.
 */

public class ThumbnailModel {

    private String fileName;
    private Bitmap bitmap;

    public ThumbnailModel() {

    }

    public ThumbnailModel(String fileName) {
        this.fileName = fileName;
    }

    public ThumbnailModel(String fileName, Bitmap bitmap) {
        this.fileName = fileName;
        this.bitmap = bitmap;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
