package com.vivavu.dream.activity.bucket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketListAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.DataRepository;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 10.
 */
public class BucketGroupViewActivity extends BaseActionBarActivity {

    @InjectView(R.id.grid_bucket_list)
    GridView mGridBucketList;
    @InjectView(R.id.btn_add_bucket)
    Button mBtnAddBucket;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;
    @InjectView(R.id.menu_previous)
    Button mMenuPrevious;
    @InjectView(R.id.btn_today)
    Button mBtnToday;
    @InjectView(R.id.layout_sub_view_background)
    RelativeLayout mLayoutSubViewBackground;
    private Bucket bucket;

    /*private MainContentsFragment mainContentsFragment;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.bucket_sub_view);
        setResult(RESULT_CANCELED);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_before);
        actionBar.setCustomView(R.layout.actionbar_sub_view);
        actionBar.setDisplayShowCustomEnabled(true);

        ButterKnife.inject(this);

        Intent data = getIntent();
        String groupRange = data.getStringExtra("groupRange");

        List<Bucket> bucketList = DataRepository.listBucketByRange(groupRange);
        //List itemList = CircularViewTestActivity.getDummyData();
        if(null == groupRange){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_life());
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg00);
        } else if("10".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_10());
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg10);
        }else if("20".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_20());
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg20);
        }else if("30".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_30());
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg30);
        }else if("40".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_40());
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg40);
        }else if("50".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_50());
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg50);
        }else if("60".equals(groupRange)){
            mTxtTitle.setText(DreamApp.getInstance().getUser().getTitle_60());
            mLayoutSubViewBackground.setBackgroundResource(R.drawable.mainview_bg60);
        }

        mGridBucketList.setAdapter(new BucketListAdapter(DreamApp.getInstance(), bucketList));
        mGridBucketList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(BucketGroupViewActivity.this, position + "번 아이템 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BucketGroupViewActivity.this, "투데이", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
