package com.vivavu.dream.fragment.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketAdapter2;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;

import java.util.ArrayList;
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

    private int mShortAnimationDuration, mMediumAnimationDuration, mLongAnimationDuration;

    @InjectView(R.id.main_pager)
    ViewPager mMainPager;
    @InjectView(R.id.main_pager_bg0)
    ImageView mMainPageBg0;
    @InjectView(R.id.main_pager_bg1)
    ImageView mMainPageBg1;




    private List<BucketGroup> bucketGroupList;
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
        mMainPager.setOnPageChangeListener(new MainViewPageChangeListener());
        mMainPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
        mMainPager.setCurrentItem(DreamApp.getInstance().getUser().getUserAge()/10);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mLongAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Thread thread = new Thread(new DataThread());
//        thread.start();
    }

    public void updateContents(List<BucketGroup> obj){
        bucketGroupList.clear();
        bucketGroupList.addAll(obj);
        if(bucketAdapter2 == null) {
            bucketAdapter2 = new BucketAdapter2(this, bucketGroupList);
        }
        bucketAdapter2.setBucketGroupList(bucketGroupList);
        bucketAdapter2.notifyDataSetChanged();
        mMainPager.setCurrentItem(DreamApp.getInstance().getUser().getUserAge()/10);
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


    public class MainViewPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        private int currPage;

        @Override
        public void onPageSelected(int position){
            BitmapDrawable toBg = null;
            if(position == 0) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg00);
            } else if (position == 1) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg10);
            } else if (position == 2) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg20);
            } else if (position == 3) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg30);
            } else if (position == 4) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg40);
            } else if (position == 5) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg50);
            } else if (position == 6) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg60);
            } else {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg00);
            }
            if ((position+1)%2 == 1 ) {
                mMainPageBg0.setAlpha(0f);
                mMainPageBg0.setVisibility(View.VISIBLE);
                mMainPageBg0.setBackground(toBg);

                mMainPageBg0.animate().alpha(1f).setDuration(mMediumAnimationDuration).setListener(null);
                mMainPageBg1.animate().alpha(0f).setDuration(mLongAnimationDuration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mMainPageBg1.setVisibility(View.GONE);
                    }
                });
            } else {
                mMainPageBg1.setAlpha(0f);
                mMainPageBg1.setVisibility(View.VISIBLE);
                mMainPageBg1.setBackground(toBg);

                mMainPageBg1.animate().alpha(1f).setDuration(mMediumAnimationDuration).setListener(null);
                mMainPageBg0.animate().alpha(0f).setDuration(mLongAnimationDuration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mMainPageBg0.setVisibility(View.GONE);
                    }
                });
            }

            currPage = position;
        }

        public final int getCurrPage(){
            return currPage;
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
