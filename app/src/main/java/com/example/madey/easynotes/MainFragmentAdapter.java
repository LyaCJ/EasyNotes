package com.example.madey.easynotes;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madey.easynotes.DataObject.SimpleListDataObject;
import com.example.madey.easynotes.DataObject.SimpleNoteDataObject;

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
                    int size = snData.getImageThumbs() != null ? snData.getImageThumbs().size() : 0;
                    int calcWidth = 0;
                    if (size == 0) {
                        LinearLayout layout = sndoh.gv;
// Gets the layout params that will allow you to resize the layout
                        ViewGroup.LayoutParams params = layout.getLayoutParams();
// Changes the height and width to the specified *pixels*
                        params.height = 0;
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        layout.setLayoutParams(params);
                    } else
                        calcWidth = Utils.getDimension(sndoh.gv.getContext()).x / size;
                    for (int i = 0; i < sndoh.gv.getChildCount(); i++) {
                        ((ImageView) sndoh.gv.getChildAt(i)).setImageDrawable(null);
                        sndoh.gv.getChildAt(i).setVisibility(View.INVISIBLE);
                    }
                    for (int i = 0; i < size; i++) {
                        sndoh.gv.getChildAt(i).getLayoutParams().width = calcWidth;
                        sndoh.gv.getChildAt(i).getLayoutParams().height = calcWidth;
                        ((ImageView) sndoh.gv.getChildAt(i)).setImageBitmap(snData.getImageThumbs().get(i));
                        sndoh.gv.getChildAt(i).setVisibility(View.VISIBLE);
                    }

                }

                break;
            case TYPE_LIST:
                SimpleListDataObjectHolder sldoh = (SimpleListDataObjectHolder) holder;
                SimpleListDataObject slData = (SimpleListDataObject) mDataset.get(position);
                sldoh.title.setText(slData.getTitle());
                sldoh.content.setText(slData.getContent().toString());
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
        LinearLayout gv;
        TextView title;
        TextView content;
        TextView created;
        TextView modified;

        public SimpleNoteDataObjectHolder(View itemView) {
            super(itemView);
            gv = (LinearLayout) itemView.findViewById(R.id.image_preview);
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
        TextView content;

        public SimpleListDataObjectHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.sl_title);
            content = (TextView) itemView.findViewById(R.id.sl_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("DEBUG", "List Clicked!");
        }
    }

}