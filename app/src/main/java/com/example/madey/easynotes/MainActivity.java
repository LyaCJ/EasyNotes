package com.example.madey.easynotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.madey.easynotes.models.SimpleNoteModel;
import com.example.madey.easynotes.uicomponents.ImagePagerFragment;
import com.example.madey.easynotes.uicomponents.MainFragment;
import com.example.madey.easynotes.uicomponents.NewNoteFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";


    public static FRAGMENTS CURRENT_FRAGMENT = FRAGMENTS.MAIN;
    public List<SimpleNoteModel> notes = new ArrayList<>(5);
    private MainFragment mf;

    public List<SimpleNoteModel> getNotes() {
        return notes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initDimensions(this);
        Utils.initStoragePreference(this);
        //check storage permissions on the main thread.
        //Utils.verifyStoragePermissions(this);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (savedInstanceState == null) {
            mf = new MainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_fragment, mf, Utils.FRAGMENT_TAG_MAIN).commit();
        } else {

            CURRENT_FRAGMENT = (FRAGMENTS) savedInstanceState.getSerializable("curr_fragment");
            System.out.println("MainActivity.onCreate/: " + CURRENT_FRAGMENT);
            switch (CURRENT_FRAGMENT) {
                case MAIN:
                    mf = (MainFragment) getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_MAIN);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, mf, Utils.FRAGMENT_TAG_MAIN).commit();
                    break;
                case NEWNOTE:
                    NewNoteFragment nnf = (NewNoteFragment) getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_NEWNOTE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
                    break;
            }

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        CURRENT_FRAGMENT = (FRAGMENTS) savedInstanceState.getSerializable("curr_fragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        menu.getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Display the fragment as the main content.
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        System.out.println("In MainActivity.onBackPressed/: BackStack count is " + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
            System.out.println("In MainActivity.onBackPressed/: Current Fragment is " + MainActivity.CURRENT_FRAGMENT.toString());
            switch (MainActivity.CURRENT_FRAGMENT) {
                case NEWNOTE:
                    NewNoteFragment newNoteFragment = (NewNoteFragment) getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_NEWNOTE);
                    newNoteFragment.saveOrUpdateNote(notes);
                    break;
                case PAGER:
                    ImagePagerFragment imagePagerFragment = (ImagePagerFragment) getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_PAGER);
                    getSupportFragmentManager().beginTransaction().remove(imagePagerFragment).commit();

                    break;

            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("curr_fragment", CURRENT_FRAGMENT);
    }

    public enum FRAGMENTS {
        MAIN, NEWNOTE, SEARCH, PAGER
    }
}
