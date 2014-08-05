package com.vivavu.dream.activity.setup;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.ReportingType;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.Inquiry;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.repository.connector.InquiryConnector;
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

	protected Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 0:
					Toast.makeText(InquiryActivity.this, getString(R.string.txt_inquiry_complete), Toast.LENGTH_SHORT).show();
					finish();
					break;
				case 1:
					Toast.makeText(InquiryActivity.this, getString(R.string.txt_inquiry_fail), Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};
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
	                // 문의하기 변경 -> json 타입으로 할텐데....
	                Inquiry inquiry = new Inquiry();
	                inquiry.setReportingType(ReportingType.INQUIRY);
	                inquiry.setEmail(mTxtAnswerEmail.getText().toString());
	                inquiry.setSubject(mTxtEmailTitle.getText().toString());
	                inquiry.setReport(mTxtEmailBody.getText().toString());

	                NetworkThread networkThread = new NetworkThread(inquiry);
	                handler.post(networkThread);

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

	private class NetworkThread implements Runnable{
		private Inquiry inquiry;

		private NetworkThread(Inquiry inquiry) {
			this.inquiry = inquiry;
		}

		@Override
		public void run() {

			InquiryConnector connector = new InquiryConnector();
			ResponseBodyWrapped<Inquiry> result = connector.post(inquiry);

			if(result.isSuccess()) {
				handler.sendEmptyMessage(0);
			}else if(result.getResponseStatus() == ResponseStatus.TIMEOUT) {
				defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
			}else {
				handler.sendEmptyMessage(1);
			}
		}
	}
}
