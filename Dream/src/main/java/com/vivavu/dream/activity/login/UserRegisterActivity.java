package com.vivavu.dream.activity.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.ValidationUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 11.
 */
public class UserRegisterActivity extends BaseActionBarActivity {


    @InjectView(R.id.login_status_message)
    TextView mLoginStatusMessage;
    @InjectView(R.id.login_status)
    LinearLayout mLoginStatus;
    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.password_dup)
    EditText mPasswordDup;
    @InjectView(R.id.regist_button)
    Button mRegistButton;
    @InjectView(R.id.register_form)
    ScrollView mRegisterForm;
    @InjectView(R.id.txt_response_info)
    TextView mTxtResponseInfo;
    @InjectView(R.id.txt_regist_agreement)
    TextView mTxtRegistAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        mRegistButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == mRegistButton) {
            attemptRegist();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void attemptRegist() {
        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String mEmailValue = mEmail.getText().toString();
        String mPasswordValue = mPassword.getText().toString();
        String mPasswordDupValue = mPasswordDup.getText().toString();

        // Check for a valid email address.
        if (!ValidationUtils.isValidEmail(mEmail)) {
            mEmail.requestFocus();
            return;
        }

        // Check for a valid password.
        if (!ValidationUtils.isValidPassword(mPassword)) {
            mPassword.requestFocus();
            return;
        }

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
        UserRegisterTask mAuthTask = new UserRegisterTask();

        LoginInfo user = new LoginInfo();
        user.setEmail(mEmailValue);
        user.setPassword(mPasswordValue);

        mAuthTask.execute(user);
    }

    public class UserRegisterTask extends AsyncTask<LoginInfo, Void, ResponseBodyWrapped<SecureToken>> {

        @Override
        protected ResponseBodyWrapped<SecureToken> doInBackground(LoginInfo... params) {
            LoginInfo user = null;
            if (params.length > 0) {
                user = params[0];
            } else {
                return null;
            }

            ResponseBodyWrapped<SecureToken> userInfo = DataRepository.registUser(user);
            if (userInfo == null ) {
                return null;
            } else {
                return userInfo;
            }
        }

        @Override
        protected void onPostExecute(final ResponseBodyWrapped<SecureToken> success) {
            if (success.isSuccess()) {
                context.setLogin(true);
                context.setUser(success.getData().getUser());
                context.setUsername(success.getData().getUser().getUsername());
                context.setToken(success.getData().getToken());
                context.setTokenType("unused");
                context.saveAppDefaultInfo();

                setResult(RESULT_OK);
                finish();
            } else {
                this.cancel(false);
                context.setLogin(false);
                mTxtResponseInfo.setText(success.getDescription());
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            this.cancel(true);
        }
    }
}
