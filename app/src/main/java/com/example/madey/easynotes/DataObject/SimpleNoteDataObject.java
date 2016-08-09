package com.example.madey.easynotes.DataObject;

import android.graphics.Bitmap;
import android.text.Editable;

import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by madey on 8/6/2016.
 */
public class SimpleNoteDataObject implements Serializable {
    private String title;

    public SimpleNoteDataObject(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public SimpleNoteDataObject(){}

    private String content;
    private Date creationDate;
    private Date lastModifiedDate;
    private transient ArrayList<Bitmap> imageList;

    private transient boolean dataLoaded=false;

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

    private transient boolean imageLoaded=false;

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

    private transient ArrayList<Bitmap> imageThumbs;

    private ArrayList<String> imagePath;



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

    public ArrayList<Bitmap> getImageURI() {
        return imageList;
    }

    public void setImageURI(ArrayList<Bitmap> imageURI) {
        this.imageList = imageURI;
    }


    public void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException{
        is.defaultReadObject();

        //run an asynchtask to initialize trasnient

    }

    // Converts the Bitmap into a byte array for serialization
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

}
