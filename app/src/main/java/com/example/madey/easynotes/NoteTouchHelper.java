package com.example.madey.easynotes;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by madey on 8/10/2016.
 */
public class NoteTouchHelper extends ItemTouchHelper.SimpleCallback {

    private MainFragmentAdapter mainFragmentAdapter;

    /**
     * Constructor
     */
    public NoteTouchHelper(MainFragmentAdapter adapter){
        super(ItemTouchHelper.LEFT , ItemTouchHelper.RIGHT);
        mainFragmentAdapter=adapter;

    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //remove item
        mainFragmentAdapter.onItemRemove(viewHolder.getAdapterPosition());

    }
}
