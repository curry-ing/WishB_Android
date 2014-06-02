package com.vivavu.dream.activity.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.activity.bucket.BucketViewActivity;
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
    TextView mActionbarMainToday;

    View noticeView;
    CustomPopupWindow mPopupNotice;

    MainBucketListFragment mainBucketListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_main);
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

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothicBold.mp3");

        SpannableString text = new SpannableString("logos");
        text.setSpan(new ForegroundColorSpan(R.color.skyblue),0,1,0);
        text.setSpan(new ForegroundColorSpan(R.color.white),1,2,0);
        text.setSpan(new ForegroundColorSpan(R.color.lightgreen),2,3,0);
        text.setSpan(new ForegroundColorSpan(R.color.lightred),3,4,0);

//        mActionbarMainTitle.setText(text, TextView.BufferType.SPANNABLE);
        mActionbarMainTitle.setText("Wish Ballon");
        mActionbarMainTitle.setTypeface(typeface);
        mActionbarMainTitle.setTextSize(22);
        mActionbarMainTitle.setTextColor(Color.WHITE);

        mActionbarMainToday.setTypeface(typeface);
        mActionbarMainToday.setTextColor(Color.WHITE);
        mActionbarMainToday.setTextSize(14);
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
                goAddBucket();
                break;
        }
    }

    private void goAddBucket() {
        Intent intent;
        intent = new Intent();
        intent.setClass(this, BucketAddActivity.class);
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
