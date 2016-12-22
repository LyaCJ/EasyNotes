package com.example.madey.easynotes.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madey on 8/6/2016.
 */
public class SimpleListDataObject implements Parcelable {
    public static final Parcelable.Creator<SimpleListDataObject> CREATOR
            = new Parcelable.Creator<SimpleListDataObject>() {
        public SimpleListDataObject createFromParcel(Parcel in) {
            return new SimpleListDataObject(in);
        }

        public SimpleListDataObject[] newArray(int size) {
            return new SimpleListDataObject[size];
        }
    };
    private SimpleListDataObject.ListTitleDataObject title;
    private List<String> activeItems;
    private List<String> doneItems;
    private long lastModifiedDate;
    private long creationDate;
    private List<String> fileNames;

    public SimpleListDataObject(){
        activeItems=new ArrayList<>();
        doneItems=new ArrayList<>();
        fileNames=new ArrayList<>();
    }

    public SimpleListDataObject(SimpleListDataObject.ListTitleDataObject title, List<String> active, List<String> done) {
        this.title = title;
        this.activeItems = active;
        this.doneItems = done;
    }

    private SimpleListDataObject(Parcel in) {
        title = in.readParcelable(null);
        activeItems = new ArrayList<>();
        doneItems = new ArrayList<>();
        in.readList(activeItems, null);
        in.readList(doneItems, null);
        lastModifiedDate = in.readLong();
        creationDate = in.readLong();
        fileNames = new ArrayList<>();
        in.readList(fileNames, null);

    }

    public List<String> getActiveItems() {
        return activeItems;
    }

    public void setActiveItems(List<String> activeItems) {
        this.activeItems = activeItems;
    }

    public List<String> getDoneItems() {
        return doneItems;
    }

    public void setDoneItems(List<String> doneItems) {
        this.doneItems = doneItems;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public SimpleListDataObject.ListTitleDataObject getTitle() {
        return title;
    }

    public void setTitle(SimpleListDataObject.ListTitleDataObject title) {
        this.title = title;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(title,flags);
        dest.writeList(activeItems);
        dest.writeList(doneItems);
        dest.writeLong(lastModifiedDate);
        dest.writeLong(creationDate);
        dest.writeList(fileNames);

    }


    public static class ListTitleDataObject implements Parcelable {
        public static final Parcelable.Creator<ListTitleDataObject> CREATOR
                = new Parcelable.Creator<ListTitleDataObject>() {
            public ListTitleDataObject createFromParcel(Parcel in) {
                return new ListTitleDataObject(in);
            }

            public ListTitleDataObject[] newArray(int size) {
                return new ListTitleDataObject[size];
            }
        };
        String title = new String();

        public ListTitleDataObject(String title) {
            this.title=title;
        }

        private ListTitleDataObject(Parcel in) {
            title = in.readString();
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
        }
    }

}
