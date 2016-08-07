package com.example.madey.easynotes.DataObject;

import java.util.Date;

/**
 * Created by madey on 8/6/2016.
 */
public class SimpleNoteDataObject {
    private String title;
    private String content;
    private Date creationDate;
    private Date lastModifiedDate;
    private String imageURI;

    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SimpleNoteDataObject (String text1, String text2){
        title = text1;
        content = text2;
    }


}
