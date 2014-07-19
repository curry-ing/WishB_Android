package com.vivavu.dream.activity.setup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.ValidationUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InquiryActivity extends BaseActionBarActivity {

    private static final int SEND_EMAIL = 0;
    @InjectView(R.id.txt_answer_email)
    EditText mTxtAnswerEmail;
    @InjectView(R.id.txt_email_title)
    EditText mTxtEmailTitle;
    @InjectView(R.id.txt_email_body)
    EditText mTxtEmailBody;
    @InjectView(R.id.btn_send_email)
    Button mBtnSendEmail;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_inquiry);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_more);

        ButterKnife.inject(this);

        if(DreamApp.getInstance().getUser() != null){
            mTxtAnswerEmail.setText(DreamApp.getInstance().getUser().getEmail());
        } else {
            String[] emailAccountName = AndroidUtils.getEmailAccountName(DreamApp.getInstance());
            if (emailAccountName != null && emailAccountName.length > 0) {
                mTxtAnswerEmail.setText(emailAccountName[0]);
            }
        }

        mTxtTitle.setText(getString(R.string.inquiry));
        mTxtTitle.setTypeface(getNanumBarunGothicBoldFont());
        //mTxtTitle.setTextSize(20);
        mTxtTitle.setTextColor(Color.WHITE);
        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnSendEmail.setTypeface(getNanumBarunGothicBoldFont());
        mBtnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validationData()) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    String emailBody = mTxtEmailBody.getText().toString() + "\r\n Receive a response e-mail address : " + mTxtAnswerEmail.getText().toString();

                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_info)});
                    email.putExtra(Intent.EXTRA_SUBJECT, mTxtEmailTitle.getText().toString());
                    email.putExtra(Intent.EXTRA_TEXT, emailBody);
                    email.setType("message/rfc822");
                    //startActivity(Intent.createChooser(email, "Choose an Email client :"));
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                } else {

                }
            }
        });
    }

    private boolean validationData(){
        if(!ValidationUtils.isValidEmail(mTxtAnswerEmail)){
            return false;
        }

        if(!ValidationUtils.isValidNotEmpty(mTxtEmailTitle)){
            return false;
        }

        if(!ValidationUtils.isValidNotEmpty(mTxtEmailBody)){
            return false;
        }

        return true;
    }

}
