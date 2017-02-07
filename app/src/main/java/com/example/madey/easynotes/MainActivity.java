package com.example.madey.easynotes;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

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

    /*public List<SimpleNoteModel> getNotes() {
        return notes;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initDimensions(this);
        Utils.initStoragePreference(this);
        //check storage permissions on the main thread.
        //Utils.verifyStoragePermissions(this);

        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mf = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_fragment, mf, Utils.FRAGMENT_TAG_MAIN).commit();
        } else {

            CURRENT_FRAGMENT = (FRAGMENTS) savedInstanceState.getSerializable("curr_fragment");
            System.out.println("MainActivity.onCreate/: " + CURRENT_FRAGMENT);
            /*
            switch (CURRENT_FRAGMENT) {
                case MAIN:
                    mf = (MainFragment) getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_MAIN);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, mf, Utils.FRAGMENT_TAG_MAIN).commit();
                    break;
                case NEWNOTE:
                    NewNoteFragment nnf = (NewNoteFragment) getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_NEWNOTE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
                    break;
            }*/

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        Log.d(LOG_TAG, "Inside onCreateOptionsMenu() Menu is " + menu);
        if (menu != null) {
            menu.setGroupVisible(R.id.main_menu_group, true);
        }
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //restore notes and notify adapter
        if (savedInstanceState != null) {
            CURRENT_FRAGMENT = (FRAGMENTS) savedInstanceState.getSerializable("curr_fragment");
        }
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
                    break;
                case PAGER:
                    ImagePagerFragment imagePagerFragment = (ImagePagerFragment) getSupportFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_PAGER);
                    getSupportFragmentManager().beginTransaction().remove(imagePagerFragment).commit();
                    break;

            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            FragmentManager fm = getSupportFragmentManager();
            DataFragment dataFragment = (DataFragment) fm.findFragmentByTag(Utils.TAG_DATA_FRAGMENT);
            // we will not need this fragment anymore, this may also be a good place to signal
            // to the retained fragment object to perform its own cleanup.
            fm.beginTransaction().remove(dataFragment).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("curr_fragment", CURRENT_FRAGMENT);
        //save the notes
    }

    public enum FRAGMENTS {
        MAIN, NEWNOTE, SEARCH, PAGER
    }
}
