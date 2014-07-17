package com.vivavu.dream.fragment.bucket.option.description;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;
import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
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
    @InjectView(R.id.description)
    TextView mDescription;

    public DescriptionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.bucket_option_description, container, false);
        ButterKnife.inject(this, rootView);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mDescription.setTypeface(BaseActionBarActivity.getNanumBarunGothicBoldFont());

        mBucketOptionNote.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());
        mBucketOptionNote.setTextSize(18);
        mBucketOptionNote.setTextColor(Color.GRAY);


        mBucketOptionNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = mBucketOptionNote.getText();

                if(contents.getDescription() == null){
                    if(text == null ) {
                        setModFlag(false);
                    }else if(text != null && text.toString().length() == 0){
                        setModFlag(false);
                    } else {
                        setModFlag(true);
                    }
                } else {
                    if(text == null){
                        setModFlag(contents.getDescription().length() > 0);
                    } else {
                        setModFlag(!contents.getDescription().equals(text.toString()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        update();
        return rootView;
    }

    @Override
    public void update() {
        if(contents == null){
            return;
        }
        mBucketOptionNote.setText(contents.getDescription());
    }

    @Override
    public void bind() {
        contents.setDescription(mBucketOptionNote.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        mBucketOptionNote.selectAll();
        mBucketOptionNote.requestFocus();
    }
}
