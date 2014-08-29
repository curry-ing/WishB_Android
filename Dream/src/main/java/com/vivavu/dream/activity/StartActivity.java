package com.vivavu.dream.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.connector.UserInfoConnector;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by yuja on 14. 2. 10.
 */
public class StartActivity extends BaseActionBarActivity {

	private static final int GET_USER_INFO_START = 0;
	private static final int GET_USER_INFO_SUCCESS = 1;
	private static final int GET_USER_INFO_FAIL = 2;

	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GET_USER_INFO_START:
					break;
				case GET_USER_INFO_SUCCESS:
					BaseInfo baseInfo = (BaseInfo) msg.obj;
					context.setUser(baseInfo);
					context.setUsername(baseInfo.getUsername());
					context.setLogin(true);
					context.setAppVersionInfo(baseInfo.getAppVersionInfo());
					context.setFbToken(baseInfo.getFbToken());
					DataRepository.deleteBucketsNotEqualUserId(baseInfo.getId());//로그인 사용자 이외의 데이터 삭제
					DataRepository.saveUser(baseInfo);

					branch();

					break;
				case GET_USER_INFO_FAIL:
					logout();
					break;
			}
		}
	};
	@InjectView(R.id.imageView)
	ImageView mImageView;
	@InjectView(R.id.test)
	TextView mTest;
	@InjectView(R.id.progress_bar)
	ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		ButterKnife.inject(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
			case Code.ACT_MAIN:
				checkAppExit();
			case Code.ACT_INTRO:
				if (resultCode == RESULT_OK) {
					goMain();
				} else {
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

	public void checkAppExit() {
		Intent intent = getIntent();
		boolean isAppExit = intent.getBooleanExtra("isAppExit", false);

		if (isAppExit) {
			finish();
		} else {
			if(context.hasValidToken()) {
				mProgressBar.setVisibility(View.VISIBLE);
				Thread thread = new Thread(new LoginThread());
				thread.start();
			} else {
				goIntro();
			}
		}
	}

	public void branch() {
		Intent intent = getIntent();
		Boolean goToday = intent.getBooleanExtra("goToday", false);

		if (checkLogin()) {
			if (goToday) {
				goToday(true);
			} else {
				goMain();
			}
		} else {
			goIntro();
		}
	}

	public void goMain() {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivityForResult(intent, Code.ACT_MAIN);
	}

	public class LoginThread implements Runnable {

		@Override
		public void run() {
			handler.sendEmptyMessage(GET_USER_INFO_START);

			UserInfoConnector userInfoConnector = new UserInfoConnector();
			ResponseBodyWrapped<BaseInfo> response = userInfoConnector.getBaseInfo();

			if (response.isSuccess()) {
				Message message = handler.obtainMessage(GET_USER_INFO_SUCCESS, response.getData());
				handler.sendMessage(message);
			} else {
				handler.sendEmptyMessage(GET_USER_INFO_FAIL);
			}
		}
	}

}
