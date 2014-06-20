package com.vivavu.dream.activity.bucket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.activity.main.TodayActivity;
import com.vivavu.dream.adapter.bucket.BucketListAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.DataRepository;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 10.
 */
public class BucketGroupViewActivity extends BaseActionBarActivity {

    private static final int REQUEST_TIMELINE_VIEW = 0;
    private static final int REQUEST_BUCKET_ADD  = 1;
    @InjectView(R.id.grid_bucket_list)
    GridView mGridBucketList;
    @InjectView(R.id.btn_add_bucket)
    Button mBtnAddBucket;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.btn_today)
    Button mBtnToday;
    @InjectView(R.id.layout_sub_view_background)
    RelativeLayout mLayoutSubViewBackground;

    String groupRange;
    BucketListAdapter bucketListAdapter;
    boolean dataModifyFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.bucket_sub_view);
        setResult(RESULT_CANCELED);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.actionbar_sub_view);
        actionBar.setDisplayShowCustomEnabled(true);

        ButterKnife.inject(this);

        Intent data = getIntent();

        groupRange = data.getStringExtra("groupRange");

        List<Bucket> bucketList = DataRepository.listBucketByRange(groupRange);

        bucketListAdapter = new BucketListAdapter(DreamApp.getInstance(), bucketList);

        updateGroupRangeInfo();

        mGridBucketList.setAdapter(bucketListAdapter);
        bucketListAdapter.setOnBucketImageViewClickListener(new BucketListAdapter.OnBucketImageViewClick() {
            @Override
            public void onItemClick(View view, int position, long id) {
                goTimelineActivity((int) id);
            }
        });

        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BucketGroupViewActivity.this, TodayActivity.class);
                startActivity(intent);
            }
        });

        mBtnAddBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddBucket();
            }
        });

        mTxtTitle.setTypeface(getNanumBarunGothicBoldFont());
        mBtnToday.setTypeface(getNanumBarunGothicBoldFont());
    }

    private void updateGroupRangeInfo() {
        String title = null;
        if(null == groupRange){
            title = DreamApp.getInstance().getUser().getTitle_life();
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg00);
            bucketListAdapter.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_lt));
        } else if("10".equals(groupRange)){
            title = DreamApp.getInstance().getUser().getTitle_10();
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg10);
            bucketListAdapter.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_10));
        }else if("20".equals(groupRange)){
            title = DreamApp.getInstance().getUser().getTitle_20();
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg20);
            bucketListAdapter.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_20));
        }else if("30".equals(groupRange)){
            title = DreamApp.getInstance().getUser().getTitle_30();
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg30);
            bucketListAdapter.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_30));
        }else if("40".equals(groupRange)){
            title = DreamApp.getInstance().getUser().getTitle_40();
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg40);
            bucketListAdapter.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_40));
        }else if("50".equals(groupRange)){
            title = DreamApp.getInstance().getUser().getTitle_50();
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg50);
            bucketListAdapter.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_50));
        }else if("60".equals(groupRange)){
            title = DreamApp.getInstance().getUser().getTitle_60();
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg60);
            bucketListAdapter.setProgressBarColor(DreamApp.getInstance().getResources().getColor(R.color.progress_60));
        }

        if(title == null && groupRange != null){
            title = groupRange + "대";
        } else if(title == null && groupRange == null){
            title = "In My Life";
        }

        mTxtTitle.setText(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TIMELINE_VIEW){
            if(resultCode == RESULT_USER_DATA_DELETED){
                List<Bucket> bucketList = DataRepository.listBucketByRange(groupRange);
                bucketListAdapter.setList(bucketList);
                bucketListAdapter.notifyDataSetChanged();
                setResult(RESULT_USER_DATA_MODIFIED);
                dataModifyFlag = true;
            } else if(resultCode == RESULT_USER_DATA_UPDATED){
                List<Bucket> bucketList = DataRepository.listBucketByRange(groupRange);
                bucketListAdapter.setList(bucketList);
                bucketListAdapter.notifyDataSetChanged();
                setResult(RESULT_USER_DATA_MODIFIED);
                dataModifyFlag = true;
            }
        }

        if(requestCode == REQUEST_BUCKET_ADD){
            if(resultCode == RESULT_OK){
                Integer bucketRange = data.getIntExtra(BucketEditActivity.RESULT_EXTRA_BUCKET_RANGE, -1);
                setResult( dataModifyFlag ? RESULT_USER_DATA_MODIFIED : RESULT_OK, data);

                if (bucketRange != null && bucketRange > 0) {
                    groupRange = String.valueOf(bucketRange);
                } else {
                    groupRange = null;
                }

                updateGroupRangeInfo();
                List<Bucket> bucketList = DataRepository.listBucketByRange(groupRange);
                bucketListAdapter.setList(bucketList);
                bucketListAdapter.notifyDataSetChanged();
            }
        }
    }

    public void goTimelineActivity(int bucketId){
        Intent intent = new Intent();
        intent.setClass(this, TimelineActivity.class);
        intent.putExtra(TimelineActivity.extraKey, bucketId);
        startActivityForResult(intent, REQUEST_TIMELINE_VIEW);
    }

    private void goAddBucket() {
        Intent intent;
        intent = new Intent();
        intent.setClass(this, BucketEditActivity.class);
        if(groupRange != null) {
            intent.putExtra(MainActivity.EXTRA_BUCKET_DEFAULT_RANGE, Integer.valueOf(groupRange));
        }

        startActivityForResult(intent, REQUEST_BUCKET_ADD);
    }
}
