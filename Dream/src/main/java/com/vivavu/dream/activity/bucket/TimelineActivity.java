package com.vivavu.dream.activity.bucket;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.timeline.TimelineCalendarActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemEditActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemViewActivity;
import com.vivavu.dream.adapter.bucket.timeline.TimelineListAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.enums.RepeatType;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.option.OptionRepeat;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.model.bucket.timeline.Timeline;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.connector.TimelineConnector;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.TextImageView;

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


    Bucket bucket;
    TimelineMetaInfo timelineMetaInfo;
    TimelineListAdapter timelineListAdapter;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.txt_bucket_title)
    TextView mTxtBucketTitle;
    @InjectView(R.id.txt_bucket_deadline)
    TextView mTxtBucketDeadline;
    @InjectView(R.id.txt_bucket_remain)
    TextView mTxtBucketRemain;
    @InjectView(R.id.btn_achieve)
    ImageButton mBtnAchieve;
    @InjectView(R.id.btn_edit)
    ImageButton mBtnEdit;
    @InjectView(R.id.img_bucket)
    TextImageView mImgBucket;
    @InjectView(R.id.txt_bucket_description)
    TextView mTxtBucketDescription;
    @InjectView(R.id.layout_bucket_default_info)
    LinearLayout mLayoutBucketDefaultInfo;
    @InjectView(R.id.btn_add_post)
    Button mBtnAddPost;
    @InjectView(R.id.list_timeline)
    ListView mListTimeline;
    @InjectView(R.id.layout_timeline_info)
    RelativeLayout mLayoutTimelineInfo;
    @InjectView(R.id.layout_bucket_default_info_repeat_week)
    LinearLayout mLayoutBucketDefaultInfoRepeatWeek;
    @InjectView(R.id.layout_bucket_default_info_repeat_month)
    LinearLayout mLayoutBucketDefaultInfoRepeatMonth;
    @InjectView(R.id.btn_bucket_option_mon)
    Button mBtnBucketOptionMon;
    @InjectView(R.id.btn_bucket_option_tue)
    Button mBtnBucketOptionTue;
    @InjectView(R.id.btn_bucket_option_wen)
    Button mBtnBucketOptionWen;
    @InjectView(R.id.btn_bucket_option_thu)
    Button mBtnBucketOptionThu;
    @InjectView(R.id.btn_bucket_option_fri)
    Button mBtnBucketOptionFri;
    @InjectView(R.id.btn_bucket_option_sat)
    Button mBtnBucketOptionSat;
    @InjectView(R.id.btn_bucket_option_sun)
    Button mBtnBucketOptionSun;
    @InjectView(R.id.btn_bucket_option_month)
    View mBtnBucketOptionMonth;
    @InjectView(R.id.btn_bucket_option_week)
    View mBtnBucketOptionWeek;
    @InjectView(R.id.txt_bucket_option_repeat_cnt)
    TextView mTxtBucketOptionRepeatCnt;

    private ProgressDialog progressDialog;
    private static final int FETCH_DATA_START = 0;
    private static final int FETCH_DATA_SUCCESS = 1;
    private static final int FETCH_DATA_FAIL = 2;
    private static final int UPDATE_BUCKET_DATA_SUCCESS = 3;
    private static final int UPDATE_BUCKET_DATA_FAIL = 4;

    private Timeline timeline;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FETCH_DATA_START:
                    progressDialog.show();
                    break;
                case FETCH_DATA_SUCCESS:
                    timeline = (Timeline) msg.obj;
                    List<Post> dataList = timeline.getTimelineData();
                    initTimelineContents(dataList);
                    progressDialog.dismiss();
                    break;
                case FETCH_DATA_FAIL:
                    progressDialog.dismiss();
                    break;
                case UPDATE_BUCKET_DATA_SUCCESS:
                    bucket = (Bucket) msg.obj;
                    bindData(bucket);
                    progressDialog.dismiss();
                    break;
                case UPDATE_BUCKET_DATA_FAIL:
                    Toast.makeText(TimelineActivity.this, "버킷 정보 갱신에 실패하였습니다.", Toast.LENGTH_SHORT ).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_timeline);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
