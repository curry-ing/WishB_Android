package com.vivavu.dream.activity.login;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuja on 14. 2. 11.
 */
public class UserRegisterActivity extends BaseActionBarActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    @InjectView(R.id.actionbar_login_title)
    TextView mActionbarLoginTitle;

    @InjectView(R.id.register_status_message)
    TextView mRegisterStatusMessage;
    @InjectView(R.id.register_status)
    LinearLayout mRegisterStatus;
    @InjectView(R.id.register_email)
    EditText mRegisterEmail;
    @InjectView(R.id.register_password)
    EditText mRegisterPassword;
    @InjectView(R.id.register_button)
    Button mRegisterButton;
    @InjectView(R.id.register_txt_response_info)
    TextView mRegisterTxtResponseInfo;
    @InjectView(R.id.register_agreement)
    TextView mRegisterAgreement;
//    @InjectView(R.id.register_fb_explain_txt)
//    TextView mRegisterFbExplainTxt;

    private UserRegisterTask mRegisterTask = null;
    private String mEmail;
    private String mPassword;
    private Integer mInvalidType = 0;  // 1: Empty Email | 2: Empty PW | 3: Invalid Email | 4: InvalidPW | 5: Unregistered Email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_register);
        getLoaderManager().initLoader(0, null, this);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_login);

        ButterKnife.inject(this);

        Typeface NanumBold = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothicBold.mp3");
        mActionbarLoginTitle.setText("회원가입");
        mActionbarLoginTitle.setTypeface(NanumBold);
        mActionbarLoginTitle.setTextSize(20);
        mActionbarLoginTitle.setTextColor(Color.WHITE);

        mRegisterTxtResponseInfo.setTypeface(NanumBold);
        mRegisterTxtResponseInfo.setTextSize(15);
        mRegisterTxtResponseInfo.setTextColor(Color.WHITE);

        mRegisterAgreement.setTypeface(NanumBold);
        mRegisterAgreement.setTextSize(15);
        mRegisterAgreement.setTextColor(Color.WHITE);

        // Set up the login form.
        mRegisterEmail.setText(mEmail);
        mRegisterEmail.setText(context.getEmail());
        mRegisterEmail.setTypeface(NanumBold);
        mRegisterEmail.setTextSize(15);
        mRegisterEmail.setTextColor(Color.GRAY);
        mRegisterEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                    if (mInvalidType==3||mInvalidType==1) {
                        setmRegisterTxtResponseInfo(mInvalidType=0);
                    }
                } else {
                    if (!ValidationUtils.isValidEmail(mRegisterEmail)) {
                        mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                        setmRegisterTxtResponseInfo(mInvalidType=3);
                    } else {
                        mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                        if (ValidationUtils.isValidPassword(mRegisterPassword)) {
                            setmRegisterTxtResponseInfo(mInvalidType=9);
                        }
                        setmRegisterTxtResponseInfo(mInvalidType=0);
                    }
                }
            }
        });

        mRegisterPassword.setTypeface(NanumBold);
        mRegisterPassword.setTextSize(15);
        mRegisterPassword.setTextColor(Color.GRAY);
        mRegisterPassword.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                } else {
                    if (!ValidationUtils.isValidPassword(mRegisterPassword)){
                        mRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                        if (TextUtils.isEmpty(mRegisterPassword.getText())){
                            setmRegisterTxtResponseInfo(mInvalidType=2);
                        } else {
                            setmRegisterTxtResponseInfo(mInvalidType=4);
                        }
                    } else {
                        mRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                        if (ValidationUtils.isValidEmail(mRegisterEmail)){
                            setmRegisterTxtResponseInfo(mInvalidType=9);
                        }
                    }
                }
            }
        });


        mRegisterEmail.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (ValidationUtils.isValidPassword(mRegisterPassword)) {
                    if (!ValidationUtils.isValidEmail(mRegisterEmail)) {
                        setmRegisterTxtResponseInfo(mInvalidType = 0); }
                    else {
                        setmRegisterTxtResponseInfo(mInvalidType = 9);
                    }
                }
                return false;
            }
        });

        mRegisterPassword.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (ValidationUtils.isValidEmail(mRegisterEmail)) {
                    if (ValidationUtils.isValidPassword(mRegisterPassword)) {
                        setmRegisterTxtResponseInfo(mInvalidType = 9);
                    } else if (TextUtils.isEmpty(mRegisterPassword.getText())) {
                        setmRegisterTxtResponseInfo(mInvalidType = 0);
                    } else if (!ValidationUtils.isValidPassword(mRegisterPassword)) {
                        setmRegisterTxtResponseInfo(mInvalidType = 0);
                    }
                } else {
                    if (TextUtils.isEmpty(mRegisterEmail.getText())) {
                        setmRegisterTxtResponseInfo(mInvalidType = 1);
                    } else if (!ValidationUtils.isValidEmail(mRegisterEmail)) {
                        setmRegisterTxtResponseInfo(mInvalidType = 3);
                    }
                }
                return false;
            }
        });

        mRegisterPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

