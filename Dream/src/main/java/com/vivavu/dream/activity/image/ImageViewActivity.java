package com.vivavu.dream.activity.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-06-20.
 */
public class ImageViewActivity extends BaseActionBarActivity {
    @InjectView(R.id.image_view)
    ImageView mImageView;

    public static final String IMAGE_VIEW_DATA_KEY = "image_uri";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ButterKnife.inject(this);
        Intent intent = getIntent();
        String imageUri = intent.getStringExtra(IMAGE_VIEW_DATA_KEY);

        ImageLoader.getInstance().displayImage(imageUri, mImageView);
    }
}
