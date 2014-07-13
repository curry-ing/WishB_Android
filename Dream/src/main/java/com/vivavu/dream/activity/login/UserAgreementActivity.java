package com.vivavu.dream.activity.login;

import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
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
    @InjectView(R.id.agreement_btn)
    Button mUserAgreementBtn;
    @InjectView(R.id.agreement_text)
    TextView mUserAgreementText;
    @InjectView(R.id.privacy_btn)
    Button mUserPrivacyBtn;
    @InjectView(R.id.privacy_text)
    TextView mUserPrivacyText;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;

    Boolean mAgreementVisible = true;
    Boolean mPrivacyVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_user_agreement);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_more);

        ButterKnife.inject(this);
        mUserAgreementBtn.setTypeface(getNanumBarunGothicBoldFont());
        mUserAgreementBtn.setTextColor(Color.WHITE);
        mUserAgreementText.setText(getString(R.string.agreement_text));
        mUserAgreementText.setTypeface(getNanumBarunGothicFont());
        mUserAgreementText.setTextColor(Color.DKGRAY);
        mUserAgreementBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);

        mUserPrivacyBtn.setTypeface(getNanumBarunGothicBoldFont());
        mUserPrivacyBtn.setTextColor(Color.WHITE);
        mUserPrivacyBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
        mUserPrivacyText.setText(getString(R.string.privacy_text));
        mUserPrivacyText.setTypeface(getNanumBarunGothicFont());
        mUserPrivacyText.setTextColor(Color.DKGRAY);
        //Intent data = getIntent();
        mTxtTitle.setText(getString(R.string.lbl_access_terms));
        mTxtTitle.setTypeface(getNanumBarunGothicBoldFont());
        mTxtTitle.setTextSize(19);
        mTxtTitle.setTextColor(Color.WHITE);

        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUserAgreementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (mAgreementVisible){
                    mUserAgreementText.setVisibility(View.GONE);
                    mUserAgreementBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                } else {
                    mUserAgreementText.setVisibility(View.VISIBLE);
                    mUserAgreementBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
                }
                mAgreementVisible = !mAgreementVisible;
            }
        });

        mUserPrivacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (mPrivacyVisible){
                    mUserPrivacyText.setVisibility(View.GONE);
                    mUserPrivacyBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                } else {
                    mUserPrivacyText.setVisibility(View.VISIBLE);
                    mUserPrivacyBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
                }
                mPrivacyVisible = !mPrivacyVisible;
            }
        });
    }
}
