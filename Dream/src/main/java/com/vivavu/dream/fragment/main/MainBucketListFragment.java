package com.vivavu.dream.fragment.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.adapter.bucket.BucketAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
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

import static com.vivavu.dream.common.DreamApp.getInstance;

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
    static public final int OFF_SCREEN_PAGE_LIMIT = 3;

    private int mShortAnimationDuration, mMediumAnimationDuration, mLongAnimationDuration;
    protected int mSdkVersion = Build.VERSION.SDK_INT;
    protected int currentPage = -1;



    @InjectView(R.id.main_pager)
    ViewPager mMainPager;
    @InjectView(R.id.main_pager_bg0)
    ImageView mMainPageBg0;
    @InjectView(R.id.main_pager_bg1)
    ImageView mMainPageBg1;

    private List<BucketGroup> bucketGroupList;
    private BucketAdapter bucketAdapter;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_row, container, false);
        ButterKnife.inject(this, rootView);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.in_progress));

        Thread thread = new Thread(new NetworkThread());
        thread.start();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bucketAdapter = new BucketAdapter(this, bucketGroupList);
        mMainPager.setAdapter(bucketAdapter);
        if (currentPage < 0 && DreamApp.getInstance().getUser().getUserAge() == 0) {
            currentPage = 0;
            if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                mMainPageBg0.setBackgroundDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0));
            } else {
                mMainPageBg0.setBackground((BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0));
            }
        } else if(currentPage < 0 ) {
            currentPage = getInstance().getUser().getUserAge() / 10;
        }
        mMainPager.setOnPageChangeListener(new MainViewPageChangeListener());
        mMainPager.setCurrentItem(currentPage, false);
        mMainPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Code.ACT_ADD_BUCKET:
                if(resultCode == Activity.RESULT_OK) {
                    Integer bucketRange = data.getIntExtra(BucketEditActivity.RESULT_EXTRA_BUCKET_RANGE, -1);

                    if (bucketRange > 0) {
                        mMainPager.setCurrentItem(bucketRange / 10, false);
                    }
                    handler.post(new DataThread());
                }
                break;
            case Code.ACT_VIEW_BUCKET_GROUP:
                if(resultCode == Activity.RESULT_OK || resultCode == BaseActionBarActivity.RESULT_USER_DATA_MODIFIED) {
                    if(data != null) {
                        Integer bucketRange = data.getIntExtra(BucketEditActivity.RESULT_EXTRA_BUCKET_RANGE, -1);

                        if (bucketRange > 0) {
                            mMainPager.setCurrentItem(bucketRange / 10, false);
                        } else if (bucketRange < 0) {
                            mMainPager.setCurrentItem(0, false);
                        }
                    }
                    if (resultCode == BaseActionBarActivity.RESULT_USER_DATA_MODIFIED) {
                        handler.post(new DataThread());
                    }
                }
                break;
        }
    }

    public void updateContents(List<BucketGroup> obj){
        bucketGroupList.clear();
        bucketGroupList.addAll(obj);
        if(bucketAdapter == null) {
            bucketAdapter = new BucketAdapter(this, bucketGroupList);
        }
        bucketAdapter.setBucketGroupList(bucketGroupList);
        bucketAdapter.notifyDataSetChanged();
        if (currentPage < 0 && DreamApp.getInstance().getUser().getUserAge() == 0) {
            currentPage= 0;

            if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                mMainPageBg0.setBackgroundDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0));
            } else {
                mMainPageBg0.setBackground((BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0));
            }
        } else if(currentPage < 0 ){
            currentPage = getInstance().getUser().getUserAge() / 10;
        }

        mMainPager.setCurrentItem(currentPage);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bucketAdapter != null) {
            bucketAdapter.notifyDataSetChanged();
        }
    }

    public int getViewPagerPage(){
        return mMainPager.getCurrentItem();
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
        public void onPageSelected(final int position){
            Tracker tracker = DreamApp.getInstance().getTracker();
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_main_activity)).setAction(getString(R.string.ga_event_action_swipe_page));
            tracker.send(eventBuilder.build());
            BitmapDrawable toBg = null;
            if(position == 0) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0);
            } else if (position == 1) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg1);
            } else if (position == 2) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg2);
            } else if (position == 3) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg3);
            } else if (position == 4) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg4);
            } else if (position == 5) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg5);
            } else if (position == 6) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg6);
            } else {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0);
            }
            if ((position+1)%2 == 1 ) {
                mMainPageBg0.setAlpha(0.5f);
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mMainPageBg0.setBackgroundDrawable(toBg);
                } else {
                    mMainPageBg0.setBackground(toBg);
                }

                mMainPageBg0.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
                mMainPageBg1.animate().alpha(0f).setDuration(mShortAnimationDuration).setListener(null);
            } else {
                mMainPageBg1.setAlpha(0.5f);
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mMainPageBg1.setBackgroundDrawable(toBg);
                } else {
                    mMainPageBg1.setBackground(toBg);
                }

                mMainPageBg1.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
                mMainPageBg0.animate().alpha(0f).setDuration(mShortAnimationDuration).setListener(null);
            }

            currPage = position;
            MainBucketListFragment.this.currentPage = position;
        }

    }

}
