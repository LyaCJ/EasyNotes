package com.example.madey.easynotes;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.madey.easynotes.DataObject.SimpleListDataObject;
import com.example.madey.easynotes.DataObject.SimpleNoteDataObject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by madey on 8/6/2016.
 */
public class MainActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mDataset;

    private final int TYPE_NOTE = 0;
    private final int TYPE_LIST = 2;
    //private List<SimpleListDataObject> lDataset;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SimpleNoteDataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TableLayout tl;
        ImageView image;
        TextView title;
        TextView content;
        TextView created;
        TextView modified;

        public SimpleNoteDataObjectHolder(View itemView) {
            super(itemView);
            tl=(TableLayout) itemView.findViewById(R.id.grid_image_preview);
            title = (TextView) itemView.findViewById(R.id.sn_title);
            content = (TextView) itemView.findViewById(R.id.sn_content);
            created = (TextView) itemView.findViewById(R.id.sn_created);
            modified = (TextView) itemView.findViewById(R.id.sn_lastmodified);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

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

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityAdapter() {
        mDataset = getMDataSet();
    }

    //Called when creating recycler view and instance of this adapter im MainFragment
    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityAdapter(List<Object> data) {
        mDataset = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_NOTE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.simple_note_card_view, parent, false);

                return new SimpleNoteDataObjectHolder(view);
            case TYPE_LIST:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.simple_list_card_view, parent, false);

                return new SimpleListDataObjectHolder(view);
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


                if(snData.getTitle()!=null)
                    sndoh.title.setText(snData.getTitle());
                if(snData.getContent()!=null)
                    sndoh.content.setText(snData.getContent());

                if(snData.getCreationDate()!=null)
                    sndoh.created.setText(snData.getCreationDate().toString());

                if(snData.getLastModifiedDate() != null)
                    sndoh.modified.setText(snData.getLastModifiedDate().toString());

                if(snData.getImageURI()!= null && snData.getImageURI().size()>0){
                    ImageView imageView=new ImageView(sndoh.tl.getContext());
                imageView.setImageBitmap(snData.getImageURI().get(0));
                sndoh.tl.addView(imageView);
                }
                System.out.println("Title: Inside onBind");
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
        List<Object> results = new ArrayList<>();
        /*for (int index = 0; index < 5; index++) {
            SimpleNoteDataObject obj = new SimpleNoteDataObject("",
                    "Secondary " + index);
            results.add(obj);
        }
        for (int index = 0; index < 5; index++) {
            SimpleListDataObject obj = new SimpleListDataObject("Some Primary Text " + index,
                    Arrays.asList(new String[]{"Mommy", "Daddy"}));
            results.add(obj);
        }*/
        return results;
    }
}