package com.vivavu.dream.activity.bucket.timeline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.activity.image.ImageViewActivity;
import com.vivavu.dream.adapter.bucket.timeline.SocialReactListAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SocialReact;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.repository.connector.TimelineConnector;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by yuja on 2014-03-28.
 */
public class TimelineItemViewActivity extends BaseActionBarActivity {
	public static final String TAG = "com.vivavu.dream.activity.bucket.timeline.TimelineItemViewActivity";
	public static final String extraKeyReturnValue = "extraKeyReturnValue";
	public static final int REQUEST_MOD_POST = 0;


	@InjectView(R.id.menu_previous)
	ImageButton mMenuPrevious;
	@InjectView(R.id.txt_title)
	TextView mTxtTitle;
	@InjectView(R.id.menu_more)
	ImageButton mMenuMore;
	@InjectView(R.id.list_item_view)
	ListView mListItemView;
	@InjectView(R.id.content_frame)
	LinearLayout mContentFrame;


	Post post;
	HeaderViewHolder headerViewHolder;
	protected SocialReactListAdapter socialReactListAdapter;

	private static final int SEND_DATA_START = 0;
	private static final int SEND_DATA_DELETE_SUCCESS = 1;
	private static final int SEND_DATA_DELETE_FAIL = 2;

	protected final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SEND_DATA_START:
					progressDialog.show();
					break;
				case SEND_DATA_DELETE_SUCCESS:
					progressDialog.dismiss();
					Toast.makeText(TimelineItemViewActivity.this, getString(R.string.txt_timeline_view_delete_success), LENGTH_LONG).show();
					setResult(RESULT_OK);
					finish();
					break;
				case SEND_DATA_DELETE_FAIL:
					progressDialog.dismiss();
					Toast.makeText(TimelineItemViewActivity.this, getString(R.string.txt_timeline_view_delete_fail), LENGTH_LONG).show();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
		setContentView(R.layout.activity_timeline_item_view);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(R.layout.actionbar_timeline_view);
		actionBar.setDisplayShowCustomEnabled(true);

