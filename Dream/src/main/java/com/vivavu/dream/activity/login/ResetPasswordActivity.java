package com.vivavu.dream.activity.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
    @InjectView(R.id.findpw_send_btn)
    Button mFindpwSendBtn;
    @InjectView(R.id.findpw_email)
    EditText mFindpwEmail;
    @InjectView(R.id.findpw_txt_response_info)
    TextView mFindpwTxtResponseInfo;

    @InjectView(R.id.actionbar_login_title)
    TextView mActionbarLoginTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_find_pw);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_login);

        ButterKnife.inject(this);

//        Typeface NanumGothic = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothic.mp3");
        Typeface NanumBold = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothicBold.mp3");
        mActionbarLoginTitle.setText("비밀번호 찾기");
        mActionbarLoginTitle.setTypeface(NanumBold);
        mActionbarLoginTitle.setTextSize(20);
        mActionbarLoginTitle.setTextColor(Color.WHITE);

        mFindpwTxtResponseInfo.setTypeface(NanumBold);
        mFindpwTxtResponseInfo.setTextSize(15);
        mFindpwTxtResponseInfo.setTextColor(Color.WHITE);

        mFindpwEmail.setTypeface(NanumBold);
        mFindpwEmail.setTextSize(15);
        mFindpwEmail.setTextColor(Color.GRAY);
        mFindpwEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                    mFindpwTxtResponseInfo.setText("가입한 이메일 주소를 적어주세요.");
                } else {
                    if (!ValidationUtils.isValidEmail(mFindpwEmail)) {
                        mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                        mFindpwTxtResponseInfo.setText("올바르지 않은 이메일 형식입니다.");
                    }
                }
            }
        });

        mFindpwEmail.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (!ValidationUtils.isValidEmail(mFindpwEmail)) {
                    mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                    mFindpwSendBtn.setBackground(getResources().getDrawable(R.drawable.findpw_send_inactive_btn));
                } else {
                    mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                    mFindpwSendBtn.setBackground(getResources().getDrawable(R.drawable.findpw_send_active_btn));
                }
                return false;
            }
        });

        mFindpwSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        if (ValidationUtils.isValidEmail(mFindpwEmail)) {
            ResetPasswordTask task = new ResetPasswordTask();
            task.execute();
        } else {
            mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
            mFindpwSendBtn.setBackground(getResources().getDrawable(R.drawable.findpw_send_inactive_btn));
            mFindpwTxtResponseInfo.setText("알림:  올바르지 않은 이메일 형식입니다.");
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
            ResponseBodyWrapped<LoginInfo> response =  userInfoConnector.resetPassword(String.valueOf(mFindpwEmail.getText()));

            return response;
        }

        @Override
        protected void onPostExecute(ResponseBodyWrapped<LoginInfo> loginInfoResponseBodyWrapped) {

            if(loginInfoResponseBodyWrapped != null && loginInfoResponseBodyWrapped.isSuccess()){
                AlertDialog.Builder alert = new AlertDialog.Builder(ResetPasswordActivity.this);
                alert.setTitle("메일 발송 완료");
                alert.setMessage("비밀번호 변경 안내 메일을 발송했습니다.\n메일 내용을 확인 해주세요.");
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
                mFindpwTxtResponseInfo.setText(loginInfoResponseBodyWrapped.getDescription());
            }
        }
    }
}
