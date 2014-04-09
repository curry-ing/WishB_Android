package com.vivavu.dream.listener.bucket;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by yuja on 14. 1. 10.
 */
public class BucketListClickListener implements AdapterView.OnItemClickListener, View.OnClickListener {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(view.getContext(), adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {

    }
}
