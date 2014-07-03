package com.vivavu.dream.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.util.AndroidUtils;

/**
 * Created by yuja on 14. 1. 24.
 */
public class CustomBaseFragment extends Fragment implements View.OnClickListener{

    @Override
    public void onClick(View view) {
        //todo:이게 호출되는지 확인 필요.
        AndroidUtils.autoVisibleSoftInputFromWindow(view);
    }

    public CustomBaseFragment(){
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        Tracker tracker = DreamApp.getInstance().getTracker(DreamApp.TrackerName.APP_TRACKER);
        tracker.setScreenName(this.getClass().getName());
    }

    @Override
    public void onStop() {
        super.onStop();
        Tracker tracker = DreamApp.getInstance().getTracker(DreamApp.TrackerName.APP_TRACKER);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
