package com.vivavu.dream.fragment.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.adapter.bucket.BucketListAdapter;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class BucketListFragment extends CustomBaseFragment { //} implements PullToRefreshListView.OnRefreshListener<ListView> {
	static public String TAG = "com.vivavu.dream.fragment.main.BucketListFragment";

	private static final int REQUEST_TIMELINE_VIEW = 0;
	private static final int REQUEST_BUCKET_ADD  = 1;
	static public final int SEND_REFRESH_START = 0;
	static public final int SEND_REFRESH_STOP = 1;
	static public final int SEND_BUKET_LIST_UPDATE = 2;
	private static final int SEND_NETWORK_DATA = 3;

	@InjectView(R.id.grid_bucket_list)
	GridView mGridBucketList;
	@InjectView(R.id.btn_add_bucket)
	Button mBtnAddBucket;
	@InjectView(R.id.layout_sub_view_background)
	RelativeLayout mLayoutSubViewBackground;

	private ProgressDialog progressDialog;
	private BucketListAdapter bucketListAdapter;

	protected final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SEND_REFRESH_START:
					progressDialog.show();
					break;
				case SEND_REFRESH_STOP:
					updateContents((List<Bucket>) msg.obj);
					break;
				case SEND_BUKET_LIST_UPDATE:
					updateContents((List<Bucket>) msg.obj);
					progressDialog.dismiss();
					break;
				case SEND_NETWORK_DATA:
					break;
			}
		}
	};

	private void updateContents(List<Bucket> bucketList) {
		if (bucketListAdapter == null) {
			bucketListAdapter = new BucketListAdapter(getActivity(), bucketList);
			mGridBucketList.setAdapter(bucketListAdapter);
			bucketListAdapter.setOnBucketImageViewClickListener(new BucketListAdapter.OnBucketImageViewClick() {
				@Override
				public void onItemClick(View view, int position, long id) {
					goTimelineActivity((int) id);
				}
			});

		}

		bucketListAdapter.setList(bucketList);
		bucketListAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_bucket_list, container, false);
		ButterKnife.inject(this, rootView);

		mBtnAddBucket.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goAddBucket();
			}
		});

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(getString(R.string.in_progress));

		Thread thread = new Thread(new NetworkThread());
		thread.start();

		return rootView;
	}

	public void goTimelineActivity(int bucketId){

		Tracker tracker = DreamApp.getInstance().getTracker();
		HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_list_fragment)).setAction(getString(R.string.ga_event_action_move_timeline));
		tracker.send(eventBuilder.build());

		Intent intent = new Intent();
		intent.setClass(getActivity(), TimelineActivity.class);
		intent.putExtra(TimelineActivity.extraKey, bucketId);
		startActivityForResult(intent, REQUEST_TIMELINE_VIEW);
	}

	private void goAddBucket() {

		Tracker tracker = DreamApp.getInstance().getTracker();
		HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_list_fragment)).setAction(getString(R.string.ga_event_action_add_bucket));
		tracker.send(eventBuilder.build());

		Intent intent;
		intent = new Intent();
		intent.setClass(getActivity(), BucketEditActivity.class);

		startActivityForResult(intent, REQUEST_BUCKET_ADD);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_TIMELINE_VIEW){
			List<Bucket> bucketList = DataRepository.listBucket();
			updateContents(bucketList);
		}

		if(requestCode == REQUEST_BUCKET_ADD){
			if(resultCode == Activity.RESULT_OK){
				List<Bucket> bucketList = DataRepository.listBucket();
				updateContents(bucketList);
			}
		}
	}

	public class NetworkThread implements Runnable {
		@Override
		public void run() {
			handler.sendEmptyMessage(SEND_REFRESH_START);
			BucketConnector bucketConnector = new BucketConnector();
			ResponseBodyWrapped<List<Bucket>> result = bucketConnector.getBucketList();
			if (result != null) {
				DataRepository.saveBuckets(result.getData());
			}

			handler.post(new DataThread());

		}
	}

	public class DataThread implements Runnable {
		@Override
		public void run() {
			List<Bucket> bucketList = DataRepository.listBucket();
			Message message = handler.obtainMessage(SEND_BUKET_LIST_UPDATE, bucketList);
			handler.sendMessage(message);
		}
	}
}
