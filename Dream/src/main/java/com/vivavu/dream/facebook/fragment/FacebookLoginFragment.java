package com.vivavu.dream.facebook.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.intro.IntroActivity;
import com.vivavu.dream.broadcastReceiver.AlarmManagerBroadcastReceiver;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.repository.connector.UserInfoConnector;

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
    private ProgressDialog progressDialog;

	protected int START_LOGIN = 0;
	protected int END_LOGIN = 1;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 0:
					if(!progressDialog.isShowing()){
						progressDialog.show();
					}
					break;
				case 1:
					if(progressDialog.isShowing()){
						progressDialog.dismiss();
					}
					break;
			}
		}
	};

    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
	        if (state == SessionState.OPENING){
				//handler.sendEmptyMessage(START_LOGIN);
	        }
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = DreamApp.getInstance();
        final View rootView = inflater.inflate(R.layout.include_facebook_login, container, false);
        ButterKnife.inject(this, rootView);
        List<String> readPermissions = new ArrayList<String>();
        readPermissions.add("public_profile");
        readPermissions.add("email");
        readPermissions.add("user_birthday");

	    LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = layoutInflater.inflate(R.layout.transparent_progress_dialog, null);
	    progressDialog = new ProgressDialog(getActivity(), R.style.progress_bar_style);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //progressDialog.setContentView(v);
	    //progressDialog.setMessage(getString(R.string.in_login));

	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.progress_bar_style);
	    builder.setView(v);
	    AlertDialog alertDialog = builder.create();
	    //builder.show();

        mTxtFacebookLoginExplain.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());
        //mTxtFacebookLoginExplain.setTextSize(14);
        mTxtFacebookLoginExplain.setTextColor(Color.WHITE);

        mAuthButton.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());
        //mAuthButton.setTextSize(15);
        mAuthButton.setReadPermissions(readPermissions);
        mAuthButton.setFragment(this);
        mAuthButton.setBackgroundResource(R.drawable.btn_fb_login);

        if(getActivity() instanceof IntroActivity){
            mTxtFacebookLoginExplain.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    //기본적으로 이 화면이 호출되는 시점은 로그인 타임이므로 무조건 로그아웃 시켜버린다.
	    Session session = Session.getActiveSession();
	    if(session != null && !session.isClosed()){
		    session.closeAndClearTokenInformation();
	    }
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
	            handler.sendEmptyMessage(START_LOGIN);
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
                return new ResponseBodyWrapped<SecureToken>(ResponseStatus.UNKNOWN_ERROR, "unknown", new SecureToken());
            }

            UserInfoConnector userInfoConnector = new UserInfoConnector();
            ResponseBodyWrapped<SecureToken> userInfo = userInfoConnector.getToken(user.getEmail(), user.getPassword());

            return userInfo;
        }

        @Override
        protected void onPostExecute(final ResponseBodyWrapped<SecureToken> success) {
	        handler.sendEmptyMessage(END_LOGIN);
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
                    //Session.getActiveSession().closeAndClearTokenInformation();

                }
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else {
	            if(success.getResponseStatus() == ResponseStatus.TIMEOUT) {
		            handler.post(new Runnable() {
			            @Override
			            public void run() {
				            Toast.makeText(getActivity(), R.string.server_timeout, Toast.LENGTH_SHORT).show();
			            }
		            });
	            }

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
