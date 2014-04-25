package com.vivavu.lib.view.circular;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by yuja on 2014-04-17.
 */
public abstract class CircularAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mList;

    public CircularAdapter(Context mContext, List<T> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract CircularItemContainer getView(int position, View convertView, ViewGroup parent);

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> mList) {
        this.mList = mList;
    }
}
