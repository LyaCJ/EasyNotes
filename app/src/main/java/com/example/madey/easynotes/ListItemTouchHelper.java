package com.example.madey.easynotes;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;

/**
 * Created by madey on 8/31/2016.
 */
public class ListItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private OnItemSwipedListener onItemSwipedListener;


    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * { #getSwipeDirs(RecyclerView, ViewHolder)}
     * and / or { #getDragDirs(RecyclerView, ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of { #LEFT}, { #RIGHT}, { #START}, {
     *                  #END},
     *                  { #UP} and { #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of { #LEFT}, { #RIGHT}, { #START}, {
     *                  #END},
     *                  { #UP} and { #DOWN}.
     */
    public ListItemTouchHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public void setOnItemSwipedListener(OnItemSwipedListener onItemSwipedListener) {
        this.onItemSwipedListener = onItemSwipedListener;
    }

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to
     * the new position.
     * <p/>
     * If this method returns true, ItemTouchHelper assumes {@code viewHolder} has been moved
     * to the adapter position of {@code target} ViewHolder
     * ({ ViewHolder#getAdapterPosition()
     * ViewHolder#getAdapterPosition()}).
     * <p/>
     * If you don't support drag & drop, this method will never be called.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder   The ViewHolder which is being dragged by the user.
     * @param target       The ViewHolder over which the currently active item is being
     *                     dragged.
     * @return True if the {@code viewHolder} has been moved to the adapter position of
     * {@code target}.
     * @see #onMoved(RecyclerView, android.support.v7.widget.RecyclerView.ViewHolder, int, android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)
     */
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // get the viewHolder's and target's positions in your adapter data, swap them
        Collections.swap(((ItemListAdapter) recyclerView.getAdapter()).getDataSet(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
        // and notify the adapter that its dataset has changed
        recyclerView.getAdapter().notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        return (current instanceof ItemListAdapter.ActiveListItemHolder && target instanceof ItemListAdapter.ActiveListItemHolder) || (current instanceof ItemListAdapter.DoneListItemHolder && target instanceof ItemListAdapter.DoneListItemHolder);
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     * <p/>
     * If you are returning relative directions ({ #START} , { #END}) from the
     * { #getMovementFlags(RecyclerView, ViewHolder)} method, this method
     * will also use relative directions. Otherwise, it will use absolute directions.
     * <p/>
     * If you don't support swiping, this method will never be called.
     * <p/>
     * ItemTouchHelper will keep a reference to the View until it is detached from
     * RecyclerView.
     * As soon as it is detached, ItemTouchHelper will call
     * { #clearView(RecyclerView, ViewHolder)}.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction  The direction to which the ViewHolder is swiped. It is one of
     *                   { #UP}, { #DOWN},
     *                   { #LEFT} or { #RIGHT}. If your
     *                   { #getMovementFlags(RecyclerView, ViewHolder)}
     *                   method
     *                   returned relative flags instead of { #LEFT} / { #RIGHT};
     *                   `direction` will be relative as well. ({ #START} or {
     *                   #END}).
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        onItemSwipedListener.itemSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    //defines the enabled move directions in each state (idle, swiping, dragging).
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ItemListAdapter.ActiveListItemHolder || viewHolder instanceof ItemListAdapter.DoneListItemHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else
            return 0;
    }


    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ItemListAdapter.ActiveListItemHolder || viewHolder instanceof ItemListAdapter.DoneListItemHolder)
            return super.getSwipeDirs(recyclerView, viewHolder);
        else
            return 0;
    }

    @Override
    public int getDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ItemListAdapter.ActiveListItemHolder || viewHolder instanceof ItemListAdapter.DoneListItemHolder)
            return super.getDragDirs(recyclerView, viewHolder);
        else
            return 0;
    }
}