		ButterKnife.inject(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.in_progress));

		mTxtTitle.setTypeface(getNanumBarunGothicBoldFont());

		Intent data = getIntent();
		Bucket bucket = (Bucket) data.getSerializableExtra(TimelineActivity.extraKeyBucket);

		post = (Post) data.getSerializableExtra(TimelineActivity.extraKeyPost);
		post.setBucketId(bucket.getId());

		mTxtTitle.setText(bucket.getTitle());

		mMenuPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mMenuMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View popupView = getLayoutInflater().inflate(R.layout.popup_menu_timeline_view, null);
				MenuHolder holder = new MenuHolder(popupView);

				PopupWindow mPopupWindow = AndroidUtils.makePopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

				mPopupWindow.setAnimationStyle(0);
				View.OnClickListener deleteListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						removePost();
					}
				};

				holder.mLayoutDelete.setOnClickListener(deleteListener);
				holder.mBtnIcDelete.setOnClickListener(deleteListener);
				holder.mBtnDelete.setOnClickListener(deleteListener);

				View.OnClickListener shareListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						sharedPost();
					}
				};
				holder.mLayoutShare.setOnClickListener(shareListener);
				holder.mBtnIcShare.setOnClickListener(shareListener);
				holder.mBtnShare.setOnClickListener(shareListener);

				mPopupWindow.showAsDropDown(mMenuMore);
			}
		});

		View headerView = getLayoutInflater().inflate(R.layout.activity_timeline_item_view_list_header, null);

		headerViewHolder = new HeaderViewHolder(headerView);
		mListItemView.addHeaderView(headerView);

		bindData(post);
		headerViewHolder.mBtnTimelineEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goEdit();
			}
		});
		headerViewHolder.mIvTimelineImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TimelineItemViewActivity.this, ImageViewActivity.class);
				intent.putExtra(ImageViewActivity.IMAGE_VIEW_DATA_KEY, post.getImgUrl());
				startActivity(intent);
			}
		});


	}

	private void bindData(Post post) {
		headerViewHolder.mTxtPostText.setText(post.getText());
		headerViewHolder.mTxtPostDate.setText(DateUtils.getDateString(post.getContentDt(), "yyyy.MM.dd"));
		headerViewHolder.mTxtPostTime.setText(DateUtils.getDateString(post.getContentDt(), "HH:mm"));
		ImageLoader.getInstance().displayImage(post.getImgUrl(), headerViewHolder.mIvTimelineImage, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				// 이미지가 없을 경우에는 imageview 자체를 안보여줌
				if (loadedImage != null) {
					view.setVisibility(View.VISIBLE);
				} else {
					view.setVisibility(View.GONE);
				}
			}
		});

		final List<SocialReact> socialReactList = new ArrayList<SocialReact>();
		initList(socialReactList);
		String facebookFeedId = post.getFbFeedId();
		if (facebookFeedId != null) {
			headerViewHolder.mLayoutSocialReact.setVisibility(View.VISIBLE);
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
										if (!jsonObject.isNull("likes")) {
											JSONObject likes = jsonObject.getJSONObject("likes");
											if (!likes.isNull("data")) {
												JSONArray likesData = likes.getJSONArray("data");
												headerViewHolder.mTxtSocialLikeCount.setText(String.format("좋아요 %d개", likesData == null ? 0 : likesData.length()));
											}
										}
										if (!jsonObject.isNull("comments")) {
											JSONObject comments = jsonObject.getJSONObject("comments");
											if (!comments.isNull("data")) {
												JSONArray commentsData = comments.getJSONArray("data");
												headerViewHolder.mTxtSocialReplyCount.setText(String.format("답글 %d개", commentsData == null ? 0 : commentsData.length()));
												for (int index = 0; index < commentsData.length(); index++) {
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
		} else {
			headerViewHolder.mLayoutSocialReact.setVisibility(View.GONE);
		}
	}

	public void initList(List<SocialReact> socialReactList) {
		if (socialReactListAdapter == null) {
			socialReactListAdapter = new SocialReactListAdapter(this);
			mListItemView.setAdapter(socialReactListAdapter);
		}
		socialReactListAdapter.setSocialReactList(socialReactList);
		socialReactListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_MOD_POST:
				if (resultCode == RESULT_OK) {
					post = (Post) data.getSerializableExtra(extraKeyReturnValue);
					Intent intent = getIntent();
					intent.putExtra(TimelineActivity.extraKeyPost, post);
					bindData(post);
					setResult(RESULT_OK);
				} else {
					setResult(RESULT_CANCELED);
				}
				return;
		}
	}

	private void removePost() {
		AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
		alertConfirm.setMessage(getString(R.string.txt_timeline_view_confirm_delete_body)).setCancelable(false).setPositiveButton(getString(R.string.confirm_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (post != null && post.getId() != null && post.getId() > 0) {
							Tracker tracker = DreamApp.getInstance().getTracker();
							HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_view_activity)).setAction(getString(R.string.ga_event_action_delete));
							tracker.send(eventBuilder.build());
							Thread thread = new Thread(new PostDeleteThread());
							thread.start();
						} else {
							finish();
						}
					}
				}
		).setNegativeButton(getString(R.string.confirm_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}
		);
		AlertDialog alert = alertConfirm.create();
		alert.show();


	}

	private void sharedPost() {
		Tracker tracker = DreamApp.getInstance().getTracker();
		HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_view_activity)).setAction(getString(R.string.ga_event_action_share_facebook));
		tracker.send(eventBuilder.build());
	}

	private void goEdit() {
		Tracker tracker = DreamApp.getInstance().getTracker();
		HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_view_activity)).setAction(getString(R.string.ga_event_action_edit_item));
		tracker.send(eventBuilder.build());
		Intent intent = getIntent();
		intent.setClass(this, TimelineItemEditActivity.class);
		startActivityForResult(intent, REQUEST_MOD_POST);
	}

	private class PostDeleteThread implements Runnable {

		@Override
		public void run() {

			handler.sendEmptyMessage(SEND_DATA_START);

			if (post != null && post.getId() > 0) {
				TimelineConnector timelineConnector = new TimelineConnector();
				ResponseBodyWrapped<Post> result = timelineConnector.delete(post);

				if (result != null && result.isSuccess()) {
					handler.sendEmptyMessage(SEND_DATA_DELETE_SUCCESS);
				} else if (result != null && result.getResponseStatus() == ResponseStatus.TIMEOUT) {
					defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
				} else {
					handler.sendEmptyMessage(SEND_DATA_DELETE_FAIL);
				}
			}
		}
	}

	/**
	 * This class contains all butterknife-injected Views & Layouts from layout file 'popup_menu_timeline_view.xml'
	 * for easy to all layout elements.
	 *
	 * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
	 */
	static class MenuHolder {
		@InjectView(R.id.layout_delete)
		LinearLayout mLayoutDelete;
		@InjectView(R.id.layout_share)
		LinearLayout mLayoutShare;
		@InjectView(R.id.btn_ic_delete)
		Button mBtnIcDelete;
		@InjectView(R.id.btn_delete)
		Button mBtnDelete;
		@InjectView(R.id.btn_ic_share)
		Button mBtnIcShare;
		@InjectView(R.id.btn_share)
		Button mBtnShare;

		MenuHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	/**
	 * This class contains all butterknife-injected Views & Layouts from layout file 'activity_timeline_item_view_list_header.xml'
	 * for easy to all layout elements.
	 *
	 * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
	 */
	static class HeaderViewHolder {
		@InjectView(R.id.txt_post_date)
		TextView mTxtPostDate;
		@InjectView(R.id.txt_post_time)
		TextView mTxtPostTime;
		@InjectView(R.id.btn_timeline_edit)
		ImageButton mBtnTimelineEdit;
		@InjectView(R.id.txt_post_text)
		TextView mTxtPostText;
		@InjectView(R.id.iv_timeline_image)
		ImageView mIvTimelineImage;
		@InjectView(R.id.txt_social_like_count)
		TextView mTxtSocialLikeCount;
		@InjectView(R.id.txt_social_reply_count)
		TextView mTxtSocialReplyCount;
		@InjectView(R.id.layout_social_react)
		LinearLayout mLayoutSocialReact;
		@InjectView(R.id.container_post_info)
		LinearLayout mContainerPostInfo;

		HeaderViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
