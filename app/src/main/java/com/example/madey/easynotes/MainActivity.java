package com.example.madey.easynotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.example.madey.easynotes.AsyncTasks.ReadSimpleNoteFilesTask;
import com.example.madey.easynotes.DataObject.SimpleNoteDataObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ReadSimpleNoteFilesTask.OnNoteLoadedListener {

    private List<Object> notes = new ArrayList<>(10);
    private MainFragment mf;
    private List<String> noteFileNames;

    public List<Object> getNotes(){
        return notes;
    }

    public List<String> getNoteFileNames() {
        return noteFileNames;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        mf = new MainFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_fragment, mf).commit();
        /*for (File file: getFilesDir().listFiles()){
            if(file.isFile()){
                file.delete();
            }
        }*/
        //retrieve references to note files
        noteFileNames= Arrays.asList(getFilesDir().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith("note");
            }
        }));

        for(String name: noteFileNames) {
            SimpleNoteDataObject sndo = new SimpleNoteDataObject();
            sndo.setNoteFile(getFileStreamPath(name));
            ReadSimpleNoteFilesTask rsnft = new ReadSimpleNoteFilesTask(sndo, this);
            rsnft.setOnNoteLoadedListener(this);
            rsnft.execute(name);
            notes.add(0,sndo);

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
    public void onNoteLoaded(SimpleNoteDataObject obj) {
    if(mf!= null){
        mf.getmAdapter().notifyItemChanged(notes.indexOf(obj));
    }
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
}
