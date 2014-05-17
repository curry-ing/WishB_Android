package com.vivavu.dream.activity.bucket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.CircleAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.view.CircleBucketImageView;
import com.vivavu.lib.view.circular.CircularAdapter;
import com.vivavu.lib.view.circular.SemiCircularList;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CircleBucketListActivity extends BaseActionBarActivity {
    @InjectView(R.id.txt_indicator)
    TextView mTxtIndicator;
    @InjectView(R.id.layout_card)
    SemiCircularList mLayoutCard;
    @InjectView(R.id.btn_add)
    Button mBtnAdd;
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;
    @InjectView(R.id.btn_today)
    Button mBtnToday;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_bucket_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_sub_view);
        ButterKnife.inject(this);

        mContext = DreamApp.getInstance();

        Intent data = getIntent();
        String groupRange = data.getStringExtra("groupRange");

        List<Bucket> bucketList = DataRepository.listBucketByRange(groupRange);
        //List itemList = CircularViewTestActivity.getDummyData();
        if(null == groupRange){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_life());
        } else if("10".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_10());
        }else if("20".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_20());
        }else if("30".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_30());
        }else if("40".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_40());
        }else if("50".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_50());
        }else if("60".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_60());
        }

        CircularAdapter circularAdapter;
        circularAdapter = new CircleAdapter(mContext, bucketList);
        mLayoutCard.setAdapter(circularAdapter);

        if(bucketList.size() > 0) {
            mTxtIndicator.setText(String.format("%d", bucketList.size()));
        }

        mLayoutCard.setOnMainItemChangedListener(new SemiCircularList.OnMainItemChangedListener() {
            @Override
            public void onMainItemChanged(int position, View view) {

                if(!mLayoutCard.isDrag() && view instanceof CircleBucketImageView) {
                    int index = ((CircleBucketImageView) view).getIndex();
                    Adapter adapter = mLayoutCard.getAdapter();
                    Bucket item = (Bucket) adapter.getItem(index);
                    mTitle.setText(index + "   " + item.getTitle());
                }
            }
        });

        mLayoutCard.setOnRotateEndedListener(new SemiCircularList.OnRotateEndedListener() {
            @Override
            public void onRotateEnded(int position, View mainItem) {
                if( mainItem instanceof CircleBucketImageView) {
                    int index = ((CircleBucketImageView) mainItem).getIndex();
                    Adapter adapter = mLayoutCard.getAdapter();
                    Bucket item = (Bucket) adapter.getItem(index);
                    mTitle.setText(index + "   " + item.getTitle());
                }
            }
        });

        mLayoutCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CircleBucketListActivity.this, "아이템 선택 : #"+position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
