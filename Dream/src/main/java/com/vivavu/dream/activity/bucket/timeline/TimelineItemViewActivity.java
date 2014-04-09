package com.vivavu.dream.activity.bucket.timeline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.util.DateUtils;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-28.
 */
public class TimelineItemViewActivity extends BaseActionBarActivity{
    public static final String TAG = "com.vivavu.dream.activity.bucket.timeline.TimelineItemViewActivity";
    public static final String extraKeyReturnValue = "extraKeyReturnValue";
    public static final int REQUEST_MOD_POST = 0;

    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.txt_start_date)
    TextView mTxtStartDate;
    @InjectView(R.id.txt_current_date)
    TextView mTxtCurrentDate;
    @InjectView(R.id.txt_end_date)
    TextView mTxtEndDate;
    @InjectView(R.id.container_bucket_info)
    LinearLayout mContainerBucketInfo;
    @InjectView(R.id.txt_post_text)
    TextView mTxtPostText;
    @InjectView(R.id.txt_post_date)
    TextView mTxtPostDate;
    @InjectView(R.id.content_frame)
    LinearLayout mContentFrame;
    @InjectView(R.id.btn_timeline_title)
    Button mBtnTimelineTitle;
    @InjectView(R.id.iv_timeline_image)
    ImageView mIvTimelineImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_item_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_timeline_title_only);

        ButterKnife.inject(this);

        Intent data = getIntent();
        Bucket bucket = (Bucket) data.getSerializableExtra(TimelineActivity.extraKeyBucket);
        Post post  = (Post) data.getSerializableExtra(TimelineActivity.extraKeyPost);
        post.setBucketId(bucket.getId());

        bindData(bucket);
        bindData(post);

    }

    private void bindData(Post post) {
        mTxtPostText.setText(post.getText());
        mTxtPostDate.setText(DateUtils.getDateString(post.getRegDt(), "yyyy.MM.dd hh:mm"));
        ImageLoader.getInstance().displayImage(post.getImgUrl(), mIvTimelineImage, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                // 이미지가 없을 경우에는 imageview 자체를 안보여줌
                if(loadedImage != null) {
                    view.setVisibility(View.VISIBLE);
                }else {
                    view.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline_item_view_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit:
                goEdit();
                return true;
            case R.id.menu_remove:
                removePost();
                return true;
            case R.id.menu_share:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_MOD_POST:
                if(resultCode == RESULT_OK){
                    Post returnValue = (Post) data.getSerializableExtra(extraKeyReturnValue);
                    bindData(returnValue);
                }
                return;
        }
    }

    private void removePost() {

    }

    private void goEdit() {
        Intent intent = getIntent();
        intent.setClass(this, TimelineItemEditActivity.class);
        startActivityForResult(intent, REQUEST_MOD_POST);
    }

    private void bindData(Bucket bucket) {
        mBtnTimelineTitle.setText(bucket.getTitle());
        Date start = bucket.getRegDate();
        Date current = new Date();
        Date end = bucket.getDeadline();
        mTxtStartDate.setText(DateUtils.getDateString(start, "yyyy.MM.dd"));
        mTxtCurrentDate.setText(DateUtils.getDateString(current, "yyyy.MM.dd"));
        mTxtEndDate.setText(DateUtils.getDateString(end, "yyyy.MM.dd"));

        if(start != null && end != null) {
            int progress = DateUtils.getProgress(start, end);
            mProgressBar.setProgress(progress);
        }
    }
}
