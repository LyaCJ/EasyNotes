package com.example.madey.easynotes.uicomponents;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.madey.easynotes.AsyncTasks.ReadSimpleNoteTask;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.MainFragmentAdapter;
import com.example.madey.easynotes.NoteTouchHelper;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.SimpleNoteModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "MainFragment";

    private FloatingActionMenu menuRed;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private RecyclerView mRecyclerView;
    private MainFragmentAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_note:
                    //Create a blank instance for this note
                    SimpleNoteModel simpleNoteModel = new SimpleNoteModel();
                    NewNoteFragment nnf = NewNoteFragment.newInstance(simpleNoteModel);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack("new_note").replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
                    menuRed.close(true);
                    break;
            }
        }
    };

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
        final MainActivity ctx = (MainActivity) this.getActivity();
        Toolbar toolbar = ((Toolbar) ctx.findViewById(R.id.my_toolbar));
        getActivity().setTitle("Easy Notes");


        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);

        final DrawerLayout dl = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        final ListView dListView = (ListView) rootView.findViewById(R.id.left_drawer);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycler_view);
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainFragmentAdapter(ctx.getNotes(), this);
        Log.d(LOG_TAG, "onCreateView() Notes in MainFragment: " + ctx.getNotes());
        mRecyclerView.setAdapter(mAdapter);




        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dl.openDrawer(Gravity.LEFT);
            }
        });
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
        menuRed = (FloatingActionMenu) rootView.findViewById(R.id.menu_red);

        fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab_list);
        fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab_note);
        fab3 = (FloatingActionButton) rootView.findViewById(R.id.fab_video_note);

        menuRed.setClosedOnTouchOutside(true);

        menuRed.setVisibility(View.VISIBLE);

        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);

        menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuRed.toggle(true);
            }
        });
        menuRed.showMenuButton(true);

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
    }
}
