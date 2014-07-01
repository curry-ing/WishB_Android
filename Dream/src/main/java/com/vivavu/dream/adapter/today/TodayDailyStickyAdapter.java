package com.vivavu.dream.adapter.today;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemEditActivity;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.ShadowImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class TodayDailyStickyAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter, View.OnClickListener{
    private Context context;
    private LayoutInflater mInflater;
    private List<Today> todayList;

    public TodayDailyStickyAdapter(Context context, List<Today> todayList) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayList = new ArrayList<Today>(todayList);
    }

    public TodayDailyStickyAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayList = new ArrayList<Today>();
    }

    @Override
    public int getCount() {
        return todayList.size();
    }

    @Override
    public Object getItem(int position) {
        return todayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ButterknifeViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.today_item, parent, false);
            holder = new ButterknifeViewHolder(convertView);
            convertView.setTag(holder);
        } else{
            holder = (ButterknifeViewHolder) convertView.getTag();
        }
        init(holder, todayList.get(position));

        return convertView;
    }

    public void init(ButterknifeViewHolder holder, final Today today) {

        holder.mBucketItemTitle.setText(today.getTitle());
        holder.mBucketItemDeadline.setText(DateUtils.getDateString(today.getDeadline(), "yyyy.MM.dd"));

        holder.mBucketItemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.setClass(context, TimelineItemEditActivity.class);
                Bucket bucket = DataRepository.getBucket(today.getBucketId());
                Post post = new Post(new Date());
                intent.putExtra(TimelineActivity.extraKeyBucket, bucket);
                intent.putExtra(TimelineActivity.extraKeyPost, post);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(intent);
            }
        });

        // Finally load the image asynchronously into the ImageView, this also takes care of
        // setting a placeholder image while the background thread runs
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .showImageOnFail(R.drawable.ic_bucket_empty)
                .showImageForEmptyUri(R.drawable.ic_bucket_empty)
                .build();
        ImageLoader.getInstance().displayImage(today.getCvrImgUrl(), holder.mBucketItemImg, options    );
    }

    @Override
    public void onClick(View v) {

    }

    public List<Today> getTodayList() {
        return todayList;
    }

    public void setTodayList(List<Today> todayList) {
        this.todayList = todayList;
    }

    @Override
    public long getHeaderId(int position) {
        Today item = (Today) getItem(position);
        long result = 0;
        if( item.getDate() != null ){
            result = item.getDate().getTime();
        }
        return result;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        ButterknifeHeaderViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_today_list_sticky_header, parent, false);
            holder = new ButterknifeHeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else{
            holder = (ButterknifeHeaderViewHolder) convertView.getTag();
        }

        holder.mStickyHeader.setText(DateUtils.getDateString(DateUtils.getDate(getHeaderId(position)), "yyyy.MM.dd"));

        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    static class ButterknifeHeaderViewHolder {
        @InjectView(R.id.sticky_header)
        TextView mStickyHeader;

        ButterknifeHeaderViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    static class ButterknifeViewHolder {
        @InjectView(R.id.bucket_item_img)
        ShadowImageView mBucketItemImg;
        @InjectView(R.id.bucket_item_title)
        TextView mBucketItemTitle;
        @InjectView(R.id.bucket_item_deadline)
        TextView mBucketItemDeadline;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
