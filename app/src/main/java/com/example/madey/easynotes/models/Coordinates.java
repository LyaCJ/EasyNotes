package com.example.madey.easynotes.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Madeyedexter on 25-12-2016.
 */

public class Coordinates implements Parcelable {

    public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        @Override
        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };
    private Double x;
    private Double y;

    public Coordinates(String s) {
        x = Double.parseDouble(s.split(",")[0]);
        y = Double.parseDouble(s.split(",")[1]);
    }

    public Coordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {
        this.x = 0d;
        this.y = 0d;


    }

    protected Coordinates(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
