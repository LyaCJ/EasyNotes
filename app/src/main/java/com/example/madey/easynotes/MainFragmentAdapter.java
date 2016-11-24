package com.example.madey.easynotes;

import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madey.easynotes.data.SimpleListDataObject;
import com.example.madey.easynotes.data.SimpleNoteDataObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madey on 8/6/2016.
 */
public class MainFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_NOTE = 0;
    private final int TYPE_LIST = 2;
    SimpleDateFormat dt = new SimpleDateFormat("MMM dd, yyyy");
    private List<Object> mDataset;

    //private View cardView;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainFragmentAdapter() {
        mDataset = new ArrayList<>(20);
    }
    //private List<SimpleListDataObject> lDataset;


    //Called when creating recycler view and instance of this adapter im MainFragment
    // Provide a suitable constructor (depends on the kind of dataset)
    public MainFragmentAdapter(List<Object> data) {
        mDataset = data;
    }

    public void onItemRemove(int adapterPosition) {
        ((SimpleNoteDataObject) mDataset.get(adapterPosition)).removeFromDisk();
        mDataset.remove(adapterPosition);

        this.notifyItemRemoved(adapterPosition);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View cardView = null;

        switch (viewType) {
            case TYPE_NOTE:
                cardView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.simple_note_card_view, parent, false);
                return new SimpleNoteDataObjectHolder(cardView);
            case TYPE_LIST:
                cardView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.simple_list_card_view, parent, false);

                return new SimpleListDataObjectHolder(cardView);
            default:
                return null;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        switch (holder.getItemViewType()) {
            case TYPE_NOTE:
                SimpleNoteDataObjectHolder sndoh = (SimpleNoteDataObjectHolder) holder;
                SimpleNoteDataObject snData = (SimpleNoteDataObject) mDataset.get(position);

                int width = Utils.dimension.x;
                if (snData.getTitle() != null)
                    sndoh.title.setText(snData.getTitle());
                if (snData.getContent() != null)
                    sndoh.content.setText(snData.getContent());

                if (snData.getCreationDate() != null) {
                    String date = dt.format(snData.getCreationDate());
                    sndoh.created.setText("Created: " + date);
                }

                if (snData.getLastModifiedDate() != null) {
                    String date = dt.format(snData.getLastModifiedDate());
                    sndoh.modified.setText("Modified: " + date);
                }

                System.out.println("Title: " + snData.getTitle() + " Thumbs: " + snData.getImageThumbs() + " Size: " + snData.getImageThumbs().size());

                if (snData.getImageThumbs() != null) {
                    int size = snData.getImageThumbs().size();
                    if (size > 0)
                        width = width / size;
                    sndoh.gv.removeAllViews();
                    switch (size) {
                        case 1:
                            sndoh.gv.setColumnCount(size);
                            break;
                        case 2:
                            sndoh.gv.setColumnCount(size);
                            break;
                        case 3:
                            sndoh.gv.setColumnCount(size);
                            break;
                        case 4:
                            sndoh.gv.setColumnCount(size);
                            break;
                    }
                    for (int i = 0; i < size; i++) {
                        ImageView iv = new ImageView(sndoh.gv.getContext());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
                        iv.setLayoutParams(layoutParams);
                        //iv.getLayoutParams().width=width;
                        //iv.getLayoutParams().height=width;
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iv.setImageBitmap(snData.getImageThumbs().get(i));
                        sndoh.gv.addView(iv);
                    }

                }

                break;
            case TYPE_LIST:
                SimpleListDataObjectHolder sldoh = (SimpleListDataObjectHolder) holder;
                SimpleListDataObject slData = (SimpleListDataObject) mDataset.get(position);
                sldoh.title.setText(slData.getTitle());
                break;
            default:
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (mDataset.get(position) instanceof SimpleNoteDataObject)
            return TYPE_NOTE;
        if (mDataset.get(position) instanceof SimpleListDataObject)
            return TYPE_LIST;
        return -1;
    }

    public List<Object> getMDataSet() {
        return mDataset;
    }

    public void remove(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SimpleNoteDataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        GridLayout gv;
        TextView title;
        TextView content;
        TextView created;
        TextView modified;

        public SimpleNoteDataObjectHolder(View itemView) {
            super(itemView);
            gv = (GridLayout) itemView.findViewById(R.id.image_preview);
            title = (TextView) itemView.findViewById(R.id.sn_title);
            content = (TextView) itemView.findViewById(R.id.sn_content);
            created = (TextView) itemView.findViewById(R.id.sn_created);
            modified = (TextView) itemView.findViewById(R.id.sn_lastmodified);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Card Clicked", Toast.LENGTH_SHORT).show();

        }
    }

    public static class SimpleListDataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView title;

        public SimpleListDataObjectHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.sl_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("DEBUG", "List Clicked!");
        }
    }


}