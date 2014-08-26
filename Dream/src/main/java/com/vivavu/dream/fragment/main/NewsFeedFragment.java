package com.vivavu.dream.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.newsfeed.NewsFeedListAdapter;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.NewsFeed;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.connector.NewsFeedConnector;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class NewsFeedFragment extends CustomBaseFragment { //} implements PullToRefreshListView.OnRefreshListener<ListView> {
	static public String TAG = "com.vivavu.dream.fragment.main.NewsFeedFragment";

	private static final int REQUEST_TIMELINE_VIEW = 0;
	private static final int REQUEST_BUCKET_ADD = 1;
	static public final int SEND_REFRESH_START = 0;
	static public final int SEND_REFRESH_STOP = 1;
	static public final int SEND_REFRESH_FAIL = 4;
	static public final int SEND_LIST_UPDATE = 2;
	private static final int SEND_NETWORK_DATA = 3;

	@InjectView(R.id.news_feed_list)
	ListView mNewsFeedList;
	@InjectView(R.id.layout_news_feed_background)
	RelativeLayout mLayoutNewsFeedBackground;

	protected int newsfeedPage = 1;
	protected int lastPageNum = 1;
	@InjectView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	private NewsFeedListAdapter newsFeedListAdapter;
	private NetworkThread networkThread;

	protected final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SEND_REFRESH_START:
					break;
				case SEND_REFRESH_STOP:
					updateContents((List<NewsFeed>) msg.obj);
					mSwipeRefreshLayout.setRefreshing(false);
					break;
				case SEND_LIST_UPDATE:
					updateContents((List<NewsFeed>) msg.obj);
					mSwipeRefreshLayout.setRefreshing(false);
					break;
				case SEND_REFRESH_FAIL:
					mSwipeRefreshLayout.setRefreshing(false);
					break;
				case SEND_NETWORK_DATA:
					break;
			}
		}
	};
	private boolean lastitemVisibleFlag = false;

	private void updateContents(List<NewsFeed> newsFeedList) {
		if (newsFeedListAdapter == null) {
			newsFeedListAdapter = new NewsFeedListAdapter(getActivity());
			mNewsFeedList.setAdapter(newsFeedListAdapter);
		}
		if(lastPageNum == 1){
			newsFeedListAdapter.clearList();
		}
		newsFeedListAdapter.addList(newsFeedList);
		newsFeedListAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_news_feed_list, container, false);
		ButterKnife.inject(this, rootView);

		networkThread = new NetworkThread(newsfeedPage);

		mSwipeRefreshLayout.setColorScheme(R.color.progress_10, R.color.progress_20, R.color.progress_30, R.color.progress_40);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 새로고침 이벤트가 발생할 경우 수행되는 코드.
				networkThread.setPage(1);
				Thread thread = new Thread(networkThread);
				thread.start();
			}
		});

		mNewsFeedList.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
				//즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
				if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
					//TODO 화면이 바닦에 닿을때 처리
					// 맨 밑으로 내려가면 데이터를 더 들고오게 한다.
					mSwipeRefreshLayout.setRefreshing(true);
					networkThread.setPage(lastPageNum + 1);

					Tracker tracker = DreamApp.getInstance().getTracker();
					HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_activity)).setAction(getString(R.string.ga_event_action_more_page));
					eventBuilder.setValue(networkThread.getPage());
					tracker.send(eventBuilder.build());

					Thread thread = new Thread(networkThread);
					thread.start();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
				lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);

			}
		});

		Thread thread = new Thread(networkThread);
		thread.start();

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	public class NetworkThread implements Runnable {
		protected int page = 1;

		public NetworkThread(int page) {
			this.page = page;
		}

		@Override
		public void run() {
			handler.sendEmptyMessage(SEND_REFRESH_START);
			NewsFeedConnector newsFeedConnector = new NewsFeedConnector();
			ResponseBodyWrapped<List<NewsFeed>> result = newsFeedConnector.getList(page);

			if (result != null && result.isSuccess()) {
				lastPageNum = page;
				Message message = handler.obtainMessage(SEND_LIST_UPDATE, result.getData());
				handler.sendMessage(message);
			}

			handler.sendEmptyMessage(SEND_REFRESH_FAIL);
		}

		public int getPage() {
			return page;
		}

		synchronized public void setPage(int page) {
			this.page = page;
		}
	}

	public class DataThread implements Runnable {
		@Override
		public void run() {
			List<Bucket> bucketList = DataRepository.listBucket();
			Message message = handler.obtainMessage(SEND_LIST_UPDATE, bucketList);
			handler.sendMessage(message);
		}
	}
}
