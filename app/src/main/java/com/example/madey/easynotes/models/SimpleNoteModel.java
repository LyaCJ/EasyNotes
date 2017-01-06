package com.example.madey.easynotes.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madey on 8/6/2016.
 */
public class SimpleNoteModel implements Parcelable {

    public static final Creator<SimpleNoteModel> CREATOR = new Creator<SimpleNoteModel>() {
        @Override
        public SimpleNoteModel createFromParcel(Parcel in) {
            return new SimpleNoteModel(in);
        }

        @Override
        public SimpleNoteModel[] newArray(int size) {
            return new SimpleNoteModel[size];
        }
    };

    //A thumb bitmap for image preview
    private Bitmap thumb;
    //An id for the note. if the note has been persisted, the id should be set.
    private long id = 0;
    //title
    private String title;
    //content
    private String content;
    //dates
    private long creationDate = 0;
    private long lastModifiedDate = 0;
    //location
    private Boolean isLocationEnabled = false;
    private Coordinates coordinates = new Coordinates();
    private CoarseAddress coarseAddress;
    //audio
    private Boolean hasAudioRecording = false;
    private List<AudioClipModel> audioClipModels = new ArrayList<>();
    //images
    private Boolean hasImages = false;
    private ArrayList<String> imageFileNames = new ArrayList<>();
    //some future proofing for notes involving Lists
    private Boolean hasList = false;
    private String jsonListString;

    public SimpleNoteModel(String title, String content) {

        this.title = title;
        this.content = content;
    }

    public SimpleNoteModel() {
    }

    private SimpleNoteModel(Parcel in) {
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        creationDate = in.readLong();
        lastModifiedDate = in.readLong();
        in.readList(imageFileNames, null);
        isLocationEnabled = (Boolean) in.readSerializable();
        coordinates = new Coordinates(in.readString());
        coarseAddress = new CoarseAddress(in.readString());

    }

    public Boolean getHasAudioRecording() {
        return hasAudioRecording;
    }

    public void setHasAudioRecording(Boolean hasAudioRecording) {
        this.hasAudioRecording = hasAudioRecording;
    }

    public Boolean getHasImages() {
        return hasImages;
    }

    public void setHasImages(Boolean hasImages) {
        this.hasImages = hasImages;
    }

    public Boolean getHasList() {
        return hasList;
    }

    public void setHasList(Boolean hasList) {
        this.hasList = hasList;
    }

    public String getJsonListString() {
        return jsonListString;
    }

    public void setJsonListString(String jsonListString) {
        this.jsonListString = jsonListString;
    }

    public List<AudioClipModel> getAudioClipModels() {
        return audioClipModels;
    }

    public void setAudioClipModels(List<AudioClipModel> audioClipModels) {
        this.audioClipModels = audioClipModels;
    }

    public CoarseAddress getCoarseAddress() {
        return coarseAddress;
    }

    public void setCoarseAddress(CoarseAddress coarseAddress) {
        this.coarseAddress = coarseAddress;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Boolean getLocationEnabled() {
        return isLocationEnabled;
    }

    public void setLocationEnabled(Boolean locationEnabled) {
        isLocationEnabled = locationEnabled;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(creationDate);
        dest.writeLong(lastModifiedDate);
        dest.writeList(imageFileNames);
        dest.writeSerializable(isLocationEnabled);
        dest.writeString(coordinates.toString());
        dest.writeParcelable(coarseAddress, flags);

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<String> getImageFileNames() {
        return imageFileNames;
    }

    public void setImageFileNames(ArrayList<String> imageFileNames) {
        this.imageFileNames = imageFileNames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
}
