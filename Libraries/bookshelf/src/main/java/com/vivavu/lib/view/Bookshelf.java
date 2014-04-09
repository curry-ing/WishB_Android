package com.vivavu.lib.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.lib.R;
import com.vivavu.lib.model.DummyData;

import java.util.ArrayList;

/**
 * Created by yuja on 14. 2. 26.
 */
public class Bookshelf extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mResourceId;
    private ArrayList<DummyData> mDataSets;

    public Bookshelf(Context mContext, int mResourceId, ArrayList<DummyData> mDataSets) {
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResourceId = mResourceId;
        this.mDataSets = mDataSets;

    }

    @Override
    public int getCount() {
        return mDataSets.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.bookshelf, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mShelfTitle.setText("ttt");
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.mShelfRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalViewHolder.mShelfContainer.getVisibility() == View.VISIBLE){
                    finalViewHolder.mShelfContainer.setVisibility(View.GONE);
                    finalViewHolder.mShelfRoll.setText("펼치기");
                }else{
                    finalViewHolder.mShelfContainer.setVisibility(View.VISIBLE);
                    finalViewHolder.mShelfRoll.setText("접기");
                }
            }
        });

        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    private class ViewHolder {
        TextView mShelfTitle;
        Button mShelfRoll;
        LinearLayout mShelfContainer;

        ViewHolder(View view) {
            mShelfTitle = (TextView) view.findViewById(R.id.shelf_title);
            mShelfRoll = (Button) view.findViewById(R.id.shelf_roll);
            mShelfContainer = (LinearLayout) view.findViewById(R.id.shelf_container);
        }
    }
}
