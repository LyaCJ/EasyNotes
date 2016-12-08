package com.example.madey.easynotes.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.madey.easynotes.data.SimpleListDataObject;

/**
 * Created by 834619 on 12/7/2016.
 */
public abstract class WriteSimpleListTask extends AsyncTask<SimpleListDataObject,Void,Boolean>{

    private Context ctx;
    public WriteSimpleListTask(Activity activity) {
        this.ctx=activity;
    }

    protected abstract void onSaved(Boolean bool);

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        onSaved(aBoolean);
    }

    @Override
    protected Boolean doInBackground(SimpleListDataObject... params) {

        return null;
    }
}
