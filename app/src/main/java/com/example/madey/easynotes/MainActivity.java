package com.example.madey.easynotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.madey.easynotes.NoteFragments.NewNoteFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static FRAGMENTS CURRENT_FRAGMENT = FRAGMENTS.MAIN;

    private MainFragment mf;
    private List<Object> notes = new ArrayList<>(5);

    public List<Object> getNotes() {
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
            getFragmentManager().beginTransaction().replace(R.id.frame_fragment, mf, Utils.FRAGMENT_TAG_MAIN).commit();
        } else {

            CURRENT_FRAGMENT = (FRAGMENTS) savedInstanceState.getSerializable("curr_fragment");
            System.out.println("MainActivity: " + CURRENT_FRAGMENT);
            switch (CURRENT_FRAGMENT) {
                case MAIN:
                    mf = (MainFragment) getFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_MAIN);
                    getFragmentManager().beginTransaction().replace(R.id.frame_fragment, mf, Utils.FRAGMENT_TAG_MAIN).commit();
                    CURRENT_FRAGMENT = FRAGMENTS.MAIN;
                    break;
                case NEWNOTE:
                    NewNoteFragment nnf = (NewNoteFragment) getFragmentManager().findFragmentByTag(Utils.FRAGMENT_TAG_NEWNOTE);
                    getFragmentManager().beginTransaction().replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
                    CURRENT_FRAGMENT = FRAGMENTS.NEWNOTE;
                    break;
            }

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        notes.addAll((List<Object>) savedInstanceState.getSerializable("notes"));

        //mf will be null when we are restoring another fragment which was visible before rotation.
        if (mf != null) {
            mf.getmAdapter().notifyItemRangeInserted(0, notes.size());
            mf.getmRecyclerView().setVisibility(View.VISIBLE);
            ((View) mf.getmRecyclerView().getParent()).findViewById(R.id.empty_view).setVisibility(View.GONE);
            System.out.println("Instance Restored: " + mf.getmAdapter().getItemCount());
        }


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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            getFragmentManager().popBackStack();
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("notes", (ArrayList<Object>) notes);
        outState.putSerializable("curr_fragment", CURRENT_FRAGMENT);
    }

    public enum FRAGMENTS {
        MAIN, NEWNOTE, NEWLIST, NEWAUDIO, SEARCH
    }
}
