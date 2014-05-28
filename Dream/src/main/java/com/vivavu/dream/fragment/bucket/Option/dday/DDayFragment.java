package com.vivavu.dream.fragment.bucket.option.dday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.vivavu.dream.R;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.OptionDDay;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class DDayFragment extends OptionBaseFragment<OptionDDay> implements View.OnClickListener{

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
        if (view.getTag() != null && view.getTag() instanceof OptionDDay) {
            OptionDDay dday = (OptionDDay) view.getTag();
            updateUiData(dday);
        }
    }

    private void addEventListener() {

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

}
