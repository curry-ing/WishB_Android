package com.vivavu.dream.common;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.StartActivity;
import com.vivavu.dream.activity.intro.IntroActivity;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.activity.main.TodayActivity;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.AppVersionInfo;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.connector.UserInfoConnector;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.FacebookUtils;
import com.vivavu.dream.util.NetworkUtil;

/**
 * Created by yuja on 14. 2. 6.
 */
public class BaseActionBarActivity extends ActionBarActivity implements View.OnClickListener{
    protected DreamApp context;
    protected NetworkChangeReceiver networkChangeReceiver;
    protected IntentFilter intentFilterChange;
    protected IntentFilter intentFilterWifi;
    public static final String EXTRA_KEY_FROM_ALARM = "fromAlarm";
    public static final int RESULT_USER_DATA_DELETED = RESULT_FIRST_USER;
    public static final int RESULT_USER_DATA_UPDATED = RESULT_FIRST_USER + 1;
    public static final int RESULT_USER_DATA_MODIFIED = RESULT_FIRST_USER + 2;
	public static final int SERVER_TIMEOUT = 0;
    public static Typeface denseRegularFont = null;
    public static Typeface nanumBarunGothicFont = null;
    public static Typeface nanumBarunGothicBoldFont = null;
    public static Typeface ptSansFont = null;
    public static Typeface droidSansFallback = null;
    public static Typeface roboto = null;
    public static Typeface robotoBold = null;

	protected ProgressDialog progressDialog;

