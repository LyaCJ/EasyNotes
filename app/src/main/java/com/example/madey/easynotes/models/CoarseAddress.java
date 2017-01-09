package com.example.madey.easynotes.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by 834619 on 12/27/2016.
 */

public class CoarseAddress implements Parcelable {
    public static final Creator<CoarseAddress> CREATOR = new Creator<CoarseAddress>() {
        @Override
        public CoarseAddress createFromParcel(Parcel in) {
            return new CoarseAddress(in);
        }

        @Override
        public CoarseAddress[] newArray(int size) {
            return new CoarseAddress[size];
        }
    };
    //The City
    private String city;
    //The Country
    private String country;

    protected CoarseAddress(Parcel in) {
        city = in.readString();
        country = in.readString();
    }

    public CoarseAddress(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public CoarseAddress(String s) {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
        dest.writeString(city);
        dest.writeString(country);
    }

    public String toAddressString() {
        return city + ", " + country;
    }
}
