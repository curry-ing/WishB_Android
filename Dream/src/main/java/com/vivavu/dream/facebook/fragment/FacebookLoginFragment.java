package com.vivavu.dream.facebook.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.intro.IntroActivity;
import com.vivavu.dream.broadcastReceiver.AlarmManagerBroadcastReceiver;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.repository.connector.UserInfoConnector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 18.
 */
public class FacebookLoginFragment extends CustomBaseFragment {
    private DreamApp context = null;
    private static final String TAG = "FacebookLoginFragment";
    @InjectView(R.id.authButton)
    LoginButton mAuthButton;
    @InjectView(R.id.txt_facebook_login_explain)
    TextView mTxtFacebookLoginExplain;
    @InjectView(R.id.fb_login_progress_bar)
    ProgressBar mFbLoginProgressBar;
    private UiLifecycleHelper uiHelper;
//    private ProgressDialog progressDialog;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = DreamApp.getInstance();
        final View rootView = inflater.inflate(R.layout.include_facebook_login, container, false);
        ButterKnife.inject(this, rootView);
//        mAuthButton.setBackgroundResource(R.drawable.intro_fb_btn);
        Typeface NanumBold = Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunGothicBold.mp3");
        List<String> readPermissions = new ArrayList<String>();
        readPermissions.add("public_profile");
        readPermissions.add("email");
        readPermissions.add("user_birthday");

        mTxtFacebookLoginExplain.setTypeface(NanumBold);
        mTxtFacebookLoginExplain.setTextSize(15);
        mTxtFacebookLoginExplain.setTextColor(Color.WHITE);

        mAuthButton.setReadPermissions(readPermissions);
        mAuthButton.setFragment(this);

        if(getActivity() instanceof IntroActivity){
            mTxtFacebookLoginExplain.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage(getString(R.string.in_progress));
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            DreamApp.getInstance().setToken(session.getAccessToken(), "facebook");

            /* SessionStateChange에서 activity를 실행시키니 중복실행되는 문제가 있음
            if(getActivity() instanceof BaseActionBarActivity){
                BaseActionBarActivity activity = (BaseActionBarActivity) getActivity();
                activity.checkAppExit();
            }*/
        } else if (state.isClosed()) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

//        mAuthButton.setVisibility(View.GONE);
        if (requestCode == Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                LoginInfo loginInfo = new LoginInfo();
                loginInfo.setEmail(Session.getActiveSession().getAccessToken());
                loginInfo.setPassword("facebook");

                GetTokenTask getTokenTask = new GetTokenTask();
                getTokenTask.execute(loginInfo);
                /*if (getActivity() instanceof BaseActionBarActivity) {
                    BaseActionBarActivity activity = (BaseActionBarActivity) getActivity();
                    //activity.checkAppExit();
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }*/
                Session session = Session.getActiveSession();
                if (!session.getPermissions().contains("publish_actions")){
                    Session.NewPermissionsRequest newPermissionsRequest =
                            new Session.NewPermissionsRequest(this, Arrays.asList("publish_actions"));
                    session.requestNewPublishPermissions(newPermissionsRequest);
                }
            }
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    public class GetTokenTask extends AsyncTask<LoginInfo, Void, ResponseBodyWrapped<SecureToken>> {

        @Override
        protected ResponseBodyWrapped<SecureToken> doInBackground(LoginInfo... params) {
            showProgress(true);
            LoginInfo user = null;
            if (params.length > 0) {
                user = params[0];
            } else {
                return new ResponseBodyWrapped<SecureToken>("error", "unknown", new SecureToken());
            }

            UserInfoConnector userInfoConnector = new UserInfoConnector();
            ResponseBodyWrapped<SecureToken> userInfo = userInfoConnector.getToken(user.getEmail(), user.getPassword());

            return userInfo;
        }

        @Override
        protected void onPostExecute(final ResponseBodyWrapped<SecureToken> success) {

            if (success != null && success.isSuccess()) {
                DreamApp.getInstance().setLogin(true);
                DreamApp.getInstance().setUser(success.getData().getUser());
                DreamApp.getInstance().setUsername(success.getData().getUser().getUsername());
                DreamApp.getInstance().setToken(success.getData().getToken());
                DreamApp.getInstance().setTokenType("unused");
                DreamApp.getInstance().saveAppDefaultInfo();

                /* Set Notifications On */
                AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
                alarm.setEverydayAlarm(context, true, 0);

                Session session = Session.getActiveSession();
                if (session != null && (session.isOpened() || session.isClosed())) {
                    Session.getActiveSession().closeAndClearTokenInformation();

                }
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else {
                this.cancel(false);
                Session session = Session.getActiveSession();
                if (session != null && (session.isOpened() || session.isClosed())) {
                    Session.getActiveSession().closeAndClearTokenInformation();
                }

                DreamApp.getInstance().setLogin(false);
            }
            showProgress(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected void onCancelled() {
            this.cancel(true);
            showProgress(false);
        }
    }

    private void showProgress(boolean b) {
        if(b) {
//            mFbLoginProgressBar.setVisibility(View.VISIBLE);
//            progressDialog.show();
//            mAuthButton.setVisibility(View.GONE);
        }else {
//            mFbLoginProgressBar.setVisibility(View.GONE);
//            progressDialog.dismiss();
//            mAuthButton.setVisibility(View.VISIBLE);
        }
    }
}
