package com.vivavu.dream.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.lib.view.circular.CircularItemContainer;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-05-02.
 */
public class CircleBucketImageView extends CircularItemContainer {
    protected Bucket bucket;

    @InjectView(R.id.img_bucket)
    TextImageView mImgBucket;
    @InjectView(R.id.txt)
    TextView mTxt;

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
        /*ImageSize imageSize = new ImageSize(getWidth(), getHeight());
        ImageLoader.getInstance().loadImage("drawable://" + R.drawable.sub_view_circle_big_trans, imageSize, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(loadedImage != null){
                    mImgBucket.setFrontImage(loadedImage);
                }
            }
        });*/

        if(bucket != null) {
            mTxt.setText(bucket.getTitle());
            mImgBucket.setText(bucket.getTitle());
            if(bucket.getRegDate() != null && bucket.getDeadline()!= null) {
                mImgBucket.setPercent(DateUtils.getProgress(bucket.getRegDate(), bucket.getDeadline()));
            }
            //mImgBucket.setForegroundResource(R.drawable.sub_view_circle_trans);
            if(bucket.getCvrImgUrl() != null) {
                //mTxt.setVisibility(VISIBLE);
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.no_image)
                        .showImageOnFail(R.drawable.no_image)
                        .cacheInMemory(true)
                        .cacheOnDisc(true)
                        .build();
                ImageLoader.getInstance().displayImage(bucket.getCvrImgUrl(), mImgBucket, options);
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

    public ImageView getImgBucket() {
        return mImgBucket;
    }

    public TextView getTxt() {
        return mTxt;
    }

    @Override
    public void setMainItem(boolean isMainItem) {
        /*if(isMainItem() != isMainItem) {*/
            super.setMainItem(isMainItem());
            mImgBucket.setMain(isMainItem);
        /*}*/
            mImgBucket.invalidate();
    }
}
