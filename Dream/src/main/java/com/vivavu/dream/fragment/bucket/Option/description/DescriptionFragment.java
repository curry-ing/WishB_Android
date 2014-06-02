package com.vivavu.dream.fragment.bucket.option.description;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.vivavu.dream.R;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.OptionDescription;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class DescriptionFragment extends OptionBaseFragment<OptionDescription> implements View.OnClickListener {
    public static final String TAG = "com.vivavu.dream.fragment.bucket.option.description.DescriptionFragment";
    @InjectView(R.id.btn_bucket_option_note)
    Button mBtnBucketOptionNote;
    @InjectView(R.id.bucket_option_note)
    EditText mBucketOptionNote;
    @InjectView(R.id.layout_bucket_option_note)
    LinearLayout mLayoutBucketOptionNote;

    public DescriptionFragment(OptionDescription originalData) {
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

        final View rootView = inflater.inflate(R.layout.bucket_option_description, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);//배경선택시 키보드 없애기 위해 호출
        switch (view.getId()) {

        }
    }

    @Override
    public void update() {
        mBucketOptionNote.setText(contents.getDescription());
    }

    @Override
    public void bind() {
        contents.setDescription(mBucketOptionNote.getText().toString());
    }


}
