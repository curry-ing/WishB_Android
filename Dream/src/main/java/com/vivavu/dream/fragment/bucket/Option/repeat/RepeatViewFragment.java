package com.vivavu.dream.fragment.bucket.option.repeat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.common.enums.RepeatType;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.OptionRepeat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class RepeatViewFragment extends OptionBaseFragment<OptionRepeat> implements View.OnClickListener{
    public static final String TAG = "com.vivavu.dream.fragment.bucket.option.repeat.RepeatViewFragment";
    @InjectView(R.id.btn_bucket_option_repeat)
    Button mBtnBucketOptionRepeat;
    @InjectView(R.id.btn_bucket_option_mon)
    Button mBtnBucketOptionMon;
    @InjectView(R.id.btn_bucket_option_tue)
    Button mBtnBucketOptionTue;
    @InjectView(R.id.btn_bucket_option_wen)
    Button mBtnBucketOptionWen;
    @InjectView(R.id.btn_bucket_option_thu)
    Button mBtnBucketOptionThu;
    @InjectView(R.id.btn_bucket_option_fri)
    Button mBtnBucketOptionFri;
    @InjectView(R.id.btn_bucket_option_sat)
    Button mBtnBucketOptionSat;
    @InjectView(R.id.btn_bucket_option_sun)
    Button mBtnBucketOptionSun;
    @InjectView(R.id.layout_bucket_option_repeat_week)
    LinearLayout mLayoutBucketOptionRepeatWeek;
    @InjectView(R.id.btn_bucket_option_week)
    Button mBtnBucketOptionWeek;
    @InjectView(R.id.btn_bucket_option_month)
    Button mBtnBucketOptionMonth;
    @InjectView(R.id.txt_bucket_option_repeat_cnt)
    TextView mTxtBucketOptionRepeatCnt;
    @InjectView(R.id.layout_bucket_option_repeat_custom)
    LinearLayout mLayoutBucketOptionRepeatCustom;

    public RepeatViewFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.bucket_option_repeat_view, container, false);
        ButterKnife.inject(this, rootView);
        for(int i = 0; i < rootView.getChildCount(); i++){
            View v = rootView.getChildAt(i);
            v.setOnClickListener(this);
        }
        mTxtBucketOptionRepeatCnt.setOnClickListener(this);
        update();
        return rootView;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);//배경선택시 키보드 없애기 위해 호출
        BucketEditActivity bucketEditActivity = (BucketEditActivity) getActivity();
        bucketEditActivity.goOptionRepeat();
    }

    @Override
    public void update() {
        if (contents.getRepeatType() == RepeatType.WKRP) {
            enableWeek(true);
            mBtnBucketOptionSun.setBackgroundResource(contents.isSun()?R.drawable.ic_week_sun_release : R.drawable.ic_week_sun_press);
            mBtnBucketOptionMon.setBackgroundResource(contents.isMon()?R.drawable.ic_week_mon_release : R.drawable.ic_week_mon_press);
            mBtnBucketOptionTue.setBackgroundResource(contents.isTue()?R.drawable.ic_week_tue_release : R.drawable.ic_week_tue_press);
            mBtnBucketOptionWen.setBackgroundResource(contents.isWen()?R.drawable.ic_week_wen_release : R.drawable.ic_week_wen_press);
            mBtnBucketOptionThu.setBackgroundResource(contents.isThu()?R.drawable.ic_week_thu_release : R.drawable.ic_week_thu_press);
            mBtnBucketOptionFri.setBackgroundResource(contents.isFri()?R.drawable.ic_week_fri_release : R.drawable.ic_week_fri_press);
            mBtnBucketOptionSat.setBackgroundResource(contents.isSat()?R.drawable.ic_week_sat_release : R.drawable.ic_week_sat_press);
        } else if (contents.getRepeatType() == RepeatType.WEEK) {
            enableWeek(false);

            mBtnBucketOptionWeek.setBackgroundResource(R.drawable.ic_option_week_release);
            mBtnBucketOptionMonth.setBackgroundResource(R.drawable.ic_option_month_press);
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(contents.getRepeatCount()));
        } else if (contents.getRepeatType() == RepeatType.MNTH) {
            enableWeek(false);

            mBtnBucketOptionWeek.setBackgroundResource(R.drawable.ic_option_week_press);
            mBtnBucketOptionMonth.setBackgroundResource(R.drawable.ic_option_month_release);
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(contents.getRepeatCount()));
        }
    }

    private void enableWeek(boolean week){
        if(week) {
            mLayoutBucketOptionRepeatWeek.setVisibility(View.VISIBLE);
            mLayoutBucketOptionRepeatCustom.setVisibility(View.GONE);
        }else{
            mLayoutBucketOptionRepeatWeek.setVisibility(View.GONE);
            mLayoutBucketOptionRepeatCustom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void bind() {

    }
}
