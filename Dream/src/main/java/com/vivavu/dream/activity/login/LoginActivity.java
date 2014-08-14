package com.vivavu.dream.activity.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.broadcastReceiver.AlarmManagerBroadcastReceiver;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.repository.connector.UserInfoConnector;
import com.vivavu.dream.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends BaseActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    @InjectView(R.id.txt_response_info)
    TextView mTxtResponseInfo;
    @InjectView(R.id.actionbar_login_title)
    TextView mActionbarLoginTitle;
    @InjectView(R.id.actionbar_login_back)
    ImageView mActionbarLoginBack;

    /**
     * The default email to populate the email field with.
     */
    @InjectView(R.id.sign_in_button)
    Button mSignInButton;
    @InjectView(R.id.txt_forgot_password)
    TextView mTxtForgotPassword;

    protected int mSdkVersion = Build.VERSION.SDK_INT;
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;
    private Integer mInvalidType = 0;  // 1: Empty Email | 2: Empty PW | 3: Invalid Email | 4: InvalidPW | 5: Unregistered Email

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                    setmTxtResponseInfo(7);
                    break;
                case 1:
                    mEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                    if (ValidationUtils.isValidPassword(mPasswordView)) {
                        setmTxtResponseInfo(mInvalidType=9);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        mAuthTask = null;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_login);
        getLoaderManager().initLoader(0, null, this);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_login);

        ButterKnife.inject(this);

//        Typeface NanumBold = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothicBold.mp3");
        mActionbarLoginTitle.setText(getString(R.string.login));
        mActionbarLoginTitle.setTypeface(getNanumBarunGothicBoldFont());
        //mActionbarLoginTitle.setTextSize(20);
        mActionbarLoginTitle.setTextColor(Color.WHITE);

        mTxtResponseInfo.setTypeface(getNanumBarunGothicFont());
        //mTxtResponseInfo.setTextSize(14);
        mTxtResponseInfo.setTextColor(Color.WHITE);

        // Set up the login form.
        mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(mEmail);
        mEmailView.setText(context.getEmail());
        mEmailView.setTypeface(getNanumBarunGothicFont());
        //mEmailView.setTextSize(15);
        mEmailView.setTextColor(Color.GRAY);
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    if (ValidationUtils.isValidEmail(mEmailView)){
                        mEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                    } else {
                        mEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                        if (mInvalidType == 3 || mInvalidType == 1) {
                            setmTxtResponseInfo(mInvalidType = 0);
                        }
                    }
                } else {
                    if (!ValidationUtils.isValidEmail(mEmailView)) {
                        mEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                        setmTxtResponseInfo(mInvalidType=3);
                    } else {
                        mEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                        setmTxtResponseInfo(mInvalidType=0);
                    }
                }
            }
        });

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setTypeface(getNanumBarunGothicFont());
        //mPasswordView.setTextSize(15);
        mPasswordView.setTextColor(Color.GRAY);
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (ValidationUtils.isValidPassword(mPasswordView)){
                        mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                    } else {
                        mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                    }
                } else {
                    if (!ValidationUtils.isValidPassword(mPasswordView)){
                        mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                        if (TextUtils.isEmpty(mPasswordView.getText())){
                            setmTxtResponseInfo(mInvalidType=2);
                        } else {
                            setmTxtResponseInfo(mInvalidType=4);
                        }
                    } else {
                        mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                        if (ValidationUtils.isValidEmail(mEmailView)){
                            setmTxtResponseInfo(mInvalidType=9);
                        }
                    }
                }
            }
        });

        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationUtils.isValidEmail(mEmailView)){
                    mEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                } else {
                    mEmailView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                }
                if (ValidationUtils.isValidPassword(mPasswordView)) {
                    if (!ValidationUtils.isValidEmail(mEmailView)) {
                        setmTxtResponseInfo(mInvalidType = 0); }
                    else {
                        setmTxtResponseInfo(mInvalidType = 9);
                    }
                }
            }
        });


        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationUtils.isValidPassword(mPasswordView)) {
                    mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                } else {
                    mPasswordView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                }
                if (ValidationUtils.isValidEmail(mEmailView)) {
                    if (ValidationUtils.isValidPassword(mPasswordView)) {
                        setmTxtResponseInfo(mInvalidType = 9);
                    } else if (TextUtils.isEmpty(mPasswordView.getText())) {
                        setmTxtResponseInfo(mInvalidType = 0);
                    } else if (!ValidationUtils.isValidPassword(mPasswordView)) {
                        setmTxtResponseInfo(mInvalidType = 0);
                    }
                } else {
                    if (TextUtils.isEmpty(mEmailView.getText())) {
                        setmTxtResponseInfo(mInvalidType = 1);
                    } else if (!ValidationUtils.isValidEmail(mEmailView)) {
                        setmTxtResponseInfo(mInvalidType = 3);
                    }
                }
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        // 로그인 버튼 클릭 이벤트
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mTxtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ResetPasswordActivity.class);
                startActivityForResult(intent, Code.ACT_RESET_PASSWORD);
            }
        });

        mActionbarLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setmTxtResponseInfo(int invalidType){
        switch(invalidType){
            case 0:
                mTxtResponseInfo.setVisibility(View.INVISIBLE);
                mTxtResponseInfo.setText("");
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mSignInButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mSignInButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 1:
                mTxtResponseInfo.setVisibility(View.VISIBLE);
                mTxtResponseInfo.setTextColor(Color.WHITE);
                mTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_required));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mSignInButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mSignInButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 2:
                mTxtResponseInfo.setVisibility(View.VISIBLE);
                mTxtResponseInfo.setTextColor(Color.WHITE);
                mTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.passwd_required));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mSignInButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mSignInButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 3:
                mTxtResponseInfo.setVisibility(View.VISIBLE);
                mTxtResponseInfo.setTextColor(Color.WHITE);
                mTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_not_valid));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mSignInButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mSignInButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 4:
                mTxtResponseInfo.setVisibility(View.VISIBLE);
                mTxtResponseInfo.setTextColor(Color.WHITE);
                mTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.passwd_not_valid));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mSignInButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mSignInButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 5:
                mTxtResponseInfo.setVisibility(View.VISIBLE);
                mTxtResponseInfo.setTextColor(Color.WHITE);
                mTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_unregistered));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mSignInButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mSignInButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 6:
                mTxtResponseInfo.setVisibility(View.VISIBLE);
                mTxtResponseInfo.setTextColor(Color.WHITE);
                mTxtResponseInfo.setText(getString(R.string.notify) + getResources().getString(R.string.login_failed));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mSignInButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mSignInButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 9:
                mTxtResponseInfo.setVisibility(View.INVISIBLE);
                mTxtResponseInfo.setText("");
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mSignInButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_active));
                } else {
                    mSignInButton.setBackground(this.getResources().getDrawable(R.drawable.btn_active));
                }
                break;

        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);

        // Check for a valid email address.
        if (ValidationUtils.isValidEmail(mEmailView)) {
            if (TextUtils.isEmpty(mPasswordView.getText())) {
                setmTxtResponseInfo(mInvalidType = 2);
                return;
            } else if (!ValidationUtils.isValidPassword(mPasswordView)) {
                setmTxtResponseInfo(mInvalidType = 4);
                return;
            }
        } else {
            if (TextUtils.isEmpty(mEmailView.getText())) {
                setmTxtResponseInfo(mInvalidType = 1);
                return;
            } else if (!ValidationUtils.isValidEmail(mEmailView)) {
                setmTxtResponseInfo(mInvalidType = 3);
                return;
            }
        }
