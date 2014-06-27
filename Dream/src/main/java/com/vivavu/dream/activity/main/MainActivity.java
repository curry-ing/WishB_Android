package com.vivavu.dream.activity.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.activity.bucket.BucketViewActivity;
import com.vivavu.dream.broadcastReceiver.AlarmManagerBroadcastReceiver;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.drawable.RoundedAvatarDrawable;
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
    ImageView mActionbarMainTitle;
    @InjectView(R.id.actionbar_main_today)
    TextView mActionbarMainToday;

    View noticeView;
    CustomPopupWindow mPopupNotice;

    View customActionBarView;
    View customActionBarViewProfile;

    MainBucketListFragment mainBucketListFragment;

    public static final String EXTRA_BUCKET_DEFAULT_RANGE="extraBucketDefaultRange";
    Boolean doubleBackToExitPressedOnce = false;

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
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        customActionBarView = inflater.inflate(R.layout.actionbar_main, null);
        customActionBarViewProfile = inflater.inflate(R.layout.actionbar_main_profile, null);
        ActionBarProfileViewHolder actionBarProfileViewHolder = new ActionBarProfileViewHolder(customActionBarViewProfile);
        customActionBarViewProfile.setTag(actionBarProfileViewHolder);


        actionBar.setCustomView(customActionBarView);
        RelativeLayout.LayoutParams actionbarLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        customActionBarView.setLayoutParams(actionbarLp);
        customActionBarViewProfile.setLayoutParams(actionbarLp);
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
        /*mActionbarMainNotice.setOnClickListener(new View.OnClickListener() {
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
        });*/

        mActionbarMainToday.setTypeface(getNanumBarunGothicBoldFont());

        mActionbarMainToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, TodayActivity.class);
//                startActivity(intent);
                goToday();
            }
        });

        updateProfileImg();

        actionBarProfileViewHolder.mTxtProfile.setTypeface(getNanumBarunGothicFont());

        // Drawer Menu(profile) Control
        mContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mContainer.setScrimColor(getResources().getColor(R.color.transparent));
        mContainer.setFocusableInTouchMode(false); //for close drawer when press back button;
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContainer.openDrawer(Gravity.LEFT);
            }
        });

        actionBarProfileViewHolder.mProfile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mContainer.closeDrawer(Gravity.LEFT);
            }
        });

        mContainer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                ActionBar bar = getSupportActionBar();
                bar.setCustomView(customActionBarViewProfile);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                ActionBar bar = getSupportActionBar();
                bar.setCustomView(customActionBarView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    public void updateProfileImg() {
        final ActionBarProfileViewHolder actionBarProfileViewHolder = (ActionBarProfileViewHolder) customActionBarViewProfile.getTag();
        String userProfileImgUrl = DreamApp.getInstance().getUser().getProfileImgUrl();
        int pixelSize = getResources().getDimensionPixelSize(R.dimen.actionbar_profile_img_size);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .showImageForEmptyUri(R.drawable.mainview_profile)
                .build();
        ImageLoader.getInstance().loadImage(userProfileImgUrl, new ImageSize(pixelSize, pixelSize), options, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(loadedImage != null) {
                    Drawable avatar = new RoundedAvatarDrawable(loadedImage);
                    mProfile.setImageDrawable(avatar);
                    actionBarProfileViewHolder.mProfile.setImageDrawable(avatar);
                } else {
                    mProfile.setImageResource(R.drawable.mainview_profile);
                    actionBarProfileViewHolder.mProfile.setImageResource(R.drawable.mainview_profile);
                }
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

        mainBucketListFragment.startActivityForResult(intent, Code.ACT_ADD_BUCKET );
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
//        Boolean doubleBackToExitPressedOnce = false;
        if(mContainer.isDrawerOpen(Gravity.LEFT)){
            mContainer.closeDrawer(Gravity.LEFT);
        } else if(mPopupNotice != null && mPopupNotice.isShowing()){
            mPopupNotice.hide();
        } else {
            if(doubleBackToExitPressedOnce) {
                exit();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
//            exit();
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
