package com.vivavu.lib.view.circular;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.lib.R;

import java.util.List;

/**
 * Created by yuja on 2014-04-17.
 */
public class CircularAdapter extends BaseAdapter {
    protected Context mContext;
    protected List mList;

    public CircularAdapter(Context mContext, List mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CircularItemContainer getView(int position, View convertView, ViewGroup parent) {
        CircularItemContainer circularItemContainer = new CircularItemContainer(mContext);
        circularItemContainer.setIndex(position);
        View v = null;
        CircularViewTestActivity.DummyData data = (CircularViewTestActivity.DummyData) mList.get(position);
        if (v == null)
        {
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.card, null);

            LinearLayout layout_row = (LinearLayout) v.findViewById(R.id.layout_row);
            TextView title = (TextView) v.findViewById(R.id.card_title);
            //TextView description = (TextView) v.findViewById(R.id.card_desc);

            title.setText(data.mName);
            //description.setText(data.mName);
            layout_row.setBackgroundColor(data.mBGColor);

        }
        circularItemContainer.addView(v);
        return circularItemContainer;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public List getmList() {
        return mList;
    }

    public void setmList(List mList) {
        this.mList = mList;
    }
}
