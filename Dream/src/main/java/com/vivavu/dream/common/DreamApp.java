package com.vivavu.dream.common;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vivavu.dream.R;
import com.vivavu.dream.model.user.User;

import java.util.HashMap;

/**
 * Created by yuja on 14. 1. 17.
 */
public class DreamApp extends Application {
    public static final String LOG_TAG = "dream";
    public static boolean debugMode = true;

    private User user  = null;
    private String token = null;

    private String tokenType = null;
    private String username = null;
    private String email = null;
    private String password = null;

    private boolean login=false;

    protected static DreamApp dreamApp;
    public static DreamApp getInstance(){

        return dreamApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadAppDefaultInfo();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.no_image)
                .showImageOnFail(R.drawable.no_image)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
        dreamApp = this;
    }

    @Override
    public void onTerminate() {
        saveAppDefaultInfo();
        super.onTerminate();
    }

    public void logout(){
        setLogin(false);
        setUser(null);
        setToken(null);
        setTokenType(null);
        saveAppDefaultInfo();
    }

    public void loadAppDefaultInfo() {

        //기존 저장된 로그인 관련 정보 불러오기
        SharedPreferences settings = getSharedPreferences(Constants.settings, MODE_PRIVATE);
        String email = settings.getString(Constants.email, "");
        String token = settings.getString(Constants.token, "");
        String tokenType = settings.getString(Constants.tokenType, "");

        setEmail(email);
        setToken(token);
        setTokenType(tokenType);
    }

    public void saveAppDefaultInfo() {

        // 프리퍼런스 설정
        SharedPreferences prefs = getSharedPreferences(Constants.settings, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Constants.email, getEmail());
        editor.putString(Constants.token, getToken());   // String
        editor.putString(Constants.tokenType, getTokenType());   // String

        editor.commit();
    }

    public void setToken(String token, String tokenType){
        this.token = token;
        this.tokenType = tokenType;
        saveAppDefaultInfo();
    }

    public boolean hasValidToken(){
        if(this.token != null && this.token.length() > 0){
            return true;
        }
        return false;
    }
    public static String getLogTag() {
        return LOG_TAG;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        DreamApp.debugMode = debugMode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        if(username == null){
            return getUser().getUsername();
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized public Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.enableAutoActivityReports(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(getResources().getString(R.string.ga_trackingId))
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(0)
                    : analytics.newTracker(1);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    synchronized public Tracker getTracker(){
        if (!mTrackers.containsKey(TrackerName.APP_TRACKER)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.enableAutoActivityReports(this);
            Tracker t = analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(TrackerName.APP_TRACKER, t);

        }

        return mTrackers.get(TrackerName.APP_TRACKER);
    }

}
