package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.example.madey.easynotes.models.CoarseAddress;
import com.example.madey.easynotes.models.Coordinates;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by Madeyedexter on 14-01-2017.
 */

public class AddressRetrieverTask extends AsyncTask<Coordinates, Void, CoarseAddress> {

    private static final String LOG_TAG = "AddressRetrieverTask";

    private Activity activity;

    public AddressRetrieverTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected CoarseAddress doInBackground(Coordinates[] params) {
        Coordinates coordinates = params[0];
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        CoarseAddress coarseAddress = null;

        try {
            System.out.println("Coordinates are: " + coordinates);
            Address address = geocoder.getFromLocation(coordinates.getX(), coordinates.getY(), 1).get(0);
            System.out.println("Address is: " + address);
            coarseAddress = new CoarseAddress(address.getLocality(), address.getCountryName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("In " + LOG_TAG + ": Address is: " + coarseAddress);
        }
        System.out.println("Returning from doInBackground");
        return coarseAddress;
    }
}
