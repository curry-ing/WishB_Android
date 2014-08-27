package com.vivavu.dream.activity.bucket.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.timeline.SocialReactListAdapter;
import com.vivavu.dream.adapter.bucket.timeline.TimelineListAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.SocialReact;
import com.vivavu.dream.util.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SocialReactViewActivity extends BaseActionBarActivity {

	@InjectView(R.id.list_social_reply)
	ListView mListSocialReply;
	@InjectView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	protected SocialReactListAdapter socialReactListAdapter;
	ViewHolder headerViewHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_react_view);
		ButterKnife.inject(this);
		View view = getLayoutInflater().inflate(R.layout.activity_social_react_view_list_header, null);

		headerViewHolder = new ViewHolder(view);
		view.setTag(headerViewHolder);
		mListSocialReply.addHeaderView(view);

		Intent data = getIntent();
		final String facebookFeedId = data.getStringExtra(TimelineListAdapter.EXTRA_KEY_FACEBOOK_FEED_ID);

		if (facebookFeedId != null) {
			updateSocialReact(facebookFeedId);
		}

		mSwipeRefreshLayout.setColorScheme(R.color.progress_10, R.color.progress_20, R.color.progress_30, R.color.progress_40);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				updateSocialReact(facebookFeedId);

			}
		});
	}

	private void updateSocialReact(String fbFeedId) {
		final String s = fbFeedId.split("_")[1];
		List<String> readPermissions = new ArrayList<String>();
		readPermissions.add("publish_actions");
		AccessToken fromExistingAccessToken = AccessToken.createFromExistingAccessToken(DreamApp.getInstance().getFbToken(), null, null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, readPermissions);
		Session.openActiveSessionWithAccessToken(context, fromExistingAccessToken, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (state.isOpened()) {
					Bundle bundle = new Bundle();
					bundle.putString("fields", "comments{attachment,message,from,created_time},likes{name,pic}");
					new Request(Session.getActiveSession(), String.format("/%s", s), bundle, HttpMethod.GET, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							if (response != null && response.getGraphObject() != null) {
								GraphObject graphObject = response.getGraphObject();
								JSONObject jsonObject = graphObject.getInnerJSONObject();
								try {
									headerViewHolder.mTxtLikesCount.setVisibility(View.GONE);
									if (!jsonObject.isNull("likes")) {
										JSONObject likes = jsonObject.getJSONObject("likes");
										if (!likes.isNull("data")) {
											JSONArray likesData = likes.getJSONArray("data");
											headerViewHolder.mTxtLikesCount.setText(String.format("%d명이 응원하고 있습니다", likesData.length()));
											headerViewHolder.mTxtLikesCount.setVisibility(View.VISIBLE);
										}
									}
									if (!jsonObject.isNull("comments")) {
										JSONObject comments = jsonObject.getJSONObject("comments");
										if (!comments.isNull("data")) {
											JSONArray commentsData = comments.getJSONArray("data");
											List<SocialReact> socialReactList = new ArrayList<SocialReact>();
											for (int index = 0; index < commentsData.length(); index++) {
												JSONObject item = (JSONObject) commentsData.get(index);
												SocialReact socialReact = new SocialReact();
												socialReact.setSocialType(SocialReact.SocialType.FACEBOOK);
												socialReact.setMessage(item.getString("message"));
												socialReact.setName(item.getJSONObject("from").getString("name"));
												Date createdTime = DateUtils.getDateFromString(item.getString("created_time"), "yyyy-MM-dd'T'HH:mm:ssZ", new Date());
												if (!item.isNull("attachment") && !item.getJSONObject("attachment").isNull("media")
														&& !item.getJSONObject("attachment").getJSONObject("media").isNull("image")) {
													JSONObject imageObject = item.getJSONObject("attachment").getJSONObject("media").getJSONObject("image");
													if (!imageObject.isNull("src")) {
														socialReact.setAttachmentUrl(imageObject.getString("src"));
													}
												}
												socialReact.setCreatedTime(createdTime);
												socialReactList.add(socialReact);
											}
											initList(socialReactList);
										}
									}

								} catch (JSONException e) {
									Log.e(SocialReactViewActivity.class.getName(), e.toString());
								} finally {
									mSwipeRefreshLayout.setRefreshing(false);
								}
							}
							mSwipeRefreshLayout.setRefreshing(false);
						}
					}).executeAsync();
				}
			}
		});
	}

	public void initList(List<SocialReact> socialReactList) {
		if (socialReactListAdapter == null) {
			socialReactListAdapter = new SocialReactListAdapter(this);
			mListSocialReply.setAdapter(socialReactListAdapter);
		}
		socialReactListAdapter.setSocialReactList(socialReactList);
		socialReactListAdapter.notifyDataSetChanged();
	}

	/**
	 * This class contains all butterknife-injected Views & Layouts from layout file 'activity_social_react_view_list_header.xml'
	 * for easy to all layout elements.
	 *
	 * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
	 */
	static class ViewHolder {
		@InjectView(R.id.txt_likes_count)
		TextView mTxtLikesCount;

		ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
