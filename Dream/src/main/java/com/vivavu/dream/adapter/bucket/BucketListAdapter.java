package com.vivavu.dream.adapter.bucket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.TextImageView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-06-02.
 */
public class BucketListAdapter extends BaseAdapter {
    public static final int INVALID_ITEM_ID = -1;
    private List<Bucket> list;
    private Context mContext;

    public BucketListAdapter(Context mContext, List list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    @Override
    public Bucket getItem(int position) {
        int size = getCount();
        if(size > 0 && size > position && position > -1){
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Bucket item = getItem(position);
        if( item != null){
            return item.getId();
        }
        return INVALID_ITEM_ID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bucket item = getItem(position);
        if(item != null) {

            ButterknifeViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.bucket_sub_list_item, parent, false);
                holder = new ButterknifeViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ButterknifeViewHolder) convertView.getTag();
            }

            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.sub_view_item_padding);
            if(position < 2){
                convertView.setPadding(padding, mContext.getResources().getDimensionPixelSize(R.dimen.actionbar_height) + padding , padding, padding);
            } else {
                convertView.setPadding(padding, padding, padding, padding);
            }
            holder.mBucketItemTitle.setText(item.getTitle());
            holder.mBucketItemDeadline.setText(DateUtils.getDateString(item.getDeadline(), "yyyy.MM.dd"));
            ImageLoader.getInstance().displayImage(item.getCvrImgUrl(), holder.mBucketItemImg);
            return convertView;

        }
        return null;
    }
/**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static class ButterknifeViewHolder {
        @InjectView(R.id.bucket_item_img)
        TextImageView mBucketItemImg;
        @InjectView(R.id.bucket_item_title)
        TextView mBucketItemTitle;
        @InjectView(R.id.bucket_item_deadline)
        TextView mBucketItemDeadline;

        ButterknifeViewHolder(View view) {
             ButterKnife.inject(this, view);
        }
    }
}
