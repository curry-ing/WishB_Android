package com.vivavu.dream.repository.task;

import android.os.AsyncTask;

/**
 * Created by yuja on 14. 3. 6.
 */
public abstract class CustomAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected OnPostExecuteCallback onPostExecuteCallback;

    public interface OnPostExecuteCallback{
        public void onPostExecuteCallback();
    }

    public OnPostExecuteCallback getOnPostExecuteCallback() {
        return onPostExecuteCallback;
    }

    public void setOnPostExecuteCallback(OnPostExecuteCallback onPostExecuteCallback) {
        this.onPostExecuteCallback = onPostExecuteCallback;
    }
}
