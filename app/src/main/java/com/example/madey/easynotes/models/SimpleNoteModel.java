package com.example.madey.easynotes.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    // making it transient so that Gson ignores it.
    private transient Bitmap thumb;
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
    private Set<AudioClipModel> audioClipModels = new HashSet<>();
    //images
    private Boolean hasImages = false;
    private List<ImageModel> imageModels = new ArrayList<>();
    //some future proofing for notes involving Lists
    private Boolean hasList = false;
    private List<ListItemModel> listItems = new ArrayList<>();

    protected SimpleNoteModel(Parcel in) {
        thumb = in.readParcelable(Bitmap.class.getClassLoader());
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        creationDate = in.readLong();
        lastModifiedDate = in.readLong();
        coordinates = in.readParcelable(Coordinates.class.getClassLoader());
        coarseAddress = in.readParcelable(CoarseAddress.class.getClassLoader());
        audioClipModels = new HashSet<>(in.createTypedArrayList(AudioClipModel.CREATOR));
        imageModels = in.createTypedArrayList(ImageModel.CREATOR);
        listItems = in.createTypedArrayList(ListItemModel.CREATOR);
    }

    public SimpleNoteModel(String title, String content) {

        this.title = title;
        this.content = content;
    }

    public SimpleNoteModel() {
    }

    public List<ListItemModel> getListItems() {
        return listItems;
    }

    public void setListItems(List<ListItemModel> listItems) {
        this.listItems = listItems;
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


    public Set<AudioClipModel> getAudioClipModels() {
        return audioClipModels;
    }

    public void setAudioClipModels(Set<AudioClipModel> audioClipModels) {
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



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ImageModel> getImageModels() {
        return imageModels;
    }

    public void setImageModels(List<ImageModel> imageModels) {
        this.imageModels = imageModels;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(thumb, flags);
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(creationDate);
        dest.writeLong(lastModifiedDate);
        dest.writeParcelable(coordinates, flags);
        dest.writeParcelable(coarseAddress, flags);
        dest.writeTypedArray(audioClipModels.toArray(new AudioClipModel[audioClipModels.size()]), flags);
        dest.writeTypedList(imageModels);
        dest.writeTypedList(listItems);
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
}
