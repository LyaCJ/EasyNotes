package com.example.madey.easynotes.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Madeyedexter on 24-11-2016.
 */
/* @javadoc
    Wrapper which delegates all method calls to java.util.ArrayList and maintains count of each type of item present in the ArrayList.
    Uses a HashMap<Object,Integer> to maintain the count of each type of Class object.

 */

public class HeterogeneousArrayList<E extends Object> implements Parcelable {

    /* All Objects will be stored within this delegate.*/
    private ArrayList<E> delegate;

    /*HashSet for counters*/
    private HashMap<Object, Integer> counters;

    public HeterogeneousArrayList() {
        delegate = new ArrayList<>();
        counters = new HashMap<>(5);
    }

    public static final Parcelable.Creator<HeterogeneousArrayList> CREATOR
            = new Parcelable.Creator<HeterogeneousArrayList>() {
        public HeterogeneousArrayList createFromParcel(Parcel in) {
            return new HeterogeneousArrayList(in);
        }

        public HeterogeneousArrayList[] newArray(int size) {
            return new HeterogeneousArrayList[size];
        }
    };

    private HeterogeneousArrayList(Parcel in) {
        delegate = in.readArrayList(null);
        counters = in.readHashMap(null);
    }

    public boolean add(E obj) {
        Integer value = counters.get(obj.getClass()) != null ? counters.put(obj.getClass(), counters.get(obj.getClass()) + 1) : counters.put(obj.getClass(), 1);
        return delegate.add(obj);
    }

    public boolean remove(E obj) {
        Integer value = counters.get(obj.getClass()) != null ? counters.put(obj.getClass(), counters.get(obj.getClass()) - 1) : counters.put(obj.getClass(), 0);
        return delegate.remove(obj);
    }

    public void add(int position, E obj) {
        Integer value = counters.get(obj.getClass()) != null ? counters.put(obj.getClass(), counters.get(obj.getClass()) + 1) : counters.put(obj.getClass(), 1);
        delegate.add(position, obj);
    }

    public int size() {
        return delegate.size();
    }

    public E get(int position) {
        return delegate.get(position);
    }

    public int getCount(Object _class) {
        return counters.get(_class);
    }

    public void swap(int initial, int target) {
        Collections.swap(delegate, initial, target);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(delegate);
        dest.writeMap(counters);
    }
}
