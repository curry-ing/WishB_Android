package com.vivavu.dream.activity.setup;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.notice.NoticeAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.Notice;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.connector.NoticeConnector;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NoticeActivity extends BaseActionBarActivity {

	private static final int FETCH_DATA_START = 0;
	private static final int FETCH_DATA_SUCCESS = 1;
	private static final int FETCH_DATA_FAIL = 2;
	@InjectView(R.id.menu_previous)
	ImageButton mMenuPrevious;
	@InjectView(R.id.txt_title)
	TextView mTxtTitle;
	@InjectView(R.id.list_notice)
	ListView mListNotice;
	@InjectView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	NoticeAdapter noticeAdapter;

	protected final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			List<Notice> notices;
			switch (msg.what){
				case FETCH_DATA_START:
					break;
				case FETCH_DATA_SUCCESS:
					notices = (List<Notice>) msg.obj;
					updateNoticeList(notices);
					mSwipeRefreshLayout.setRefreshing(false);
					break;
				case FETCH_DATA_FAIL:
					mSwipeRefreshLayout.setRefreshing(false);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
		setContentView(R.layout.activity_notice);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.actionbar_notice);

		ButterKnife.inject(this);

		mTxtTitle.setTypeface(getNanumBarunGothicBoldFont());
		mTxtTitle.setTextColor(Color.WHITE);
		mMenuPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mSwipeRefreshLayout.setColorScheme(R.color.progress_10, R.color.progress_20, R.color.progress_30, R.color.progress_40);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 새로고침 이벤트가 발생할 경우 수행되는 코드.
				Thread thread = new Thread(new NoticeRunnable());
				thread.start();
			}
		});


		noticeAdapter = new NoticeAdapter(this);
		mListNotice.setAdapter(noticeAdapter);
		mSwipeRefreshLayout.setRefreshing(true);
		Thread thread = new Thread(new NoticeRunnable());
		thread.start();
	}

	public void updateNoticeList(List<Notice> notices){
		noticeAdapter.setNotices(notices);
		noticeAdapter.notifyDataSetChanged();
	}

	private class NoticeRunnable implements Runnable {

		@Override
		public void run() {
			handler.sendEmptyMessage(FETCH_DATA_START);
			NoticeConnector noticeConnector = new NoticeConnector();
			ResponseBodyWrapped<List<Notice>> noticeResponseBodyWrapped = noticeConnector.getList();
			if(noticeResponseBodyWrapped.isSuccess()){
				DataRepository.saveNotieList(noticeResponseBodyWrapped.getData());
				Message message = handler.obtainMessage(FETCH_DATA_SUCCESS, DataRepository.getNoticeList());
				handler.sendMessage(message);
				return;
			}else if(noticeResponseBodyWrapped.getResponseStatus() == ResponseStatus.TIMEOUT) {
				defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
				return;
			}

			handler.sendEmptyMessage(FETCH_DATA_FAIL);
		}
	}

}
