package com.example.madey.easynotes.uicomponents;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madey.easynotes.AsyncTasks.CreateThumbsTask;
import com.example.madey.easynotes.R;
import com.example.madey.easynotes.Utils;
import com.example.madey.easynotes.models.ImageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madeyedexter on 23-12-2016.
 */

public class ThumbsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ImageModel> dataSet;

    private View.OnClickListener clickListener;

    public ThumbsRecyclerViewAdapter(List<ImageModel> dataSet, View.OnClickListener clickListener) {
        this.dataSet = dataSet;
        this.clickListener = clickListener;
    }

    public ThumbsRecyclerViewAdapter() {
        this.dataSet = new ArrayList<>();
    }

    public List<ImageModel> getDataSet() {
        return dataSet;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ThumbnailModelHolder thumbnailModelHolder = (ThumbnailModelHolder) holder;
        final ImageModel imageModel = dataSet.get(position);
        thumbnailModelHolder.imageView.setImageBitmap(null);
        thumbnailModelHolder.imageView.setTag(position);
        if (imageModel.getThumbBitmap() != null) {
            thumbnailModelHolder.imageView.setImageBitmap(imageModel.getThumbBitmap());
        } else {
            new CreateThumbsTask(thumbnailModelHolder.imageView.getContext(), new Point(Utils.DEVICE_WIDTH / 4, Utils.DEVICE_WIDTH / 4)) {
                @Override
                public void onCompleted(ArrayList<Bitmap> bmp) {
                    thumbnailModelHolder.imageView.setImageBitmap(bmp.get(0));
                    imageModel.setThumbBitmap(bmp.get(0));
                    ThumbsRecyclerViewAdapter.this.notifyItemChanged(position);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageModel.getImageFileName());
        }
        thumbnailModelHolder.textView.setText(imageModel.getImageFileName());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_thumb_image_view, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = Utils.DEVICE_WIDTH / 4;
        layoutParams.height = Utils.DEVICE_WIDTH / 4;
        return new ThumbsRecyclerViewAdapter.ThumbnailModelHolder(view, clickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private static class ThumbnailModelHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ThumbnailModelHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.thumb_image_view);
            imageView.setOnClickListener(clickListener);
            textView = (TextView) itemView.findViewById(R.id.thumb_text_view);
        }
    }
}
