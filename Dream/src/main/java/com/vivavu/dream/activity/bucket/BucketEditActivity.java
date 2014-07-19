package com.vivavu.dream.activity.bucket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.enums.FacebookShareType;
import com.vivavu.dream.common.enums.RepeatType;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.common.enums.Scope;
import com.vivavu.dream.fragment.bucket.option.description.DescriptionViewFragment;
import com.vivavu.dream.fragment.bucket.option.repeat.RepeatViewFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.option.OptionDDay;
import com.vivavu.dream.model.bucket.option.OptionDescription;
import com.vivavu.dream.model.bucket.option.OptionRepeat;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.task.CustomAsyncTask;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.util.FileUtils;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.util.ValidationUtils;
import com.vivavu.dream.view.ShadowImageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 13.
 */
public class BucketEditActivity extends BaseActionBarActivity {

    private static final String TAG = BucketEditActivity.class.getSimpleName();

    public enum RequestCode {
        ACT_ADD_BUCKET_TAKE_CAMERA
        , ACT_ADD_BUCKET_TAKE_GALLERY
        , ACT_ADD_BUCKET_CROP_FROM_CAMERA
        , ACT_ADD_BUCKET_OPTION_DDAY
        , ACT_ADD_BUCKET_OPTION_DESCRIPTION
        , ACT_ADD_BUCKET_OPTION_REPEAT
    }

    private static final int SEND_DATA_START = 0;
    private static final int SEND_DATA_END = 1;
    private static final int SEND_DATA_ERROR = 2;
    private static final int SEND_DATA_DELETE = 3;
    private static final int SEND_DATA_DELETE_ERROR = 4;
    public static final String RESULT_EXTRA_BUCKET = "bucket";
    public static final String RESULT_EXTRA_BUCKET_ID = "bucketId";
    public static final String RESULT_EXTRA_BUCKET_RANGE = "bucketRange";

