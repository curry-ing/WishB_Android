package com.vivavu.dream.adapter.bucket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.model.bucket.Bucket;
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
        if(position >= getCount()){
            return new Bucket("");
        }

        return super.getItem(position);
    }

    @Override
    public CircularItemContainer getView(int position, View convertView, ViewGroup parent) {

        CircularItemContainer circularItemContainer = new CircularItemContainer(mContext);
        circularItemContainer.setIndex(position);
        circularItemContainer.setBackgroundResource(R.drawable.sub_view_default_circle);

        View v = null;
        Bucket data = getItem(position);

        if (v == null)
        {
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.sub_view_circle_item, null);
            final ButterknifeViewHolder viewHolder = new ButterknifeViewHolder(v);

            viewHolder.mTxt.setText(position + " " + data.getTitle());

            //ImageLoader.getInstance().loadImage();
            ImageLoader.getInstance().displayImage(data.getCvrImgUrl(), viewHolder.mImgBucket);

        }

        circularItemContainer.addView(v);

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
