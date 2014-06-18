package com.vivavu.dream.activity.bucket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.DataRepository;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 10.
 */
public class BucketViewActivity extends BaseActionBarActivity {

    @InjectView(R.id.grid_bucket_list)
    GridView mGridBucketList;
    @InjectView(R.id.btn_add_bucket)
    Button mBtnAddBucket;
    @InjectView(android.R.id.home)
    Button mHome;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;
    @InjectView(R.id.btn_today)
    Button mBtnToday;
    private Bucket bucket;

    /*private MainContentsFragment mainContentsFragment;*/

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
        Integer bucketId = getIntent().getIntExtra("bucketId", -1);

        bucket = null;
        if (bucketId > 0) {
            bucket = DataRepository.getBucket(bucketId);
        }
        Log.d("dream", bucket.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Code.ACT_MOD_BUCKET_DEFAULT_CARD:
                //데이터 새로고침
                bucket = DataRepository.getBucket(bucket.getId());

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.bucket_view_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
