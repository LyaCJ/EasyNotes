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

import com.example.madey.easynotes.NoteFragments.NewNoteFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends android.app.Fragment{

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
                    NewNoteFragment nnf=new NewNoteFragment();
                    getFragmentManager().beginTransaction().addToBackStack("Main").replace(R.id.frame_fragment,nnf).commit();

                    //getFragmentManager().beginTransaction().replace(R.id.frame_fragment, nnf).commit();
                    menuRed.close(true);
                    break;
            }
        }
    };


    public MainFragment() {
        // Required empty public constructor
    }

    public MainFragmentAdapter getmAdapter() {
        return mAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_main, container, false);
        MainActivity ctx=(MainActivity)this.getActivity();
        Toolbar toolbar = ((Toolbar) ctx.findViewById(R.id.my_toolbar));
        toolbar.setTitle("Easy Notes");

        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        final DrawerLayout dl = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        final ListView dListView = (ListView) v.findViewById(R.id.left_drawer);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Drawer: " + dl);
                dl.openDrawer(dListView);
            }
        });
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
            mRecyclerView = (RecyclerView) v.findViewById(R.id.main_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(ctx);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)

            mAdapter = new MainFragmentAdapter(ctx.getNotes());
        ItemTouchHelper.Callback callback=new NoteTouchHelper(mAdapter);
        ItemTouchHelper helper=new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);



            mRecyclerView.setAdapter(mAdapter);


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

        System.out.println("Files:"+Arrays.asList(ctx.getFilesDir().list()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
