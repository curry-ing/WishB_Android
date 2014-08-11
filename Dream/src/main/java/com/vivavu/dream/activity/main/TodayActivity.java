package com.vivavu.dream.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.main.TodayListFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-21.
 */
public class TodayActivity extends BaseActionBarActivity {
    @InjectView(R.id.actionbar_main_title)
    TextView mActionbarMainTitle;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;

    TodayListFragment todayListFragment;
    protected boolean fromAlarm = false;
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
        actionBar.setCustomView(R.layout.actionbar_today);

        ButterKnife.inject(this);

        mActionbarMainTitle.setTypeface(getNanumBarunGothicBoldFont());
        Intent intent = getIntent();
        fromAlarm = intent.getBooleanExtra(BaseActionBarActivity.EXTRA_KEY_FROM_ALARM, false);

        intent.putExtra(BaseActionBarActivity.EXTRA_KEY_FROM_ALARM, false);
        if (savedInstanceState == null) {
            todayListFragment = new TodayListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, todayListFragment, todayListFragment.TAG)
                    .commit();
        }

        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //finish();
                //or goMain();
            }
        });
        //mPopupNotice.setAnimationStyle(R.style.AnimationPopup);
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

/*        mActionbarMainToday.setTypeface(getNanumBarunGothicBoldFont());
        mActionbarMainToday.setTextColor(Color.WHITE);
        mActionbarMainToday.setTextSize(14);

        mActionbarMainToday.setText("Main");
        mActionbarMainToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                goMain();
            }
        });*/
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
	protected void onStart() {
		super.onStart();

		if(fromAlarm){
			Tracker tracker = DreamApp.getInstance().getTracker();
			HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_today_activity)).setAction(getString(R.string.ga_event_action_from_alert));
			tracker.send(eventBuilder.build());
		}

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
        if(fromAlarm){
            exit();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }
}