package com.vivavu.dream.activity.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.vivavu.dream.fragment.main.BucketListFragment;
import com.vivavu.dream.fragment.main.MainBucketListFragment;
import com.vivavu.dream.fragment.main.NewsFeedFragment;
import com.vivavu.dream.fragment.main.TodayListFragment;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActionBarActivity  implements ActionBar.TabListener{
	public static final int OFFSCREEN_PAGE_LIMIT = 3;
	private AlarmManagerBroadcastReceiver alarm;

    @InjectView(R.id.actionbar_main_title)
    ImageView mActionbarMainTitle;
    @InjectView(R.id.actionbar_main_today)
    TextView mActionbarMainToday;

    View customActionBarView;
    View customActionBarViewProfile;

	SectionsPagerAdapter sectionsPagerAdapter;
	ViewPager mViewPager;

    MainBucketListFragment mainBucketListFragment;

    public static final String EXTRA_BUCKET_DEFAULT_RANGE="extraBucketDefaultRange";
    Boolean doubleBackToExitPressedOnce = false;

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
        actionBar.setDisplayShowHomeEnabled(true);//로고 버튼 보이는 것 설정
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

	    // 여기서부터 탭 설정
	    actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);
	    sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
	    // Set up the ViewPager with the sections adapter.
	    mViewPager = (ViewPager) findViewById(R.id.content_view_pager);
	    mViewPager.setAdapter(sectionsPagerAdapter);
	    mViewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);

	    // When swiping between different sections, select the corresponding
	    // tab. We can also use ActionBar.Tab#select() to do this if we have
	    // a reference to the Tab.
	    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
		    @Override
		    public void onPageSelected(int position) {
			    actionBar.setSelectedNavigationItem(position);
		    }
	    });

	    // For each of the sections in the app, add a tab to the action bar.
	    for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
		    // Create a tab with text corresponding to the page title defined by
		    // the adapter. Also specify this Activity object, which implements
		    // the TabListener interface, as the callback (listener) for when
		    // this tab is selected.
		    actionBar.addTab(
				    actionBar.newTab()
						    .setText(sectionsPagerAdapter.getPageTitle(i))
						    .setTabListener(this));
	    }

        mActionbarMainToday.setTypeface(getNanumBarunGothicBoldFont());

        mActionbarMainToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	            Tracker tracker = DreamApp.getInstance().getTracker();
	            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_main_activity)).setAction(getString(R.string.ga_event_action_move_today));
	            tracker.send(eventBuilder.build());

                goToday();
            }
        });

        updateProfileImg();

        actionBarProfileViewHolder.mTxtProfile.setTypeface(getNanumBarunGothicFont());

        // Drawer Menu(profile) Control
        mContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mContainer.setScrimColor(getResources().getColor(R.color.transparent));
        mContainer.setFocusableInTouchMode(false); //for close drawer when press back button;
        mContainer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mProfile.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
		        ActionBar bar = getSupportActionBar();
		        bar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_STANDARD);
		        mContainer.openDrawer(Gravity.LEFT);
	        }
        });

        actionBarProfileViewHolder.mProfile.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
		        mContainer.closeDrawer(Gravity.LEFT);
	        }
        });

	    actionBarProfileViewHolder.mTxtProfile.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    mContainer.closeDrawer(Gravity.LEFT);
		    }
	    });

        mContainer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
	        @Override
	        public void onDrawerOpened(View drawerView) {
		        ActionBar bar = getSupportActionBar();
		        bar.setCustomView(customActionBarViewProfile);
		        Tracker tracker = DreamApp.getInstance().getTracker();
		        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_main_activity)).setAction(getString(R.string.ga_event_action_open_profile));
		        tracker.send(eventBuilder.build());
	        }

	        @Override
	        public void onDrawerClosed(View drawerView) {
		        ActionBar bar = getSupportActionBar();
		        bar.setCustomView(customActionBarView);
		        Tracker tracker = DreamApp.getInstance().getTracker();
		        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_close_profile));
		        tracker.send(eventBuilder.build());
		        bar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);
	        }

        });

	    if(isNeedUpdate()){
		    AlertDialog.Builder ab = new AlertDialog.Builder(this);
		    ab.setMessage(R.string.need_update);
		    ab.setNegativeButton(R.string.update_later, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {

			    }
		    });
		    ab.setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    Intent i = new Intent(Intent.ACTION_VIEW);
				    i.setData(Uri.parse(DreamApp.getInstance().getAppVersionInfo().getUrl()));
				    startActivity(i);
			    }
		    });
		    ab.show();
	    }

	    View homeIcon = findViewById( android.R.id.home );
	    ((View) homeIcon.getParent()).setVisibility(View.GONE);
	    /*actionBar.setDisplayShowHomeEnabled(false);*/
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

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}

	/**
	 * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position){
				case 0:
					return new NewsFeedFragment();
				case 1:
					return new BucketListFragment();
				case 2:
					return new TodayListFragment();
				default:
					return new TodayListFragment();
			}

		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
				case 2:
					return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
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
