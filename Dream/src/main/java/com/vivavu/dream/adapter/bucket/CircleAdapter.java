package com.vivavu.dream.adapter.bucket;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.view.CircleBucketImageView;
import com.vivavu.lib.view.circular.CircularAdapter;
import com.vivavu.lib.view.circular.CircularItemContainer;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuja on 2014-04-24.
 */
public class CircleAdapter extends CircularAdapter<Bucket> {
    public CircleAdapter(Context mContext, List<Bucket> mList) {
        super(mContext, mList);
    }

    @Override
    public Bucket getItem(int position) {
        if(0 > position || position >= getCount()){
            return null;
        }

        return super.getItem(position);
    }

    @Override
    public CircularItemContainer getView(int position, View convertView, ViewGroup parent) {

        Bucket data = getItem(position);

        CircleBucketImageView circularItemContainer = new CircleBucketImageView(mContext);
        circularItemContainer.setIndex(position);
        circularItemContainer.setBackgroundResource(R.drawable.sub_view_default_circle);

        circularItemContainer.setBucket(data);
        if(data == null ){
            circularItemContainer.setEmpty(true);
        }else{
            circularItemContainer.setEmpty(false);
        }

        return circularItemContainer;
    }
/**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static

    class ButterknifeViewHolder {
        @InjectView(R.id.txt)
        TextView mTxt;
        /*@InjectView(R.id.img_bucket)
        ImageView mImgBucket;
        @InjectView(R.id.img_mask)
        ImageView mImgMask;*/

        @InjectView(R.id.img_bucket)
        CircleImageView mImgBucket;
        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
