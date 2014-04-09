package com.vivavu.dream.fragment.bucket.option.dday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.vivavu.dream.R;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.OptionDDay;
import com.vivavu.dream.util.DateUtils;

import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class DDayFragment extends OptionBaseFragment<OptionDDay> implements View.OnClickListener{
    @InjectView(R.id.btn_bucket_option_dday)
    Button mBtnBucketOptionDday;
    @InjectView(R.id.btn_range_1)
    Button mBtnRange1;
    @InjectView(R.id.btn_range_2)
    Button mBtnRange2;
    @InjectView(R.id.btn_range_3)
    Button mBtnRange3;
    @InjectView(R.id.btn_range_4)
    Button mBtnRange4;
    @InjectView(R.id.btn_range_5)
    Button mBtnRange5;
    @InjectView(R.id.btn_range_6)
    Button mBtnRange6;
    @InjectView(R.id.btn_range_in_my_life)
    Button mBtnRangeInMyLife;
    @InjectView(R.id.btn_range_custom)
    Button mBtnRangeCustom;
    @InjectView(R.id.layout_dday_option)
    LinearLayout mLayoutDdayOption;
    @InjectView(R.id.btn_custom_date_set)
    Button mBtnCustomDateSet;
    @InjectView(R.id.btn_custom_date_cancel)
    Button mBtnCustomDateCancel;
    @InjectView(R.id.custom_date)
    DatePicker mCustomDate;
    @InjectView(R.id.layout_bucket_add_custom_date)
    LinearLayout mLayoutBucketAddCustomDate;
    @InjectView(R.id.layout_bucket_option_note)
    LinearLayout mLayoutBucketOptionNote;

    public DDayFragment(OptionDDay originalData) {
        super(originalData);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<OptionDDay> list = DateUtils.getUserDdays(DateUtils.getDateFromString(DreamApp.getInstance().getUser().getBirthday(), "yyyyMMdd", null));
        makeDdaysButtonUi(list);
        addEventListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.bucket_option_dday, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);//배경선택시 키보드 없애기 위해 호출
        switch (view.getId()) {

            case R.id.btn_bucket_option_repeat_save:
                break;
            case R.id.btn_bucket_option_repeat_cancel:
                break;

        }
        if (view.getTag() != null && view.getTag() instanceof OptionDDay) {
            OptionDDay dday = (OptionDDay) view.getTag();
            updateUiData(dday);
        } else if (view.getId() == R.id.btn_range_in_my_life) {
            OptionDDay dday = new OptionDDay(getString(R.string.txt_btn_range_in_my_life), null);
            updateUiData(dday);
        } else if (view.getId() == R.id.btn_range_custom) {
            mLayoutBucketAddCustomDate.setVisibility(View.VISIBLE);
            mLayoutDdayOption.setVisibility(LinearLayout.GONE);
        } else if (view.getId() == R.id.btn_custom_date_set) {
            mLayoutDdayOption.setVisibility(LinearLayout.GONE);
            Date selectedDate = DateUtils.getDate(mCustomDate.getYear(), mCustomDate.getMonth(), mCustomDate.getDayOfMonth());
            OptionDDay dday = new OptionDDay("custom", selectedDate);

            updateUiData(dday);
        } else if(view.getId() == R.id.btn_custom_date_cancel){
            mLayoutBucketAddCustomDate.setVisibility(View.GONE);
            mLayoutDdayOption.setVisibility(LinearLayout.VISIBLE);
        }
    }

    private void addEventListener() {
        //기한선택
        mBtnRange1.setOnClickListener(this);
        mBtnRange2.setOnClickListener(this);
        mBtnRange3.setOnClickListener(this);
        mBtnRange4.setOnClickListener(this);
        mBtnRange5.setOnClickListener(this);
        mBtnRange6.setOnClickListener(this);
        mBtnRangeInMyLife.setOnClickListener(this);
        mBtnRangeCustom.setOnClickListener(this);

        //커스텀 기한선택
        mBtnCustomDateSet.setOnClickListener(this);
        //mCustomDate.set
        mBtnCustomDateCancel.setOnClickListener(this);
    }

    private void updateUiData(OptionDDay dday) {
        Intent intent = new Intent();
        intent.putExtra("option.result", dday);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void update() {

    }

    @Override
    public void bind() {

    }

    private void makeDdaysButtonUi(List<OptionDDay> ddays) {

        if (ddays.size() == 6) {
            mBtnRange1.setTag(ddays.get(0));
            mBtnRange2.setTag(ddays.get(1));
            mBtnRange3.setTag(ddays.get(2));
            mBtnRange4.setTag(ddays.get(3));
            mBtnRange5.setTag(ddays.get(4));
            mBtnRange6.setTag(ddays.get(5));

            mBtnRange1.setText(ddays.get(0).getRangeString());
            mBtnRange2.setText(ddays.get(1).getRangeString());
            mBtnRange3.setText(ddays.get(2).getRangeString());
            mBtnRange4.setText(ddays.get(3).getRangeString());
            mBtnRange5.setText(ddays.get(4).getRangeString());
            mBtnRange6.setText(ddays.get(5).getRangeString());
        }

        return;
    }
}
