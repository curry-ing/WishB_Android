package com.vivavu.dream.activity.setup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.login.UserAgreementActivity;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.repository.connector.UserInfoConnector;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MoreActivity extends BaseActionBarActivity {

    @InjectView(R.id.btn_alert_setting)
    Button mBtnAlertSetting;
    @InjectView(R.id.btn_public_setting)
    Button mBtnPublicSetting;
    @InjectView(R.id.btn_help)
    Button mBtnHelp;
    @InjectView(R.id.btn_inquiry)
    Button mBtnInquiry;
    @InjectView(R.id.btn_access_terms)
    Button mBtnAccessTerms;
//    @InjectView(R.id.btn_privacy)
//    Button mBtnPrivacy;
    @InjectView(R.id.btn_member_leave)
    Button mBtnMemberLeave;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;

    private static final int SEND_DATA_START = 0;
    private static final int SEND_DATA_END = 1;
    private static final int SEND_DATA_ERROR = 2;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_DATA_START:
                    break;
                case SEND_DATA_END:
                    logout();
                    break;
                case SEND_DATA_ERROR:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_more);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_more);

        ButterKnife.inject(this);

        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTxtTitle.setTypeface(getNanumBarunGothicBoldFont());
        //mTxtTitle.setTextSize(20);
        mTxtTitle.setTextColor(Color.WHITE);

        mBtnAlertSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_more_activity)).setAction(getString(R.string.ga_event_action_setting));
                tracker.send(eventBuilder.build());
                Intent intent = new Intent(MoreActivity.this, AlertSettingsActivity.class);
                startActivity(intent);
            }
        });
        mBtnPublicSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mBtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MoreActivity.this, "도움말보기", Toast.LENGTH_SHORT).show();
            }
        });
        mBtnInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_more_activity)).setAction(getString(R.string.ga_event_action_inquiry));
                tracker.send(eventBuilder.build());
                Intent intent = new Intent(MoreActivity.this, InquiryActivity.class);
                startActivity(intent);
            }
        });
        mBtnAccessTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_more_activity)).setAction(getString(R.string.ga_event_action_access_terms));
                tracker.send(eventBuilder.build());
                Intent intent = new Intent(MoreActivity.this, UserAgreementActivity.class);
                startActivity(intent);
            }
        });
//        mBtnPrivacy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Tracker tracker = DreamApp.getInstance().getTracker();
//                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_more_activity)).setAction(getString(R.string.ga_event_action_privacy));
//                tracker.send(eventBuilder.build());
//                Intent intent = new Intent(MoreActivity.this, PrivacyActivity.class);
//                startActivity(intent);
//            }
//        });

        mBtnMemberLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_more_activity)).setAction(getString(R.string.ga_event_action_member_leave));
                tracker.send(eventBuilder.build());
                /*Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);*/
                Toast.makeText(MoreActivity.this, getString(R.string.txt_more_member_leave), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alertConfirm = new AlertDialog.Builder(MoreActivity.this);
                alertConfirm.setMessage(getString(R.string.txt_more_member_leave_body)).setCancelable(false).setPositiveButton(getString(R.string.confirm_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Thread thread = new Thread(new UserModifyThread(DreamApp.getInstance().getUser()));
                                thread.start();
                            }
                        }
                ).setNegativeButton(getString(R.string.confirm_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }
                );
                AlertDialog alert = alertConfirm.create();
                alert.show();

            }
        });

    }

    private class UserModifyThread implements Runnable {
        private User user;

        private UserModifyThread(User user) {
            this.user = user;
        }

        @Override
        public void run() {
            UserInfoConnector userInfoConnector = new UserInfoConnector();
            ResponseBodyWrapped<User> responseBodyWrapped = new ResponseBodyWrapped<User>();

            if(user != null ){
                responseBodyWrapped = userInfoConnector.delete(user);
            }


            if(responseBodyWrapped.isSuccess()){
	            handler.sendEmptyMessage(SEND_DATA_END);
                return;
            }else if(responseBodyWrapped.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
	            return;
            }

	        handler.sendEmptyMessage(SEND_DATA_ERROR);

        }
    }

}
