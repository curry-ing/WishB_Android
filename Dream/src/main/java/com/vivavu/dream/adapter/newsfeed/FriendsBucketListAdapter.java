package com.vivavu.dream.adapter.newsfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.CustomBaseAdapter;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.ShadowImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-08-27.
 */
public class FriendsBucketListAdapter extends CustomBaseAdapter<Bucket> {

	private OnBucketImageViewClick onBucketImageViewClickListener;

	public FriendsBucketListAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Bucket item = getItem(position);
		if(item != null) {
			ButterknifeViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.bucket_sub_list_item, parent, false);
				holder = new ButterknifeViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ButterknifeViewHolder) convertView.getTag();
			}

			int padding = mContext.getResources().getDimensionPixelSize(R.dimen.sub_view_item_padding);
			convertView.setPadding(padding, padding, padding, padding);

			int progress = DateUtils.getProgress(item.getRegDate(), item.getDeadline());

			holder.mBucketItemTitle.setText(item.getTitle());
			holder.mBucketItemDeadline.setText(DateUtils.getDateString(item.getDeadline(), "yyyy.MM.dd"));
			holder.mBucketItemImg.setPercent(progress);

			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.considerExifParams(true)
					.showImageForEmptyUri(R.drawable.ic_bucket_empty)
					.build();

			ImageLoader.getInstance().displayImage(item.getCvrImgUrl(), holder.mBucketItemImg, options);


			holder.mBucketItemImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(onBucketImageViewClickListener != null) {
						onBucketImageViewClickListener.onItemClick(v, position, item.getId());
					} else {

					}
				}
			});

			return convertView;

		}
		return null;
	}

	public interface OnBucketImageViewClick {
		public void onItemClick(View view, int position, long id);
	}

	public OnBucketImageViewClick getOnBucketImageViewClickListener() {
		return onBucketImageViewClickListener;
	}

	public void setOnBucketImageViewClickListener(OnBucketImageViewClick onBucketImageViewClickListener) {
		this.onBucketImageViewClickListener = onBucketImageViewClickListener;
	}

	/**
	 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
	 * for easy to all layout elements.
	 *
	 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
	 */
	static class ButterknifeViewHolder {
		@InjectView(R.id.bucket_item_img)
		ShadowImageView mBucketItemImg;
		@InjectView(R.id.bucket_item_title)
		TextView mBucketItemTitle;
		@InjectView(R.id.bucket_item_deadline)
		TextView mBucketItemDeadline;

		ButterknifeViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
