package com.vivavu.dream.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    private Tracker tracker;

	protected Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = DreamApp.getInstance().getTracker();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(tracker != null){
            this.tracker.setScreenName(getClass().getName());
            this.tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onStop() {
        if(tracker != null){

        }
        super.onStop();
    }
}
