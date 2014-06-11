package com.vivavu.dream.activity.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.login.PrivacyActivity;
import com.vivavu.dream.activity.login.UserAgreementActivity;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.common.BaseActionBarActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MoreActivity extends BaseActionBarActivity {

    @InjectView(R.id.btn_alert_setting)
    Button mBtnAlertSetting;
    @InjectView(R.id.btn_public_setting)
    Button mBtnPublicSetting;
    @InjectView(R.id.btn_help)
    Button mBtnHelp;
    @InjectView(R.id.btn_inquiry)
    Button mBtnInquiry;
    @InjectView(R.id.btn_access_terms)
    Button mBtnAccessTerms;
    @InjectView(R.id.btn_privacy)
    Button mBtnPrivacy;
    @InjectView(R.id.btn_member_leave)
    Button mBtnMemberLeave;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_more);

        ButterKnife.inject(this);

        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTxtTitle.setTypeface(getNanumBarunGothicFont());

        mBtnAlertSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, AlertSettingsActivity.class);
                startActivity(intent);
            }
        });
        mBtnPublicSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mBtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MoreActivity.this, "도움말보기", Toast.LENGTH_SHORT).show();
            }
        });
        mBtnInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);*/
                Toast.makeText(MoreActivity.this, "문의하기", Toast.LENGTH_SHORT).show();
            }
        });
        mBtnAccessTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, UserAgreementActivity.class);
                startActivity(intent);
            }
        });
        mBtnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });
        mBtnMemberLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);*/
                Toast.makeText(MoreActivity.this, "회원탈퇴", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