    @InjectView(R.id.bucket_img)
    ShadowImageView mBucketImg;
    @InjectView(R.id.bucket_input_title)
    EditText mBucketInputTitle;
    @InjectView(R.id.bucket_input_deadline)
    TextView mBucketInputDeadline;
    @InjectView(R.id.btn_bucket_option_note)
    Button mBtnBucketOptionNote;
    @InjectView(R.id.btn_bucket_option_repeat)
    Button mBtnBucketOptionRepeat;
    @InjectView(R.id.btn_bucket_option_public)
    Button mBtnBucketOptionPublic;
    @InjectView(R.id.btn_bucket_option_del)
    Button mBtnBucketOptionDel;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.menu_save)
    Button mMenuSave;
    @InjectView(R.id.btn_bucket_option_facebook)
    Button mBtnBucketOptionFacebook;

    private LayoutInflater layoutInflater;
    private Bucket bucket = null;
    private boolean modFlag = false;
    protected Uri mImageCaptureUri;
    private String modString;

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
                    Bucket bucket = (Bucket) msg.obj;
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_EXTRA_BUCKET_ID, (Integer) bucket.getId());
                    intent.putExtra(RESULT_EXTRA_BUCKET, bucket);
                    intent.putExtra(RESULT_EXTRA_BUCKET_RANGE, bucket.getRange() == null ? 0 : Integer.valueOf(bucket.getRange()));
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case SEND_DATA_ERROR:
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(BucketEditActivity.this, getString(R.string.txt_bucket_edit_fail), Toast.LENGTH_LONG).show();
                    break;
                case SEND_DATA_DELETE:
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    setResult(RESULT_USER_DATA_DELETED);
                    finish();
                    break;
                case SEND_DATA_DELETE_ERROR:
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(BucketEditActivity.this, getString(R.string.txt_bucket_edit_delete_fail), Toast.LENGTH_LONG).show();
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
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.actionbar_bucket_edit);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.in_progress));

        Intent data = getIntent();

        int bucketId = data.getIntExtra(RESULT_EXTRA_BUCKET_ID, -1);
        int range = data.getIntExtra(MainActivity.EXTRA_BUCKET_DEFAULT_RANGE, -1);
        bucket = DataRepository.getBucket(bucketId);

        ButterKnife.inject(this);

        mBucketInputTitle.setTypeface(getNanumBarunGothicFont());
        mBucketInputTitle.setTextColor(Color.WHITE);
        //mBucketInputTitle.setTextSize(20);
        mBucketInputDeadline.setTypeface(getDenseRegularFont());
        mBucketInputTitle.setTextColor(Color.WHITE);
        //mBucketInputDeadline.setTextSize(28);

        if(range > -1 && DreamApp.getInstance().getUser().getBirthday() != null){

            Date birthday = DateUtils.getDateFromString(DreamApp.getInstance().getUser().getBirthday(), "yyyyMMdd", new Date());
            Date temp = DateUtils.getLastDayOfPeriod(birthday, range);
            mBucketInputDeadline.setText(DateUtils.getDateString(temp, "yyyy.MM.dd"));
            bucket.setDeadline(temp);
            bucket.setRange(String.valueOf(range));
        }
        addEventListener();
        checkRequireElement();
        bindData();
        modFlag = false;

    }

    public void saveBucket() {
        if (bucket == null || bucket.getTitle() == null || bucket.getTitle().trim().length() <= 0) {
            Toast.makeText(this, getString(R.string.txt_bucket_edit_need_required_fields), Toast.LENGTH_SHORT).show();
        }else{
            Tracker tracker = DreamApp.getInstance().getTracker();
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_save));
            tracker.send(eventBuilder.build());

            handler.sendEmptyMessage(SEND_DATA_START);
            BucketAddTask bucketAddTask = new BucketAddTask();
            bucketAddTask.execute(bucket);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RequestCode requestCodeEnum = RequestCode.values()[requestCode];
        switch (requestCodeEnum){
            case ACT_ADD_BUCKET_OPTION_DDAY:
                if(resultCode == RESULT_OK){
                    modFlag = true;
                    OptionDDay dDay = (OptionDDay) data.getSerializableExtra("option.result");
                    updateUiData(dDay);
                }
                break;
            case ACT_ADD_BUCKET_OPTION_DESCRIPTION:
                if(resultCode == RESULT_OK){
                    modFlag = true;
                    OptionDescription description = (OptionDescription) data.getSerializableExtra("option.result");
                    updateUiData(description);
                }
                break;
            case ACT_ADD_BUCKET_OPTION_REPEAT:
                if(resultCode == RESULT_OK){
                    modFlag = true;
                    OptionRepeat repeat = (OptionRepeat) data.getSerializableExtra("option.result");
                    updateUiData(repeat);
                }
                break;
            case ACT_ADD_BUCKET_TAKE_CAMERA:
                if(resultCode == RESULT_OK){
                    Tracker tracker = DreamApp.getInstance().getTracker();
                    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_image_camera));
                    tracker.send(eventBuilder.build());
                    modFlag = true;
                    doCropPhoto();
                }
                break;
            case ACT_ADD_BUCKET_TAKE_GALLERY:
                if(resultCode == RESULT_OK){
                    modFlag = true;
                    Tracker tracker = DreamApp.getInstance().getTracker();
                    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_image_gallery));
                    tracker.send(eventBuilder.build());
                    if(data != null ) {
                        mImageCaptureUri = data.getData();
                        doCropPhoto();
                    }
                }
                break;
            case ACT_ADD_BUCKET_CROP_FROM_CAMERA:
                if(data != null && data.getExtras() != null && data.getExtras().getParcelable("data") != null) {
                    Bitmap photo = data.getExtras().getParcelable("data");
                    mBucketImg.setImageBitmap(photo);
                } else if(data != null && data.getExtras() != null && data.getExtras().getParcelable("output") != null){
                    Uri cropFileUri = data.getExtras().getParcelable("output");
                    File f = null;
                    if("file".equals(cropFileUri.getScheme() )){
                        f = new File(cropFileUri.getPath());
                    } else if("content".equals(cropFileUri.getScheme())){
                        String path = AndroidUtils.convertContentsToFileSchema(DreamApp.getInstance(), cropFileUri.toString());
                        f = new File(path);
                    }

                    if(f!= null && f.exists() && f.isFile()){
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisc(true)
                                .considerExifParams(true)
                                .showImageForEmptyUri(R.drawable.ic_camera_big)
                                .showImageOnFail(R.drawable.ic_picture_big)
                                .build();
                        ImageLoader.getInstance().displayImage(cropFileUri.toString(), mBucketImg, options, new SimpleImageLoadingListener(){
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                mBucketImg.setExpand(false);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                mBucketImg.setExpand(loadedImage != null);
                            }
                        });
                        bucket.setFile(f);
                    }

                } else if(data != null && data.getDataString() != null){
                    String path = AndroidUtils.convertContentsToFileSchema(DreamApp.getInstance(), data.getDataString());
                    File f = new File(path);
                    if(f.exists() && f.isFile()){
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisc(true)
                                .considerExifParams(true)
                                .showImageForEmptyUri(R.drawable.ic_camera_big)
                                .showImageOnFail(R.drawable.ic_picture_big)
                                .build();
                        ImageLoader.getInstance().displayImage(data.getDataString(), mBucketImg, options, new SimpleImageLoadingListener(){
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                mBucketImg.setExpand(false);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                mBucketImg.setExpand(loadedImage != null);
                            }
                        });
                        bucket.setFile(f);
                    }
                }
                break;
        }
    }

    private void updateUiData(OptionDDay dday) {
        bucket.setTitle(mBucketInputTitle.getText().toString());
        bucket.setScope(Scope.DECADE.getValue());
        Date birthday = DateUtils.getDateFromString(DreamApp.getInstance().getUser().getBirthday(), "yyyyMMdd", new Date());
        Calendar instance = Calendar.getInstance();
        instance.setTime(birthday);
        int year = instance.get(Calendar.YEAR);
        instance.setTime(dday.getDeadline());
        int deadlinYear = instance.get(Calendar.YEAR);
        int diff = (deadlinYear - year + 1 )/10;
        diff *= 10;
        if(diff > 0){
            bucket.setRange(String.valueOf(diff) );
        } else {
            bucket.setRange(dday.getRange());
        }
        bucket.setDeadline(dday.getDeadline());
        Tracker tracker = DreamApp.getInstance().getTracker();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_edit_bucket_deadline));
        tracker.send(eventBuilder.build());
        bindData();
    }

    private void updateUiData(OptionRepeat repeat) {
        bucket.setRptType(repeat.getRepeatType().getCode());
        bucket.setRptCndt(repeat.getOptionStat());
        Tracker tracker = DreamApp.getInstance().getTracker();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_edit_bucket_repeat));
        if(repeat.getRepeatType() == RepeatType.WKRP){
            eventBuilder.setValue(RepeatType.WKRP.ordinal());
        } else if(repeat.getRepeatType() == RepeatType.WEEK){
            eventBuilder.setValue(RepeatType.WEEK.ordinal());
        } else if(repeat.getRepeatType() == RepeatType.MNTH){
            eventBuilder.setValue(RepeatType.MNTH.ordinal());
        }
        tracker.send(eventBuilder.build());
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
	    mBtnBucketOptionFacebook.setOnClickListener(this);

        mBtnBucketOptionDel.setOnClickListener(this);
        mBucketImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String items[] = null;
	            if(bucket.getCvrImgUrl() != null || bucket.getFile() != null) {
		            items = DreamApp.getInstance().getResources().getStringArray(R.array.array_image_attach);
	            } else {
		            items = DreamApp.getInstance().getResources().getStringArray(R.array.array_attach_image_only);
	            }
                AlertDialog.Builder ab = new AlertDialog.Builder(BucketEditActivity.this);
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
                                modFlag = true;
                                FileUtils.deleteFile(bucket.getFile());
                                Tracker tracker = DreamApp.getInstance().getTracker();
                                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_image_delete));
                                tracker.send(eventBuilder.build());
                                bucket.setFile(null);
                                bucket.setCvrImgId(null);
                                bucket.setCvrImgUrl(null);
                                dialog.dismiss();
                                bindData();
                                break;
                            default:
                                break;
                        }
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

        mMenuSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBucket();
            }
        });

        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
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
                        }else {
                            view.setVisibility(View.GONE);
                        }
                    }
                });

            }
        }
        checkRequireElement();
    }

    private void bindData() {
        mBucketInputTitle.setText(bucket.getTitle());

        if(bucket.getFile() == null){
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .considerExifParams(true)
                    .showImageForEmptyUri(R.drawable.ic_camera_big)
                    .showImageOnFail(R.drawable.ic_picture_big)
                    .build();
            ImageLoader.getInstance().displayImage(bucket.getCvrImgUrl(), mBucketImg, options, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mBucketImg.setExpand(false);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mBucketImg.setExpand(loadedImage != null);
                }

            });
        }

        if (bucket.getDeadline() != null) {
            mBucketInputDeadline.setText( DateUtils.getDateString(bucket.getDeadline(), "yyyy. MM. dd"));
        } else {
            mBucketInputDeadline.setText(getString(R.string.txt_default_in_my_life_title));
        }

        checkRequireElement();//

        // 눌린 상태가 비공개
        mBtnBucketOptionPublic.setSelected( bucket.getIsPrivate() == null || bucket.getIsPrivate() == 1 );
		mBtnBucketOptionFacebook.setSelected(bucket.getFbFeedId() != null || FacebookShareType.SHARE.getCode().equalsIgnoreCase(bucket.getFbShare()));
        DescriptionViewFragment descriptionViewFragment = (DescriptionViewFragment) getSupportFragmentManager().findFragmentByTag(DescriptionViewFragment.TAG);
        if(ValidationUtils.isNotEmpty(bucket.getDescription())){
            OptionDescription option = new OptionDescription(bucket.getDescription());
            if (descriptionViewFragment == null) {
                descriptionViewFragment = new DescriptionViewFragment();
                descriptionViewFragment.setContents(option);
                getSupportFragmentManager().beginTransaction().add(R.id.option_contents_note, descriptionViewFragment, DescriptionViewFragment.TAG).commit();
            } else {
                descriptionViewFragment.setContents(option);
                descriptionViewFragment.update();
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
                repeatFragment = new RepeatViewFragment();
                repeatFragment.setContents(option);
                getSupportFragmentManager().beginTransaction().add(R.id.option_contents_repeat, repeatFragment, RepeatViewFragment.TAG).commit();
            } else {
                repeatFragment.setContents(option);
                repeatFragment.update();
            }
            mBtnBucketOptionRepeat.setVisibility(View.GONE);
        } else {
            if (repeatFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(repeatFragment).commit();
            }
            mBtnBucketOptionRepeat.setVisibility(View.VISIBLE);
        }
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
            Tracker tracker = DreamApp.getInstance().getTracker();
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_edit_bucket_public));
            tracker.send(eventBuilder.build());
            modFlag = true;
            mBtnBucketOptionPublic.setSelected(!mBtnBucketOptionPublic.isSelected());
            bucket.setIsPrivate( mBtnBucketOptionPublic.isSelected() ? 1 : 0 );
        } else if (view == mBtnBucketOptionDel){
            doDelte();
        } else if(view == mBtnBucketOptionFacebook){
	        boolean flag = !mBtnBucketOptionFacebook.isSelected();
	        mBtnBucketOptionFacebook.setSelected(flag);

	        Tracker tracker = DreamApp.getInstance().getTracker();
	        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_share_facebook));
	        eventBuilder.setValue(flag ? 1 : 0);
	        tracker.send(eventBuilder.build());

	        bucket.setFbShare( flag ? FacebookShareType.SHARE.getCode() : FacebookShareType.NONE.getCode());
        }
    }

    private void doDelte() {
        AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
        alertConfirm.setMessage(getString(R.string.txt_bucket_edit_confirm_delete_body)).setCancelable(false).setPositiveButton(getString(R.string.confirm_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bucket != null && bucket.getId() != null && bucket.getId() > 0) {
                            Tracker tracker = DreamApp.getInstance().getTracker();
                            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_edit_bucket_delete));
                            tracker.send(eventBuilder.build());
                            BucketDeleteTask bucketDeleteTask = new BucketDeleteTask();
                            bucketDeleteTask.execute(bucket);
                        } else {
                            finish();
                        }
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

    public void goOptionRepeat() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionRepeat repeat = new OptionRepeat(RepeatType.fromCode(bucket.getRptType()), bucket.getRptCndt());
        intent.putExtra("option", repeat);
        startActivityForResult(intent, RequestCode.ACT_ADD_BUCKET_OPTION_REPEAT.ordinal());
    }

    public void goOptionDescription() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionDescription description = new OptionDescription(bucket.getDescription());
        intent.putExtra("option", description);
        startActivityForResult(intent, RequestCode.ACT_ADD_BUCKET_OPTION_DESCRIPTION.ordinal());
    }

    public void goOptionDday() {
        Intent intent = new Intent();
        intent.setClass(this, BucketOptionActivity.class);
        OptionDDay dDay = new OptionDDay(bucket.getRange(), bucket.getDeadline());
        intent.putExtra("option", dDay);
        startActivityForResult(intent, RequestCode.ACT_ADD_BUCKET_OPTION_DDAY.ordinal());
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
            if(!s.toString().equals(bucket.getTitle())){
                modFlag = true;
            }
            if(s.toString().length() < 1){
                bucket.setTitle(null);
            }else {
                bucket.setTitle(s.toString());
            }
            checkRequireElement();
        }
    };
    private void checkRequireElement(){
        if (bucket == null || bucket.getTitle() == null || bucket.getTitle().trim().length() <= 0) {
            mMenuSave.setVisibility(View.INVISIBLE);
        }else{
            mMenuSave.setVisibility(View.VISIBLE);
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
                startActivityForResult(intent, RequestCode.ACT_ADD_BUCKET_TAKE_CAMERA.ordinal());
            }
        }else{
            Toast.makeText(this, getString(R.string.txt_camera_not_exc), Toast.LENGTH_LONG).show();
        }
    }

    private void doTakeAlbumAction(){
        Intent intent = new Intent( Intent.ACTION_PICK ) ;
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE) ;
        startActivityForResult( intent, RequestCode.ACT_ADD_BUCKET_TAKE_GALLERY.ordinal() ) ;
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

        startActivityForResult(intent, RequestCode.ACT_ADD_BUCKET_CROP_FROM_CAMERA.ordinal());
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
            if(bucketWrappedResponseBodyWrapped.getData() != null && bucketWrappedResponseBodyWrapped.isSuccess()){
                Bucket bucket = bucketWrappedResponseBodyWrapped.getData();
                if(bucket != null){
                    DataRepository.saveBucket(bucket);
                    // 파일 전송 후 해제 시킴
                    FileUtils.deleteFile(BucketEditActivity.this.bucket.getFile());
                    BucketEditActivity.this.bucket.setFile(null);
                    Message message = handler.obtainMessage(SEND_DATA_END, bucket);
                    handler.sendMessage(message);
                }
            }else if(bucketWrappedResponseBodyWrapped.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
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
            }else if(bucketWrappedResponseBodyWrapped.getResponseStatus() == ResponseStatus.TIMEOUT) {
	            defaultHandler.sendEmptyMessage(SERVER_TIMEOUT);
            }else {
                handler.sendEmptyMessage(SEND_DATA_DELETE_ERROR);
            }
        }
    }


    @Override
    public void onBackPressed() {
        confirm();
    }

    public void confirm(){
        if(modFlag) {
            AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
            alertConfirm.setMessage(getString(R.string.txt_bucket_edit_confirm_edit_body)).setCancelable(false).setPositiveButton(getString(R.string.confirm_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Tracker tracker = DreamApp.getInstance().getTracker();
                            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_bucket_edit_activity)).setAction(getString(R.string.ga_event_action_cancel));
                            tracker.send(eventBuilder.build());
                            finish();
                            return;
                        }
                    }
            ).setNegativeButton(getString(R.string.confirm_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //saveBucket();
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
    protected void onResume() {
        super.onResume();
        mBucketInputTitle.clearFocus();
    }

    @Override
    public void finish() {
        FileUtils.deleteFile(bucket.getFile());
        bucket.setFile(null);
        super.finish();
    }
}
