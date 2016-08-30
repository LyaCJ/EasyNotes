package com.example.madey.easynotes.DataObject;

import java.util.Date;
import java.util.List;

/**
 * Created by madey on 8/6/2016.
 */
public class SimpleListDataObject {
    private String title;
    private List<String> activeItems;
    private List<String> doneItems;


    private Date lastModifiedDate;
    private Date creationDate;

    public SimpleListDataObject(String title, List<String> active, List<String> done) {
        this.title=title;
        this.activeItems = active;
        this.doneItems = done;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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




}
