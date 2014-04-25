package com.vivavu.dream.adapter.bucket;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.vivavu.dream.R;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.drawable.RoundedAvatarDrawable;
import com.vivavu.dream.model.bucket.BucketGroup;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by masunghoon on 4/20/14.
 */
public class BucketAdapter2 extends PagerAdapter implements View.OnClickListener{

    private Context context;
    private Fragment fragment;
    private LayoutInflater mInflater;
    private List<BucketGroup> bucketGroupList;
    private List<Bitmap> mainImages;

    private String userBirth;

    static public final int PROGRESS_BAR_BASELINE = 270;

    public BucketAdapter2 (Fragment fragment, List<BucketGroup> bucketGroupList) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketGroupList = new ArrayList<BucketGroup>(bucketGroupList);
    }

    public BucketAdapter2 (Fragment fragment) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketGroupList = new ArrayList<BucketGroup>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
//        int i = position;
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.main_contents, container, false);
        ButterknifeViewHolder holder = new ButterknifeViewHolder(viewGroup);
        viewGroup.setTag(holder);

        try {
            init(holder, bucketGroupList.get(position), position);
        } catch (IOException e) {
            e.printStackTrace();
        }
        container.addView(viewGroup);

        return viewGroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount(){
        return bucketGroupList.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return (view == object);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void init(ButterknifeViewHolder holder, BucketGroup bucketGroup, int pos) throws IOException {
        this.mainImages = new ArrayList<Bitmap>();

        holder.mBtnDecade.setText(bucketGroup.getRangeText());
        holder.mBtnDecade.setOnClickListener(this);
        int cnt = bucketGroup.getCount();
        if (cnt > 0) {
            holder.mBktCount.setVisibility(View.VISIBLE);
            for (int i=0; i<cnt; i++){
                if(bucketGroup.getBukets().get(i).getCvrImgUrl() != null) {
                    URL url = new URL(bucketGroup.getBukets().get(i).getCvrImgUrl());
                    mainImages.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                }
            }
        }
        if(mainImages.size()==0){
            mainImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.up_logo));
        }
        holder.mBktCount.setText(String.valueOf(cnt));
        for (int j=0; j<mainImages.size(); j++){
            switch (j){
                case 0:
                    holder.mMainImage1.setBackground(new RoundedAvatarDrawable(mainImages.get(j), j+1, mainImages.size()));
                    break;
                case 1:
                    holder.mMainImage2.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 2:
                    holder.mMainImage3.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 3:
                    holder.mMainImage4.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 4:
                    holder.mMainImage5.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 5:
                    holder.mMainImage6.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 6:
                    holder.mMainImage7.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 7:
                    holder.mMainImage8.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
            }
        }
        if(pos>0){
            Calendar cal = java.util.Calendar.getInstance();
            int startY = Integer.parseInt(DreamApp.getInstance().getUser().getBirthday().substring(0,4))+(pos*10) - 1;
            int endY = Integer.parseInt(DreamApp.getInstance().getUser().getBirthday().substring(0,4))+((pos+1)*10-1) - 1;
            int thisY = cal.get(cal.YEAR);

            holder.mPeriod.setText(String.valueOf(startY)+" ~ "+String.valueOf(endY));
            if(thisY > endY) {
                holder.mMainProgress.setImageDrawable(new RoundedAvatarDrawable(null, 360, PROGRESS_BAR_BASELINE));
            } else if (thisY < startY) {
                holder.mMainProgress.setImageDrawable(new RoundedAvatarDrawable(null, 0, PROGRESS_BAR_BASELINE));
            } else {
                holder.mMainProgress.setImageDrawable(new RoundedAvatarDrawable(null, cal.get(cal.DAY_OF_YEAR), PROGRESS_BAR_BASELINE));
            }
        } else {
            int imsi = (int) ((float) DreamApp.getInstance().getUser().getUserAge() / 100 * 360);
            holder.mMainProgress.setImageDrawable(new RoundedAvatarDrawable(null, imsi, PROGRESS_BAR_BASELINE));
            holder.mPeriod.setText(String.valueOf(DreamApp.getInstance().getUser().getUserAge()) + " 짤 임미다!!");
        }
    }

    public List<BucketGroup> getBucketGroupList() {
        return bucketGroupList;
    }

    public void setBucketGroupList(List<BucketGroup> bucketGroupList) {
        this.bucketGroupList = bucketGroupList;
    }

    @Override
    public void onClick(View v){

    }

    class ButterknifeViewHolder {
        @InjectView(R.id.btn_decade)
        Button mBtnDecade;
        @InjectView(R.id.bkt_count)
        TextView mBktCount;
        @InjectView(R.id.main_image1)
        ImageView mMainImage1;
        @InjectView(R.id.main_image2)
        ImageView mMainImage2;
        @InjectView(R.id.main_image3)
        ImageView mMainImage3;
        @InjectView(R.id.main_image4)
        ImageView mMainImage4;
        @InjectView(R.id.main_image5)
        ImageView mMainImage5;
        @InjectView(R.id.main_image6)
        ImageView mMainImage6;
        @InjectView(R.id.main_image7)
        ImageView mMainImage7;
        @InjectView(R.id.main_image8)
        ImageView mMainImage8;
        @InjectView(R.id.progress_bar)
        ImageView mMainProgress;
        @InjectView(R.id.period)
        TextView mPeriod;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
