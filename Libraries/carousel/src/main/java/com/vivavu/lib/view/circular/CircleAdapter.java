package com.vivavu.lib.view.circular;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.lib.R;

import java.util.List;

/**
 * Created by yuja on 2014-04-24.
 */
public class CircleAdapter extends CircularAdapter {
    public CircleAdapter(Context mContext, List mList) {
        super(mContext, mList);
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
}
