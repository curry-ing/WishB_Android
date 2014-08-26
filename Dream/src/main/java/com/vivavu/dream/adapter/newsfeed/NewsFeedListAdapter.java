package com.vivavu.dream.adapter.newsfeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.CustomBaseAdapter;
import com.vivavu.dream.model.NewsFeed;
import com.vivavu.dream.util.DateUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-06-02.
 */
public class NewsFeedListAdapter extends CustomBaseAdapter<NewsFeed> {

	private OnBucketImageViewClick onBucketImageViewClickListener;

	public NewsFeedListAdapter(Context mContext) {
		super(mContext);
	}

	public NewsFeedListAdapter(Context mContext, List<NewsFeed> list) {
		super(mContext, list);
	}

	@Override
	public long getItemId(int position) {
		NewsFeed item = getItem(position);
		if (item != null) {
			return item.getId();
		}
		return INVALID_ITEM_ID;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final NewsFeed item = getItem(position);
		if (item != null) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(R.layout.news_feed_item, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.mTxtNewsFeedTitle.setText(item.getTitle());
			viewHolder.mTxtNewsFeedDeadline.setText(DateUtils.getDateString(item.getDeadline(), "yyyy-MM-dd"));
			viewHolder.mTxtNewsFeedWriterName.setText(item.getUsername());
			viewHolder.mTxtNewsFeedUpdateTime.setText(DateUtils.getDateString(item.getLstModDt(), "yyyy-MM-dd"));
			viewHolder.mTxtNewsFeedBody.setText(item.getContents().getText());
			viewHolder.mImgNewsFeedImg.setVisibility(View.GONE);
			if(item.getContents().getImg() != null){
				//ImageLoader.getInstance().displayImage(item.getContents().getImg(), viewHolder.mImgNewsFeedImg);
				final ViewHolder finalViewHolder = viewHolder;
				ImageLoader.getInstance().loadImage(item.getContents().getImg(), new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						finalViewHolder.mImgNewsFeedImg.setVisibility(View.VISIBLE);
						finalViewHolder.mImgNewsFeedImg.setImageBitmap(loadedImage);
					}
				});
			}
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
	 * This class contains all butterknife-injected Views & Layouts from layout file 'news_feed_item.xml'
	 * for easy to all layout elements.
	 *
	 * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
	 */
	static class ViewHolder {
		@InjectView(R.id.txt_news_feed_title)
		TextView mTxtNewsFeedTitle;
		@InjectView(R.id.txt_news_feed_deadline)
		TextView mTxtNewsFeedDeadline;
		@InjectView(R.id.txt_news_feed_writer_name)
		TextView mTxtNewsFeedWriterName;
		@InjectView(R.id.txt_news_feed_update_time)
		TextView mTxtNewsFeedUpdateTime;
		@InjectView(R.id.txt_news_feed_body)
		TextView mTxtNewsFeedBody;
		@InjectView(R.id.img_news_feed_img)
		ImageView mImgNewsFeedImg;
		@InjectView(R.id.btn_news_feed_like)
		Button mBtnNewsFeedLike;
		@InjectView(R.id.btn_news_feed_reply)
		Button mBtnNewsFeedReply;

		ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