//        if (!ValidationUtils.isValidEmail(mEmailView)) {
//            mEmailView.requestFocus();
//            return;
//        }

        // Check for a valid password.
//        if (!ValidationUtils.isValidPassword(mPasswordView)) {
//            mPasswordView.requestFocus();
//            return;
//        }


        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();



        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
//        mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
        mAuthTask = new UserLoginTask();

        LoginInfo user = new LoginInfo();
        user.setEmail(mEmail);
        user.setPassword(mPassword);

        mAuthTask.execute(user);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

//            mLoginFormView.setVisibility(View.VISIBLE);
//            mLoginFormView.animate()
//                    .setDuration(shortAnimTime)
//                    .alpha(show ? 0 : 1)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                        }
//                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            // Potentially filter on ProfileQuery.IS_PRIMARY
            cursor.moveToNext();
        }
//        mTxtResponseInfo.setVisibility(View.VISIBLE);
//        mTxtResponseInfo.setText(emails.get(0));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<LoginInfo, Void, ResponseBodyWrapped<SecureToken>> {

        @Override
        protected ResponseBodyWrapped<SecureToken> doInBackground(LoginInfo... params) {
            LoginInfo user = null;
            if (params.length > 0) {
                user = params[0];
            } else {
                return new ResponseBodyWrapped<SecureToken>(ResponseStatus.UNKNOWN_ERROR, "unknown", new SecureToken());
            }

            UserInfoConnector userInfoConnector = new UserInfoConnector();

            return UserInfoConnector.getToken(user.getEmail(), user.getPassword());
        }

        @Override
        protected void onPostExecute(final ResponseBodyWrapped<SecureToken> result) {
            mAuthTask = null;
//            showProgress(false);

            if (result != null && result.isSuccess()) {
                context.setLogin(true);
	            context.setAppVersionInfo(result.getData().getUser().getAppVersionInfo());
                context.setUser(result.getData().getUser());
                context.setUsername(result.getData().getUser().getUsername());
                context.setToken(result.getData().getToken());
                context.setTokenType("unused");
	            context.setFbToken(result.getData().getUser().getFbToken());
                context.saveAppDefaultInfo();

                /* Set Notifications On */
                AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
                alarm.setEverydayAlarm(context, true, 0);
//                alarm.SetAlarm(context, 1, true, 23, 0);
//                alarm.SetAlarm(context, 2, true, 11, 0);

                setResult(RESULT_OK);
                finish();
            }else if(result.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
            } else {
                this.cancel(false);
                context.setLogin(false);
                setmTxtResponseInfo(mInvalidType=6);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgress(true);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            this.cancel(true);
//            showProgress(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (context != null && context.isLogin()) {
            super.onBackPressed();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }


}
