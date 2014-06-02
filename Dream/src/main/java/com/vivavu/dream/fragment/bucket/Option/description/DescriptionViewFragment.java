package com.vivavu.dream.fragment.bucket.option.description;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketEditActivity;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.OptionDescription;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class DescriptionViewFragment extends OptionBaseFragment<OptionDescription> implements View.OnClickListener {
    public static final String TAG = "com.vivavu.dream.fragment.bucket.option.description.DescriptionViewFragment";
    @InjectView(R.id.btn_bucket_option_note)
    Button mBtnBucketOptionNote;
    @InjectView(R.id.bucket_option_note)
    TextView mBucketOptionNote;
    @InjectView(R.id.layout_bucket_option_note)
    LinearLayout mLayoutBucketOptionNote;

    public DescriptionViewFragment(OptionDescription originalData) {
        super(originalData);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.bucket_option_description_view, container, false);
        ButterKnife.inject(this, rootView);
        for(int i = 0; i < rootView.getChildCount(); i++){
            View v = rootView.getChildAt(i);
            v.setOnClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);//배경선택시 키보드 없애기 위해 호출
        BucketEditActivity bucketEditActivity = (BucketEditActivity) getActivity();
        bucketEditActivity.goOptionDescription();
    }

    @Override
    public void update() {
        mBucketOptionNote.setText(contents.getDescription());
    }

    @Override
    public void bind() {

    }
}
