package com.vivavu.dream.activity.bucket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.timeline.TimelineCalendarActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemEditActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemViewActivity;
import com.vivavu.dream.adapter.bucket.timeline.TimelineListAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.RepeatType;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.option.OptionRepeat;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.model.bucket.timeline.Timeline;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.connector.TimelineConnector;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.ShadowImageView;

import java.util.ArrayList;
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
	public static final String extraKeyIsMind = "extraKeyIsMind";

    private static final int OFF_SCREEN_PAGE_LIMIT = 1;
    public static final int REQUEST_CALENDAR = 1;
    public static final int REQUEST_ADD_POST = 2;
    public static final int REQUEST_MOD_POST = 4;
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
    ShadowImageView mImgBucket;
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
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int FETCH_DATA_START = 0;
    private static final int FETCH_DATA_SUCCESS = 1;
    private static final int FETCH_DATA_FAIL = 2;
    private static final int UPDATE_BUCKET_DATA_SUCCESS = 3;
    private static final int UPDATE_BUCKET_DATA_FAIL = 4;

	private static final int FETCH_FRIENDS_BUCKET_DATA_SUCCESS = 5;
	private static final int FETCH_FRIENDS_BUCKET_DATA_FAIL = 6;

    private Timeline timeline;
    protected TimelineThread timelineThread;
    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
    private Integer lastPageNum = 1;
    protected  List<Post> timelineList;
	protected boolean isMind;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FETCH_DATA_START:
                    break;
                case FETCH_DATA_SUCCESS:
                    timeline = (Timeline) msg.obj;
                    List<Post> dataList = timeline.getTimelineData();
                    initTimelineContents(dataList);
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case FETCH_DATA_FAIL:
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case UPDATE_BUCKET_DATA_SUCCESS:
                    bucket = (Bucket) msg.obj;
                    bindData(bucket);
                    break;
                case UPDATE_BUCKET_DATA_FAIL:
                    Toast.makeText(TimelineActivity.this, getString(R.string.txt_timeline_bucket_info_update_fail), Toast.LENGTH_SHORT ).show();
                    break;
	            case FETCH_FRIENDS_BUCKET_DATA_SUCCESS:
		            bucket = (Bucket) msg.obj;
		            afterFetchBucket();
		            break;
	            case FETCH_FRIENDS_BUCKET_DATA_FAIL:
		            Toast.makeText(TimelineActivity.this, getString(R.string.txt_timeline_bucket_info_update_fail), Toast.LENGTH_SHORT ).show();
		            finish();
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

        ButterKnife.inject(this);

        mTxtBucketTitle.setTypeface(getNanumBarunGothicBoldFont());
        mTxtBucketDeadline.setTypeface(getDenseRegularFont());
        mTxtBucketRemain.setTypeface(getDenseRegularFont());
        mTxtBucketDescription.setTypeface(getNanumBarunGothicFont());

        Intent data = getIntent();
	    Integer bucketId = data.getIntExtra(extraKey, -1);

	    isMind = data.getBooleanExtra(extraKeyIsMind, true);
	    if(isMind) {
			localBucket(bucketId);
		} else {
			networkBucket(bucketId);
		}


    }

	private void localBucket(Integer bucketId){

		bucket = DataRepository.getBucket(bucketId);
		bindData(bucket);
		initEvent();

		timelineMetaInfo = new TimelineMetaInfo();
		timelineThread = new TimelineThread(bucket);
		timelineList = new ArrayList<Post>();

		Thread thread = new Thread(timelineThread);
		thread.start();
	}

	private void networkBucket(Integer bucketId){


		Thread thread = new Thread(new BucketNetworkThread(bucketId));
		thread.start();
	}

	private void afterFetchBucket(){

		bindData(bucket);
		initEvent();

		disableEditMode(false);

		timelineMetaInfo = new TimelineMetaInfo();
		timelineThread = new TimelineThread(bucket);
		timelineList = new ArrayList<Post>();

		Thread thread = new Thread(timelineThread);
		thread.start();
	}

	private void disableEditMode(boolean isMind) {
		if (!isMind) {
			mBtnEdit.setVisibility(View.GONE);
			mBtnAddPost.setVisibility(View.INVISIBLE);
			mBtnAchieve.setOnClickListener(null);
		}
	}

	private void initTimelineContents(List<Post> dataList) {
        if(lastPageNum == 1){
            timelineList.clear();
        }
        timelineList.addAll(dataList);

        if(timelineListAdapter == null) {
            timelineListAdapter = new TimelineListAdapter(this);
            mListTimeline.setAdapter(timelineListAdapter);
        }
        timelineMetaInfo.setBucketId(bucket.getId());
        timelineListAdapter.setTimelineMetaInfo(timelineMetaInfo);
        timelineListAdapter.setPostList(timelineList);
        timelineListAdapter.notifyDataSetChanged();
    }

    private void bindData(Bucket bucket) {
	    if(bucket != null && bucket.getId() != null && bucket.getId() > 0) {
		    mTxtBucketTitle.setText(bucket.getTitle());
		    Date start = bucket.getRegDate();
		    Date end = bucket.getDeadline();
		    mTxtBucketDeadline.setText(DateUtils.getDateString(end, "yyyy.MM.dd"));
		    Long remainDay = DateUtils.getRemainDay(end);
		    if (remainDay >= 0) {
			    mTxtBucketRemain.setText(String.format("D - %05d", remainDay));
			    mTxtBucketRemain.setTextColor(getResources().getColor(R.color.default_text_color));
		    } else {
			    mTxtBucketRemain.setText(String.format("D + %05d", Math.abs(remainDay)));
			    mTxtBucketRemain.setTextColor(getResources().getColor(R.color.text_color_dday_over));
		    }

		    if (bucket.getStatus() == 0) {
			    mBtnAchieve.setSelected(false);
			    if ("10".equals(bucket.getRange())) {
				    mImgBucket.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_10));
			    } else if ("20".equals(bucket.getRange())) {
				    mImgBucket.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_20));
			    } else if ("30".equals(bucket.getRange())) {
				    mImgBucket.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_30));
			    } else if ("40".equals(bucket.getRange())) {
				    mImgBucket.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_40));
			    } else if ("50".equals(bucket.getRange())) {
				    mImgBucket.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_50));
			    } else if ("60".equals(bucket.getRange())) {
				    mImgBucket.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_60));
			    } else {
				    mImgBucket.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_lt));
			    }
		    } else {
			    mBtnAchieve.setSelected(true);
			    mImgBucket.setProgressBarColor(getResources().getColor(R.color.progress_complete));
		    }

		    int progress = DateUtils.getProgress(start, end);
		    ImageLoader.getInstance().displayImage(bucket.getCvrImgUrl(), mImgBucket);
		    mImgBucket.setPercent(progress);

		    DisplayImageOptions options = new DisplayImageOptions.Builder()
				    .cacheInMemory(true)
				    .cacheOnDisc(true)
				    .considerExifParams(true)
				    .showImageForEmptyUri(R.drawable.ic_bucket_empty)
				    .build();
		    ImageLoader.getInstance().displayImage(bucket.getCvrImgUrl(), mImgBucket, options);

		    mTxtBucketDescription.setText(bucket.getDescription());
		    OptionRepeat repeat = new OptionRepeat(RepeatType.fromCode(bucket.getRptType()), bucket.getRptCndt());
		    if (repeat.getRepeatType() == RepeatType.WKRP) {
			    mLayoutBucketDefaultInfoRepeatWeek.setVisibility(View.VISIBLE);
			    mLayoutBucketDefaultInfoRepeatMonth.setVisibility(View.GONE);

			    mBtnBucketOptionSun.setBackgroundResource(repeat.isSun() ? R.drawable.ic_week_sun_release : R.drawable.ic_week_sun_press);
			    mBtnBucketOptionMon.setBackgroundResource(repeat.isMon() ? R.drawable.ic_week_mon_release : R.drawable.ic_week_mon_press);
			    mBtnBucketOptionTue.setBackgroundResource(repeat.isTue() ? R.drawable.ic_week_tue_release : R.drawable.ic_week_tue_press);
			    mBtnBucketOptionWen.setBackgroundResource(repeat.isWen() ? R.drawable.ic_week_wen_release : R.drawable.ic_week_wen_press);
			    mBtnBucketOptionThu.setBackgroundResource(repeat.isThu() ? R.drawable.ic_week_thu_release : R.drawable.ic_week_thu_press);
			    mBtnBucketOptionFri.setBackgroundResource(repeat.isFri() ? R.drawable.ic_week_fri_release : R.drawable.ic_week_fri_press);
			    mBtnBucketOptionSat.setBackgroundResource(repeat.isSat() ? R.drawable.ic_week_sat_release : R.drawable.ic_week_sat_press);

		    } else if (repeat.getRepeatType() == RepeatType.WEEK) {
			    mLayoutBucketDefaultInfoRepeatWeek.setVisibility(View.GONE);
			    mLayoutBucketDefaultInfoRepeatMonth.setVisibility(View.VISIBLE);

			    mBtnBucketOptionWeek.setBackgroundResource(R.drawable.ic_option_week_release);
			    mBtnBucketOptionMonth.setBackgroundResource(R.drawable.ic_option_month_press);
			    mTxtBucketOptionRepeatCnt.setText(String.valueOf(repeat.getRepeatCount()));
		    } else if (repeat.getRepeatType() == RepeatType.MNTH) {
			    mLayoutBucketDefaultInfoRepeatWeek.setVisibility(View.GONE);
			    mLayoutBucketDefaultInfoRepeatMonth.setVisibility(View.VISIBLE);

			    mBtnBucketOptionWeek.setBackgroundResource(R.drawable.ic_option_week_press);
			    mBtnBucketOptionMonth.setBackgroundResource(R.drawable.ic_option_month_release);
			    mTxtBucketOptionRepeatCnt.setText(String.valueOf(repeat.getRepeatCount()));
		    }
	    }
    }

    private void initEvent() {
        mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_activity)).setAction(getString(R.string.ga_event_action_edit_bucket));
                tracker.send(eventBuilder.build());

                Intent intent = new Intent(TimelineActivity.this, BucketEditActivity.class);
                intent.putExtra(BucketEditActivity.RESULT_EXTRA_BUCKET_ID, (Integer) bucket.getId());
                startActivityForResult(intent, REQUEST_MOD_BUCKET);
            }
        });

        mBtnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_activity)).setAction(getString(R.string.ga_event_action_add_timeline_item));
                tracker.send(eventBuilder.build());

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
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_activity)).setAction(getString(R.string.ga_event_action_achieve_bucket));

                if(bucket.getStatus() == 0) {
	                mBtnAchieve.setSelected(true);
                    bucket.setStatus(1);
                    eventBuilder.setValue(1);
                } else {
	                mBtnAchieve.setSelected(false);
                    bucket.setStatus(0);
                    eventBuilder.setValue(0);
                }
                tracker.send(eventBuilder.build());
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
			    timelineThread.setPage(1);
			    Thread thread = new Thread(timelineThread);
			    thread.start();
		    }
	    });

	    mListTimeline.setOnScrollListener(new AbsListView.OnScrollListener() {
		    @Override
		    public void onScrollStateChanged(AbsListView view, int scrollState) {
			    //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
			    //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
			    if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
				    //TODO 화면이 바닦에 닿을때 처리
				    // 맨 밑으로 내려가면 데이터를 더 들고오게 한다.
				    mSwipeRefreshLayout.setRefreshing(true);
				    timelineThread.setPage(lastPageNum + 1);
				    Tracker tracker = DreamApp.getInstance().getTracker();
				    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_activity)).setAction(getString(R.string.ga_event_action_more_page));
				    eventBuilder.setValue(timelineThread.getPage());
				    tracker.send(eventBuilder.build());
				    Thread thread = new Thread(timelineThread);
				    thread.start();
			    }
		    }

		    @Override
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			    //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
			    lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);

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
                if(resultCode == RESULT_OK) {
                    // 포스트 작성되었을 경우
                    timelineThread.setPage(1);
                    Thread thread = new Thread(timelineThread);
                    thread.start();
                }
                break;
            case REQUEST_MOD_POST:
                if(resultCode == RESULT_OK) {
                    // 포스트 작성되었을 경우
                    timelineThread.setPage(1);
                    Thread thread = new Thread(timelineThread);
                    thread.start();
                }
                break;
            case REQUEST_MOD_BUCKET:
                if(resultCode == RESULT_OK) {
                    Bucket result = (Bucket) data.getSerializableExtra(BucketEditActivity.RESULT_EXTRA_BUCKET);
                    setResult(RESULT_USER_DATA_UPDATED);
                    bindData(result);
                } else if (resultCode == RESULT_USER_DATA_DELETED) {
                    setResult(RESULT_USER_DATA_DELETED);
                    finish();
                }
                break;
        }
    }

    public void viewPost(Post post){
        Tracker tracker = DreamApp.getInstance().getTracker();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_activity)).setAction(getString(R.string.ga_event_action_view_timeline_item));
        tracker.send(eventBuilder.build());
	    if (!isMind) {
		    post.setBucketTitle(bucket.getTitle());
	    }
        Intent intent = new Intent(this, TimelineItemViewActivity.class);
	    intent.putExtra(extraKeyIsMind, isMind);
	    intent.putExtra(TimelineActivity.extraKeyBucket, bucket);
        intent.putExtra(TimelineActivity.extraKeyPost, post);
        startActivityForResult(intent, REQUEST_MOD_POST);
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
            }else if(result.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
            }else {
                handler.sendEmptyMessage(FETCH_DATA_FAIL);
            }
        }
    }

    private class TimelineThread implements Runnable{
        private Integer page = 1;
	    private Bucket bucket;

	    public TimelineThread(){
		    bucket = null;
		    page = 1;
	    }

	    public TimelineThread(Bucket bucket) {
		    this();
		    this.bucket = bucket;
	    }

	    public Bucket getBucket() {
		    return bucket;
	    }

	    public void setBucket(Bucket bucket) {
		    this.bucket = bucket;
	    }

	    @Override
        public void run() {
	        if(bucket != null) {
		        handler.sendEmptyMessage(FETCH_DATA_START);

		        TimelineConnector timelineConnector = new TimelineConnector();
		        ResponseBodyWrapped<Timeline> result = timelineConnector.getTimelineWithPage(bucket.getId(), page);

		        if (result.isSuccess()) {
			        lastPageNum = page;
			        Message message = handler.obtainMessage(FETCH_DATA_SUCCESS, result.getData());
			        handler.sendMessage(message);
		        } else if (result.getResponseStatus() == ResponseStatus.TIMEOUT) {
			        defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
		        } else {
			        handler.sendEmptyMessage(FETCH_DATA_FAIL);
		        }
	        }
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
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
            }else if(result.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
            }else {
                handler.sendEmptyMessage(UPDATE_BUCKET_DATA_FAIL);
            }
        }
    }

	private class BucketNetworkThread implements Runnable{
		private Integer bucketId;

		private BucketNetworkThread(Integer bucketId) {
			this.bucketId = bucketId;
		}

		@Override
		public void run() {
			if(bucket == null) {
				handler.sendEmptyMessage(FETCH_DATA_START);

				BucketConnector bucketConnector = new BucketConnector();
				ResponseBodyWrapped<Bucket> result = bucketConnector.getBucket(bucketId);

				if (result.isSuccess()) {
					Message message = handler.obtainMessage(FETCH_FRIENDS_BUCKET_DATA_SUCCESS, result.getData());
					handler.sendMessage(message);
				} else if (result.getResponseStatus() == ResponseStatus.TIMEOUT) {
					defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
				} else {
					handler.sendEmptyMessage(FETCH_FRIENDS_BUCKET_DATA_FAIL);
				}
			}
		}
	}
}
