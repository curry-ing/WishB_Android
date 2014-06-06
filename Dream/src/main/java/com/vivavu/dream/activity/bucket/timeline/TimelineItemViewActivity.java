package com.vivavu.dream.activity.bucket.timeline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.util.DateUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-28.
 */
public class TimelineItemViewActivity extends BaseActionBarActivity{
    public static final String TAG = "com.vivavu.dream.activity.bucket.timeline.TimelineItemViewActivity";
    public static final String extraKeyReturnValue = "extraKeyReturnValue";
    public static final int REQUEST_MOD_POST = 0;
    @InjectView(R.id.txt_post_date)
    TextView mTxtPostDate;
    @InjectView(R.id.btn_timeline_edit)
    Button mBtnTimelineEdit;
    @InjectView(R.id.txt_post_text)
    TextView mTxtPostText;
    @InjectView(R.id.iv_timeline_image)
    ImageView mIvTimelineImage;
    @InjectView(R.id.container_post_info)
    LinearLayout mContainerPostInfo;
    @InjectView(R.id.content_frame)
    LinearLayout mContentFrame;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;
    @InjectView(R.id.menu_more)
    ImageButton mMenuMore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_timeline_item_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_before);
        actionBar.setCustomView(R.layout.actionbar_timeline_view);
        actionBar.setDisplayShowCustomEnabled(true);

        ButterKnife.inject(this);

        Intent data = getIntent();
        Bucket bucket = (Bucket) data.getSerializableExtra(TimelineActivity.extraKeyBucket);
        Post post  = (Post) data.getSerializableExtra(TimelineActivity.extraKeyPost);
        post.setBucketId(bucket.getId());

        mTxtTitle.setText(bucket.getTitle());
        bindData(post);
        mBtnTimelineEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEdit();
            }
        });

        mMenuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu, giving it the clicked view for an anchor
                PopupMenu popup = new PopupMenu(TimelineItemViewActivity.this, v);

                // Inflate our menu resource into the PopupMenu's Menu
                popup.getMenuInflater().inflate(R.menu.menu_timeline_item_view, popup.getMenu());

                // Set a listener so we are notified if a menu item is clicked
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_remove:

                                return true;
                            case R.id.menu_share:

                                return true;
                        }
                        return false;
                    }
                });

                // Finally show the PopupMenu
                popup.show();
            }
        });

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
}
