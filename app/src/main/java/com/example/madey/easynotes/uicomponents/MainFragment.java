package com.example.madey.easynotes.uicomponents;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toolbar;

import com.example.madey.easynotes.AsyncTasks.ReadSimpleNoteTask;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.MainFragmentAdapter;
import com.example.madey.easynotes.NoteTouchHelper;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.SettingsActivity;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.SimpleNoteModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "MainFragment";

    private android.support.design.widget.FloatingActionButton fabAddNote;
    private RecyclerView mRecyclerView;
    private MainFragmentAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private View rootView;


    public MainFragment() {
        // Required empty public constructor
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
        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "Inside onCreate, savedInstanceState is null");
            ReadSimpleNoteTask rsnft = new ReadSimpleNoteTask(this.getActivity()) {
                @Override
                public void onResponseReceived(List<SimpleNoteModel> obj) {
                    //mAdapter.notifyDataSetChanged();
                    //mRecyclerView.smoothScrollToPosition(0);
                    mAdapter.getMDataSet().addAll(obj);
                    mAdapter.notifyItemRangeInserted(0, obj.size());
                    if (obj.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        //Will need to be changed if recycler view is nested inside something other than MainFragment
                        ((View) mRecyclerView.getParent()).findViewById(R.id.empty_view).setVisibility(View.GONE);
                    }
                    Log.d(LOG_TAG, "Items in MainFragment: " + obj.size());
                }
            };
            rsnft.execute();
        } else
            Log.d(LOG_TAG, "Inside onCreate, savedInstanceState is not null");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        MainActivity.CURRENT_FRAGMENT = MainActivity.FRAGMENTS.MAIN;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);
        final MainActivity ctx = (MainActivity) this.getActivity();


        //toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setTitle("Easy Notes");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycler_view);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainFragmentAdapter(ctx.getNotes(), this);
        Log.d(LOG_TAG, "onCreateView() Notes in MainFragment: " + ctx.getNotes());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                mRecyclerView.setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.empty_view).setVisibility(View.GONE);
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (ctx.getNotes().isEmpty()) {
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
        //save the notes
        Log.d(LOG_TAG, "In onSAveInstanceState. Saving Notes in Bundle: " + ((MainActivity) getActivity()).getNotes());
        outState.putParcelableArrayList("notes", (ArrayList<? extends Parcelable>) ((MainActivity) getActivity()).getNotes());

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
        Log.d(LOG_TAG, "Notes in onActivityCreated(): " + ((MainActivity) getActivity()).getNotes());
        Log.d(LOG_TAG, "savedInstanceState in onActivityCreated(): " + savedInstanceState);
        MainActivity mainActivity = ((MainActivity) getActivity());
        //restore notes and notify adapter
        if (savedInstanceState != null) {
            List<SimpleNoteModel> notes = savedInstanceState.getParcelableArrayList("notes");
            mainActivity.getNotes().addAll(notes);
            mAdapter.notifyDataSetChanged();
        }
        //notes added.
        if (mainActivity.getNotes().isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            rootView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
        Log.d(LOG_TAG, "Notes in onActivityCreated(): " + ((MainActivity) getActivity()).getNotes().size());
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
            SimpleNoteModel simpleNoteModel = ((MainActivity) getActivity()).getNotes().get(index);
            //pass a reference to the Fragment for editing by user.
            NewNoteFragment nnf = NewNoteFragment.newInstance(simpleNoteModel);
            getFragmentManager().beginTransaction().addToBackStack("new_note").replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
        }
        switch (v.getId()) {
            case R.id.fab_add_note:
                //Create a blank instance for this note
                SimpleNoteModel simpleNoteModel = new SimpleNoteModel();
                NewNoteFragment nnf = NewNoteFragment.newInstance(simpleNoteModel);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack("new_note").replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
                break;
        }
    }
}
