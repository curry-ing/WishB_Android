package com.vivavu.dream.activity.bucket.timeline;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.FacebookShareType;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.repository.connector.TimelineConnector;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.view.ShadowImageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by yuja on 2014-03-28.
 */
public class TimelineItemEditActivity extends BaseActionBarActivity {
    public static final String TAG = "com.vivavu.dream.activity.bucket.timeline.TimelineItemEditActivity";
    public static final int REQUEST_CODE_TAKE_CAMERA = 0;


    Bucket bucket;
    Post post;
    protected Uri mImageCaptureUri;
    @InjectView(R.id.txt_post_date)
    TextView mTxtPostDate;
    @InjectView(R.id.txt_post_text)
    EditText mTxtPostText;
    @InjectView(R.id.iv_timeline_image)
    ImageView mIvTimelineImage;
    @InjectView(R.id.container_post_info)
    LinearLayout mContainerPostInfo;
    @InjectView(R.id.content_frame)
    LinearLayout mContentFrame;
    @InjectView(R.id.btn_post_facebook)
    Button mBtnPostFacebook;
    @InjectView(R.id.btn_post_camera)
    Button mBtnPostCamera;
    @InjectView(R.id.layout_timeline_option)
    LinearLayout mLayoutTimelineOption;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.txt_title)
    TextView mTxtTitle;
    @InjectView(R.id.menu_save)
    Button mMenuSave;
    @InjectView(R.id.txt_post_time)
    TextView mTxtPostTime;

    boolean modFlag = false;
    @InjectView(R.id.btn_timeline_attach)
    ShadowImageView mBtnTimelineAttach;

    private static final int SEND_DATA_START = 0;
    private static final int SEND_DATA_SUCCESS = 1;
    private static final int SEND_DATA_FAIL = 2;

    TextWatcher textWatcherInput = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // count 처음 입력된
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!s.toString().equals(post.getText())){
                modFlag = true;
            }
            if(s.toString().length() < 1){
                post.setText(null);
            }else {
                post.setText(s.toString());
            }
            checkRequireElement();
        }
    };

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_DATA_START:
                    progressDialog.show();
                    break;
                case SEND_DATA_SUCCESS:
                    progressDialog.dismiss();
                    Intent intent = new Intent();
                    Post obj = (Post) msg.obj;
                    intent.putExtra(TimelineItemViewActivity.extraKeyReturnValue, obj);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case SEND_DATA_FAIL:
                    progressDialog.dismiss();
                    Toast.makeText(TimelineItemEditActivity.this, getString(R.string.txt_timeline_edit_save_fail), LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.activity_timeline_item_edit);
        setResult(RESULT_CANCELED);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.actionbar_timeline_edit);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.in_progress));

        ButterKnife.inject(this);

        Intent data = getIntent();
        bucket = (Bucket) data.getSerializableExtra(TimelineActivity.extraKeyBucket);
        post = (Post) data.getSerializableExtra(TimelineActivity.extraKeyPost);
        if(post.getId() == null || post.getId() < 1) {
            post.setBucketId(bucket.getId());
        } else {
            mTxtPostText.setHint(null);
        }

        mTxtTitle.setTypeface(getNanumBarunGothicBoldFont());
        mTxtPostText.setTypeface(getNanumBarunGothicFont());
        bindData(bucket);
        bindData(post);
        mTxtPostText.addTextChangedListener(textWatcherInput);// 순서 중요. 데이터가 bind 된 이후 해야 변경사항 체크 가

        checkRequireElement();

        initEvent();
    }

    private void initEvent() {
        mBtnPostCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = DreamApp.getInstance().getResources().getStringArray(R.array.array_attach_image_only);
                AlertDialog.Builder ab = new AlertDialog.Builder(TimelineItemEditActivity.this);
                ab.setTitle(getString(R.string.choose));
                ab.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                doTakePhotoAction();
                                dialog.dismiss();
                                break;
                            case 1:
                                doTakeAlbumAction();
                                dialog.dismiss();
                                break;
                            case 2:
                                modFlag = true;
                                post.setImgUrl(null);
                                post.setPhoto(null);
                                Tracker tracker = DreamApp.getInstance().getTracker();
                                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_edit_activity)).setAction(getString(R.string.ga_event_action_image_delete));
                                tracker.send(eventBuilder.build());

                                bindData(post);
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

        mBtnTimelineAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = DreamApp.getInstance().getResources().getStringArray(R.array.array_image_attach);
                AlertDialog.Builder ab = new AlertDialog.Builder(TimelineItemEditActivity.this);
                ab.setTitle(getString(R.string.choose));
                ab.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                doTakePhotoAction();
                                dialog.dismiss();
                                break;
                            case 1:
                                doTakeAlbumAction();
                                dialog.dismiss();
                                break;
                            case 2:
                                modFlag = true;
                                post.setImgUrl(null);
                                post.setPhoto(null);
                                bindData(post);
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

        mBtnPostFacebook.setSelected(post.getFbFeedId() != null);
        mBtnPostFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	            if(DreamApp.getInstance().getUser().isFacebookLogin()) {
		            modFlag = true;
		            checkRequireElement();
		            boolean flag = !mBtnPostFacebook.isSelected();
		            mBtnPostFacebook.setSelected(flag);

		            Tracker tracker = DreamApp.getInstance().getTracker();
		            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_edit_activity)).setAction(getString(R.string.ga_event_action_share_facebook));
		            eventBuilder.setValue(flag ? 1 : 0);
		            tracker.send(eventBuilder.build());

		            post.setFbShare(flag ? FacebookShareType.SHARE.getCode() : FacebookShareType.NONE.getCode());
	            } else {
		            AlertDialog.Builder alertConfirm = new AlertDialog.Builder(TimelineItemEditActivity.this);
		            alertConfirm.setMessage(getString(R.string.txt_bucket_edit_confirm_need_facebook_login)).setPositiveButton(getString(R.string.confirm_check),
				            new DialogInterface.OnClickListener() {
					            @Override
					            public void onClick(DialogInterface dialog, int which) {
						            dialog.dismiss();
					            }
				            }
		            );
		            AlertDialog alert = alertConfirm.create();
		            alert.show();
	            }
            }
        });

        mTxtPostDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // monthOfYear가 -1 되어 들어옴
                        Tracker tracker = DreamApp.getInstance().getTracker();
                        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_edit_activity)).setAction(getString(R.string.ga_event_action_edit_date));
                        tracker.send(eventBuilder.build());
                        mTxtPostDate.setText(String.format("%4d.%02d.%02d", year, monthOfYear+1, dayOfMonth ));
                        modFlag = true;
                        checkRequireElement();
                    }
                };
                Calendar calendar = Calendar.getInstance();
                if(post.getContentDt() != null) {
                    calendar.setTime(post.getContentDt());
                }

                DatePickerDialog dialog = new DatePickerDialog(TimelineItemEditActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        mTxtPostTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Tracker tracker = DreamApp.getInstance().getTracker();
                        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_edit_activity)).setAction(getString(R.string.ga_event_action_edit_time));
                        tracker.send(eventBuilder.build());
                        mTxtPostTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                        modFlag = true;
                        checkRequireElement();
                    }
                };
                Calendar calendar = Calendar.getInstance();
                if(post.getContentDt() != null) {
                    calendar.setTime(post.getContentDt());
                }
                TimePickerDialog dialog = new TimePickerDialog(TimelineItemEditActivity.this, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true );
                dialog.show();
            }
        });
        mMenuSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSave();
            }
        });

        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void bindData(Post post) {
        mTxtPostText.setText(post.getText());
        mTxtPostDate.setText(DateUtils.getDateString(post.getContentDt(), "yyyy.MM.dd", new Date()));
        mTxtPostTime.setText(DateUtils.getDateString(post.getContentDt(), "HH:mm", new Date()));
        ImageLoader.getInstance().displayImage(post.getImgUrl(), mBtnTimelineAttach, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // 이미지가 없을 경우에는 imageview 자체를 안보여줌
                if(loadedImage != null) {
                    view.setVisibility(View.VISIBLE);
                    mBtnPostCamera.setVisibility(View.GONE);
                }else {
                    view.setVisibility(View.GONE);
                    mBtnPostCamera.setVisibility(View.VISIBLE);
                }
            }
        });
        checkRequireElement();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Code.ACT_ADD_BUCKET_TAKE_GALLERY:
                if(resultCode == RESULT_OK){
                    if(data != null ) {
                        Tracker tracker = DreamApp.getInstance().getTracker();
                        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_edit_activity)).setAction(getString(R.string.ga_event_action_image_gallery));
                        tracker.send(eventBuilder.build());

                        modFlag = true;
                        mImageCaptureUri = data.getData();
                        showImage(mImageCaptureUri, mBtnTimelineAttach);
                    }
                }
                break;
            case Code.ACT_ADD_BUCKET_TAKE_CAMERA:
                if(resultCode == RESULT_OK){
                    Tracker tracker = DreamApp.getInstance().getTracker();
                    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_edit_activity)).setAction(getString(R.string.ga_event_action_image_camera));
                    tracker.send(eventBuilder.build());

                    modFlag = true;
                    showImage(mImageCaptureUri, mBtnTimelineAttach);
                }
                break;
        }
    }

    private void showImage(Uri imageUri, ImageView view) {
        if(imageUri != null){
            File f = null;

            if("file".equals(imageUri.getScheme() )){
                f = new File(imageUri.getPath());
            } else if("content".equals(imageUri.getScheme())){
                String path = AndroidUtils.convertContentsToFileSchema(DreamApp.getInstance(), imageUri.toString());
                f = new File(path);
            }

            if(f!= null && f.exists() && f.isFile()){
                ImageLoader.getInstance().displayImage(imageUri.toString(), view, new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // 이미지가 없을 경우에는 imageview 자체를 안보여줌
                        if(loadedImage != null) {
                            view.setVisibility(View.VISIBLE);
                            mBtnPostCamera.setVisibility(View.GONE);
                        }else {
                            view.setVisibility(View.GONE);
                            mBtnPostCamera.setVisibility(View.VISIBLE);
                        }
                    }
                });
                post.setPhoto(f);
            }
        }
        checkRequireElement();
    }

    private boolean checkRequireElement(){
        if (post != null && modFlag
                && (( post.getId() != null && post.getId() > 0 && ( post.getText() != null || post.getPhoto() != null || post.getImgUrl() != null)) // 글 수정시 세가지 중 하나만 있어도 수정 가능
                || ((post.getId() == null || post.getId() < 1) && (post.getText() != null || post.getPhoto() != null)) // 글 추가시에는 둘 중 하나만 있어도 추가 가능
                )
                ) {
            mMenuSave.setVisibility(View.VISIBLE);
            return true;
        }else{
            mMenuSave.setVisibility(View.INVISIBLE);
            return false;
        }
    }

    private void postSave() {
        if(checkRequireElement()) {
            AndroidUtils.hideSoftInputFromWindow(DreamApp.getInstance(), mTxtPostText);
            Post post = getPost();

            Tracker tracker = DreamApp.getInstance().getTracker();
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_edit_activity)).setAction(getString(R.string.ga_event_action_save));
            tracker.send(eventBuilder.build());

            NetworkThread networkThread = new NetworkThread(post);
            Thread thread = new Thread(networkThread);
            thread.start();
        }
    }

    public Post getPost() {
        post.setText(String.valueOf(mTxtPostText.getText()));
        post.setContentDt(DateUtils.getDateFromString(String.valueOf(mTxtPostDate.getText() + " " + mTxtPostTime.getText()), "yyyy.MM.dd HH:mm", new Date()));
        return post;
    }

    private void bindData(Bucket bucket) {
        mTxtTitle.setText(bucket.getTitle());
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
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ImageUtil.createImageFile(); // 갤러리에 저장될 파일을 생성해놓음
                mImageCaptureUri = Uri.fromFile(photoFile); // 파일명 가져오기
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
                Toast.makeText(this, getString(R.string.txt_camera_ready_error), Toast.LENGTH_LONG).show();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", false);
                startActivityForResult(intent, Code.ACT_ADD_BUCKET_TAKE_CAMERA);
            }
        }else{
            Toast.makeText(this, getString(R.string.txt_camera_not_exc), Toast.LENGTH_LONG).show();
        }
    }

    private void doTakeAlbumAction(){
        Intent intent = new Intent( Intent.ACTION_PICK ) ;
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE) ;
        startActivityForResult( intent, Code.ACT_ADD_BUCKET_TAKE_GALLERY ) ;
    }

    public void confirm(){
        if(checkRequireElement()) {
            AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
            alertConfirm.setMessage(getString(R.string.txt_timeline_edit_confirm_edit_body)).setCancelable(false).setPositiveButton(getString(R.string.confirm_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Tracker tracker = DreamApp.getInstance().getTracker();
                            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_timeline_item_edit_activity)).setAction(getString(R.string.ga_event_action_cancel));
                            tracker.send(eventBuilder.build());

                            finish();
                            return;
                        }
                    }
            ).setNegativeButton(getString(R.string.confirm_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //postSave();
                        }
                    }
            );
            AlertDialog alert = alertConfirm.create();
            alert.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        confirm();
    }

    private class NetworkThread implements Runnable{
        protected Post post;

        public NetworkThread(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            handler.sendEmptyMessage(SEND_DATA_START);

            TimelineConnector timelineConnector = new TimelineConnector();
            ResponseBodyWrapped<Post> result;
            if(post.getId() != null && post.getId() > 1){
                result = timelineConnector.put(post);
            }else {
                result = timelineConnector.post(post);
            }
            if(result.isSuccess()) {
                Message message = handler.obtainMessage(SEND_DATA_SUCCESS, result.getData());
                handler.sendMessage(message);
            }else if(result.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
            }else {
                handler.sendEmptyMessage(SEND_DATA_FAIL);
            }
        }
    }
}
