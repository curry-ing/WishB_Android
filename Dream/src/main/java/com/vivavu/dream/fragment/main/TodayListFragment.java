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
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.today.TodayDailyStickyAdapter;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.model.bucket.TodayPager;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 23.
 */
public class TodayListFragment extends CustomBaseFragment {
    public static String TAG = "com.vivavu.dream.fragment.main.TodayListFragment";
    static public final int REQUEST_CODE_CHANGE_DAY = 0;

    static public final int OFF_SCREEN_PAGE_LIMIT = 5;
    static public final int SEND_REFRESH_START = 0;
    static public final int SEND_REFRESH_STOP = 1;
    static public final int SEND_BUKET_LIST_UPDATE = 2;
    private static final int SEND_NETWORK_DATA = 3;
    @InjectView(R.id.listview)
    StickyGridHeadersGridView mListview;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Today> todayGroupList;

    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
    private Integer lastPageNum = 1;
    NetworkThread networkThread;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_REFRESH_START:
                    break;
                case SEND_BUKET_LIST_UPDATE:
                    updateContents((List<Today>) msg.obj);
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };
    private TodayDailyStickyAdapter todayDailyViewAdapter;

    public TodayListFragment() {
        this.todayGroupList = new ArrayList<Today>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_today_list, container, false);
        ButterKnife.inject(this, rootView);

        networkThread = new NetworkThread();
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

        mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_today_activity)).setAction(getString(R.string.ga_event_action_more_page));
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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        networkThread.setPage(1);
        Thread thread = new Thread(networkThread);
        thread.start();
    }

    protected void updateContents(List<Today> obj) {
        if(lastPageNum == 1){
            todayGroupList.clear();
        }
        todayGroupList.addAll(obj );
        if(todayDailyViewAdapter == null){
            todayDailyViewAdapter = new TodayDailyStickyAdapter(getActivity(), todayGroupList);
            mListview.setAdapter(todayDailyViewAdapter);
        }
        todayDailyViewAdapter.setTodayList(todayGroupList);
        todayDailyViewAdapter.notifyDataSetChanged();
    }

    public class NetworkThread implements Runnable{
        protected Integer page = 1;
        @Override
        public void run() {
            handler.sendEmptyMessage(SEND_REFRESH_START);
            BucketConnector bucketConnector = new BucketConnector();

            ResponseBodyWrapped<TodayPager> result = bucketConnector.getTodayList(page);
            if(result.isSuccess()) {
                lastPageNum = page;
                DataRepository.saveTodays(result.getData().getPageData());
                Message message = handler.obtainMessage(SEND_BUKET_LIST_UPDATE, result.getData().getPageData());
                handler.sendMessage(message);
            } else if(result.getResponseStatus() == ResponseStatus.TIMEOUT){
	            handler.post(new Runnable() {
		            @Override
		            public void run() {
			            Toast.makeText(getActivity(), R.string.server_timeout, Toast.LENGTH_SHORT).show();
		            }
	            });

	            return;
            }
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            default:
                break;
        }
    }
}
