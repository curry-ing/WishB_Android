package com.vivavu.dream.adapter.today;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.TodayCalendarActivity;
import com.vivavu.dream.fragment.main.MainTodayDailyFragment;
import com.vivavu.dream.model.bucket.TodayGroup;
import com.vivavu.dream.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class TodayDailyViewAdapter extends PagerAdapter implements View.OnClickListener{

    private Context context;
    private Fragment fragment;
    private LayoutInflater mInflater;
    private List<TodayGroup> todayGroupList;
    private TodayDailyItemAdapter mAdapter;

    public TodayDailyViewAdapter(Fragment fragment, List<TodayGroup> todayGroupList) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayGroupList = new ArrayList<TodayGroup>(todayGroupList);
    }

    public TodayDailyViewAdapter(Fragment fragment) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayGroupList = new ArrayList<TodayGroup>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.today_daily_view, container, false);
        ButterknifeViewHolder holder = new ButterknifeViewHolder(viewGroup);
        viewGroup.setTag(holder);

        init(holder, todayGroupList.get(position));
        container.addView(viewGroup);
        return viewGroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return todayGroupList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    public void init(ButterknifeViewHolder holder, TodayGroup todayGroup) {
        holder.mBtnDate.setText(DateUtils.getDateString(todayGroup.getDate(), "yyyy-MM-dd"));
        holder.mBtnDate.setTag(todayGroup.getDate());
        holder.mBtnDate.setOnClickListener(this);
        mAdapter = new TodayDailyItemAdapter(context, todayGroup.getTodayList());
        holder.mTodayContents.setAdapter(mAdapter);
        /*holder.mTodayContents.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                    }
                } else {
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });*/
    }

    public List<TodayGroup> getTodayGroupList() {
        return todayGroupList;
    }

    public void setTodayGroupList(List<TodayGroup> todayGroupList) {
        this.todayGroupList = todayGroupList;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_date){
            //날짜 이동
            Intent intent = new Intent(context, TodayCalendarActivity.class);
            intent.putExtra(TodayCalendarActivity.dateExtraName, (java.io.Serializable) v.getTag());
            fragment.startActivityForResult(intent, MainTodayDailyFragment.REQUEST_CODE_CHANGE_DAY);
        }
    }

    class ButterknifeViewHolder {
        @InjectView(R.id.btn_date)
        Button mBtnDate;
        @InjectView(R.id.today_contents)
        GridView mTodayContents;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
