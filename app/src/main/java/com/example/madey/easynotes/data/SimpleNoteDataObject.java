package com.example.madey.easynotes.data;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by madey on 8/6/2016.
 */
public class SimpleNoteDataObject implements Parcelable {
    private String title;
    private String content;
    private Date creationDate;
    private Date lastModifiedDate;
    private transient ArrayList<Bitmap> imageList = new ArrayList<>();
    private transient File noteFile;
    private transient boolean dataLoaded = false;
    private transient boolean imageLoaded = false;
    private transient ArrayList<Bitmap> imageThumbs = new ArrayList<>();
    private ArrayList<String> imagePath;
    public SimpleNoteDataObject(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public SimpleNoteDataObject() {
    }

    public File getNoteFile() {
        return noteFile;
    }

    public void setNoteFile(File noteFile) {
        this.noteFile = noteFile;
    }

    public boolean isImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(boolean imageLoaded) {
        this.imageLoaded = imageLoaded;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void setDataLoaded(boolean dataLoaded) {
        this.dataLoaded = dataLoaded;
    }

    public ArrayList<Bitmap> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<Bitmap> imageList) {
        this.imageList = imageList;
    }

    public ArrayList<Bitmap> getImageThumbs() {
        return imageThumbs;
    }

    public void setImageThumbs(ArrayList<Bitmap> imageThumbs) {
        this.imageThumbs = imageThumbs;
    }

    public ArrayList<String> getImagePath() {
        return imagePath;
    }

    public void setImagePath(ArrayList<String> imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }


    public void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();

        //run an asynchtask to initialize trasnient

    }

    // Converts the Bitmap into a byte array for serialization
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    public void removeFromDisk() {
        if (noteFile != null)
            noteFile.delete();
        for (String f : SimpleNoteDataObject.this.getImagePath()) {
            if (f != null)
                new File(noteFile.getParentFile().getAbsolutePath() + "//" + f).delete();
        }

    }

    public void createThumbs(Point dim) {
        /*if (imageList.size() == 1) {
            this.createThumb(dim.x / 3, dim.x / 3);
        }
        if (imageList.size() == 2) {
            this.createThumb(dim.x / 6, dim.x / 3);
        }
        if (imageList.size() == 3) {
            this.createThumb(dim.x / 12, dim.x / 3);
        } else {
            this.createThumb(dim.x / 6, dim.x / 6);
        }*/
        createThumb(dim.x / 3, dim.x / 3);
    }

    private void createThumb(int width, int height) {
        if (getImageList().size() > 0 && getImageThumbs().size() == 0) {
            for (Bitmap bmp : getImageList()) {
                Bitmap thumb = ThumbnailUtils.extractThumbnail(bmp, width, height);
                getImageThumbs().add(thumb);
            }
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}