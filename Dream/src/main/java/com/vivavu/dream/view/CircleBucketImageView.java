package com.vivavu.dream.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.lib.view.circular.CircularItemContainer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuja on 2014-05-02.
 */
public class CircleBucketImageView extends CircularItemContainer {
    protected Bucket bucket;
    protected boolean isMainItem = false;

    @InjectView(R.id.img_bucket)
    CircleImageView mImgBucket;
    @InjectView(R.id.txt)
    TextView mTxt;
    @InjectView(R.id.empty_image_view)
    TextView mEmptyImageView;
    @InjectView(R.id.empty_image_text)
    TextView mEmptyImageText;

    public CircleBucketImageView(Context context) {
        this(context, null);
    }

    public CircleBucketImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleBucketImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.sub_view_circle_item, null);
        ButterKnife.inject(this, v);

        update();

        addView(v);
    }

    public void update(){
        if(bucket != null) {
            mTxt.setText(bucket.getTitle());
            if(bucket.getCvrImgUrl() != null) {
                mEmptyImageView.setVisibility(GONE);
                mEmptyImageView.setVisibility(GONE);
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.no_image)
                        .showImageOnFail(R.drawable.no_image)
                        .cacheInMemory(true)
                        .cacheOnDisc(true)
                        .build();
                ImageLoader.getInstance().displayImage(bucket.getCvrImgUrl(), mImgBucket, options);
            }else{
                mEmptyImageView.setVisibility(VISIBLE);
                if(isMainItem){
                    mEmptyImageText.setVisibility(VISIBLE);
                }else{
                    mEmptyImageView.setVisibility(GONE);
                }
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(isMainItem){

        }
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public CircleImageView getImgBucket() {
        return mImgBucket;
    }

    public TextView getTxt() {
        return mTxt;
    }

    public boolean isMainItem() {
        return isMainItem;
    }

    public void setMainItem(boolean isMainItem) {
        this.isMainItem = isMainItem;
    }
}
