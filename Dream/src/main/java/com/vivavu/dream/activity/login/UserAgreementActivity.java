package com.vivavu.dream.activity.login;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 20.
 */
public class UserAgreementActivity extends BaseActionBarActivity {
    @InjectView(R.id.txt_contents)
    TextView mTxtContents;
    @InjectView(R.id.scrollView)
    ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        ButterKnife.inject(this);
        mTxtContents.setText(getString(R.string.user_agreement_contents));
        //Intent data = getIntent();

    }
}
