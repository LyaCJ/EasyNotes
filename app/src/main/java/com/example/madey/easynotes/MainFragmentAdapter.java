package com.example.madey.easynotes;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madey.easynotes.AsyncTasks.CreateThumbsTask;
import com.example.madey.easynotes.models.SimpleNoteModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by madey on 8/6/2016.
 */
public class MainFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_NOTE = 0;
    private final int TYPE_LIST = 2;
    SimpleDateFormat dt = new SimpleDateFormat("MMM dd, yyyy");
    private List<SimpleNoteModel> mDataset;


    private View.OnClickListener cardClickListener;
    //private View cardView;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainFragmentAdapter(View.OnClickListener cardClickListener) {
        this.cardClickListener = cardClickListener;
        mDataset = new ArrayList<>(20);
    }


    //Called when creating recycler view and instance of this adapter im MainFragment
    // Provide a suitable constructor (depends on the kind of dataset)
    public MainFragmentAdapter(List<SimpleNoteModel> data, View.OnClickListener cardClickListener) {
        this.cardClickListener = cardClickListener;
        mDataset = data;
    }

    public void onItemRemove(int adapterPosition) {
        //((SimpleNoteModel) mDataset.get(adapterPosition)).removeFromDisk();
        mDataset.remove(adapterPosition);
        this.notifyItemRemoved(adapterPosition);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View cardView = null;
        //System.out.println("OnCreate called");

        switch (viewType) {
            case TYPE_NOTE:
                cardView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.simple_note_card_view, parent, false);
                return new SimpleNoteDataObjectHolder(cardView, cardClickListener);
            default:
                return null;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //System.out.println("OnBind called");
        switch (holder.getItemViewType()) {
            case TYPE_NOTE:

                final SimpleNoteDataObjectHolder sndoh = (SimpleNoteDataObjectHolder) holder;
                final SimpleNoteModel snData = mDataset.get(position);
                //bind a tag with position as value
                sndoh.cardView.setTag(position);
                if (snData.getTitle() != null)
                    sndoh.title.setText(snData.getTitle());
                else
                    sndoh.title.setText("");
                if (snData.getContent() != null)
                    sndoh.content.setText(snData.getContent());
                else
                    sndoh.content.setText("");
                if (snData.getLastModifiedDate() != 0) {
                    String date = dt.format(new Date(snData.getLastModifiedDate()));
                    sndoh.modified.setText(date);
                }
                if (snData.getHasImages()) {
                    if (snData.getThumb() == null) {
                        CreateThumbsTask ctt = new CreateThumbsTask(sndoh.iv.getContext(), new Point(300, 300)) {
                            @Override
                            public void onCompleted(ArrayList<Bitmap> bmp) {
                                Bitmap bitmap = bmp.get(0);
                                sndoh.iv.setImageBitmap(bitmap);
                                snData.setThumb(bitmap);
                            }
                        };
                        ctt.execute(snData.getImageModels().get(0).getImageFileName());
                    } else
                        sndoh.iv.setImageBitmap(snData.getThumb());
                } else
                    sndoh.iv.setImageBitmap(null);
                if (snData.getHasAudioRecording()) {
                    sndoh.hasAudio.setVisibility(View.VISIBLE);
                } else
                    sndoh.hasAudio.setVisibility(View.INVISIBLE);

                if (snData.getLocationEnabled()) {
                    sndoh.hasLocation.setVisibility(View.VISIBLE);
                    sndoh.hasLocation.setText(snData.getCoarseAddress() == null ? "Unknown Location" : snData.getCoarseAddress().toAddressString());
                } else {
                    sndoh.hasLocation.setVisibility(View.INVISIBLE);
                    sndoh.hasLocation.setText("");
                }


                //hiding permanently for now.
                sndoh.hasLinks.setVisibility(View.INVISIBLE);

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
        if (mDataset.get(position) instanceof SimpleNoteModel)
            return TYPE_NOTE;
        return -1;
    }

    public List<SimpleNoteModel> getMDataSet() {
        return mDataset;
    }

    public void remove(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SimpleNoteDataObjectHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView iv;
        TextView title;
        TextView content;
        TextView modified;

        ImageView hasAudio;
        TextView hasLocation;
        ImageView hasLinks;


        public SimpleNoteDataObjectHolder(View itemView, View.OnClickListener listener) {
            super(itemView);
            cardView = (CardView) itemView;
            iv = (ImageView) itemView.findViewById(R.id.image_preview);
            title = (TextView) itemView.findViewById(R.id.sn_title);
            content = (TextView) itemView.findViewById(R.id.sn_content);
            modified = (TextView) itemView.findViewById(R.id.sn_lastmodified);
            hasAudio = (ImageView) itemView.findViewById(R.id.audio_image_view);
            hasLocation = (TextView) itemView.findViewById(R.id.location_text_view);
            hasLinks = (ImageView) itemView.findViewById(R.id.link_image_view);
            itemView.setOnClickListener(listener);
        }
    }
}