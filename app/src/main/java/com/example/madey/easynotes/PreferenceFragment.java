package com.example.madey.easynotes;

import android.os.Bundle;

public class PreferenceFragment extends android.preference.PreferenceFragment {

    public PreferenceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
