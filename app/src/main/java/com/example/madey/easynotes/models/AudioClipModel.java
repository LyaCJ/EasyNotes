package com.example.madey.easynotes.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

/**
 * Created by Madeyedexter on 28-12-2016.
 */

public class AudioClipModel implements Parcelable {
    public static final Creator<AudioClipModel> CREATOR = new Creator<AudioClipModel>() {
        @Override
        public AudioClipModel createFromParcel(Parcel in) {
            return new AudioClipModel(in);
        }

        @Override
        public AudioClipModel[] newArray(int size) {
            return new AudioClipModel[size];
        }
    };
    //audio file name of the file
    private String fileName;
    //the user description of the file
    private String audioDescription;

    //the public constructor
    public AudioClipModel(@NonNull String fileName, String audioDescription) {
        this.fileName = fileName;
        this.audioDescription = audioDescription;
    }

    protected AudioClipModel(Parcel in) {
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
        Gson gson = new Gson();
        return gson.toJson(this);
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
        return obj instanceof AudioClipModel && obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return audioDescription == null ? fileName.hashCode() : fileName.hashCode() + audioDescription.hashCode();
    }
}
