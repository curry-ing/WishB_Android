package com.vivavu.dream.activity.login;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.connector.UserInfoConnector;
import com.vivavu.dream.util.ValidationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 11.
 */
public class UserRegisterActivity extends BaseActionBarActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    @InjectView(R.id.actionbar_login_title)
    TextView mActionbarLoginTitle;
    @InjectView(R.id.actionbar_login_back)
    ImageView mActionbarLoginBack;

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
    @InjectView(R.id.register_agreement_txt)
    TextView mRegisterAgreementTxt;
//    @InjectView(R.id.register_fb_explain_txt)
//    TextView mRegisterFbExplainTxt;

    protected int mSdkVersion = Build.VERSION.SDK_INT;
	@InjectView(R.id.register_birthday)
	TextView mRegisterBirthday;

	private UserRegisterTask mRegisterTask = null;
    private String mEmail;
    private String mPassword;
	private String birthday;
    private Integer mInvalidType = 0;  // 1: Empty Email | 2: Empty PW | 3: Invalid Email | 4: InvalidPW | 5: Unregistered Email
    private boolean mAvailableEmail = false;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (ValidationUtils.isValidEmail(mRegisterEmail)) {
                        mAvailableEmail = true;
                        mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                    }
                    if (ValidationUtils.isValidPassword(mRegisterPassword)) {
                        setmRegisterTxtResponseInfo(mInvalidType = 9);
                    } else {
                        setmRegisterTxtResponseInfo(mInvalidType = 0);
                    }
                    break;
                case 0:
                    mAvailableEmail = false;
                    mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                    setmRegisterTxtResponseInfo(mInvalidType = 7);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_register);
        getLoaderManager().initLoader(0, null, this);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_login);

        ButterKnife.inject(this);

//        Typeface NanumBold = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothicBold.mp3");
        mActionbarLoginTitle.setText("회원가입");
        mActionbarLoginTitle.setTypeface(getNanumBarunGothicBoldFont());
        //mActionbarLoginTitle.setTextSize(20);
        mActionbarLoginTitle.setTextColor(Color.WHITE);

        mRegisterTxtResponseInfo.setTypeface(getNanumBarunGothicFont());
        //mRegisterTxtResponseInfo.setTextSize(14);
        mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
        setmRegisterTxtResponseInfo(mInvalidType = 0);

        mRegisterAgreementTxt.setTypeface(getNanumBarunGothicFont());
        //mRegisterAgreementTxt.setTextSize(12);

        mRegisterButton.setTypeface(getNanumBarunGothicFont());
        //mRegisterButton.setTextSize(15);
        mRegisterButton.setTextColor(Color.WHITE);

        SpannableString agreementText = new SpannableString(getResources().getString(R.string.regist_agreement));
        ClickableSpan agreement = new ClickableSpan() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "agreement", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, UserAgreementActivity.class);
                startActivity(intent);
            }
        };
        ClickableSpan privacy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "privacy", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, PrivacyActivity.class);
                startActivity(intent);
            }
        };
        agreementText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 26, 0);
        agreementText.setSpan(agreement, 27, 31, 0);
        agreementText.setSpan(new ForegroundColorSpan(Color.WHITE), 27, 31, 0);
        agreementText.setSpan(new MyclickableSpan("test"), 27, 31, 0);
        agreementText.setSpan(new StyleSpan(Typeface.BOLD), 27, 31, 0);

        mRegisterAgreementTxt.setMovementMethod(LinkMovementMethod.getInstance());
        mRegisterAgreementTxt.setText(agreementText, TextView.BufferType.SPANNABLE);

        // Set up the login form.
        mRegisterEmail.setText(mEmail);
        mRegisterEmail.setText(context.getEmail());
        mRegisterEmail.setTypeface(getNanumBarunGothicFont());
        //mRegisterEmail.setTextSize(15);
        mRegisterEmail.setTextColor(Color.GRAY);
        mRegisterEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    if (ValidationUtils.isValidEmail(mRegisterEmail)){
                        if (mAvailableEmail) {
                            mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                        } else {
                            mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                            setmRegisterTxtResponseInfo(mInvalidType = 7);
                        }
                    } else {
                        mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                        if (mInvalidType == 3 || mInvalidType == 1) {
                            setmRegisterTxtResponseInfo(mInvalidType = 0);
                        }
                    }
                } else {
                    if (!ValidationUtils.isValidEmail(mRegisterEmail)) {
                        mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                        setmRegisterTxtResponseInfo(mInvalidType=3);
                    } else {
                        Thread thread = new Thread(new NetworkThread());
                        thread.start();
//                        mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
//                        if (ValidationUtils.isValidPassword(mRegisterPassword)) {
//                            setmRegisterTxtResponseInfo(mInvalidType=9);
//                        }
//                        setmRegisterTxtResponseInfo(mInvalidType=0);
                    }
                }
            }
        });

        mRegisterPassword.setTypeface(getNanumBarunGothicFont());
        //mRegisterPassword.setTextSize(15);
        mRegisterPassword.setTextColor(Color.GRAY);
        mRegisterPassword.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (ValidationUtils.isValidPassword(mRegisterPassword)){
                        mRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                    } else {
                        mRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                    }
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


        mRegisterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationUtils.isValidEmail(mRegisterEmail)) {
                    mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                } else {
                    mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                }
                if (ValidationUtils.isValidPassword(mRegisterPassword)) {
                    if (!ValidationUtils.isValidEmail(mRegisterEmail)) {
                        setmRegisterTxtResponseInfo(mInvalidType = 0);
                    } else {
                        setmRegisterTxtResponseInfo(mInvalidType = 9);
                    }
                } else {
                    if (TextUtils.isEmpty(mRegisterPassword.getText())) {
                        setmRegisterTxtResponseInfo(mInvalidType = 0);
                    } else {
                        setmRegisterTxtResponseInfo(mInvalidType = 4);
                    }
                }
            }
        });


        mRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationUtils.isValidPassword(mRegisterPassword)) {
                    mRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ok_icon, 0);
                } else {
                    mRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_ing_icon, 0);
                }
                if (ValidationUtils.isValidEmail(mRegisterEmail)) {
                    if (ValidationUtils.isValidPassword(mRegisterPassword)) {
                        if (mAvailableEmail) {
                            setmRegisterTxtResponseInfo(mInvalidType = 9);
                        } else {
                            setmRegisterTxtResponseInfo(mInvalidType = 0);
                        }
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

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mActionbarLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

	    mRegisterBirthday.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
				    @Override
				    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					    // monthOfYear가 -1 되어 들어옴
					    Tracker tracker = DreamApp.getInstance().getTracker();
					    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_user_register_activity)).setAction(getString(R.string.ga_event_action_edit_date));
					    tracker.send(eventBuilder.build());
					    mRegisterBirthday.setText(String.format("%4d.%02d.%02d", year, monthOfYear+1, dayOfMonth ));
				    }
			    };
			    Calendar calendar = Calendar.getInstance();
			    DatePickerDialog dialog = new DatePickerDialog(UserRegisterActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			    dialog.show();
		    }
	    });

	    mRegisterBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {

		    }
	    });

	    mRegisterBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {

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
	            if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
		            mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
	            } else {
		            mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
	            }
