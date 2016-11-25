package com.example.madey.easynotes.NoteFragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.madey.easynotes.CustomViews.ListItemEditText;
import com.example.madey.easynotes.ItemListAdapter;
import com.example.madey.easynotes.ListItemTouchHelper;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.data.HeterogeneousArrayList;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { NewListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewListFragment extends android.app.Fragment implements ListItemEditText.OnDelListener, OnStartDragListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "activeItems";
    private static final String ARG_PARAM2 = "doneItems";



    // TODO: Create and Set instance variables after init.
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
    // TODO: Rename and change types and number of parameters
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
        itemsRecyclerView.getRecycledViewPool().setMaxRecycledViews(ItemListAdapter.ITEM_TYPE_TITLE, 0);
        itemsRecyclerView.getRecycledViewPool().setMaxRecycledViews(ItemListAdapter.ITEM_TYPE_SEPARATOR, 0);
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.END;
        ListItemTouchHelper callback = new ListItemTouchHelper(dragFlags, swipeFlags);
        callback.setOnItemSwipedListener(listItemAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(itemsRecyclerView);
        return rootView;
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
                if (imageHolderLayout.getChildCount() < 4) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Utils.CAMERA_REQUEST);
                } else
                    Snackbar.make(getView(), "Image Limit Reached", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.action_pictures:
                if (imageHolderLayout.getChildCount() < 4) {
                    Utils.verifyStoragePermissions(getActivity());
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), Utils.PICTURE_REQUEST);
                } else
                    Snackbar.make(getView(), "Image Limit Reached", Snackbar.LENGTH_SHORT).show();
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
        int width = 0;
        ImageView imageView;
        if (resultCode == Activity.RESULT_OK) {
            width = imageHolderLayout.getWidth();
            imageHolderLayout.setMinimumHeight(width / 4);
            imageView = new ImageView(getActivity());
            imageView.setAdjustViewBounds(false);
            imageView.setMaxWidth(width / 4);
            imageView.setMaxHeight(width / 4);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundColor(getResources().getColor(R.color.accent_dark));
            int THUMBSIZE = width / 4;
            if (requestCode == Utils.CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                bitmaps.add(photo);
                Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                thumbs.add(thumb);
                imageView.setImageBitmap(thumb);
                imageHolderLayout.addView(imageView);
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getData() != null) {
                Bitmap photo = null;
                try {
                    photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
                } catch (FileNotFoundException e) {
                    System.out.println("FileNotFoundException: " + e.getMessage());
                }
                bitmaps.add(photo);
                Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                thumbs.add(thumb);
                imageView.setImageBitmap(thumb);
                imageHolderLayout.addView(imageView);
            }
            if (requestCode == Utils.PICTURE_REQUEST && null != data && data.getClipData() != null) {
                System.out.println("Clip Data:" + data.getClipData());
                for (int i = 0; bitmaps.size() <= 4 && i < data.getClipData().getItemCount(); i++) {
                    imageView = new ImageView(getActivity());
                    imageView.setAdjustViewBounds(false);
                    imageView.setMaxWidth(width / 4);
                    imageView.setMaxHeight(width / 4);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    ClipData.Item item = data.getClipData().getItemAt(i);
                    InputStream is = null;
                    try {
                        is = getActivity().getContentResolver().openInputStream(item.getUri());
                    } catch (FileNotFoundException e) {
                        System.out.println("Exception: " + e.getMessage());
                    }
                    Bitmap photo = BitmapFactory.decodeStream(is);
                    bitmaps.add(photo);
                    Bitmap thumb = ThumbnailUtils.extractThumbnail(photo, THUMBSIZE, THUMBSIZE);
                    thumbs.add(thumb);
                    imageView.setImageBitmap(thumb);
                    imageHolderLayout.addView(imageView);
                }
            }
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

}