	protected Handler defaultHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case SERVER_TIMEOUT:
					if(progressDialog != null && progressDialog.isShowing()){
						progressDialog.dismiss();
					}
					Toast.makeText(BaseActionBarActivity.this, getString(R.string.server_timeout), Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DreamApp.getInstance().getTracker();
        context = DreamApp.getInstance();
        networkChangeReceiver = new NetworkChangeReceiver();
        intentFilterChange = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilterWifi = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        checkNetwork();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, intentFilterChange);
        registerReceiver(networkChangeReceiver, intentFilterWifi);
	    /*if(DreamApp.getInstance() == null || DreamApp.getInstance().getUser() == null){
		    checkLogin();
	    }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    protected void onNetworkStateChanged(boolean connected){
        if(!connected) {
            Toast.makeText(this, getText(R.string.no_network_connection_toast), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkNetwork() {
        boolean connected = NetworkUtil.isAvaliableNetworkAccess(context);
        onNetworkStateChanged(connected);
        return connected;
    }

    public DreamApp getContext() {
        return context;
    }

    public void setContext(DreamApp context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        AndroidUtils.autoVisibleSoftInputFromWindow(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View view = AndroidUtils.getRootView(this);
        view.setOnClickListener(this);//root view에 click listener를 달아 두어 다른곳을 선책하면 키보드가 없어지도록 함

        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(DreamApp.getInstance()).reportActivityStart(this);
        /*Tracker tracker = DreamApp.getInstance().getTracker(DreamApp.TrackerName.APP_TRACKER);
        tracker.setScreenName(this.getLocalClassName());*/
    }

    @Override
    protected void onStop() {
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(DreamApp.getInstance()).reportActivityStop(this);
        /*Tracker tracker = DreamApp.getInstance().getTracker(DreamApp.TrackerName.APP_TRACKER);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());*/
        super.onStop();
    }

    public void goIntro(){
        Intent intent = new Intent();
        intent.setClass(this, IntroActivity.class);
        startActivityForResult(intent, Code.ACT_INTRO);
    }

    public void goMain(){
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivityForResult(intent, Code.ACT_MAIN);
    }

    public void goHome(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_FORWARD_RESULT
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

    public void goStartActivity(){
        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void goToday(){
        goToday(false);
    }

    public void goToday(boolean fromAlarm){
        Intent intent = new Intent();
        intent.setClass(this, TodayActivity.class);
        intent.putExtra(EXTRA_KEY_FROM_ALARM, fromAlarm);
        startActivity(intent);
    }
    public void checkAppExit() {
        Intent intent = getIntent();
        boolean isAppExit = intent.getBooleanExtra("isAppExit", false);

        if(isAppExit){
            finish();
        }else{
            branch();
        }
    }

    public void branch(){
        Intent intent = getIntent();
        Boolean goToday = intent.getBooleanExtra("goToday", false);

        if(checkLogin()){
            if (goToday){
                goToday(true);
            } else {
                goMain();
            }
        }else{
            goIntro();
        }
    }

    public boolean checkLogin(){
        if(!checkNetwork()){
            return false;
        }
        if(!context.isLogin() || FacebookUtils.isOpen()){
            if(context.hasValidToken()){
                UserInfoConnector userInfoConnector = new UserInfoConnector();
                ResponseBodyWrapped<BaseInfo> response = userInfoConnector.getBaseInfo();
                if(response.isSuccess()){
                    BaseInfo baseInfo = response.getData();
                    context.setUser(baseInfo);
                    context.setUsername(baseInfo.getUsername());
                    context.setLogin(true);
	                context.setAppVersionInfo(baseInfo.getAppVersionInfo());
	                context.setFbToken(baseInfo.getFbToken());
                    DataRepository.deleteBucketsNotEqualUserId(baseInfo.getId());//로그인 사용자 이외의 데이터 삭제
                    return true;
                }else if(response.getResponseStatus() == ResponseStatus.TIMEOUT) {
	                logout();
	                defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
                }else{
                    //로그인 에러시에 강제로 로그아웃 시켜서 무한루프에 안들어가도록 함
                    logout();
                }
            }
            context.setLogin(false);
            return false;
        }
        return true;
    }

    public void logout(){
        DataRepository.clearDb();
        context.logout();

        Session session = Session.getActiveSession();
        if(session != null && !session.isClosed()){
            //todo: close만 할것인지 clear 시켜서 토큰정보도 안남게 할 것인지.
            session.closeAndClearTokenInformation();
        }

        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        // activity 외부에서 activity 실행시 FLAG_ACTIVITY_NEW_TASK 를 넣어주어야한다.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isAppExit", false);
        startActivity(intent);
    }

    public void exit(){
        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isAppExit", true);
        startActivity(intent);
    }

	public boolean isNeedUpdate(){
		float version = Float.parseFloat(getString(R.string.wishb_app_version));
		User user = DreamApp.getInstance().getUser();
		if(user != null && user instanceof BaseInfo){
			AppVersionInfo lastestVersion = ((BaseInfo) user).getAppVersionInfo();
			if(lastestVersion!=null && lastestVersion.getVersion().floatValue() > version){
				return true;
			}
		}
		return false;
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*switch (requestCode){
            case Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE:
                Session activeSession = Session.getActiveSession();
                if(activeSession != null){
                    activeSession.onActivityResult(this, requestCode, resultCode, data);
                    activeSession.addCallback(new CustomFacebookStatusCallback(this));
                }

                return;
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Typeface getDenseRegularFont() {
        if(denseRegularFont == null){
            denseRegularFont = Typeface.createFromAsset(DreamApp.getInstance().getAssets(), "Dense-Regular.mp3");
        }
        return denseRegularFont;
    }

    public static Typeface getNanumBarunGothicFont() {
        if(nanumBarunGothicFont == null){
            nanumBarunGothicFont = Typeface.createFromAsset(DreamApp.getInstance().getAssets(), "NanumBarunGothic.mp3");
        }
        return nanumBarunGothicFont;
    }

    public static Typeface getNanumBarunGothicBoldFont() {
        if(nanumBarunGothicBoldFont == null){
            nanumBarunGothicBoldFont = Typeface.createFromAsset(DreamApp.getInstance().getAssets(), "NanumBarunGothicBold.mp3");
        }
        return nanumBarunGothicBoldFont;
    }

    public static Typeface getPtSansFont() {
        if(ptSansFont == null){
            ptSansFont = Typeface.createFromAsset(DreamApp.getInstance().getAssets(), "PT_SANS.ttf");
        }
        return ptSansFont;
    }

    public static Typeface getDriodSansFallback() {
        if(droidSansFallback == null) {
            droidSansFallback = Typeface.createFromAsset(DreamApp.getInstance().getAssets(), "DroidSansFallback.mp3");
        }
        return droidSansFallback;
    }

    public static Typeface getRoboto() {
        if(roboto == null) {
            roboto = Typeface.createFromAsset(DreamApp.getInstance().getAssets(), "Roboto-Regular.ttf");
        }
        return droidSansFallback;
    }

    public static Typeface getRobotoBold() {
        if(robotoBold == null) {
            robotoBold = Typeface.createFromAsset(DreamApp.getInstance().getAssets(), "Roboto-Bold.ttf");
        }
        return droidSansFallback;
    }

    public static void hideSoftKeyboard(){
        final InputMethodManager imm = (InputMethodManager) DreamApp.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public class CheckLoginTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            checkLogin();
            return null;
        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        protected boolean connected;

        @Override
        public void onReceive(Context context, Intent intent) {
            connected = NetworkUtil.isAvaliableNetworkAccess(DreamApp.getInstance());
            onNetworkStateChanged(connected);
        }

        public boolean isConnected() {
            return connected;
        }
    }
}
