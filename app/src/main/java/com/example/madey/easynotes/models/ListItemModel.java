package com.example.madey.easynotes.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by Madeyedexter on 07-01-2017.
 */

public class ListItemModel implements Parcelable {

    public static final Creator<ListItemModel> CREATOR = new Creator<ListItemModel>() {
        @Override
        public ListItemModel createFromParcel(Parcel in) {
            return new ListItemModel(in);
        }

        @Override
        public ListItemModel[] newArray(int size) {
            return new ListItemModel[size];
        }
    };
    private Boolean isChecked = false;
    private String itemText;

    public ListItemModel(String itemText) {
        this.itemText = itemText;
    }

    protected ListItemModel(Parcel in) {
        itemText = in.readString();
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
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
        dest.writeString(itemText);
    }
}
