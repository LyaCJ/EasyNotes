package com.example.madey.easynotes.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Madeyedexter on 28-12-2016.
 */

public class AudioClipDataObject implements Parcelable {
    public static final Creator<AudioClipDataObject> CREATOR = new Creator<AudioClipDataObject>() {
        @Override
        public AudioClipDataObject createFromParcel(Parcel in) {
            return new AudioClipDataObject(in);
        }

        @Override
        public AudioClipDataObject[] newArray(int size) {
            return new AudioClipDataObject[size];
        }
    };
    //audio file name of the file
    private String fileName;
    //the user description of the file
    private String audioDescription;

    //the public constructor
    public AudioClipDataObject(@NonNull String fileName, String audioDescription) {
        this.fileName = fileName;
        this.audioDescription = audioDescription;
    }

    protected AudioClipDataObject(Parcel in) {
        fileName = in.readString();
        audioDescription = in.readString();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    public String getAudioDescription() {
        return audioDescription;
    }

    public void setAudioDescription(String audioDescription) {
        this.audioDescription = audioDescription;
    }

    @Override
    public String toString() {
        return fileName + "," + audioDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(audioDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            //reflexive, symmetric and transitive
            return true;
        return obj instanceof AudioClipDataObject && obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return audioDescription == null ? fileName.hashCode() : fileName.hashCode() + audioDescription.hashCode();
    }
}
