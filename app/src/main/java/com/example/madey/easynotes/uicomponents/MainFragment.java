package com.example.madey.easynotes.uicomponents;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.madey.easynotes.AsyncTasks.ReadSimpleNoteTask;
import com.example.madey.easynotes.DataFragment;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.MainFragmentAdapter;
import com.example.madey.easynotes.NoteTouchHelper;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.SettingsActivity;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.SimpleNoteModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "MainFragment";

    private DataFragment mDataFragment;

    private android.support.design.widget.FloatingActionButton fabAddNote;
    private RecyclerView mRecyclerView;
    private MainFragmentAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private View rootView;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        Bundle args = new Bundle();
        mainFragment.setArguments(args);
        return mainFragment;
    }

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    public MainFragmentAdapter getmAdapter() {
        return mAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //initializing data
        FragmentManager fm = getActivity().getSupportFragmentManager();
        mDataFragment = (DataFragment) fm.findFragmentByTag(Utils.TAG_DATA_FRAGMENT);
        // create the fragment and data the first time
        if (mDataFragment == null) {
            // add the fragment
            mDataFragment = new DataFragment();
            fm.beginTransaction().add(mDataFragment, Utils.TAG_DATA_FRAGMENT).commit();
            mAdapter = new MainFragmentAdapter(mDataFragment.getData(), this);
            // load data from a data source or perform any calculation
            ReadSimpleNoteTask readSimpleNoteTask = new ReadSimpleNoteTask(getActivity()) {
                @Override
                public void onResponseReceived(List<SimpleNoteModel> obj) {
                    Log.d(LOG_TAG, "Inside onResponseReceived: " + obj.size());
                    mDataFragment.getData().addAll(obj);
                    mAdapter.notifyDataSetChanged();
                }
            };
            readSimpleNoteTask.execute();
        } else
            mAdapter = new MainFragmentAdapter(mDataFragment.getData(), this);
        Log.d(LOG_TAG, "onCreate() savedInstanceState is: " + savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(LOG_TAG, "onCreateView() savedInstanceState is: " + savedInstanceState);


        MainActivity.CURRENT_FRAGMENT = MainActivity.FRAGMENTS.MAIN;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final MainActivity ctx = (MainActivity) this.getActivity();


        //toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(null);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Easy Notes");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycler_view);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                Log.d(LOG_TAG, "onChildViewAttachedToWindow : Child Attached");
                mRecyclerView.setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.empty_view).setVisibility(View.GONE);
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (mAdapter.getMDataSet().isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    rootView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                }
            }
        });
        ItemTouchHelper.Callback callback = new NoteTouchHelper(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);


        // Inflate the layout for this fragment
        fabAddNote = (android.support.design.widget.FloatingActionButton) rootView.findViewById(R.id.fab_add_note);
        fabAddNote.setOnClickListener(this);

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        return rootView;
        //System.out.println("Files:"+Arrays.asList(ctx.getFilesDir().list()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "Saving instance: " + outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(LOG_TAG, "onCreateOptionsMenu(): menu is " + menu);
        if (menu != null) {
            menu.clear();
            inflater.inflate(R.menu.menu, menu);
            menu.setGroupVisible(R.id.main_menu_group, true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //clear all notes

        Log.d(LOG_TAG, "onActivityCreated savedInstanceState is  " + savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*
    Handles clicks on CarViews
     */
    @Override
    public void onClick(View v) {
        if (v instanceof CardView) {
            //the item index will be stored in the form of CardView tag
            int index = Integer.parseInt(v.getTag().toString());
            SimpleNoteModel simpleNoteModel = mAdapter.getMDataSet().get(index);
            //pass a reference to the Fragment for editing by user.
            NewNoteFragment nnf = NewNoteFragment.newInstance(simpleNoteModel);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.addToBackStack("new_note").replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
        }
        switch (v.getId()) {
            case R.id.fab_add_note:
                //Create a blank instance for this note
                SimpleNoteModel simpleNoteModel = new SimpleNoteModel();
                NewNoteFragment nnf = NewNoteFragment.newInstance(simpleNoteModel);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                ft.addToBackStack("new_note").replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
                break;
        }
    }
}
