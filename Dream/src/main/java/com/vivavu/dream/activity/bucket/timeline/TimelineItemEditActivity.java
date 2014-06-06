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
import android.util.Log;
import android.view.LayoutInflater;
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

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.enums.FacebookShareType;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.repository.connector.TimelineConnector;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.util.ImageUtil;

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
    ImageButton mMenuSave;
    @InjectView(R.id.txt_post_time)
    TextView mTxtPostTime;


    private ProgressDialog progressDialog;

    private static final int SEND_DATA_START = 0;
    private static final int SEND_DATA_SUCCESS = 1;
    private static final int SEND_DATA_FAIL = 2;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_DATA_START:
                    progressDialog.show();
                    break;
                case SEND_DATA_SUCCESS:
                    progressDialog.dismiss();
                    Toast.makeText(TimelineItemEditActivity.this, "저장하였습니다.", LENGTH_LONG).show();
                    Intent intent = new Intent();
                    Post obj = (Post) msg.obj;
                    intent.putExtra(TimelineItemViewActivity.extraKeyReturnValue, obj);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case SEND_DATA_FAIL:
                    progressDialog.dismiss();
                    Toast.makeText(TimelineItemEditActivity.this, "저장에 실패하였습니다.", LENGTH_LONG).show();
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
        actionBar.setHomeAsUpIndicator(R.drawable.ic_before);
        actionBar.setCustomView(R.layout.actionbar_timeline_edit);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("진행중");


        AlertDialog.Builder builder;

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate( R.layout.layout_date, null);

        ButterKnife.inject(this);

        Intent data = getIntent();
        bucket = (Bucket) data.getSerializableExtra(TimelineActivity.extraKeyBucket);
        post = (Post) data.getSerializableExtra(TimelineActivity.extraKeyPost);
        if(post.getId() == null || post.getId() < 1) {
            post.setBucketId(bucket.getId());
        }

        mTxtPostText.setTypeface(getNanumBarunGothicFont());

        bindData(bucket);
        bindData(post);

        initEvent();
    }

    private void initEvent() {
        mBtnPostCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = {"카메라", "겔러리"};
                AlertDialog.Builder ab = new AlertDialog.Builder(TimelineItemEditActivity.this);
                ab.setTitle("선택");
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
                            default:
                                break;
                        }
                    }
                });
                ab.show();
            }
        });
        mBtnPostFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] items = FacebookShareType.names();

                AlertDialog.Builder ab = new AlertDialog.Builder(TimelineItemEditActivity.this);
                ab.setTitle("선택");
                FacebookShareType fsType = FacebookShareType.fromCode(post.getFbShare());
                ab.setSingleChoiceItems(items, fsType.ordinal(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FacebookShareType selectedType = FacebookShareType.fromCode(items[which]);
                        post.setFbShare(selectedType.getCode());
                        if (post.getFbShare() != null) {
                            mBtnPostFacebook.setSelected(true);
                        } else {
                            mBtnPostFacebook.setSelected(false);
                        }
                        dialog.dismiss();
                    }
                });
                ab.show();
            }
        });

        mTxtPostDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mTxtPostDate.setText(String.format("%4d.%02d.%02d", year, monthOfYear, dayOfMonth ));
                    }
                };
                Calendar calendar = Calendar.getInstance();
                if(post.getRegDt() != null) {
                    calendar.setTime(post.getRegDt());
                }

                DatePickerDialog dialog = new DatePickerDialog(TimelineItemEditActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        mTxtPostTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mTxtPostTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                };
                Calendar calendar = Calendar.getInstance();
                if(post.getRegDt() != null) {
                    calendar.setTime(post.getRegDt());
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

    }

    private void bindData(Post post) {
        mTxtPostText.setText(post.getText());
        mTxtPostDate.setText(DateUtils.getDateString(post.getRegDt(), "yyyy.MM.dd", new Date()));
        mTxtPostTime.setText(DateUtils.getDateString(post.getRegDt(), "HH:mm", new Date()));
        /*ImageLoader.getInstance().displayImage(post.getImgUrl(), mIvCardImage, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                // 이미지가 없을 경우에는 imageview 자체를 안보여줌
                if(loadedImage != null) {
                    view.setVisibility(View.VISIBLE);
                }else {
                    view.setVisibility(View.GONE);
                }
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_TAKE_CAMERA:
                if(resultCode == RESULT_OK){
                    File f = new File(mImageCaptureUri.getPath());
                    // 찍은 사진을 이미지뷰에 보여준다.
                    if(data != null && data.getExtras() != null) {
                        Bitmap bm = (Bitmap) data.getExtras().getParcelable("data");
                        /*mIvCardImage.setVisibility(View.VISIBLE);
                        mIvCardImage.setImageBitmap(bm);*/
                    } else if( f.exists() ){
                        /// http://stackoverflow.com/questions/9890757/android-camera-data-intent-returns-null
                        /// EXTRA_OUTPUT을 선언해주면 해당 경로에 파일을 직접생성하고 썸네일을 리턴하지 않음
                        /*mIvCardImage.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(mImageCaptureUri.toString(), mIvCardImage);*/
                        //ImageUtil.setPic(mIvCardImage, mImageCaptureUri.getPath());
                    }
                    if(f.exists()){
                        //f.delete();
                        post.setPhoto(f);
                    }
                }
                return;
        }
    }

    private void postSave() {
        Post post = getPost();
        NetworkThread networkThread = new NetworkThread(post);
        Thread thread = new Thread(networkThread);
        thread.start();
    }

    public Post getPost() {
        post.setText(String.valueOf(mTxtPostText.getText()));
        post.setTimestamp(DateUtils.getDateFromString(String.valueOf(mTxtPostDate.getText() + " " + mTxtPostTime), "yyyy-MM-dd HH:mm", new Date()));
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
                photoFile = ImageUtil.createImageFile();
                mImageCaptureUri = Uri.fromFile(photoFile);

            } catch (IOException ex) {
                Log.e("dream", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
            }
        }else{
            Toast.makeText(this, "카메라 앱을 실행할 수 없습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void doTakeAlbumAction(){
        Intent intent = new Intent( Intent.ACTION_PICK ) ;
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE) ;
        startActivityForResult( intent, Code.ACT_ADD_BUCKET_TAKE_GALLERY ) ;
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
            }else {
                handler.sendEmptyMessage(SEND_DATA_FAIL);
            }
        }
    }
}
