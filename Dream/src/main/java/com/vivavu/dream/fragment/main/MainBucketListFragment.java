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
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.TodayCalendarActivity;
import com.vivavu.dream.adapter.bucket.BucketAdapter;
import com.vivavu.dream.adapter.bucket.BucketAdapter2;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.NetworkUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class MainBucketListFragment extends CustomBaseFragment { //} implements PullToRefreshListView.OnRefreshListener<ListView> {
    static public String TAG = "com.vivavu.dream.fragment.main.MainBucketListFragment";
    static public final int REQUEST_CODE_CHANGE_DAY = 0;
    static public final int SEND_REFRESH_START = 0;
    static public final int SEND_REFRESH_STOP = 1;
    static public final int SEND_BUKET_LIST_UPDATE = 2;
    private static final int SEND_NETWORK_DATA = 3;
    static public final int OFF_SCREEN_PAGE_LIMIT = 5;

    @InjectView(R.id.main_pager)
    ViewPager mMainPager;

    private List<BucketGroup> bucketGroupList;
//    private BucketAdapter bucketAdapter;
    private BucketAdapter2 bucketAdapter2;
    private ProgressDialog progressDialog;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_REFRESH_START:
                    progressDialog.show();
                    break;
                case SEND_REFRESH_STOP:
                    updateContents((List<BucketGroup>) msg.obj);
                    break;
                case SEND_BUKET_LIST_UPDATE:
                    updateContents((List<BucketGroup>) msg.obj);
                    progressDialog.dismiss();
                    break;
                case SEND_NETWORK_DATA:
                    break;
            }
        }
    };

    public MainBucketListFragment() {
        bucketGroupList = new ArrayList<BucketGroup>();
    }

    public MainBucketListFragment(List<BucketGroup> bucketGroupList) {
        this.bucketGroupList = bucketGroupList;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread thread = new Thread(new NetworkThread());
        thread.start();
        final View rootView = inflater.inflate(R.layout.main_row, container, false);
        ButterKnife.inject(this, rootView);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("진행중");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bucketAdapter2 = new BucketAdapter2(this, bucketGroupList);
        mMainPager.setAdapter(bucketAdapter2);
        mMainPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Thread thread = new Thread(new DataThread());
        thread.start();
    }

    public void updateContents(List<BucketGroup> obj){
        bucketGroupList.clear();
        bucketGroupList.addAll(obj);
        if(bucketAdapter2 == null) {
            bucketAdapter2 = new BucketAdapter2(this, bucketGroupList);
        }
        bucketAdapter2.setBucketGroupList(bucketGroupList);
        bucketAdapter2.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bucketAdapter2 != null) {
            bucketAdapter2.notifyDataSetChanged();
        }
    }

//    @Override
//    public void onRefresh(final PullToRefreshBase<ListView> listViewPullToRefreshBase) {
//        if(NetworkUtil.isAvaliableNetworkAccess(DreamApp.getInstance())) {
//            Thread thread = new Thread(new NetworkThread());
//            thread.start();
//        }else {
//            Toast.makeText(getActivity(), getText(R.string.no_network_connection_toast), Toast.LENGTH_SHORT).show();
//            mList.onRefreshComplete();
//        }
//    }


    public class NetworkThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(SEND_REFRESH_START);
            BucketConnector bucketConnector = new BucketConnector();
            ResponseBodyWrapped<List<Bucket>> result = bucketConnector.getBucketList();
            if(result != null) {
                DataRepository.saveBuckets(result.getData());
            }

            handler.post(new DataThread());

        }
    }
    public class DataThread implements Runnable {
        @Override
        public void run() {
            List<BucketGroup> bucketGroup = DataRepository.listBucketGroup();
            Message message = handler.obtainMessage(SEND_BUKET_LIST_UPDATE, bucketGroup);
            handler.sendMessage(message);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case REQUEST_CODE_CHANGE_DAY:
//                if(resultCode == Activity.RESULT_OK){
//                    Date selectedDate = (Date) data.getSerializableExtra(TodayCalendarActivity.selectedDateExtraName);
//                    Integer selectedIndex =  data.getIntExtra(TodayCalendarActivity.selectedDateIndexExtraName, 0);
//                    if(selectedDate != null){
//                        mMainPager.setCurrentItem(selectedIndex);
//                    }
//                    return;
//                }
//        }
//    }
}
