package com.vivavu.dream.fragment.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemEditActivity;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.CustomPopupWindow;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 28.
 */
public class MainShelfItemFragment extends CustomBaseFragment{
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
    @InjectView(R.id.book_cover_image)
    ImageView mBookCoverImage;
    private Bucket bucket;

    View popupView;
    CustomPopupWindow mPopupWindow;

    public MainShelfItemFragment() {
        this.bucket = new Bucket();
    }

    public static MainShelfItemFragment newInstance(Bucket bucket) {
        return new MainShelfItemFragment(bucket);
    }

    public MainShelfItemFragment(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.shelf_item, container, false);
        ButterKnife.inject(this, viewGroup);
        viewGroup.setBackgroundColor(Color.argb(127, 0, 0, 255));
        init();
        return viewGroup;
    }

    public void init() {

        mBookTitle.setText(bucket.getTitle());
        mBookDudate.setText(DateUtils.getDateString(bucket.getDeadline(), "yyyy-MM-dd"));
        mBtnBookWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.setClass(getActivity(), TimelineItemEditActivity.class);
                Post post = new Post(new Date());
                intent.putExtra(TimelineActivity.extraKeyBucket, bucket);
                intent.putExtra(TimelineActivity.extraKeyPost, post);
                startActivity(intent);
            }
        });
        mBtnBookSetting.setOnClickListener(this);

        popupView = getActivity().getLayoutInflater().inflate(R.layout.book_setting_menu, null);
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
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)


        mBookCover.setOnClickListener(this);
        mBookTitle.setOnClickListener(this);
        mBookDudate.setOnClickListener(this);
        mBookStatus.setOnClickListener(this);

        ImageLoader.getInstance().displayImage(bucket.getCvrImgUrl(), mBookCoverImage);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId() == mBtnBookSetting.getId()){
            if(mPopupWindow != null && !mPopupWindow.isShowing() && !view.isSelected()){
                mPopupWindow.showAsDropDown(view);
                view.setSelected(true);
            }else{
                mPopupWindow.hide();
                view.setSelected(false);
            }
        }
        if(view == mBookCover || view == mBookDudate || view == mBookDudate || view == mBookStatus){
            Intent intent = new Intent();
            intent.setClass(getActivity(), TimelineActivity.class);
            intent.putExtra(TimelineActivity.extraKey, bucket.getId());
            startActivity(intent);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    class PopupWindowViewHolder {
        @InjectView(R.id.btn_bucket_status_achievement)
        Button mBtnBucketStatusAchievement;
        @InjectView(R.id.btn_bucket_status_seal)
        Button mBtnBucketStatusSeal;
        @InjectView(R.id.btn_bucket_status_delete)
        Button mBtnBucketStatusDelete;

        PopupWindowViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
