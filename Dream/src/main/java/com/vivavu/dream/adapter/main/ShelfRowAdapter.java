package com.vivavu.dream.adapter.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class ShelfRowAdapter extends PagerAdapter implements View.OnClickListener{
    private Context context;
    private LayoutInflater mInflater;
    private List<Bucket> bucketList;

    public ShelfRowAdapter(Context context, List<Bucket> bucketList) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketList = new ArrayList<Bucket>(bucketList);
    }

    public ShelfRowAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketList = new ArrayList<Bucket>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.shelf_item, container, false);
        ButterknifeViewHolder holder = new ButterknifeViewHolder(viewGroup);
        viewGroup.setTag(holder);
        init(holder, bucketList.get(position));
        viewGroup.setBackgroundColor(Color.argb(127, 0, 0, 255));
        container.addView(viewGroup);
        return viewGroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return bucketList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    public void init(ButterknifeViewHolder holder, Bucket bucket) {

        holder.mBookTitle.setText(bucket.getTitle());
        holder.mBookDudate.setText(DateUtils.getDateString(bucket.getDeadline(), "yyyy-MM-dd"));
        holder.mBtnBookWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.setClass(context, BucketEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(intent);
            }
        });
        holder.mBtnBookSetting.setOnClickListener(this);

       /* popupView = getActivity().getLayoutInflater().inflate(R.layout.book_setting_menu, null);
        PopupWindowViewHolder holder = new PopupWindowViewHolder(popupView);
        popupView.setTag(holder);
        holder.mBtnBucketStatusAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        holder.mBtnBucketStatusSeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        holder.mBtnBucketStatusDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        mPopupWindow = AndroidUtils.makePopupWindow(popupView);
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)*/


        holder.mBookCover.setOnClickListener(this);
        holder.mBookTitle.setOnClickListener(this);
        holder.mBookDudate.setOnClickListener(this);
        holder.mBookStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    /**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static

    class ButterknifeViewHolder {
        @InjectView(R.id.book_cover_image)
        ImageView mBookCoverImage;
        @InjectView(R.id.btn_book_write)
        Button mBtnBookWrite;
        @InjectView(R.id.btn_book_setting)
        Button mBtnBookSetting;
        @InjectView(R.id.book_title)
        TextView mBookTitle;
        @InjectView(R.id.book_dudate)
        TextView mBookDudate;
        @InjectView(R.id.book_status)
        TextView mBookStatus;
        @InjectView(R.id.book_cover)
        LinearLayout mBookCover;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
