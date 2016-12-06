package com.example.madey.easynotes.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
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
    private String title;
    private List<String> activeItems;
    private List<String> doneItems;
    private Date lastModifiedDate;
    private Date creationDate;

    public SimpleListDataObject(String title, List<String> active, List<String> done) {
        this.title = title;
        this.activeItems = active;
        this.doneItems = done;
    }

    private SimpleListDataObject(Parcel in) {
        title = in.readString();
        activeItems = new ArrayList<>();
        doneItems = new ArrayList<>();
        in.readList(activeItems, null);
        in.readList(doneItems, null);
        lastModifiedDate = (Date) in.readSerializable();
        creationDate = (Date) in.readSerializable();

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeList(activeItems);
        dest.writeList(doneItems);
        dest.writeSerializable(lastModifiedDate);
        dest.writeSerializable(creationDate);

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

        public ListTitleDataObject() {

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
