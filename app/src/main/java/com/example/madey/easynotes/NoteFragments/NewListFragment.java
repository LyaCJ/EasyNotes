package com.example.madey.easynotes.NoteFragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.madey.easynotes.AsyncTasks.CreateThumbsTask;
import com.example.madey.easynotes.AsyncTasks.WriteFileTask;
import com.example.madey.easynotes.CustomViews.ListItemEditText;
import com.example.madey.easynotes.ItemListAdapter;
import com.example.madey.easynotes.ListItemTouchHelper;
import com.example.madey.easynotes.MainActivity;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.data.HeterogeneousArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { NewListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewListFragment extends NoteFragment implements ListItemEditText.OnDelListener, OnStartDragListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "activeItems";
    private static final String ARG_PARAM2 = "doneItems";

    private boolean imageWrittenFlag = false;

    private ArrayList<Uri> fileUris = new ArrayList<>();


    private LinearLayout imageHolderLayout;
    private TextView addItemButton;
    private View rootView;
    private EditText etslTitle;
    private RecyclerView itemsRecyclerView;

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<Bitmap> thumbs = new ArrayList<>();
    private ItemListAdapter listItemAdapter;
    private LinearLayoutManager activeItemsRecyclerViewLayoutManager;
    private ItemTouchHelper itemTouchHelper;
    private HeterogeneousArrayList<Object> listItems;


    public NewListFragment() {
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param listItems List of Objects Items.
     * @return A new instance of fragment NewListFragment.
     */

    public static NewListFragment newInstance(HeterogeneousArrayList<Object> listItems) {
        NewListFragment fragment = new NewListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, listItems);
        fragment.setArguments(args);
        return fragment;
    }

    public ItemListAdapter getListItemAdapter() {
        return listItemAdapter;
    }

    @Override
    public void delPressed(int position) {
        int count = listItemAdapter.getDataSet().getCount(listItemAdapter.getDataSet().get(position).getClass());
        System.out.println(count);
        if (count > 1) {
            listItemAdapter.getDataSet().remove(position);
            listItemAdapter.notifyItemRemoved(position);
            listItemAdapter.notifyItemRangeChanged(position, listItemAdapter.getDataSet().size());
            if (position > 0)
                itemsRecyclerView.getLayoutManager().findViewByPosition(position - 1).requestFocus();
            else
                etslTitle.requestFocus();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.listItems = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        MainActivity.CURRENT_FRAGMENT = MainActivity.FRAGMENTS.NEWLIST;
        setRetainInstance(true);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_new_list, container, false);

        //Menu Bar
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);

        toolbar.setTitle("Create List");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(NewListFragment.this).commit();
                getFragmentManager().popBackStack();
            }
        });

        imageHolderLayout = (LinearLayout) rootView.findViewById(R.id.pictures_holder);

        itemsRecyclerView = (RecyclerView) rootView.findViewById(R.id.activeItemsList);

        activeItemsRecyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        itemsRecyclerView.setLayoutManager(activeItemsRecyclerViewLayoutManager);
        listItemAdapter = new ItemListAdapter(this.listItems);
        listItemAdapter.setOnDelListener(this);
        listItemAdapter.setOnStartDragListener(this);
        itemsRecyclerView.setAdapter(listItemAdapter);
        listItemAdapter.setActiveListItemAddedListener(new ItemListAdapter.ActiveListItemAddedListener() {
            @Override
            public void ActiveListItemAdded(final int position) {
                activeItemsRecyclerViewLayoutManager.scrollToPosition(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (itemsRecyclerView.getChildAt(position) != null)
                            itemsRecyclerView.getChildAt(position).requestFocus();
                    }
                }, 100);

            }
        });
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.END;
        ListItemTouchHelper callback = new ListItemTouchHelper(dragFlags, swipeFlags);
        callback.setOnItemSwipedListener(listItemAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(itemsRecyclerView);
        return rootView;
    }

    @Override
    protected void saveNote() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu != null) {
            menu.setGroupVisible(R.id.main_menu_group, false);
            menu.setGroupVisible(R.id.create_note_menu_group, true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                // do s.th.
                return true;
            case R.id.action_camera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, Utils.CAMERA_REQUEST);
                return true;
            case R.id.action_pictures:
                Utils.verifyStoragePermissions(getActivity());
                Utils.verifyManageDocumentsPermissions(getActivity());
                Intent intent = null;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Utils.PICTURE_REQUEST);
                return true;
            case R.id.action_done:

                getActivity().getFragmentManager().popBackStack();
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageWrittenFlag = false;
        if (resultCode == Activity.RESULT_OK) {
            int THUMBSIZE = Utils.DEVICE_WIDTH / 4;
            //images from camera should be written to external/internal storage, and a Uri should be retrieved.
            //As soon as an image is captured, write it to disk asynchronously. Generate the thumb asap.
            //image uri won't be available until the bitmap is written to the internal storage. For that, temporarily store
            //the thumbnail in a list and show it post rotation. When the Uri is available, we will remove the thumb from
            //the list and generate thumbnails for subsequent rotations using the Uri available in the fileUris list.
            //PS1: Need to make Thumbnail generation asynchronous? Maybe??.

            if (requestCode == Utils.CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                final Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                imageHolderLayout.addView(createImageView(thumb));
                //write photo to disk, and rerieve URI
                new WriteFileTask(getActivity()){
                    @Override
                    public void onResponseReceived(Object obj) {
                        if(((List<Uri>)obj).size()>0){
                            Snackbar.make(getActivity().getCurrentFocus(), "Captured Image saved!", Snackbar.LENGTH_SHORT).show();
                            //add the Uri to the existing Uri list
                            fileUris.addAll(((List<Uri>)obj));
                            //remove the thumbnail from the bitmaps list
                            thumbs.remove(thumb);
                        }
                        else
                            Snackbar.make(getActivity().getCurrentFocus(), "Captured image not saved", Snackbar.LENGTH_SHORT).show();
                    }
                }.execute(photo);
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getData() != null) {
                //generate thumb asynchronously
                new CreateThumbsTask(getActivity(), new Point(THUMBSIZE,THUMBSIZE)) {
                    @Override
                    public void onCompleted(ArrayList<Bitmap> bitmaps) {
                        for(Bitmap bmp : bitmaps)
                            imageHolderLayout.addView(createImageView(bmp));
                    }
                }.execute(data.getData());
                //save the Uri from data.getData into list of Uris
                fileUris.add(data.getData());
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getClipData() != null) {
                System.out.println("Clip Data:" + data.getClipData());
                //multiple images from gallery
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    ClipData.Item item = data.getClipData().getItemAt(i);
                    //generate thumb asynchronously
                    new CreateThumbsTask(getActivity(), new Point(THUMBSIZE,THUMBSIZE)) {
                        @Override
                        public void onCompleted(ArrayList<Bitmap> bitmaps) {
                            for(Bitmap bmp : bitmaps)
                                imageHolderLayout.addView(createImageView(bmp));
                        }
                    }.execute(item.getUri());
                    //save the Uri from data.getData into list of Uris
                    fileUris.add(item.getUri());
                }
            }
        }
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("dataset", getListItemAdapter().getDataSet());
        outState.putParcelableArrayList("bitmap_files", fileUris);
        outState.putParcelableArrayList("bitmap_thumbs", thumbs);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            fileUris = savedInstanceState.getParcelableArrayList("bitmap_files");
            System.out.println("FileUris: " + fileUris);
            for (Uri uri : fileUris) {
                new CreateThumbsTask(getActivity(), new Point(Utils.DEVICE_WIDTH / 4, Utils.DEVICE_WIDTH / 4)) {
                    @Override
                    public void onCompleted(ArrayList<Bitmap> bitmaps) {
                        for (Bitmap bmp : bitmaps)
                            imageHolderLayout.addView(createImageView(bmp));
                    }
                }.execute(uri);
            }
            thumbs = savedInstanceState.getParcelableArrayList("bitmap_thumbs");
            for (Bitmap bmp : thumbs)
                imageHolderLayout.addView(createImageView(bmp));

            listItems = savedInstanceState.getParcelable("dataset");

        }
    }
}
