package com.vivavu.dream.adapter.bucket.timeline;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.util.DateUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-04-01.
 */
public class TimelineListAdapter extends BaseAdapter {
    protected Context context;
    protected LayoutInflater layoutInflater;
    protected List<Post> postList;
    protected TimelineMetaInfo timelineMetaInfo;

    public TimelineListAdapter(Activity context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        /*if (timelineMetaInfo == null) {
            return 0;
        }
        return timelineMetaInfo.getCount();*/
        if (postList == null) {
            return 0;
        }
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return postList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ButterknifeViewHolder viewHolder = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.fragment_timeline_item, parent, false);
            viewHolder = new ButterknifeViewHolder(convertView);
        } else {
            viewHolder = (ButterknifeViewHolder) convertView.getTag();
        }
        final Post post = (Post) getItem(position);
        viewHolder.mTxtPostText.setText(post.getText());
        if(post.getImgUrl() == null){
            viewHolder.mTxtPostText.setMaxLines(5);
        } else {
            viewHolder.mTxtPostText.setMaxLines(3);
        }
        viewHolder.mTxtPostDate.setText(DateUtils.getDateString(post.getContentDt(), "yyyy.MM.dd HH:mm"));
        viewHolder.mTxtPostText.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());
        viewHolder.mBtnSeeMore.setVisibility(View.INVISIBLE);
        final ButterknifeViewHolder finalViewHolder = viewHolder;
        viewHolder.mTxtPostText.post(new Runnable() {
            @Override
            public void run() {
                if( post.getImgUrl() == null && finalViewHolder.mTxtPostText.getLineCount() == 5){
                    finalViewHolder.mBtnSeeMore.setVisibility(View.VISIBLE);
                } else if(post.getImgUrl() != null && finalViewHolder.mTxtPostText.getLineCount() == 3){
                    finalViewHolder.mBtnSeeMore.setVisibility(View.VISIBLE);
                }
            }
        });


        ImageLoader.getInstance().displayImage(post.getImgUrl(), viewHolder.mIvTimelineImage, new SimpleImageLoadingListener(){
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

        convertView.setTag(viewHolder);
        return convertView;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public TimelineMetaInfo getTimelineMetaInfo() {
        return timelineMetaInfo;
    }

    public void setTimelineMetaInfo(TimelineMetaInfo timelineMetaInfo) {
        this.timelineMetaInfo = timelineMetaInfo;
    }


/**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static class ButterknifeViewHolder {
        @InjectView(R.id.txt_post_date)
        TextView mTxtPostDate;
        @InjectView(R.id.txt_post_text)
        TextView mTxtPostText;
        @InjectView(R.id.btn_see_more)
        TextView mBtnSeeMore;
        @InjectView(R.id.iv_timeline_image)
        ImageView mIvTimelineImage;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
