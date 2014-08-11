package com.vivavu.dream.adapter.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.model.Notice;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-08-11.
 */
public class NoticeAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<Notice> notices;

	public NoticeAdapter(Context context) {
		this.context = context;
		this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if(notices == null){
			return 0;
		}
		return notices.size();
	}

	@Override
	public Notice getItem(int position) {
		if(notices == null || position >= notices.size() ){
			return new Notice();
		}

		return notices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.notice_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Notice notice = getItem(position);
		holder.mSubject.setText(notice.getSubject());
		holder.mContents.setText(notice.getContent());

		holder.mSubject.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
		holder.mContents.setVisibility(View.GONE);

		final ViewHolder finalHolder = holder;
		holder.mSubject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				v.setSelected(!v.isSelected());
				if (v.isSelected()){
					finalHolder.mContents.setVisibility(View.VISIBLE);
					finalHolder.mSubject.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
				} else {
					finalHolder.mContents.setVisibility(View.GONE);
					finalHolder.mSubject.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
				}
			}
		});

		return convertView;
	}

	/**
	 * This class contains all butterknife-injected Views & Layouts from layout file 'notice_item.xml'
	 * for easy to all layout elements.
	 *
	 * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
	 */
	static class ViewHolder {
		@InjectView(R.id.subject)
		Button mSubject;
		@InjectView(R.id.contents)
		TextView mContents;

		ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	public List<Notice> getNotices() {
		return notices;
	}

	public void setNotices(List<Notice> notices) {
		this.notices = notices;
	}
}
