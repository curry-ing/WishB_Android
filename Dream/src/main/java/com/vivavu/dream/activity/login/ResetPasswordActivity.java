package com.vivavu.dream.activity.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.repository.connector.UserInfoConnector;
import com.vivavu.dream.util.ValidationUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 20.
 */
public class ResetPasswordActivity extends BaseActionBarActivity {
    @InjectView(R.id.btn_email_submit)
    Button mBtnEmailSubmit;
    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.txt_error)
    TextView mTxtError;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_activity);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_more);

        ButterKnife.inject(this);

        mBtnEmailSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        mTxtTitle.setText(getString(R.string.lbl_private));
        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendEmail() {
        if (ValidationUtils.isValidEmail(mEmail)) {
            ResetPasswordTask task = new ResetPasswordTask();
            task.execute();
        } else {
            mTxtError.setVisibility(View.VISIBLE);
            mTxtError.setText(getString(R.string.not_valid_email));
        }
    }

    public void complete(){
        setResult(RESULT_OK);
        finish();
    }
    public class ResetPasswordTask extends AsyncTask<Void, Void, ResponseBodyWrapped<LoginInfo>>{

        @Override
        protected ResponseBodyWrapped<LoginInfo> doInBackground(Void... voids) {
            UserInfoConnector userInfoConnector = new UserInfoConnector();
            ResponseBodyWrapped<LoginInfo> response =  userInfoConnector.resetPassword(String.valueOf(mEmail.getText()));

            return response;
        }

        @Override
        protected void onPostExecute(ResponseBodyWrapped<LoginInfo> loginInfoResponseBodyWrapped) {

            if(loginInfoResponseBodyWrapped != null && loginInfoResponseBodyWrapped.isSuccess()){
                AlertDialog.Builder alert = new AlertDialog.Builder(ResetPasswordActivity.this);
                alert.setTitle("메일 발송 완료");
                alert.setMessage("비밀번호 변경 안내 메일을 발송했습니다. 메일 내요을 확인 해주세요.");
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        complete();
                    }
                });
                alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        complete();
                    }
                });

                alert.show();

            }else{
                mTxtError.setText(loginInfoResponseBodyWrapped.getDescription());
            }
        }
    }
}
