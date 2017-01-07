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
    private List<ImageModel> imageModels;

    public SimpleListDataObject(){
        activeItems=new ArrayList<>();
        doneItems=new ArrayList<>();
        imageModels = new ArrayList<>();
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
        imageModels = new ArrayList<>();
        in.readList(imageModels, null);

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

    public List<ImageModel> getImageModels() {
        return imageModels;
    }

    public void setImageModels(List<ImageModel> imageModels) {
        this.imageModels = imageModels;
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
        dest.writeList(imageModels);

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
        StringBuilder title = new StringBuilder();

        public ListTitleDataObject(StringBuilder title) {
            this.title=title;
        }

        private ListTitleDataObject(Parcel in) {
            title = new StringBuilder(in.readString());
        }

        public StringBuilder getTitle() {
            return title;
        }

        public void setTitle(StringBuilder title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title.toString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title.toString());
        }
    }

}
