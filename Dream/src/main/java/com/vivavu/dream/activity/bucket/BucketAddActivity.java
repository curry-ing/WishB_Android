package com.vivavu.dream.activity.bucket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.enums.RepeatType;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.option.OptionDDay;
import com.vivavu.dream.model.bucket.option.OptionDescription;
import com.vivavu.dream.model.bucket.option.OptionRepeat;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.task.CustomAsyncTask;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.view.TextImageView;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 13.
 */
public class BucketAddActivity extends BaseActionBarActivity {
    private static final int SEND_DATA_START = 0;
    private static final int SEND_DATA_END = 1;
    private static final int SEND_DATA_ERROR = 2;
    private static final int SEND_DATA_DELETE = 3;
    public static final String RESULT_EXTRA_BUCKET = "bucket";
    public static final String RESULT_EXTRA_BUCKET_ID = "bucketId";

    @InjectView(R.id.bucket_img)
    TextImageView mBucketImg;
    @InjectView(R.id.bucket_input_title)
    EditText mBucketInputTitle;
    @InjectView(R.id.bucket_input_deadline)
    EditText mBucketInputDeadline;
    @InjectView(R.id.btn_bucket_option_note)
    Button mBtnBucketOptionNote;
    @InjectView(R.id.btn_bucket_option_repeat)
    Button mBtnBucketOptionRepeat;
    @InjectView(R.id.btn_bucket_option_public)
    Button mBtnBucketOptionPublic;
    @InjectView(R.id.btn_bucket_option_pic)
    Button mBtnBucketOptionPic;
    @InjectView(R.id.btn_bucket_option_gallery)
    Button mBtnBucketOptionGallery;
    @InjectView(R.id.btn_bucket_option_del)
    Button mBtnBucketOptionDel;


    private LayoutInflater layoutInflater;
    private Bucket bucket = null;
    protected Uri mImageCaptureUri;
    private String modString;

