package com.vivavu.dream.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by yuja on 2014-08-26.
 */
public abstract class CustomBaseAdapter<T> extends BaseAdapter {
	public static final int INVALID_ITEM_ID = -1;

	protected Context mContext;
	protected List<T> list;

	protected CustomBaseAdapter(Context mContext) {
		this.mContext = mContext;
	}

	protected CustomBaseAdapter(Context mContext, List<T> list) {
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		if(list == null){
			return 0;
		}
		return list.size();
	}

	@Override
	public T getItem(int position) {
		if(list == null || list.size() <= position) {
			return null;
		}
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		T item = getItem(position);
		if( item != null){
			return position;
		}
		return INVALID_ITEM_ID;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public void addList(List<T> list){
		if(getList() != null) {
			this.list.addAll(list);
		} else {
			setList(list);
		}
	}

	public void clearList(){
		if(getList() != null){
			this.list.clear();
		}
	}
}
