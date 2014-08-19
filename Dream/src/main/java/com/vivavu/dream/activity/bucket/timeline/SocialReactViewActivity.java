package com.vivavu.dream.activity.bucket.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ListView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_react_view);
		ButterKnife.inject(this);

		Intent data = getIntent();
		String facebookFeedId = data.getStringExtra(TimelineListAdapter.EXTRA_KEY_FACEBOOK_FEED_ID);
		final List<SocialReact> socialReactList = new ArrayList<SocialReact>();

		if(facebookFeedId != null){
			final String s = facebookFeedId.split("_")[1];
			List<String> readPermissions = new ArrayList<String>();
			readPermissions.add("publish_actions");
			AccessToken fromExistingAccessToken = AccessToken.createFromExistingAccessToken(DreamApp.getInstance().getFbToken(), null, null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, readPermissions);
			Session.openActiveSessionWithAccessToken(context, fromExistingAccessToken, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if (state.isOpened()) {
						new Request(Session.getActiveSession(), String.format("/%s", s), null, HttpMethod.GET, new Request.Callback() {
							@Override
							public void onCompleted(Response response) {
								if (response != null && response.getGraphObject() != null) {
									GraphObject graphObject = response.getGraphObject();
									JSONObject jsonObject = graphObject.getInnerJSONObject();
									try {
										if(!jsonObject.isNull("likes")) {
											JSONObject likes = jsonObject.getJSONObject("likes");
											JSONArray likesData = likes.getJSONArray("data");
										}
										if(!jsonObject.isNull("comments")) {
											JSONObject comments = jsonObject.getJSONObject("comments");
											if(!comments.isNull("data")) {
												JSONArray commentsData = comments.getJSONArray("data");
												for(int index = 0; index < commentsData.length(); index++){
													JSONObject item = (JSONObject) commentsData.get(index);
													SocialReact socialReact = new SocialReact();
													socialReact.setSocialType(SocialReact.SocialType.FACEBOOK);
													socialReact.setMessage(item.getString("message"));
													socialReact.setName(item.getJSONObject("from").getString("name"));
													Date createdTime = DateUtils.getDateFromString(item.getString("created_time"), "yyyy-MM-dd'T'HH:mm:ssZ", new Date());
													//createdTime = DateUtils.add(createdTime, Calendar.HOUR_OF_DAY, 9);
													socialReact.setCreatedTime(createdTime);
													socialReactList.add(socialReact);
												}
												initList(socialReactList);
											}
										}

									} catch (JSONException e) {
										Log.e(SocialReactViewActivity.class.getName(), e.toString());
									}
								}
							}
						}).executeAsync();
					}
				}
			});
		}
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {

			}
		});
	}

	public void initList(List<SocialReact> socialReactList){
		if(socialReactListAdapter == null) {
			socialReactListAdapter = new SocialReactListAdapter(this);
			mListSocialReply.setAdapter(socialReactListAdapter);
		}
		socialReactListAdapter.setSocialReactList(socialReactList);
		socialReactListAdapter.notifyDataSetChanged();
	}
}