//                mRegisterButton.setEnabled(false);
                break;
            case 1:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
                mRegisterTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_required));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 2:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
                mRegisterTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.passwd_required));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 3:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
                mRegisterTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_not_valid));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 4:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
                mRegisterTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.passwd_not_valid));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 5:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
                mRegisterTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_unregistered));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 6:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
                mRegisterTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.register_failed));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 7:
                mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
                mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
                mRegisterTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.email_already_registered));
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
                } else {
                    mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
                }
                break;
            case 9:
                mRegisterTxtResponseInfo.setVisibility(View.INVISIBLE);
                mRegisterTxtResponseInfo.setText("");
                if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                    mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_active));
                } else {
                    mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_active));
                }
                break;
	        case 10:
		        mRegisterTxtResponseInfo.setVisibility(View.VISIBLE);
		        mRegisterTxtResponseInfo.setTextColor(Color.WHITE);
		        mRegisterTxtResponseInfo.setText(getString(R.string.notify) + getString(R.string.birthday_must_required));
		        if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
			        mRegisterButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_inactive));
		        } else {
			        mRegisterButton.setBackground(this.getResources().getDrawable(R.drawable.btn_inactive));
		        }
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
	    birthday = mRegisterBirthday.getText().toString();
//        String mRegisterPasswordDupValue = mRegisterPasswordDup.getText().toString();

        // Check for a valid email address.
        if (ValidationUtils.isValidEmail(mRegisterEmail)) {
            UserInfoConnector userInfoConnector = new UserInfoConnector();
            ResponseBodyWrapped<Integer> result = userInfoConnector.checkEmailExists(mRegisterEmail.getText().toString());
	        if(result == null){
		        return;
	        }
            mAvailableEmail = (result.getData() != null && result.getData() == 1);

            if (!mAvailableEmail) {
                mRegisterEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login_check_alert_icon, 0);
                setmRegisterTxtResponseInfo(mInvalidType = 7);
                return;
            } else if (TextUtils.isEmpty(mRegisterPassword.getText())) {
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

	    if(!ValidationUtils.isValidBirthday(mRegisterBirthday)){
		    setmRegisterTxtResponseInfo(mInvalidType = 10);
		    return;
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
	    user.setBirthday(birthday.replaceAll("\\D", ""));

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
        protected void onPostExecute(final ResponseBodyWrapped<SecureToken> result) {
            if (result.isSuccess()) {
                context.setLogin(true);
                context.setUser(result.getData().getUser());
                context.setUsername(result.getData().getUser().getUsername());
                context.setToken(result.getData().getToken());
                context.setTokenType("unused");
                context.saveAppDefaultInfo();

                setResult(RESULT_OK);
                finish();
            }else if(result.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
            } else {
                this.cancel(false);
                context.setLogin(false);
                setmRegisterTxtResponseInfo(mInvalidType=6);
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

    public class NetworkThread implements Runnable{
        @Override
        public void run() {
            UserInfoConnector userInfoConnector = new UserInfoConnector();
            ResponseBodyWrapped<Integer> result = userInfoConnector.checkEmailExists(mRegisterEmail.getText().toString());
	        if(result != null && result.isSuccess()) {
		        Message message = handler.obtainMessage(result.getData());
		        handler.sendMessage(message);
	        }
        }
    }

    public class MyclickableSpan extends ClickableSpan {
        public MyclickableSpan(String string){
            super();
        }

        @Override
        public void onClick(View view){
            Toast.makeText(context, "test", Toast.LENGTH_LONG).show();

        }

        @Override
        public void updateDrawState(TextPaint ds){
            ds.setUnderlineText(false);
        }
    }
}