    private ProgressDialog progressDialog;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_DATA_START:
                    progressDialog.show();
                    break;
                case SEND_DATA_END:
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(BucketAddActivity.this, modString + "성공", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_EXTRA_BUCKET_ID, (Integer) bucket.getId());
                    intent.putExtra(RESULT_EXTRA_BUCKET, bucket);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case SEND_DATA_ERROR:
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(BucketAddActivity.this, modString + "실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                    break;
                case SEND_DATA_DELETE:
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(BucketAddActivity.this, modString + "버킷을 삭제하였습니다.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_USER_DATA_DELETED);
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능
        setContentView(R.layout.bucket_input_default);
        setResult(RESULT_CANCELED);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_before);
        actionBar.setCustomView(R.layout.actionbar_bucket_edit);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("진행중");

        Intent data = getIntent();

        int bucketId = data.getIntExtra(RESULT_EXTRA_BUCKET_ID, -1);
        bucket = DataRepository.getBucket(bucketId);
        int code;
        if (bucketId > 0) {

            code = Code.ACT_MOD_BUCKET_DEFAULT_CARD;
            modString = "수정";
            actionBar.setTitle("Mod Bucket");
        } else {
            code = Code.ACT_ADD_BUCKET_DEFAULT_CARD;
            modString = "등록";
            actionBar.setTitle("Add Bucket");
        }

        ButterKnife.inject(this);

        addEventListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.bucket_add_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.bucket_add_menu_save:

                if (bucket == null || bucket.getTitle() == null || bucket.getTitle().trim().length() <= 0) {
                    Toast.makeText(this, "필수입력 항목이 입력되지 않았음", Toast.LENGTH_SHORT).show();
                }else{
                    saveBucket();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveBucket() {
        handler.sendEmptyMessage(SEND_DATA_START);
        BucketAddTask bucketAddTask = new BucketAddTask();
        bucketAddTask.execute(bucket);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Code.ACT_ADD_BUCKET_OPTION_DDAY:
                if(resultCode == RESULT_OK){
                    OptionDDay dDay = (OptionDDay) data.getSerializableExtra("option.result");
                    updateUiData(dDay);
                }
                break;
            case Code.ACT_ADD_BUCKET_OPTION_DESCRIPTION:
                if(resultCode == RESULT_OK){
                    OptionDescription description = (OptionDescription) data.getSerializableExtra("option.result");
                    updateUiData(description);
                }
                break;
            case Code.ACT_ADD_BUCKET_OPTION_REPEAT:
                if(resultCode == RESULT_OK){
                    OptionRepeat repeat = (OptionRepeat) data.getSerializableExtra("option.result");
                    updateUiData(repeat);
                }
                break;
            case Code.ACT_ADD_BUCKET_TAKE_CAMERA:
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
                        ImageUtil.setPic(mIvCardImage, mImageCaptureUri.getPath());*/
                    }
                    if(f.exists()){
                        //f.delete();
                        bucket.setFile(f);
                    }
                }
                break;
            case Code.ACT_ADD_BUCKET_TAKE_GALLERY:
                if(resultCode == RESULT_OK){
                    // 찍은 사진을 이미지뷰에 보여준다.
                    if(data != null && data.getExtras() != null) {
                        mImageCaptureUri = data.getData();
                        File f = new File(mImageCaptureUri.getPath());
                        if(f.exists()){
                            //f.delete();
                            bucket.setFile(f);
                        }
                    }
                    doCropPhoto();
                }
                break;
            case Code.ACT_ADD_BUCKET_CROP_FROM_CAMERA:
                if(data != null && data.getExtras() != null){
                    final Bundle extras = data.getExtras();
                    if(extras != null){
                        Bitmap photo = extras.getParcelable("data");
                        mBucketImg.setImageBitmap(photo);
                    }
                }
                break;
        }
    }

    private void updateUiData(OptionDDay dday) {
        bucket.setTitle(mBucketInputTitle.getText().toString());
        bucket.setScope("DECADE");
        bucket.setRange(dday.getRange());
        bucket.setDeadline(dday.getDeadline());
        bindData();
    }

    private void updateUiData(OptionRepeat repeat) {
        bucket.setRptType(repeat.getRepeatType().getCode());
        bucket.setRptCndt(repeat.getOptionStat());
        bindData();
    }

    private void updateUiData(OptionDescription description) {
        bucket.setDescription(description.getDescription());
        bindData();
    }

    private void addEventListener() {
        mBucketInputTitle.addTextChangedListener(textWatcherInput);

        mBtnBucketOptionNote.setOnClickListener(this);
        mBtnBucketOptionRepeat.setOnClickListener(this);
        mBtnBucketOptionPublic.setOnClickListener(this);
        mBtnBucketOptionPic.setOnClickListener(this);
        mBtnBucketOptionGallery.setOnClickListener(this);

        mBtnBucketOptionDel.setOnClickListener(this);
        mBucketImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = {"카메라", "겔러리"};
                AlertDialog.Builder ab = new AlertDialog.Builder(BucketAddActivity.this);
                ab.setTitle("선택");
                ab.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                doTakePhotoAction();
                                break;
                            case 1:
                                doTakeAlbumAction();
                                break;
                            default:
                                break;
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                ab.show();
            }
        });
        mBucketInputDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOptionDday();
            }
        });
        mBucketInputDeadline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    goOptionDday();
                } else {

                }
            }
        });
    }


    private void bindData() {
        mBucketInputTitle.setText(bucket.getTitle());

        /*ImageLoader.getInstance().displayImage(bucket.getCvrImgUrl(), mIvCardImage, new SimpleImageLoadingListener(){
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

        if (bucket.getDeadline() != null) {
            mBucketInputDeadline.setText( DateUtils.getDateString(bucket.getDeadline(), "yyyy. MM. dd"));
        } else {
            mBucketInputDeadline.setText("In my life");
        }

        mBtnBucketOptionPublic.setSelected( bucket.getIsPrivate() == null || bucket.getIsPrivate() == 1 );

        /*DescriptionViewFragment descriptionViewFragment = (DescriptionViewFragment) getSupportFragmentManager().findFragmentByTag(DescriptionViewFragment.TAG);
        if(ValidationUtils.isNotEmpty(bucket.getDescription())){
            OptionDescription option = new OptionDescription(bucket.getDescription());
            if (descriptionViewFragment == null) {
                descriptionViewFragment = new DescriptionViewFragment(option);
                getSupportFragmentManager().beginTransaction().add(R.id.option_contents, descriptionViewFragment, DescriptionViewFragment.TAG).commit();
            } else {
                descriptionViewFragment.setContents(option);
            }
            mBtnBucketOptionNote.setVisibility(View.GONE);
        }else{
            if (descriptionViewFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(descriptionViewFragment).commit();
            }
            mBtnBucketOptionNote.setVisibility(View.VISIBLE);
        }

        RepeatViewFragment repeatFragment = (RepeatViewFragment) getSupportFragmentManager().findFragmentByTag(RepeatViewFragment.TAG);
        if (bucket.getRptType() != null && ValidationUtils.isValidRepeatCount(bucket.getRptCndt())) {
            OptionRepeat option = new OptionRepeat(RepeatType.fromCode(bucket.getRptType()), bucket.getRptCndt());
            if (repeatFragment == null) {
                repeatFragment = new RepeatViewFragment(option);
                getSupportFragmentManager().beginTransaction().add(R.id.option_contents, repeatFragment, RepeatViewFragment.TAG).commit();
            } else {
                repeatFragment.setContents(option);
            }
            mBtnBucketOptionRepeat.setVisibility(View.GONE);
        } else {
            if (repeatFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(repeatFragment).commit();
            }
            mBtnBucketOptionRepeat.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*ViewTreeObserver viewTreeObserver = mIvCardImage.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mIvCardImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }else{
                        mIvCardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    mIvCardImage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    bindData();

                }
            });
        }*/
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == mBucketInputDeadline){
            goOptionDday();
        }else if(view == mBtnBucketOptionNote){
            goOptionDescription();

        } else if(view == mBtnBucketOptionRepeat){
            goOptionRepeat();
        } else if(view == mBtnBucketOptionPublic){
            mBtnBucketOptionPublic.setSelected(!mBtnBucketOptionPublic.isSelected());
            bucket.setIsPrivate( mBtnBucketOptionPublic.isSelected() ? 1 : 0 );
        } else if(view == mBtnBucketOptionPic) {
            doTakePhotoAction();
        } else if( view == mBtnBucketOptionGallery ) {
            doTakeAlbumAction();
        } else if (view == mBtnBucketOptionDel){
            doDelte();
        }
    }

    private void doDelte() {

        AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
        alertConfirm.setTitle("삭제확인");
        alertConfirm.setMessage("버킷을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BucketDeleteTask bucketDeleteTask = new BucketDeleteTask();
                        bucketDeleteTask.execute(bucket);
                    }
                }
        ).setNegativeButton("아니오",
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

    public void goOptionRepeat() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionRepeat repeat = new OptionRepeat(RepeatType.fromCode(bucket.getRptType()), bucket.getRptCndt());
        intent.putExtra("option", repeat);
        startActivityForResult(intent, Code.ACT_ADD_BUCKET_OPTION_REPEAT);
    }

    public void goOptionDescription() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionDescription description = new OptionDescription(bucket.getDescription());
        intent.putExtra("option", description);
        startActivityForResult(intent, Code.ACT_ADD_BUCKET_OPTION_DESCRIPTION);
    }

    public void goOptionDday() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionDDay dDay = new OptionDDay(bucket.getRange(), bucket.getDeadline());
        intent.putExtra("option", dDay);
        startActivityForResult(intent, Code.ACT_ADD_BUCKET_OPTION_DDAY);
    }

    TextWatcher textWatcherInput = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().length() < 1){
                bucket.setTitle(null);
            }else {
                bucket.setTitle(s.toString());
            }
        }
    };

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
                startActivityForResult(intent, Code.ACT_ADD_BUCKET_TAKE_CAMERA);
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
        intent.putExtra("return-data", true);
        startActivityForResult(intent, Code.ACT_ADD_BUCKET_CROP_FROM_CAMERA);
    }

    public class BucketAddTask extends CustomAsyncTask<Bucket, Void, ResponseBodyWrapped<Bucket>>{

        @Override
        protected ResponseBodyWrapped<Bucket> doInBackground(Bucket... params) {
            BucketConnector bucketConnector = new BucketConnector();
            ResponseBodyWrapped<Bucket> responseBodyWrapped = new ResponseBodyWrapped<Bucket>();

            if(params != null && params.length > 0){
                Bucket param = params[0];
                if(param.getId() != null && param.getId() > 0) {
                    responseBodyWrapped = bucketConnector.updateBucketInfo(params[0]);
                }else{
                    responseBodyWrapped = bucketConnector.postBucketDefault(params[0]);
                }
            }

            return responseBodyWrapped;
        }

        @Override
        protected void onPostExecute(ResponseBodyWrapped<Bucket> bucketWrappedResponseBodyWrapped) {
            if(bucketWrappedResponseBodyWrapped.getData() != null){
                Bucket bucket = bucketWrappedResponseBodyWrapped.getData();
                if(bucket != null){
                    DataRepository.saveBucket(bucket);
                    handler.sendEmptyMessage(SEND_DATA_END);
                }
            }else {
                handler.sendEmptyMessage(SEND_DATA_ERROR);
            }
        }
    }

    public class BucketDeleteTask extends CustomAsyncTask<Bucket, Void, ResponseBodyWrapped<Bucket>>{

        @Override
        protected ResponseBodyWrapped<Bucket> doInBackground(Bucket... params) {
            handler.sendEmptyMessage(SEND_DATA_START);
            BucketConnector bucketConnector = new BucketConnector();
            ResponseBodyWrapped<Bucket> responseBodyWrapped = new ResponseBodyWrapped<Bucket>();

            if(params != null && params.length > 0){
                responseBodyWrapped = bucketConnector.deleteBucket(bucket);
            }

            return responseBodyWrapped;
        }

        @Override
        protected void onPostExecute(ResponseBodyWrapped<Bucket> bucketWrappedResponseBodyWrapped) {
            if(bucketWrappedResponseBodyWrapped.isSuccess()){
                DataRepository.deleteBucket(bucket);
                handler.sendEmptyMessage(SEND_DATA_DELETE);
            }else {
                handler.sendEmptyMessage(SEND_DATA_ERROR);
            }
        }
    }


    @Override
    public void onBackPressed() {
        confirm();
    }

    public void confirm(){
        Bucket compare = DataRepository.getBucket(bucket.getId());
        if(!compare.equals(bucket)) {
            AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
            alertConfirm.setTitle("내용 변경 확인");
            alertConfirm.setMessage("변경한 내용을 저장하시겠습니까?").setCancelable(false).setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(BucketAddActivity.this, "예", Toast.LENGTH_SHORT).show();
                        }
                    }
            ).setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            return;
                        }
                    }
            );
            AlertDialog alert = alertConfirm.create();
            alert.show();
        } else {
            finish();
        }
    }
}
