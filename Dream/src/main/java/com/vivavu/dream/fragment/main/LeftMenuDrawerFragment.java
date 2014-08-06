package com.vivavu.dream.fragment.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.activity.setup.MoreActivity;
import com.vivavu.dream.broadcastReceiver.AlarmManagerBroadcastReceiver;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.repository.connector.UserInfoConnector;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.FileUtils;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.view.ShadowImageView;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 23.
 */
public class LeftMenuDrawerFragment extends CustomBaseFragment {
    private DreamApp context = null;
    @InjectView(R.id.main_left_menu_btn_profile)
    ShadowImageView mMainLeftMenuBtnProfile;
    @InjectView(R.id.main_left_menu_txt_name)
    TextView mMainLeftMenuTxtName;
    @InjectView(R.id.main_left_menu_btn_cnt_bucket)
    Button mMainLeftMenuBtnCntBucket;
    @InjectView(R.id.main_left_menu_btn_cnt_friends)
    Button mMainLeftMenuBtnCntFriends;
    @InjectView(R.id.main_left_menu_btn_cnt_badge)
    Button mMainLeftMenuBtnCntBadge;
    @InjectView(R.id.main_left_menu_btn_setting)
    Button mMainLeftMenuBtnSetting;
    @InjectView(R.id.main_left_menu_btn_notice)
    Button mMainLeftMenuBtnNotice;
    @InjectView(R.id.main_left_menu_btn_update)
    Button mMainLeftMenuBtnUpdate;
    @InjectView(R.id.main_left_menu_btn_logout)
    Button mMainLeftMenuBtnLogout;

    private static final int SEND_DATA_START = 0;
    private static final int SEND_DATA_END = 1;
    private static final int SEND_DATA_ERROR = 2;

    private Uri mImageCaptureUri;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_DATA_START:
                    break;
                case SEND_DATA_END:
                    User user = (User) msg.obj;
                    DreamApp.getInstance().setUser(user);
                    bindData();
                    break;
                case SEND_DATA_ERROR:
                    Toast.makeText(getActivity(), getString(R.string.txt_fragment_profile_user_info_update_fail), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = DreamApp.getInstance();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main_left_menu, container, false);
        ButterKnife.inject(this, rootView);

        mMainLeftMenuBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_logout));
                tracker.send(eventBuilder.build());

                Toast.makeText(getActivity(), getString(R.string.txt_fragment_profile_logout_message), Toast.LENGTH_SHORT ).show();
                if(getActivity() instanceof MainActivity){
                    /* Set Notification Off */
                    AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
                    alarm.setEverydayAlarm(context, false, 0);
//                    alarm.SetAlarm(context, 1, false, 23, 0);
//                    alarm.SetAlarm(context, 2, false, 11, 0);

                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.logout();
                }
            }
        });

        mMainLeftMenuTxtName.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());
        mMainLeftMenuBtnLogout.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());
        mMainLeftMenuBtnNotice.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());
        mMainLeftMenuBtnSetting.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());
        mMainLeftMenuBtnUpdate.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());

        mMainLeftMenuBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = DreamApp.getInstance().getResources().getStringArray(R.array.array_image_attach);
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setTitle(getString(R.string.choose));
                ab.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                doTakePhotoAction();
                                dialog.dismiss();
                                break;
                            case 1:
                                doTakeAlbumAction();
                                dialog.dismiss();
                                break;
                            case 2:
                                User user = DreamApp.getInstance().getUser();
                                user.setProfileImgUrl(null);
	                            bindData();
                                // 사진을 찍고 그냥
                                FileUtils.deleteFile(user.getPhoto());
                                user.setPhoto(null);
                                Tracker tracker = DreamApp.getInstance().getTracker();
                                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_image_delete));
                                tracker.send(eventBuilder.build());

                                handler.sendEmptyMessage(SEND_DATA_START);
                                Thread thread = new Thread(new UserModifyThread(user));
                                thread.start();
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                });
                ab.show();
            }
        });

        mMainLeftMenuBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_setting));
                tracker.send(eventBuilder.build());

                Intent intent = new Intent(getActivity(), MoreActivity.class);
                startActivity(intent);
            }
        });

        mMainLeftMenuTxtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        mMainLeftMenuBtnNotice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_notice));
                tracker.send(eventBuilder.build());
            }
        });

        mMainLeftMenuBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_update));
                tracker.send(eventBuilder.build());

	            if(checkUpdate()) {
	                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		            ab.setTitle(getString(R.string.choose));
		            ab.setMessage(R.string.need_update);
		            ab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {

			            }
		            });
		            ab.setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
				            Intent i = new Intent(Intent.ACTION_VIEW);
				            i.setData(Uri.parse(DreamApp.getInstance().getAppVersionInfo().getUrl()));
				            startActivity(i);
			            }
		            });
		            ab.show();
	            } else{
		            Toast.makeText(getActivity(), "최신버전을 사용중입니다", Toast.LENGTH_SHORT).show();
	            }
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        bindData();
    }

	@Override
	public void onResume() {
		super.onResume();
		checkUpdate();
	}

	private boolean checkUpdate() {
		if(getActivity() instanceof BaseActionBarActivity) {
			BaseActionBarActivity activity = (BaseActionBarActivity) getActivity();
			if(activity.isNeedUpdate()){
				mMainLeftMenuBtnUpdate.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.login_check_alert_icon, 0);
				return true;
			} else{
				Toast.makeText(getActivity(), "최신버전을 사용중입니다", Toast.LENGTH_SHORT).show();
				mMainLeftMenuBtnUpdate.setCompoundDrawablesWithIntrinsicBounds(0,0,0, 0);
				return false;
			}
		}
		return false;
	}

	private void bindData(){
        //todo: 로그인 체크하는 것은 한곳에서만 수행할것
        if(context.isLogin()){
            mMainLeftMenuTxtName.setText(DreamApp.getInstance().getUser().getUsername());
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .considerExifParams(true)
                    .showImageForEmptyUri(R.drawable.ic_profile_empty)
                    .build();
            ImageLoader.getInstance().displayImage(DreamApp.getInstance().getUser().getProfileImgUrl(), mMainLeftMenuBtnProfile, options);

            if(getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.updateProfileImg();
            }

        }
    }

    private void createDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(64)});
        input.setSingleLine();
        input.setText(mMainLeftMenuTxtName.getText());
        input.selectAll();

        alert.setPositiveButton(getString(R.string.txt_fragment_profile_alert_submit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                mMainLeftMenuTxtName.setText(value);
                User user = DreamApp.getInstance().getUser();
                user.setUsername(value);

                Tracker tracker = DreamApp.getInstance().getTracker();
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_edit_username));
                tracker.send(eventBuilder.build());

                handler.sendEmptyMessage(SEND_DATA_START);
                Thread thread = new Thread(new UserModifyThread(user));
                thread.start();
                AndroidUtils.hideSoftInputFromWindow(DreamApp.getInstance(), input);

            }
        });

        alert.setNegativeButton(getString(R.string.txt_fragment_profile_alert_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        AndroidUtils.hideSoftInputFromWindow(DreamApp.getInstance(), input);
                    }
                }
        );
        AndroidUtils.showSoftInputFromWindow(getActivity(), input);
        alert.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Code.ACT_ADD_BUCKET_TAKE_CAMERA:
                if(resultCode == Activity.RESULT_OK){
                    Tracker tracker = DreamApp.getInstance().getTracker();
                    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_image_camera));
                    tracker.send(eventBuilder.build());

                    doCropPhoto();
                }
                break;
            case Code.ACT_ADD_BUCKET_TAKE_GALLERY:
                if(resultCode == Activity.RESULT_OK){
                    if(data != null ) {
                        Tracker tracker = DreamApp.getInstance().getTracker();
                        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_profile_fragment)).setAction(getString(R.string.ga_event_action_image_gallery));
                        tracker.send(eventBuilder.build());

                        mImageCaptureUri = data.getData();
                        doCropPhoto();
                    }
                }
                break;
            case Code.ACT_ADD_BUCKET_CROP_FROM_CAMERA:
                if(data != null && data.getExtras() != null && data.getExtras().getParcelable("data") != null){
                    Bitmap photo = data.getExtras().getParcelable("data");
                    mMainLeftMenuBtnProfile.setImageBitmap(photo);
                } else if(data != null && data.getExtras() != null && data.getExtras().getParcelable("output") != null){
                    Uri cropFileUri = data.getExtras().getParcelable("output");
                    File f = null;
                    if("file".equals(cropFileUri.getScheme() )){
                        f = new File(cropFileUri.getPath());
                    } else if("content".equals(cropFileUri.getScheme())){
                        String path = AndroidUtils.convertContentsToFileSchema(DreamApp.getInstance(), cropFileUri.toString());
                        f = new File(path);
                    }

                    if(f.exists() && f.isFile()){
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisc(true)
                                .considerExifParams(true)
                                .showImageForEmptyUri(R.drawable.ic_profile_empty)
                                .build();
                        ImageLoader.getInstance().displayImage(data.getDataString(), mMainLeftMenuBtnProfile, options);
                        User user = DreamApp.getInstance().getUser();
                        user.setPhoto(f);
                        handler.sendEmptyMessage(SEND_DATA_START);
                        Thread thread = new Thread(new UserModifyThread(user));
                        thread.start();
                    }

                } else if(data != null && data.getDataString() != null){
                    String path = AndroidUtils.convertContentsToFileSchema(DreamApp.getInstance(), data.getDataString());
                    File f = new File(path);
                    if(f.exists() && f.isFile()){
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisc(true)
                                .considerExifParams(true)
                                .showImageForEmptyUri(R.drawable.ic_profile_empty)
                                .build();
                        ImageLoader.getInstance().displayImage(data.getDataString(), mMainLeftMenuBtnProfile, options);
                        User user = DreamApp.getInstance().getUser();
                        user.setPhoto(f);
                        handler.sendEmptyMessage(SEND_DATA_START);
                        Thread thread = new Thread(new UserModifyThread(user));
                        thread.start();
                    }
                }
                break;
        }
    }

    private void doTakePhotoAction(){
        /*
        * 참고 해볼곳
        * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
        * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
        * http://www.damonkohler.com/2009/02/android-recipes.html
        * http://www.firstclown.us/tag/android/
        */

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ImageUtil.createImageFile(); // 갤러리에 저장될 파일을 생성해놓음
                mImageCaptureUri = Uri.fromFile(photoFile); // 파일명 가져오기
            } catch (IOException ex) {
                Log.e(LeftMenuDrawerFragment.class.getSimpleName(), ex.getMessage());
                Toast.makeText(getActivity(), getString(R.string.txt_camera_ready_error), Toast.LENGTH_LONG).show();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", false);
                startActivityForResult(intent, Code.ACT_ADD_BUCKET_TAKE_CAMERA);
            }
        }else{
            Toast.makeText(getActivity(), getString(R.string.txt_camera_not_exc), Toast.LENGTH_LONG).show();
        }
    }

    private void doTakeAlbumAction(){
        Intent intent = new Intent( Intent.ACTION_PICK ) ;
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE) ;
        startActivityForResult( intent, Code.ACT_ADD_BUCKET_TAKE_GALLERY ) ;
    }

    private void doCropPhoto(){
        // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
        // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageCaptureUri, "image/*");

        intent.putExtra("outputX", 540);
        intent.putExtra("outputY", 540);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        if("samsung".compareToIgnoreCase(Build.BRAND)  == 0 || "samsung".compareToIgnoreCase(Build.MANUFACTURER) == 0){
            try{
                File cropFile = ImageUtil.createImageFile();
                intent.putExtra("output", Uri.fromFile(cropFile));
            }catch (IOException e){

            }
        }

        startActivityForResult(intent, Code.ACT_ADD_BUCKET_CROP_FROM_CAMERA);
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
                responseBodyWrapped = userInfoConnector.put(user);
            }

            if(!responseBodyWrapped.isSuccess()){
	            if(responseBodyWrapped.getResponseStatus() == ResponseStatus.TIMEOUT){
		            handler.post(new Runnable() {
			            @Override
			            public void run() {
				            Toast.makeText(getActivity(), R.string.server_timeout, Toast.LENGTH_SHORT).show();
			            }
		            });
		            return;
	            }
                handler.sendEmptyMessage(SEND_DATA_ERROR);
                return;
            }

            FileUtils.deleteFile(user.getPhoto());
            user.setPhoto(null);

            Message message = handler.obtainMessage(SEND_DATA_END, responseBodyWrapped.getData());
            handler.sendMessage(message);
        }
    }

}
