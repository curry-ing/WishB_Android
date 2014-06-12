package com.vivavu.dream.fragment.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.activity.setup.MoreActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.repository.connector.UserInfoConnector;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.view.ShadowImageView;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 23.
 */
public class LeftMenuDrawerFragment extends Fragment {
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
                    Toast.makeText(getActivity(), "사용자 정보 수정 실패", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "로그아웃", Toast.LENGTH_SHORT ).show();
                if(getActivity() instanceof MainActivity){
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
                final String items[] = {"카메라", "겔러리"};
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setTitle("선택");
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
                                Toast.makeText(getActivity(), "겔러리 선택", Toast.LENGTH_SHORT).show();
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

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        bindData();
    }

    private void bindData(){
        //todo: 로그인 체크하는 것은 한곳에서만 수행할것
        if(context.isLogin()){
            mMainLeftMenuTxtName.setText(DreamApp.getInstance().getUser().getUsername());
        }
    }

    private void createDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("사용자명 수정");
        alert.setMessage("사용자명 수정해보세요.");

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);
        input.setText(mMainLeftMenuTxtName.getText());
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                value.toString();

                User user = DreamApp.getInstance().getUser();
                user.setUsername(value);
                handler.sendEmptyMessage(SEND_DATA_START);
                Thread thread = new Thread(new UserModifyThread(user));
                thread.start();

            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Code.ACT_ADD_BUCKET_TAKE_CAMERA:
                if(resultCode == Activity.RESULT_OK){
                    doCropPhoto();
                }
                break;
            case Code.ACT_ADD_BUCKET_TAKE_GALLERY:
                if(resultCode == Activity.RESULT_OK){
                    if(data != null ) {
                        mImageCaptureUri = data.getData();
                        doCropPhoto();
                    }
                }
                break;
            case Code.ACT_ADD_BUCKET_CROP_FROM_CAMERA:
                if(data != null && data.getDataString() != null){
                    String path = AndroidUtils.convertContentsToFileSchema(DreamApp.getInstance(), data.getDataString());
                    File f = new File(path);
                    if(f.exists() && f.isFile()){
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisc(true)
                                .considerExifParams(true)
                                .showImageForEmptyUri(R.drawable.ic_profile_empty)
                                .build();
                        ImageLoader.getInstance().displayImage(data.getDataString(), mMainLeftMenuBtnProfile);
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
                photoFile = ImageUtil.createImageFile(); // 겔러리에 저장될 파일을 생성해놓음
                mImageCaptureUri = Uri.fromFile(photoFile); // 파일명 가져오기
            } catch (IOException ex) {
                Log.e(LeftMenuDrawerFragment.class.getSimpleName(), ex.getMessage());
                Toast.makeText(getActivity(), "카메라 준비중 에러가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", false);
                startActivityForResult(intent, Code.ACT_ADD_BUCKET_TAKE_CAMERA);
            }
        }else{
            Toast.makeText(getActivity(), "카메라 앱을 실행할 수 없습니다.", Toast.LENGTH_LONG).show();
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

        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
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
                handler.sendEmptyMessage(SEND_DATA_ERROR);
                return;
            }
            Message message = handler.obtainMessage(SEND_DATA_END, responseBodyWrapped.getData());
            handler.sendMessage(message);
        }
    }

}
