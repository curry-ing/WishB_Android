package com.vivavu.dream.adapter.bucket.timeline;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vivavu.dream.fragment.bucket.timeline.TimelineFragment;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;

/**
 * Created by yuja on 2014-04-01.
 */
public class TimelineDailyAdapter extends FragmentStatePagerAdapter {

    protected TimelineMetaInfo timelineMetaInfo;
    protected Context context;

    public TimelineDailyAdapter(FragmentManager fm, Context context, TimelineMetaInfo timelineMetaInfo) {
        super(fm);
        this.timelineMetaInfo = timelineMetaInfo;
        this.context = context;
    }

    public TimelineMetaInfo getTimelineMetaInfo() {
        return timelineMetaInfo;
    }

    public void setTimelineMetaInfo(TimelineMetaInfo timelineMetaInfo) {
        this.timelineMetaInfo = timelineMetaInfo;
    }

    @Override
    public Fragment getItem(int position) {
        TimelineFragment timelineFragment = TimelineFragment.newInstance(timelineMetaInfo, position);
        //timelineFragment.getNetworkData();
        return timelineFragment;
    }

    @Override
    public int getCount() {
        return timelineMetaInfo.getCount();
    }
}
