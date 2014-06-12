package com.vivavu.dream.activity.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.fragment.main.MainTodayDailyFragment;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.view.CustomPopupWindow;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-21.
 */
public class TodayActivity extends BaseActionBarActivity {
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

    MainTodayDailyFragment mainTodayDailyFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_today);
        //actionbar setting
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_main);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            mainTodayDailyFragment = new MainTodayDailyFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, mainTodayDailyFragment, mainTodayDailyFragment.TAG)
                    .commit();
        }

        noticeView = getLayoutInflater().inflate(R.layout.actionbar_notice, null);
        mPopupNotice = AndroidUtils.makePopupWindow(noticeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //mPopupNotice.setAnimationStyle(R.style.AnimationPopup);
        mBtnAddBucket.setOnClickListener(this);
        mBtnAddBucket.setVisibility(View.GONE);
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

        mActionbarMainTitle.setText("Wish Ballon");
        mActionbarMainTitle.setTypeface(getNanumBarunGothicBoldFont());
        mActionbarMainTitle.setTextSize(22);
        mActionbarMainTitle.setTextColor(Color.WHITE);

        mActionbarMainToday.setTypeface(getNanumBarunGothicBoldFont());
        mActionbarMainToday.setTextColor(Color.WHITE);
        mActionbarMainToday.setTextSize(14);

        mActionbarMainToday.setText("Main");
        mActionbarMainToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        intent.setClass(this, BucketEditActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        //다시 활성화 될때.
        super.onResume();
        if (checkLogin() == false) {
            //goLogin();
        } else {

        }
    }

    @Override
    public void onBackPressed() {
        if(mPopupNotice != null && mPopupNotice.isShowing()){
            mPopupNotice.hide();
        }else{
            finish();
        }
    }
}