package com.vivavu.dream.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.activity.bucket.BucketViewActivity;
import com.vivavu.dream.activity.bucket.CircleBucketListActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.fragment.main.MainBucketListFragment;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.view.ButtonIncludeCount;
import com.vivavu.dream.view.CustomPopupWindow;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActionBarActivity {
    @InjectView(R.id.btn_add_bucket)
    Button mBtnAddBucket;
    @InjectView(R.id.actionbar_main_title)
    TextView mActionbarMainTitle;
    @InjectView(R.id.actionbar_main_notice)
    Button mActionbarMainNotice;
    @InjectView(R.id.actionbar_main_today)
    ButtonIncludeCount mActionbarMainToday;

    View noticeView;
    CustomPopupWindow mPopupNotice;

    MainBucketListFragment mainBucketListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //actionbar setting
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_main);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            mainBucketListFragment= new MainBucketListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, mainBucketListFragment, MainBucketListFragment.TAG)
                    .addToBackStack(MainBucketListFragment.TAG)
                    .commit();
        }

        noticeView = getLayoutInflater().inflate(R.layout.actionbar_notice, null);
        mPopupNotice = AndroidUtils.makePopupWindow(noticeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //mPopupNotice.setAnimationStyle(R.style.AnimationPopup);
        mBtnAddBucket.setOnClickListener(this);
        mActionbarMainNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mPopupNotice != null && !mPopupNotice.isShowing() && !v.isSelected()){
                    mPopupNotice.showAsDropDown(v);
                    v.setSelected(true);
                }else{
                    mPopupNotice.hide();
                    v.setSelected(false);
                }
            }
        });

        mActionbarMainToday.getButton().setText("Today");
        mActionbarMainToday.getTextView().setText("1");
        mActionbarMainToday.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TodayActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Activity 와 Fragment 실행순서에 따라서 Fragment UI가 다 생성된 이후에 Activity에서
        // Fragment의 UI에 접근 가능. Activity.onCreate -> Fragment.onCreate->Activity.onStart가 수행

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Code.ACT_ADD_BUCKET:
                int bucketId = data.getIntExtra(BucketAddActivity.RESULT_EXTRA_BUCKET_ID, -1);
                if (bucketId > 0) {
                    goBucketView(bucketId);
                }

                break;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_add_bucket:
                //goAddBucket();
                goCircleBucketList();
                break;
        }
    }

    private void goAddBucket() {
        Intent intent;
        intent = new Intent();
        intent.setClass(this, BucketAddActivity.class);
        startActivity(intent);
    }

    private void goCircleBucketList(){
        Intent intent;
        intent = new Intent();
        //intent.setClass(this, CircleBucketListActivity.class);
        intent.setClass(this, CircleBucketListActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    public void goBucketView(Integer bucketId) {
        Intent intent = new Intent();
        intent.setClass(this, BucketViewActivity.class);
        intent.putExtra("bucketId", bucketId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        if(mPopupNotice != null && mPopupNotice.isShowing()){
            mPopupNotice.hide();
        }else{
            exit();
        }
    }

}
