package com.vivavu.dream.fragment.bucket.option.repeat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.OptionRepeat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class RepeatViewFragment extends OptionBaseFragment<OptionRepeat> implements View.OnClickListener{
    public static final String TAG = "com.vivavu.dream.fragment.bucket.option.repeat.RepeatViewFragment";
    @InjectView(R.id.option_view_icon)
    ImageView mOptionViewIcon;
    @InjectView(R.id.option_view_contents)
    TextView mOptionViewContents;
    @InjectView(R.id.option_view_layer)
    LinearLayout mOptionViewLayer;


    public RepeatViewFragment(OptionRepeat optionRepeat) {
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

        View rootView =  inflater.inflate(R.layout.bucket_option_view, container, false);
        ButterKnife.inject(this, rootView);
        mOptionViewIcon.setImageResource(R.drawable.repeat_2);
        mOptionViewContents.setOnClickListener(this);

        update();
        return rootView;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);//배경선택시 키보드 없애기 위해 호출
        BucketAddActivity bucketAddActivity = (BucketAddActivity) getActivity();
        bucketAddActivity.goOptionRepeat();
    }

    @Override
    public void update() {
        mOptionViewContents.setText(contents.getOptionContents());
    }

    @Override
    public void bind() {

    }
}
