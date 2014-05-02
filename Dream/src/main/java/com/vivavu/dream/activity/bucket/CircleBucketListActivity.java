package com.vivavu.dream.activity.bucket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.CircleAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.lib.view.circular.CircularAdapter;
import com.vivavu.lib.view.circular.CircularItemContainer;
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

        CircularAdapter circularAdapter;
        circularAdapter = new CircleAdapter(mContext, bucketList);
        mLayoutCard.setAdapter(circularAdapter);
        if(bucketList.size() > 0) {
            mTxtIndicator.setText(String.format("%d Lists", bucketList.size()));
        } else {
            mTxtIndicator.setText(String.format("Add Lists"));
        }
        mLayoutCard.setOnMainItemChangedListener(new SemiCircularList.OnMainItemChangedListener() {
            @Override
            public void onMainItemChanged(int position, View view) {
                if(view instanceof CircularItemContainer) {
                    int index = ((CircularItemContainer) view).getIndex();
                    Adapter adapter = mLayoutCard.getAdapter();
                    Bucket item = (Bucket) adapter.getItem(index);
                    mTitle.setText(index + "   " + item.getTitle());
                }
            }
        });

    }
}
