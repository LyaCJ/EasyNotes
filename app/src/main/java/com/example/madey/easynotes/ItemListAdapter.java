package com.example.madey.easynotes;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.madey.easynotes.CustomViews.ListItemEditText;
import com.example.madey.easynotes.NoteFragments.OnStartDragListener;

import java.util.List;

/**
 * Created by madey on 8/23/2016.
 */
public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<StringBuilder> dataSet;
    private ListItemEditText.OnDelListener onDelListener;
    private OnStartDragListener onStartDragListener;

    public ItemListAdapter(List<StringBuilder> dataSet) {
        super();
        this.dataSet = dataSet;
    }

    public void setOnStartDragListener(OnStartDragListener onStartDragListener) {
        this.onStartDragListener = onStartDragListener;
    }

    public List<StringBuilder> getDataSet() {
        return dataSet;
    }

    public void setOnDelListener(ListItemEditText.OnDelListener onDelListener) {
        this.onDelListener = onDelListener;
    }

    public void addActiveListItem(int position) {
        dataSet.add(position, new StringBuilder());
        notifyItemInserted(position);
    }

    public void addActiveListItem() {
        dataSet.add(new StringBuilder());
        notifyItemInserted(dataSet.size() - 1);

    }

    /**
     * Called when RecyclerView needs a new { ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * { #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_active, parent, false);
        ListItemEditText liet = (ListItemEditText) v.findViewById(R.id.item_list);
        liet.setOnDelListener(onDelListener);
        return new ActiveListItemHolder(v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the { ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use { ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p/>
     * Override { #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        String text = dataSet.get(position).toString();
        ActiveListItemHolder aclih = (ActiveListItemHolder) holder;
        aclih.et.setText(text);
        aclih.et.requestFocus();

        aclih.handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class ActiveListItemHolder extends RecyclerView.ViewHolder implements TextWatcher {
        ListItemEditText et;
        ImageView handle;

        public ActiveListItemHolder(View itemView) {
            super(itemView);
            et = (ListItemEditText) itemView.findViewById(R.id.item_list);
            et.setHolder(this);
            et.addTextChangedListener(this);
            handle = (ImageView) itemView.findViewById(R.id.item_handle);
        }


        /**
         * This method is called to notify you that, within <code>s</code>,
         * the <code>count</code> characters beginning at <code>start</code>
         * are about to be replaced by new text with length <code>after</code>.
         * It is an error to attempt to make changes to <code>s</code> from
         * this callback.
         *
         * @param s
         * @param start
         * @param count
         * @param after
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        /**
         * This method is called to notify you that, within <code>s</code>,
         * the <code>count</code> characters beginning at <code>start</code>
         * have just replaced old text that had length <code>before</code>.
         * It is an error to attempt to make changes to <code>s</code> from
         * this callback.
         *
         * @param s
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        /**
         * This method is called to notify you that, somewhere within
         * <code>s</code>, the text has been changed.
         * It is legitimate to make further changes to <code>s</code> from
         * this callback, but be careful not to get yourself into an infinite
         * loop, because any changes you make will cause this method to be
         * called again recursively.
         * (You are not told where the change took place because other
         * afterTextChanged() methods may already have made other changes
         * and invalidated the offsets.  But if you need to know here,
         * you can use { Spannable#setSpan} in {@link #onTextChanged}
         * to mark your place and then look up from here where the span
         * ended up.
         *
         * @param s
         */
        @Override
        public void afterTextChanged(Editable s) {

            int position = getAdapterPosition();
            dataSet.get(position).delete(0, dataSet.get(position).length());
            dataSet.get(position).append(s.toString());
            if (s.length() > 0 && s.charAt(s.length() - 1) == '\n') {
                s.delete(s.length() - 1, s.length());
                dataSet.add(position + 1, new StringBuilder());
                ItemListAdapter.this.notifyItemInserted(position + 1);
            }
        }


    }


}
