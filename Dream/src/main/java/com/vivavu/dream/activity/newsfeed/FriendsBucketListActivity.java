package com.vivavu.dream.activity.newsfeed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.adapter.newsfeed.FriendsBucketListAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.connector.FriendsBucketConnector;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FriendsBucketListActivity extends BaseActionBarActivity {

	public static final String EXTRA_KEY_USER_ID = "userId";
	private static final int REQUEST_TIMELINE_VIEW = 0;
	private static final int REQUEST_BUCKET_ADD  = 1;
	static public final int SEND_REFRESH_START = 0;
	static public final int SEND_REFRESH_STOP = 1;
	static public final int SEND_BUKET_LIST_UPDATE = 2;
	private static final int FETCH_FRIENDS_BUCKET_DATA_FAIL = 3;

	@InjectView(R.id.menu_previous)
	ImageButton mMenuPrevious;
	@InjectView(R.id.txt_title)
	TextView mTxtTitle;
	@InjectView(R.id.menu_more)
	ImageButton mMenuMore;
	@InjectView(R.id.grid_bucket_list)
	GridView mGridBucketList;

	private FriendsBucketListAdapter bucketListAdapter;
	int friendsUserId;

	protected final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SEND_REFRESH_START:
					progressDialog.show();
					break;
				case SEND_REFRESH_STOP:
					updateContents((List<Bucket>) msg.obj);
					progressDialog.dismiss();
					break;
				case SEND_BUKET_LIST_UPDATE:
					updateContents((List<Bucket>) msg.obj);
					progressDialog.dismiss();
					break;
				case FETCH_FRIENDS_BUCKET_DATA_FAIL:
					Toast.makeText(FriendsBucketListActivity.this, getString(R.string.txt_timeline_bucket_info_update_fail), Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
					finish();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
		setContentView(R.layout.activity_friends_bucket_list);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(R.layout.actionbar_friends_bucket_list);
		actionBar.setDisplayShowCustomEnabled(true);

		ButterKnife.inject(this);

		Intent data = getIntent();

		friendsUserId = data.getIntExtra(EXTRA_KEY_USER_ID, -1);

		initEvent();

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.in_progress));

		Thread thread = new Thread(new NetworkThread());
		thread.start();

	}

	private void updateContents(List<Bucket> bucketList) {
		if (bucketListAdapter == null) {
			bucketListAdapter = new FriendsBucketListAdapter(this);
			mGridBucketList.setAdapter(bucketListAdapter);
			bucketListAdapter.setOnBucketImageViewClickListener(new FriendsBucketListAdapter.OnBucketImageViewClick() {
				@Override
				public void onItemClick(View view, int position, long id) {
					Intent intent = new Intent();
					intent.setClass(FriendsBucketListActivity.this, TimelineActivity.class);
					intent.putExtra(TimelineActivity.extraKey, (int)id);
					intent.putExtra(TimelineActivity.extraKeyIsMind, friendsUserId == DreamApp.getInstance().getUser().getId());
					startActivityForResult(intent, REQUEST_TIMELINE_VIEW);
				}
			});
		}

		bucketListAdapter.setList(bucketList);
		bucketListAdapter.notifyDataSetChanged();
	}

	private void initEvent() {
		mMenuPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	public class NetworkThread implements Runnable {
		@Override
		public void run() {
			handler.sendEmptyMessage(SEND_REFRESH_START);
			FriendsBucketConnector bucketConnector = new FriendsBucketConnector();
			ResponseBodyWrapped<List<Bucket>> result = bucketConnector.getList(FriendsBucketListActivity.this.friendsUserId);
			if (result != null && result.isSuccess()) {
				Message message = handler.obtainMessage(SEND_BUKET_LIST_UPDATE, result.getData());
				handler.sendMessage(message);
				return;
			}
			handler.sendEmptyMessage(FETCH_FRIENDS_BUCKET_DATA_FAIL);
		}
	}
}
