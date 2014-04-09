package com.vivavu.dream.adapter.bucket;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.main.ShelfRowFragmentAdapter;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.view.PagerContainer;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 9.
 */
public class BucketAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater mInflater;
    private List<BucketGroup> mBucketList;
    private FragmentActivity mContext;
    private int resource;
    private Fragment parentFragment;
    private final int OFF_SCREEN_PAGE_LIMIT = 3;

    public BucketAdapter(FragmentActivity context, int resource, List<BucketGroup> mBucketList) {
        super();
        this.mContext = context;
        this.resource = resource;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mBucketList = new ArrayList<BucketGroup>(mBucketList);
    }

    @Override
    public int getCount() {

        if (mBucketList != null) {
            return mBucketList.size();
        }
        return 0;

    }

    @Override
    public BucketGroup getItem(int i) {
        return mBucketList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BucketGroup item = mBucketList.get(i);

        ButterknifeViewHolder holder = null;
        /*if (view == null) {*/
            view = mInflater.inflate(R.layout.shelf_row, viewGroup, false);
            holder = new ButterknifeViewHolder(view);
            final ButterknifeViewHolder finalViewHolder = holder;
            holder.mShelfRoll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalViewHolder.mShelfContainer.getVisibility() == View.VISIBLE){
                        finalViewHolder.mShelfContainer.setVisibility(View.GONE);
                        finalViewHolder.mShelfRoll.setText("펼치기");
                    }else{
                        finalViewHolder.mShelfContainer.setVisibility(View.VISIBLE);
                        finalViewHolder.mShelfRoll.setText("접기");
                    }
                }
            });

            ViewPager pager = holder.mShelfRow.getViewPager();

            //PagerAdapter adapter = new ShelfRowAdapter(mContext);
            //ShelfRowAdapter adapter = new ShelfRowAdapter(getContext(), item.getBukets());
            ShelfRowFragmentAdapter adapter = new ShelfRowFragmentAdapter(getFragmentManager(), item.getBukets());
            //adapter.refreshDataSet(item.getBukets());
            pager.setAdapter(adapter);
            //Necessary or the pager will only have one extra page to show
            // make this at least however many pages you can see
            pager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
            //A little space between pages
            pager.setPageMargin(15);

            //If hardware acceleration is enabled, you should also remove
            // clipping on the pager for its children.
            pager.setClipChildren(false);


            view.setTag(holder);
        /*}else{
            holder = (ButterknifeViewHolder) view.getTag();
        }*/


        holder.mShelfTitle.setText(item.getRangeText());
        //ViewPager pager = holder.mShelfRow.getViewPager();
        //ShelfRowFragmentAdapter adapter = (ShelfRowFragmentAdapter) pager.getAdapter();
        adapter.refreshDataSet(item.getBukets());
        return view;
    }

    @Override
    public void onClick(View view) {

    }

    private Context getContext() {
        return this.mContext;
    }

    public void refreshDataSet(List<BucketGroup> buckets) {
        this.mBucketList.clear();
        this.mBucketList.addAll(buckets);

        super.notifyDataSetChanged();
    }

    public List<BucketGroup> getBucketList() {
        return mBucketList;
    }

    public void setBucketList(List<BucketGroup> bucketList) {
        this.mBucketList.clear();
        this.mBucketList.addAll(bucketList);
    }

    public Fragment getParentFragment() {
        return parentFragment;
    }

    public void setParentFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public FragmentManager getFragmentManager(){
        if(parentFragment != null){
            return parentFragment.getChildFragmentManager();
        }
        return mContext.getSupportFragmentManager();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    class ButterknifeViewHolder {
        @InjectView(R.id.shelf_title)
        TextView mShelfTitle;
        @InjectView(R.id.shelf_roll)
        Button mShelfRoll;
        @InjectView(R.id.shelf_row)
        PagerContainer mShelfRow;
        @InjectView(R.id.shelf_container)
        LinearLayout mShelfContainer;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
