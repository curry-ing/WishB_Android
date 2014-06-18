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
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.util.DateUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-04-01.
 */
public class TimelineListAdapter2 extends BaseAdapter {
    protected Context context;
    protected LayoutInflater layoutInflater;
    protected List<Post> postList;

    public TimelineListAdapter2(Activity context, List<Post> postList) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.postList = postList;
    }

    @Override
    public int getCount() {
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
        /*if(convertView == null){*/
            convertView = layoutInflater.inflate(R.layout.fragment_timeline_item, parent, false);
            viewHolder = new ButterknifeViewHolder(convertView);
        /*} else {
            viewHolder = (ButterknifeViewHolder) convertView.getTag();
        }*/
        Post post = (Post) getItem(position);
        viewHolder.mTxtPostText.setText(post.getText());
        //viewHolder.mTxtPostDate.setText(DateUtils.getDateString(post.getTimestamp(), "yyyy.MM.dd HH:mm"));//Todo:타임스템프로 바꿔야함
        viewHolder.mTxtPostDate.setText(DateUtils.getDateString(post.getRegDt(), "yyyy.MM.dd HH:mm"));

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
        return convertView;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }
    /**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static class ButterknifeViewHolder {
        @InjectView(R.id.txt_post_text)
        TextView mTxtPostText;
        @InjectView(R.id.iv_timeline_image)
        ImageView mIvTimelineImage;
        @InjectView(R.id.txt_post_date)
        TextView mTxtPostDate;


    ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