/*
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_timeline);
*/

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("진행중");

        ButterKnife.inject(this);

        mTxtBucketDeadline.setTypeface(getDenseRegularFont());
        mTxtBucketRemain.setTypeface(getDenseRegularFont());
        mTxtBucketDescription.setTypeface(getNanumBarunGothicFont());

        Intent data = getIntent();
        Integer bucketId = data.getIntExtra(extraKey, -1);
        bucket = DataRepository.getBucket(bucketId);
        bindData(bucket);
        timelineMetaInfo = new TimelineMetaInfo();
        initEvent();

        Thread thread = new Thread(new TimelineThread());
        thread.start();
    }

    private void initTimelineContents(List<Post> dataList) {
        if(timelineListAdapter == null) {
            timelineListAdapter = new TimelineListAdapter(this);
            mListTimeline.setAdapter(timelineListAdapter);
        }
        timelineMetaInfo.setBucketId(bucket.getId());
        timelineListAdapter.setTimelineMetaInfo(timelineMetaInfo);
        timelineListAdapter.setPostList(dataList);
        timelineListAdapter.notifyDataSetChanged();
    }

    private void bindData(Bucket bucket) {
        mTxtBucketTitle.setText(bucket.getTitle());
        Date start = bucket.getRegDate();
        Date end = bucket.getDeadline();
        mTxtBucketDeadline.setText(DateUtils.getDateString(end, "yyyy.MM.dd"));
        Long remainDay = DateUtils.getRemainDay(end);
        if(remainDay >= 0) {
            mTxtBucketRemain.setText(String.format("D - %05d", remainDay));
        }else{
            mTxtBucketRemain.setText(String.format("D + %05d", Math.abs(remainDay)));
        }

        if(bucket.getStatus() == 0) {
            mBtnAchieve.setSelected(false);
        } else {
            mBtnAchieve.setSelected(true);
        }

        int progress = DateUtils.getProgress(start, end);
        Log.v(TAG, String.valueOf( AndroidUtils.getSpFromPx(65)));
        ImageLoader.getInstance().displayImage(bucket.getCvrImgUrl(), mImgBucket);

        mTxtBucketDescription.setText(bucket.getDescription());
        OptionRepeat repeat = new OptionRepeat(RepeatType.fromCode(bucket.getRptType()), bucket.getRptCndt());
        if(repeat.getRepeatType() == RepeatType.WKRP ){
            mLayoutBucketDefaultInfoRepeatWeek.setVisibility(View.VISIBLE);
            mLayoutBucketDefaultInfoRepeatMonth.setVisibility(View.GONE);

            mBtnBucketOptionSun.setBackgroundResource(repeat.isSun() ? R.drawable.ic_week_sun_release : R.drawable.ic_week_sun_press);
            mBtnBucketOptionMon.setBackgroundResource(repeat.isMon() ? R.drawable.ic_week_mon_release : R.drawable.ic_week_mon_press);
            mBtnBucketOptionTue.setBackgroundResource(repeat.isTue() ? R.drawable.ic_week_tue_release : R.drawable.ic_week_tue_press);
            mBtnBucketOptionWen.setBackgroundResource(repeat.isWen() ? R.drawable.ic_week_wen_release : R.drawable.ic_week_wen_press);
            mBtnBucketOptionThu.setBackgroundResource(repeat.isThu() ? R.drawable.ic_week_thu_release : R.drawable.ic_week_thu_press);
            mBtnBucketOptionFri.setBackgroundResource(repeat.isFri() ? R.drawable.ic_week_fri_release : R.drawable.ic_week_fri_press);
            mBtnBucketOptionSat.setBackgroundResource(repeat.isSat() ? R.drawable.ic_week_sat_release : R.drawable.ic_week_sat_press);

        } else if(repeat.getRepeatType() == RepeatType.WEEK ){
            mLayoutBucketDefaultInfoRepeatWeek.setVisibility(View.GONE);
            mLayoutBucketDefaultInfoRepeatMonth.setVisibility(View.VISIBLE);

            mBtnBucketOptionWeek.setBackgroundResource(R.drawable.ic_option_week_release);
            mBtnBucketOptionMonth.setBackgroundResource(R.drawable.ic_option_month_press);
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(repeat.getRepeatCount()));
        } else if(repeat.getRepeatType() == RepeatType.MNTH ){
            mLayoutBucketDefaultInfoRepeatWeek.setVisibility(View.GONE);
            mLayoutBucketDefaultInfoRepeatMonth.setVisibility(View.VISIBLE);

            mBtnBucketOptionWeek.setBackgroundResource(R.drawable.ic_option_week_press);
            mBtnBucketOptionMonth.setBackgroundResource(R.drawable.ic_option_month_release);
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(repeat.getRepeatCount()));
        }
    }

    private void initEvent() {
        mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimelineActivity.this, BucketEditActivity.class);
                intent.putExtra(BucketEditActivity.RESULT_EXTRA_BUCKET_ID, (Integer) bucket.getId());
                startActivityForResult(intent, REQUEST_MOD_BUCKET);
            }
        });

        mBtnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimelineActivity.this, TimelineItemEditActivity.class);
                intent.putExtra(extraKeyBucket, bucket);
                intent.putExtra(extraKeyPost, new Post(new Date()));
                intent.putExtra(extraKeyWriteDate, new Date());
                startActivityForResult(intent, REQUEST_ADD_POST);
            }
        });

        mBtnAchieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bucket.getStatus() == 0) {
                    bucket.setStatus(1);
                } else {
                    bucket.setStatus(0);
                }

                Thread thread = new Thread(new BucketThread());
                thread.start();
            }
        });
        mListTimeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = mListTimeline.getAdapter().getItem(position);
                if(item instanceof Post) {
                    viewPost((Post) item);
                }
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
                        //mDailyTimeline.setCurrentItem(selectedIndex, true);
                    }
                    return;
                }
                break;
            case REQUEST_ADD_POST:
                break;
            case REQUEST_MOD_BUCKET:
                if(resultCode == RESULT_OK) {
                    Bucket result = (Bucket) data.getSerializableExtra(BucketEditActivity.RESULT_EXTRA_BUCKET);
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

    private class TimelineThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(FETCH_DATA_START);

            TimelineConnector timelineConnector = new TimelineConnector();
            ResponseBodyWrapped<Timeline> result = timelineConnector.getTimelineAll(bucket.getId());

            if(result.isSuccess()) {
                Message message = handler.obtainMessage(FETCH_DATA_SUCCESS, result.getData());
                handler.sendMessage(message);
            }else {
                handler.sendEmptyMessage(FETCH_DATA_FAIL);
            }
        }
    }

    private class BucketThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(FETCH_DATA_START);

            BucketConnector bucketConnector = new BucketConnector();
            ResponseBodyWrapped<Bucket> result = bucketConnector.updateBucketInfo(bucket);

            if(result.isSuccess()) {
                Message message = handler.obtainMessage(UPDATE_BUCKET_DATA_SUCCESS, result.getData());
                handler.sendMessage(message);
            }else {
                handler.sendEmptyMessage(UPDATE_BUCKET_DATA_FAIL);
            }
        }
    }
}
