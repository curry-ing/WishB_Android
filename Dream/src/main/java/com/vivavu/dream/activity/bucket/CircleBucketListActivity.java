package com.vivavu.dream.activity.bucket;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.CircleAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.repository.DataRepository;
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

        List<BucketGroup> bucketGroup = DataRepository.listBucketGroup();
        //List itemList = CircularViewTestActivity.getDummyData();

        CircularAdapter circularAdapter = new CircleAdapter(mContext, bucketGroup.get(0).getBukets());
        mLayoutCard.setAdapter(circularAdapter);
        if(bucketGroup.get(0).getBukets().size() > 0) {
            mTxtIndicator.setText(String.format("%d Lists", bucketGroup.get(0).getBukets().size()));
        } else {
            mTxtIndicator.setText(String.format("Add Lists"));
        }

    }
}
