package com.vivavu.dream.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.os.Handler;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;


/**
 * Created by yuja on 14. 2. 10.
 */
public class StartActivity extends BaseActionBarActivity {

//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Code.ACT_MAIN:
                checkAppExit();
            case Code.ACT_INTRO:
                if (resultCode == RESULT_OK) {
                    goMain();
                }else{
                    finish();
                }
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAppExit();
    }
}
