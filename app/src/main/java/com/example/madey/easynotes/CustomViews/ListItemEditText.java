package com.example.madey.easynotes.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import com.example.madey.easynotes.ItemListAdapter;

/**
 * Created by madey on 8/29/2016.
 */
public class ListItemEditText extends EditText implements View.OnKeyListener {

    private ItemListAdapter.ActiveListItemHolder holder;
    private OnDelListener onDelListener = null;

    public ListItemEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ListItemEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ListItemEditText(Context ctx) {
        super(ctx);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ListItemInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    public void setHolder(ItemListAdapter.ActiveListItemHolder holder) {
        this.holder = holder;
    }

    public void setOnDelListener(OnDelListener onDelListener) {
        this.onDelListener = onDelListener;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        System.out.println("Hardware Key: " + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && ListItemEditText.this.getText().toString().length() == 0 && holder.getAdapterPosition() > 0) {
            onDelListener.delPressed(holder.getAdapterPosition());
        }
        return false;
    }

    public interface OnDelListener {
        void delPressed(int position);
    }

    private class ListItemInputConnection extends InputConnectionWrapper {

        public ListItemInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }


        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            System.out.println("Software Key: " + event.getKeyCode());
            if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && ListItemEditText.this.getText().toString().length() == 0 && holder.getAdapterPosition() > 0) {
                onDelListener.delPressed(holder.getAdapterPosition());
                return true;
            }
            return super.sendKeyEvent(event);
        }

    }


}
