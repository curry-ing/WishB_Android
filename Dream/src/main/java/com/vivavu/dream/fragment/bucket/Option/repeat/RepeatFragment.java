package com.vivavu.dream.fragment.bucket.option.repeat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.vivavu.dream.R;
import com.vivavu.dream.common.enums.RepeatType;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.OptionRepeat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class RepeatFragment extends OptionBaseFragment<OptionRepeat> implements View.OnClickListener{
    public static final String TAG = "com.vivavu.dream.fragment.bucket.option.repeat.RepeatFragment";
    @InjectView(R.id.btn_bucket_option_sun)
    Button mBtnBucketOptionSun;
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
    @InjectView(R.id.txt_bucket_option_repeat_cnt)
    EditText mTxtBucketOptionRepeatCnt;
    @InjectView(R.id.layout_bucket_option_repeat_custom)
    LinearLayout mLayoutBucketOptionRepeatCustom;
    @InjectView(R.id.layout_bucket_option_repeat_week)
    LinearLayout mLayoutBucketOptionRepeatWeek;

    public RepeatFragment(OptionRepeat optionRepeat) {
        super(optionRepeat);
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

        final View rootView = inflater.inflate(R.layout.bucket_option_repeat, container, false);
        ButterKnife.inject(this, rootView);

        mBtnBucketOptionSun.setOnClickListener(this);
        mBtnBucketOptionMon.setOnClickListener(this);
        mBtnBucketOptionTue.setOnClickListener(this);
        mBtnBucketOptionWen.setOnClickListener(this);
        mBtnBucketOptionThu.setOnClickListener(this);
        mBtnBucketOptionFri.setOnClickListener(this);
        mBtnBucketOptionSat.setOnClickListener(this);

        mTxtBucketOptionRepeatCnt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String edit = charSequence.toString();
                if (edit.length() > 0) {
                    contents.setRepeatCount(Integer.parseInt(edit));
                } else {
                    contents.setRepeatCount(0);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        update();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);//배경선택시 키보드 없애기 위해 호출
        switch (view.getId()) {
            case R.id.btn_bucket_option_sun:
                mBtnBucketOptionSun.setSelected(!mBtnBucketOptionSun.isSelected());
                contents.setSun(mBtnBucketOptionSun.isSelected());
                break;
            case R.id.btn_bucket_option_mon:
                mBtnBucketOptionMon.setSelected(!mBtnBucketOptionMon.isSelected());
                contents.setMon(mBtnBucketOptionMon.isSelected());
                break;
            case R.id.btn_bucket_option_tue:
                mBtnBucketOptionTue.setSelected(!mBtnBucketOptionTue.isSelected());
                contents.setTue(mBtnBucketOptionTue.isSelected());
                break;
            case R.id.btn_bucket_option_wen:
                mBtnBucketOptionWen.setSelected(!mBtnBucketOptionWen.isSelected());
                contents.setWen(mBtnBucketOptionWen.isSelected());
                break;
            case R.id.btn_bucket_option_thu:
                mBtnBucketOptionThu.setSelected(!mBtnBucketOptionThu.isSelected());
                contents.setThu(mBtnBucketOptionThu.isSelected());
                break;
            case R.id.btn_bucket_option_fri:
                mBtnBucketOptionFri.setSelected(!mBtnBucketOptionFri.isSelected());
                contents.setFri(mBtnBucketOptionFri.isSelected());
                break;
            case R.id.btn_bucket_option_sat:
                mBtnBucketOptionSat.setSelected(!mBtnBucketOptionSat.isSelected());
                contents.setSat(mBtnBucketOptionSat.isSelected());
                break;
        }
    }

    @Override
    public OptionRepeat getContents() {
        if(mLayoutBucketOptionRepeatWeek.getVisibility() == View.VISIBLE){
            contents.setRepeatType(RepeatType.WKRP);
        } else {
            /*RepeatType repeatType = (RepeatType) mSpinRepeatPeriod.getSelectedItem();
            contents.setRepeatType(repeatType);*/
        }

        return contents;
    }

    @Override
    public void update() {
        if (contents.getRepeatType() == RepeatType.WKRP) {
            mBtnBucketOptionSun.setSelected(contents.isSun());
            mBtnBucketOptionMon.setSelected(contents.isMon());
            mBtnBucketOptionTue.setSelected(contents.isTue());
            mBtnBucketOptionWen.setSelected(contents.isWen());
            mBtnBucketOptionThu.setSelected(contents.isThu());
            mBtnBucketOptionFri.setSelected(contents.isFri());
            mBtnBucketOptionSat.setSelected(contents.isSat());
        } else if (contents.getRepeatType() == RepeatType.WEEK) {
            //mSpinRepeatPeriod.setSelection(0);
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(contents.getRepeatCount()));
        } else if (contents.getRepeatType() == RepeatType.MNTH) {
            //mSpinRepeatPeriod.setSelection(1);
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(contents.getRepeatCount()));
        }
    }

    @Override
    public void bind() {

    }
}
