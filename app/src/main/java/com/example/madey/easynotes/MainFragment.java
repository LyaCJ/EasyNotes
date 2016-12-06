package com.example.madey.easynotes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.madey.easynotes.AsyncTasks.ReadSimpleNoteFilesTask;
import com.example.madey.easynotes.CustomViews.DividerItemDecoration;
import com.example.madey.easynotes.NoteFragments.NewListFragment;
import com.example.madey.easynotes.NoteFragments.NewNoteFragment;
import com.example.madey.easynotes.data.HeterogeneousArrayList;
import com.example.madey.easynotes.data.SimpleListDataObject;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends android.app.Fragment {

    private FloatingActionMenu menuRed;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private RecyclerView mRecyclerView;
    private MainFragmentAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_note:
                    NewNoteFragment nnf = new NewNoteFragment();
                    getFragmentManager().beginTransaction().addToBackStack("Main").replace(R.id.frame_fragment, nnf, Utils.FRAGMENT_TAG_NEWNOTE).commit();
                    MainActivity.CURRENT_FRAGMENT = MainActivity.FRAGMENTS.NEWNOTE;

                    //getFragmentManager().beginTransaction().replace(R.id.frame_fragment, nnf).commit();
                    menuRed.close(true);
                    break;
                case R.id.fab_list:
                    HeterogeneousArrayList<Object> asb = new HeterogeneousArrayList<>();
                    asb.add(new SimpleListDataObject.ListTitleDataObject());
                    asb.add(new StringBuilder());
                    asb.add(new ItemListAdapter.ListSeparatorModel());

                    NewListFragment nlf = NewListFragment.newInstance(asb);
                    getFragmentManager().beginTransaction().addToBackStack("Main").replace(R.id.frame_fragment, nlf, Utils.FRAGMENT_TAG_NEWLIST).commit();
                    menuRed.close(true);
            }
        }
    };


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
        final MainActivity ctx = (MainActivity) this.getActivity();
        if (savedInstanceState == null) {
            ReadSimpleNoteFilesTask rsnft = new ReadSimpleNoteFilesTask(this.getActivity()) {
                @Override
                public void onResponseReceived(List<Object> obj) {
                    //mAdapter.notifyDataSetChanged();
                    //mRecyclerView.smoothScrollToPosition(0);
                    mAdapter.getMDataSet().addAll(obj);
                    mAdapter.notifyItemRangeInserted(0, obj.size());
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //Will need to be changed if recycler view is nested inside something other than MainFragment
                    ((View) mRecyclerView.getParent()).findViewById(R.id.empty_view).setVisibility(View.GONE);
                }
            };
            rsnft.execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        MainActivity.CURRENT_FRAGMENT = MainActivity.FRAGMENTS.MAIN;
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        final MainActivity ctx = (MainActivity) this.getActivity();
        Toolbar toolbar = ((Toolbar) ctx.findViewById(R.id.my_toolbar));
        toolbar.setTitle("Easy Notes");


        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);

        final DrawerLayout dl = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        final ListView dListView = (ListView) v.findViewById(R.id.left_drawer);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.main_recycler_view);

        // use a linear layout manager

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity()));


        // specify an adapter (see also next example)


        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainFragmentAdapter(ctx.getNotes());
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyItemRangeChanged(0,ctx.getNotes().size());


        System.out.println("Notes in CreateView:" + ctx.getNotes().size());

        if (ctx.getNotes().isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            v.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            v.findViewById(R.id.empty_view).setVisibility(View.GONE);
        }

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
                v.findViewById(R.id.empty_view).setVisibility(View.GONE);
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (ctx.getNotes().isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    v.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                }
            }
        });
        ItemTouchHelper.Callback callback = new NoteTouchHelper(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);


        // Inflate the layout for this fragment
        menuRed = (FloatingActionMenu) v.findViewById(R.id.menu_red);

        fab1 = (FloatingActionButton) v.findViewById(R.id.fab_list);
        fab2 = (FloatingActionButton) v.findViewById(R.id.fab_note);
        fab3 = (FloatingActionButton) v.findViewById(R.id.fab_audio_note);
        fab4 = (FloatingActionButton) v.findViewById(R.id.fab_video_note);

        menuRed.setClosedOnTouchOutside(true);

        menuRed.setVisibility(View.VISIBLE);

        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);

        menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuRed.toggle(true);
            }
        });
        menuRed.showMenuButton(true);
        return v;
        //System.out.println("Files:"+Arrays.asList(ctx.getFilesDir().list()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("AC: " + mAdapter.getItemCount());
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
