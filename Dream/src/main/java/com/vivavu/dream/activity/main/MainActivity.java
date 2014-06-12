package com.vivavu.dream.activity.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.activity.bucket.BucketViewActivity;
import com.vivavu.dream.broadcastReceiver.AlarmManagerBroadcastReceiver;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.fragment.main.MainBucketListFragment;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.view.CustomPopupWindow;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActionBarActivity {
    private AlarmManagerBroadcastReceiver alarm;

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

    View customeActionBarView;
    View customeActionBarViewProfile;

    MainBucketListFragment mainBucketListFragment;

    public static final String EXTRA_BUCKET_DEFAULT_RANGE="extraBucketDefaultRange";
    @InjectView(R.id.content_frame)
    FrameLayout mContentFrame;
    @InjectView(R.id.container)
    DrawerLayout mContainer;
    @InjectView(R.id.profile)
    ImageView mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_main);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        customeActionBarView = inflater.inflate(R.layout.actionbar_main, null);
        customeActionBarViewProfile = inflater.inflate(R.layout.actionbar_main_profile, null);
        ActionBarProfileViewHolder actionBarProfileViewHolder = new ActionBarProfileViewHolder(customeActionBarViewProfile);
        customeActionBarViewProfile.setTag(actionBarProfileViewHolder);

        actionBar.setCustomView(customeActionBarView);

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

        SpannableString text = new SpannableString("logos");
        text.setSpan(new ForegroundColorSpan(R.color.skyblue),0,1,0);
        text.setSpan(new ForegroundColorSpan(R.color.white),1,2,0);
        text.setSpan(new ForegroundColorSpan(R.color.lightgreen),2,3,0);
        text.setSpan(new ForegroundColorSpan(R.color.lightred),3,4,0);

//        mActionbarMainTitle.setText(text, TextView.BufferType.SPANNABLE);
        mActionbarMainTitle.setText("Wish Ballon");
        mActionbarMainTitle.setTypeface(getNanumBarunGothicBoldFont());
        mActionbarMainTitle.setTextSize(22);
        mActionbarMainTitle.setTextColor(Color.WHITE);

        mActionbarMainToday.setTypeface(getNanumBarunGothicBoldFont());
        mActionbarMainToday.setTextColor(Color.WHITE);
        mActionbarMainToday.setTextSize(14);


//        alarm = new AlarmManagerBroadcastReceiver();
//        alarm.SetAlarm(context, 1, true, 23);
//        alarm.SetAlarm(context, 2, true, 11);
//        alarm.CancelAlarm(context);

        actionBarProfileViewHolder.mTxtProfile.setTypeface(getNanumBarunGothicFont());

        mContainer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                ActionBar bar = getSupportActionBar();
                bar.setCustomView(customeActionBarViewProfile);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                ActionBar bar = getSupportActionBar();
                bar.setCustomView(customeActionBarView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Code.ACT_ADD_BUCKET:
                int bucketId = data.getIntExtra(BucketEditActivity.RESULT_EXTRA_BUCKET_ID, -1);
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
        intent.setClass(this, BucketEditActivity.class);
        if(mainBucketListFragment != null) {
            intent.putExtra(EXTRA_BUCKET_DEFAULT_RANGE, mainBucketListFragment.getViewPagerPage() * 10);
        }

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

/**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static class ActionBarProfileViewHolder {
        @InjectView(R.id.profile)
        ImageView mProfile;
        @InjectView(R.id.txt_profile)
        TextView mTxtProfile;

    ActionBarProfileViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
