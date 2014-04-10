package com.vivavu.dream.activity.bucket;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.timeline.TimelineCalendarActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemEditActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemViewActivity;
import com.vivavu.dream.adapter.bucket.timeline.TimelineDailyAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.fragment.bucket.timeline.TimelineFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.connector.TimelineConnector;
import com.vivavu.dream.util.DateUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-27.
 */
public class TimelineActivity extends BaseActionBarActivity {
    public static final String TAG = "com.vivavu.dream.activity.bucket.TimelineActivity";
    public static final String extraKeyBucket = "extraKeyBucket";
    public static final String extraKeyPost = "extraKeyPost";
    public static final String extraKeyWriteDate = "extraKeyWriteDate";
    public static final String extraKey = "bucketId";
    private static final int OFF_SCREEN_PAGE_LIMIT = 1;
    public static final int REQUEST_CALENDAR = 1;
    public static final int REQUEST_ADD_POST = 2;
    private static final int REQUEST_MOD_BUCKET = 3;


    @InjectView(R.id.btn_timeline_title)
    Button mBtnTimelineTitle;
    @InjectView(R.id.btn_bucket_complete)
    Button mBtnBucketComplete;
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.txt_start_date)
    TextView mTxtStartDate;
    @InjectView(R.id.txt_current_date)
    TextView mTxtCurrentDate;
    @InjectView(R.id.txt_end_date)
    TextView mTxtEndDate;
    @InjectView(R.id.container_post_info)
    LinearLayout mContainerPostInfo;
    @InjectView(R.id.daily_timeline)
    ViewPager mDailyTimeline;
    @InjectView(R.id.btn_add_timeline)
    Button mBtnAddTimeline;

    Bucket bucket;
    TimelineMetaInfo timelineMetaInfo;
    TimelineDailyAdapter timelineDailyAdapter;
    private ProgressDialog progressDialog;
    private static final int FETCH_DATA_START = 0;
    private static final int FETCH_DATA_SUCCESS = 1;
    private static final int FETCH_DATA_FAIL = 2;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FETCH_DATA_START:
                    progressDialog.show();
                    break;
                case FETCH_DATA_SUCCESS:
                    timelineMetaInfo = (TimelineMetaInfo) msg.obj;
                    List<Date> dateList = timelineMetaInfo.getDateList();
                    Collections.reverse(dateList);
                    initTimelineContents();
                    progressDialog.dismiss();
                    break;
                case FETCH_DATA_FAIL:
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_timeline);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("진행중");

        ButterKnife.inject(this);

        Intent data = getIntent();
        Integer bucketId = data.getIntExtra(extraKey, -1);
        bucket = DataRepository.getBucket(bucketId);
        bindData(bucket);
        timelineMetaInfo = new TimelineMetaInfo();
        initEvent();

        Thread thread = new Thread(new NetworkThread());
        thread.start();
    }

    private void initTimelineContents() {
        if(timelineDailyAdapter == null) {
            timelineDailyAdapter = new TimelineDailyAdapter(getSupportFragmentManager(), this, timelineMetaInfo);
            mDailyTimeline.setAdapter(timelineDailyAdapter);
            mDailyTimeline.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
        }
        timelineMetaInfo.setBucketId(bucket.getId());
        timelineDailyAdapter.setTimelineMetaInfo(timelineMetaInfo);
        timelineDailyAdapter.notifyDataSetChanged();
    }

    private void bindData(Bucket bucket) {
        mBtnTimelineTitle.setText(bucket.getTitle());
        Date start = bucket.getRegDate();
        Date current = new Date();
        Date end = bucket.getDeadline();
        mTxtStartDate.setText(DateUtils.getDateString(start, "yyyy.MM.dd"));
        mTxtCurrentDate.setText(DateUtils.getDateString(current, "yyyy.MM.dd"));
        mTxtEndDate.setText(DateUtils.getDateString(end, "yyyy.MM.dd"));

        if(start != null && end != null) {
            int progress = DateUtils.getProgress(start, end);
            mProgressBar.setProgress(progress);
        }
    }
    private void initEvent() {
        mBtnTimelineTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimelineActivity.this, BucketAddActivity.class);
                intent.putExtra(BucketAddActivity.RESULT_EXTRA_BUCKET_ID, (Integer) bucket.getId());
                startActivityForResult(intent, REQUEST_MOD_BUCKET);
            }
        });
        mBtnAddTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimelineActivity.this, TimelineItemEditActivity.class);
                intent.putExtra(extraKeyBucket, bucket);
                intent.putExtra(extraKeyPost, new Post(new Date()));
                intent.putExtra(extraKeyWriteDate, new Date());
                startActivityForResult(intent, REQUEST_ADD_POST);
            }
        });
        mDailyTimeline.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TimelineFragment item = (TimelineFragment) timelineDailyAdapter.getItem(position);
                //item.getNetworkData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CALENDAR:
                if(resultCode == Activity.RESULT_OK){
                    Date selectedDate = (Date) data.getSerializableExtra(TimelineCalendarActivity.selectedDateExtraName);
                    Integer selectedIndex =  data.getIntExtra(TimelineCalendarActivity.selectedDateIndexExtraName, 0);
                    if(selectedDate != null){
                        mDailyTimeline.setCurrentItem(selectedIndex, true);
                    }
                    return;
                }
                break;
            case REQUEST_ADD_POST:
                break;
            case REQUEST_MOD_BUCKET:
                if(resultCode == RESULT_OK) {
                    Bucket result = (Bucket) data.getSerializableExtra(BucketAddActivity.RESULT_EXTRA_BUCKET);
                    bindData(result);
                } else if (requestCode == RESULT_USER_DATA_DELETED) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    public void viewPost(Post post){
        Intent intent = new Intent(this, TimelineItemViewActivity.class);
        intent.putExtra(TimelineActivity.extraKeyBucket, bucket);
        intent.putExtra(TimelineActivity.extraKeyPost, post);
        startActivity(intent);
    }

    private class NetworkThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(FETCH_DATA_START);

            TimelineConnector timelineConnector = new TimelineConnector();
            ResponseBodyWrapped<TimelineMetaInfo> result = timelineConnector.getTimelineMetaInfo(bucket.getId());

            if(result.isSuccess()) {
                Message message = handler.obtainMessage(FETCH_DATA_SUCCESS, result.getData());
                handler.sendMessage(message);
            }else {
                handler.sendEmptyMessage(FETCH_DATA_FAIL);
            }
        }
    }
}
