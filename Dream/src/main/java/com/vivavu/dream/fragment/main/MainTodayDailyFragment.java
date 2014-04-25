package com.vivavu.dream.fragment.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.TodayCalendarActivity;
import com.vivavu.dream.adapter.today.TodayDailyViewAdapter;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.model.bucket.TodayGroup;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 23.
 */
public class MainTodayDailyFragment extends CustomBaseFragment {
    public static String TAG = "com.vivavu.dream.fragment.main.MainTodayDailyFragment";
    static public final int REQUEST_CODE_CHANGE_DAY = 0;

    static public final int OFF_SCREEN_PAGE_LIMIT = 5;
    static public final int SEND_REFRESH_START = 0;
    static public final int SEND_REFRESH_STOP = 1;
    static public final int SEND_BUKET_LIST_UPDATE = 2;
    private static final int SEND_NETWORK_DATA = 3;

    @InjectView(R.id.daily_pager)
    ViewPager mDailyPager;

    private List<TodayGroup> todayGroupList;
    private TodayDailyViewAdapter todayDailyViewAdapter;
    private ProgressDialog progressDialog;

    private int mImageThumbHeightSize;
    private int mImageThumbWidthSize;
    private static final String IMAGE_CACHE_DIR = "thumbs";

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_REFRESH_START:
                    progressDialog.show();
                    break;
                case SEND_BUKET_LIST_UPDATE:

                    updateContents((List<TodayGroup>) msg.obj);
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    public MainTodayDailyFragment() {
        this(new ArrayList<TodayGroup>());
    }

    public MainTodayDailyFragment(List<TodayGroup> todayGroupList) {
        this.todayGroupList = todayGroupList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shelf_daily_list, container, false);
        ButterKnife.inject(this, rootView);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("진행중");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todayDailyViewAdapter = new TodayDailyViewAdapter(this, todayGroupList);
        mDailyPager.setAdapter(todayDailyViewAdapter);
        mDailyPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Thread networkThread = new Thread(new NetworkThread());
        networkThread.start();
    }

    protected void updateContents(List<TodayGroup> obj) {
        todayGroupList.clear();
        todayGroupList.addAll(obj );
        if(todayDailyViewAdapter == null){
            todayDailyViewAdapter = new TodayDailyViewAdapter(this, todayGroupList);
            //mList.setAdapter(todayDailyViewAdapter);
        }
        todayDailyViewAdapter.setTodayGroupList(todayGroupList);
        todayDailyViewAdapter.notifyDataSetChanged();
    }

    public class NetworkThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(SEND_REFRESH_START);
            Date lastDate = DataRepository.lastTodayDate();
            BucketConnector bucketConnector = new BucketConnector();
            String lastTodayDate = null;
            if(lastDate != null ){
                lastTodayDate = DateUtils.getDateString(lastDate, "yyyyMMdd");
            }
            ResponseBodyWrapped<List<Today>> result = bucketConnector.getTodayList(lastTodayDate);
            if(result != null) {
                DataRepository.saveTodays(result.getData());
            }

            handler.post(new DataThread());
        }
    }
    public class DataThread implements Runnable {
        @Override
        public void run() {
            List<TodayGroup> todayGroups = DataRepository.listTodayGroupAndTodayData();
            Message message = handler.obtainMessage(SEND_BUKET_LIST_UPDATE, todayGroups);
            handler.sendMessage(message);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_CHANGE_DAY:
            if(resultCode == Activity.RESULT_OK){
                Date selectedDate = (Date) data.getSerializableExtra(TodayCalendarActivity.selectedDateExtraName);
                Integer selectedIndex =  data.getIntExtra(TodayCalendarActivity.selectedDateIndexExtraName, 0);
                if(selectedDate != null){
                    mDailyPager.setCurrentItem(selectedIndex);
                }
                return;
            }
        }
    }
}
