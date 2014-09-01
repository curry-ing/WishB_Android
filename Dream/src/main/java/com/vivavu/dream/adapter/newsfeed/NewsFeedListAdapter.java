package com.vivavu.dream.adapter.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemViewActivity;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.activity.newsfeed.FriendsBucketListActivity;
import com.vivavu.dream.adapter.CustomBaseAdapter;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.NewsFeed;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.ShadowImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

			View.OnClickListener contentsOnClickListener = null;

			if(item.getType() == NewsFeed.Type.BUCKET){
				contentsOnClickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent();
						i.setClass(mContext, TimelineActivity.class);
						i.putExtra(TimelineActivity.extraKeyIsMind, item.getUserId() == DreamApp.getInstance().getUser().getId());
						i.putExtra(TimelineActivity.extraKey, item.getId());
						mContext.startActivity(i);
					}
				};
			} else {
				contentsOnClickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent();
						i.setClass(mContext, TimelineItemViewActivity.class);
						i.putExtra(TimelineActivity.extraKeyIsMind, item.getUserId() == DreamApp.getInstance().getUser().getId());
						Post post = new Post(item.getLstModDt());
						post.setId(item.getId());
						post.setBucketTitle(item.getTitle());
						i.putExtra(TimelineActivity.extraKeyPost, post);
						mContext.startActivity(i);
					}
				};
			}

			viewHolder.mTxtNewsFeedBody.setOnClickListener(contentsOnClickListener);
			viewHolder.mImgNewsFeedImg.setOnClickListener(contentsOnClickListener);
			viewHolder.mLayoutNewsFeedSocial.setOnClickListener(contentsOnClickListener);
			viewHolder.mBtnNewsFeedReply.setOnClickListener(contentsOnClickListener);
			viewHolder.mBtnNewsFeedLike.setOnClickListener(contentsOnClickListener);
			viewHolder.mTxtNewsFeedWriterName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(item.getUserId() == DreamApp.getInstance().getUser().getId()){
						if(mContext instanceof MainActivity){
							((MainActivity) mContext).changeTab(MainActivity.INDEX_BUCKET_LIST_TAB);
						}
					} else {
						Intent i = new Intent();
						i.setClass(mContext, FriendsBucketListActivity.class);
						i.putExtra("userId", item.getUserId());
						mContext.startActivity(i);
					}
				}
			});
			viewHolder.mTxtNewsFeedTitle.setText(item.getTitle());
			viewHolder.mTxtNewsFeedDeadline.setText(DateUtils.getDateString(item.getDeadline(), "yyyy-MM-dd"));

			String infoMessage;
			if(item.getType() == NewsFeed.Type.BUCKET) {
				if(item.getAction() == NewsFeed.Action.REGISTERED) {
					infoMessage = String.format("%s님이 %s 버킷을 등록하였습니다", item.getUsername(), item.getTitle());
				} else if(item.getAction() == NewsFeed.Action.MODIFIED && item.getActionItems() != null &&item.getActionItems().getStatus() == NewsFeed.Status.COMPLETED){
					infoMessage = String.format("%s님이 %s 버킷을 달성하셨습니다", item.getUsername(), item.getTitle());
				} else {
					infoMessage = String.format("%s님이 %s 버킷을 수정하셨습니다", item.getUsername(), item.getTitle());
				}
			} else {
				if(item.getAction() == NewsFeed.Action.REGISTERED) {
					infoMessage = String.format("%s님이 %s 버킷에 일지를 입력하였습니다", item.getUsername(), item.getTitle());
				} else {
					infoMessage = String.format("%s님이 %s 버킷의 일지를 수정하였습니다", item.getUsername(), item.getTitle());
				}
			}
			//viewHolder.mTxtNewsFeedWriterName.setText(item.getUsername());
			viewHolder.mTxtNewsFeedWriterName.setText(infoMessage);
			Long diffTime = DateUtils.getDiffTime(item.getLstModDt());
			if( diffTime / 60 < 1L){
				//1분 이하
				viewHolder.mTxtNewsFeedUpdateTime.setText("방금전");
			} else if(diffTime / 60 / 60 < 1L){
				// 1시간 이하
				viewHolder.mTxtNewsFeedUpdateTime.setText(String.format("%d분 전", diffTime / 60 ));
			} else if(diffTime / 60 / 60 / 24 < 1L){
				// 24시간
				viewHolder.mTxtNewsFeedUpdateTime.setText(String.format("%d시간 전", diffTime / 60 / 60  ));
			} else {
				viewHolder.mTxtNewsFeedUpdateTime.setText(DateUtils.getDateString(item.getLstModDt(), "yyyy.MM.dd"));
			}

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

			//ImageLoader.getInstance().displayImage(item.getContents().getImg(), viewHolder.mImgNewsFeedImg);
			final ViewHolder finalViewHolder = viewHolder;
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.considerExifParams(true)
					.showImageForEmptyUri(R.drawable.ic_profile_empty)
					.build();

			ImageLoader.getInstance().loadImage(item.getUserProfileImg(), options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					finalViewHolder.mImgNewsFeedWriterImg.setImageBitmap(loadedImage);
				}
			});

			if(item.getFbFeedId() != null){
				viewHolder.mLayoutNewsFeedSocial.setVisibility(View.VISIBLE);
				final ViewHolder finalViewHolder1 = viewHolder;
				convertView.post(new Runnable() {
					@Override
					public void run() {
						final String s = item.getFbFeedId().split("_")[1];
						List<String> readPermissions = new ArrayList<String>();
						readPermissions.add("publish_actions");
						AccessToken fromExistingAccessToken = AccessToken.createFromExistingAccessToken(DreamApp.getInstance().getFbToken(), null, null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, readPermissions);
						Session.openActiveSessionWithAccessToken(mContext, fromExistingAccessToken, new Session.StatusCallback() {
							@Override
							public void call(Session session, SessionState state, Exception exception) {
								if (state.isOpened()) {
									Bundle bundle = new Bundle();
									bundle.putString("fields", "comments{attachment,message,from,created_time},likes{name,pic}");
									new Request(Session.getActiveSession(), String.format("/%s", s), bundle, HttpMethod.GET, new Request.Callback() {
										@Override
										public void onCompleted(Response response) {
											int likesCount = 0;
											int commentsCount = 0;
											if (response != null && response.getGraphObject() != null) {
												GraphObject graphObject = response.getGraphObject();
												JSONObject jsonObject = graphObject.getInnerJSONObject();
												try {
													if (!jsonObject.isNull("likes")) {
														JSONObject likes = jsonObject.getJSONObject("likes");
														if (!likes.isNull("data")) {
															JSONArray likesData = likes.getJSONArray("data");
															likesCount = likesData.length();
														}
													}

													if (!jsonObject.isNull("comments")) {
														JSONObject comments = jsonObject.getJSONObject("comments");
														if (!comments.isNull("data")) {
															JSONArray commentsData = comments.getJSONArray("data");
															commentsCount = commentsData.length();
														}
													}

													finalViewHolder1.mBtnNewsFeedLike.setText(String.format("응원 %d개", likesCount));
													finalViewHolder1.mBtnNewsFeedReply.setText(String.format("댓글 %d개", commentsCount));

												} catch (JSONException e) {
													Log.e(this.getClass().getName(), e.toString());
													finalViewHolder1.mLayoutNewsFeedSocial.setVisibility(View.GONE);
													//finalViewHolder.mFacebookLikesComments.setText(String.format("좋아요 0개 답글 0개"));
												}
											}
										}
									}).executeAsync();
								}
							}
						});
					}
				});
			} else {
				viewHolder.mLayoutNewsFeedSocial.setVisibility(View.GONE);
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
		@InjectView(R.id.img_news_feed_writer_img)
		ShadowImageView mImgNewsFeedWriterImg;
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
		@InjectView(R.id.layout_news_feed_social)
		LinearLayout mLayoutNewsFeedSocial;

		ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
