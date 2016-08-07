package com.example.madey.easynotes;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madey.easynotes.DataObject.SimpleListDataObject;
import com.example.madey.easynotes.DataObject.SimpleNoteDataObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by madey on 8/6/2016.
 */
public class MainActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mDataset;

    private final int TYPE_NOTE=0;
    private final int TYPE_LIST=2;
    //private List<SimpleListDataObject> lDataset;




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SimpleNoteDataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView image;
        TextView title;
        TextView content;

        public SimpleNoteDataObjectHolder(View itemView) {
            super(itemView);
            image= (ImageView) itemView.findViewById(R.id.sn_image);
            title = (TextView) itemView.findViewById(R.id.sn_title);
            content = (TextView) itemView.findViewById(R.id.sn_content);
            Log.i("DEBUG", "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("DEBUG", "Note Clicked!");
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
            Log.i("DEBUG", "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("DEBUG", "List Clicked!");
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityAdapter() {
        /*nDataset = getNoteDataSet();
        lDataset=getListDataSet();*/
        mDataset=getMDataSet();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View view=null;
        System.out.println("View Type:"+viewType);
        switch (viewType) {
            case TYPE_NOTE: Log.i("DEBUG", "Bolo Bolo Bolo11");
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_note_card_view, parent, false);

                return new SimpleNoteDataObjectHolder(view);
            case TYPE_LIST: Log.i("DEBUG", "Bolo Bolo Bolo2");
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_list_card_view, parent, false);

                return new SimpleListDataObjectHolder(view);
            default: Log.i("DEBUG", "Bolo Bolo Bolo3");
                return null;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        switch(holder.getItemViewType()) {
            case TYPE_NOTE:
                SimpleNoteDataObjectHolder sndoh = (SimpleNoteDataObjectHolder) holder;
                SimpleNoteDataObject snData = (SimpleNoteDataObject) mDataset.get(position);
                sndoh.title.setText(snData.getTitle());
                sndoh.content.setText(snData.getContent());
                break;
            case TYPE_LIST:
                SimpleListDataObjectHolder sldoh = (SimpleListDataObjectHolder) holder;
                SimpleListDataObject slData = (SimpleListDataObject) mDataset.get(position);
                sldoh.title.setText(slData.getTitle());
                sldoh.content.setText(slData.getContent().toString());
                break;
            default:break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("D","Count");
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(mDataset.get(position) instanceof SimpleNoteDataObject)
            return TYPE_NOTE;
        if(mDataset.get(position) instanceof SimpleListDataObject)
            return TYPE_LIST;
        return -1;
    }

    private List<SimpleNoteDataObject> getNoteDataSet() {
        List<SimpleNoteDataObject> results = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            SimpleNoteDataObject obj = new SimpleNoteDataObject("Some Primary Text " + index,
                    "Secondary " + index);
            results.add(index, obj);
        }
        return results;
    }
    private List<SimpleListDataObject> getListDataSet() {
        List<SimpleListDataObject> results = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            SimpleListDataObject obj = new SimpleListDataObject("Some Primary Text " + index,
                    Arrays.asList(new String[]{"Mommy","Daddy"}));
            results.add(index, obj);
        }
        return results;
    }

    public List<Object> getMDataSet(){
        List<Object> results = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            SimpleNoteDataObject obj = new SimpleNoteDataObject("Some Primary Text " + index,
                    "Secondary " + index);
            results.add(obj);
        }
        for (int index = 0; index < 5; index++) {
            SimpleListDataObject obj = new SimpleListDataObject("Some Primary Text " + index,
                    Arrays.asList(new String[]{"Mommy","Daddy"}));
            results.add(obj);
        }
        return results;
    }
}