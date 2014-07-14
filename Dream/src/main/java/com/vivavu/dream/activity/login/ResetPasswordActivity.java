package com.vivavu.dream.activity.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.enums.ResponseStatus;
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
    protected int mSdkVersion = Build.VERSION.SDK_INT;

    @InjectView(R.id.findpw_send_btn)
    Button mFindpwSendBtn;
    @InjectView(R.id.findpw_email)
    EditText mFindpwEmail;
    @InjectView(R.id.findpw_txt_response_info)
    TextView mFindpwTxtResponseInfo;

    @InjectView(R.id.actionbar_login_title)
    TextView mActionbarLoginTitle;
    @InjectView(R.id.actionbar_login_back)
    ImageView mActionbarLoginBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.activity_find_pw);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_login);

        ButterKnife.inject(this);

//        Typeface NanumBold = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothicBold.mp3");
        mActionbarLoginTitle.setText(getText(R.string.find_passwd));
        mActionbarLoginTitle.setTypeface(getNanumBarunGothicBoldFont());
        mActionbarLoginTitle.setTextSize(20);
        mActionbarLoginTitle.setTextColor(Color.WHITE);

        mFindpwTxtResponseInfo.setTypeface(getNanumBarunGothicBoldFont());
        mFindpwTxtResponseInfo.setTextSize(15);
        mFindpwTxtResponseInfo.setTextColor(Color.WHITE);

        mFindpwEmail.setTypeface(getNanumBarunGothicBoldFont());
        mFindpwEmail.setTextSize(15);
        mFindpwEmail.setTextColor(Color.GRAY);
        mFindpwEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                    mFindpwTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.enter_registered_email));
                } else {
                    if (!ValidationUtils.isValidEmail(mFindpwEmail)) {
                        mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                        mFindpwTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_not_valid));
                    }
                }
            }
        });

        mFindpwEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mFindpwTxtResponseInfo.setText("");
                if (!ValidationUtils.isValidEmail(mFindpwEmail)) {
                    mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                    if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                        mFindpwSendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inactive));
                    } else {
                        mFindpwSendBtn.setBackground(getResources().getDrawable(R.drawable.btn_inactive));
                    }
                } else {
                    mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                    if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                        mFindpwSendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_active));
                    } else {
                        mFindpwSendBtn.setBackground(getResources().getDrawable(R.drawable.btn_active));
                    }
                }
            }
        });
//        mFindpwEmail.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (!ValidationUtils.isValidEmail(mFindpwEmail)) {
//                    mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
//                    mFindpwSendBtn.setBackground(getResources().getDrawable(R.drawable.btn_inactive));
//                } else {
//                    mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
//                    mFindpwSendBtn.setBackground(getResources().getDrawable(R.drawable.btn_active));
//                }
//                return false;
//            }
//        });

        mFindpwSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        mActionbarLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void sendEmail() {
        if (ValidationUtils.isValidEmail(mFindpwEmail)) {
            ResetPasswordTask task = new ResetPasswordTask();
            task.execute();
        } else {
            mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
            if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                mFindpwSendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inactive));
            } else {
                mFindpwSendBtn.setBackground(getResources().getDrawable(R.drawable.btn_inactive));
            }
            mFindpwTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_not_valid));
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
                alert.setMessage("\n" + getString(R.string.sent_reset_passwd_mail) + "\n");
                alert.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideSoftKeyboard();
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
            }else if(loginInfoResponseBodyWrapped != null && loginInfoResponseBodyWrapped.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
            }else{
//                mFindpwTxtResponseInfo.setText(loginInfoResponseBodyWrapped.getDescription());
                mFindpwEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                mFindpwTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_unregistered) );
            }
        }
    }
}
