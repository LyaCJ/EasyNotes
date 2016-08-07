package com.example.madey.easynotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceFragment pf=new PreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, pf).commit();
        setContentView(R.layout.activity_settings);
    }
}