//        mRegisterFbExplainTxt.setTypeface(NanumBold);
//        mRegisterFbExplainTxt.setTextSize(15);
//        mRegisterFbExplainTxt.setTextColor(Color.WHITE);

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

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
        if (emails.size() > 0) {
            mRegisterEmail.setText(emails.get(0));
            mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
            mRegisterPassword.requestFocus();
        }
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

    public void setmRegisterTxtResponseInfo(int invalidType){
        switch(invalidType){
            case 0:
                mRegisterTxtResponseInfo.setVisibility(View.INVISIBLE);
                mRegisterTxtResponseInfo.setText("");
                mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.register_inactive_btn));
//                mRegisterButton.setEnabled(false);
                break;
            case 1:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setText("알림:  이메일 주소가 입력되지 않았습니다.");
                mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.register_inactive_btn));
//                mRegisterButton.setEnabled(false);
                break;
            case 2:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setText("알림:  패스워드가 입력되지 않았습니다.");
                mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.register_inactive_btn));
//                mRegisterButton.setEnabled(false);
                break;
            case 3:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setText("알림:  올바르지 않은 이메일 형식입니다.");
                mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.register_inactive_btn));
//                mRegisterButton.setEnabled(false);
                break;
            case 4:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setText("알림:  비밀번호(6자 이상)를 확인해 주세요.");
                mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.register_inactive_btn));
//                mRegisterButton.setEnabled(false);
                break;
            case 5:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setText("알림:  가입하지 않은 이메일입니다.");
                mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.register_inactive_btn));
//                mRegisterButton.setEnabled(false);
                break;
            case 6:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setText("알림:  비밀번호가 일치하지 않습니다.");
                mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.register_inactive_btn));
//                mRegisterButton.setEnabled(false);
                break;
            case 9:
                mRegisterTxtResponseInfo.setVisibility(View.INVISIBLE);
                mRegisterTxtResponseInfo.setText("");
                mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.register_active_btn));
//                mRegisterButton.setEnabled(true);
                break;

        }
    }

    private void attemptRegister() {
        // Reset errors.
//        mRegisterEmail.setError(null);
//        mRegisterPassword.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mRegisterEmail.getText().toString();
        mPassword = mRegisterPassword.getText().toString();
//        String mRegisterPasswordDupValue = mRegisterPasswordDup.getText().toString();

        // Check for a valid email address.
        if (ValidationUtils.isValidEmail(mRegisterEmail)) {
            if (TextUtils.isEmpty(mRegisterPassword.getText())) {
                setmRegisterTxtResponseInfo(mInvalidType = 2);
                return;
            } else if (!ValidationUtils.isValidPassword(mRegisterPassword)) {
                setmRegisterTxtResponseInfo(mInvalidType = 4);
                return;
            }
        } else {
            if (TextUtils.isEmpty(mRegisterEmail.getText())) {
                setmRegisterTxtResponseInfo(mInvalidType = 1);
                return;
            } else if (!ValidationUtils.isValidEmail(mRegisterEmail)) {
                setmRegisterTxtResponseInfo(mInvalidType = 3);
                return;
            }
        }
//        if (!ValidationUtils.isValidEmail(mRegisterEmail)) {
//            mRegisterEmail.requestFocus();
//            return;
//        }
//
//        // Check for a valid password.
//        if (!ValidationUtils.isValidPassword(mRegisterPassword)) {
//            mRegisterPassword.requestFocus();
//            return;
//        }

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //mRegisterStatusMessageView.setText(R.string.login_progress_signing_in);

        mRegisterTask = new UserRegisterTask();

        LoginInfo user = new LoginInfo();
        user.setEmail(mEmail);
        user.setPassword(mPassword);

        mRegisterTask.execute(user);
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
        protected void onPostExecute(final ResponseBodyWrapped<SecureToken> resp) {
            if (resp.isSuccess()) {
                context.setLogin(true);
                context.setUser(resp.getData().getUser());
                context.setUsername(resp.getData().getUser().getUsername());
                context.setToken(resp.getData().getToken());
                context.setTokenType("unused");
                context.saveAppDefaultInfo();

                setResult(RESULT_OK);
                finish();
            } else {
                this.cancel(false);
                context.setLogin(false);
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setText(resp.getDescription());
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
