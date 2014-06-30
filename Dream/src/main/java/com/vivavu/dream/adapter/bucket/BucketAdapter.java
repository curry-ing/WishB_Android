package com.vivavu.dream.adapter.bucket;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketGroupViewActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.drawable.FanShapeDrawable;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.repository.connector.UserInfoConnector;
import com.vivavu.dream.util.ViewUnbindHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by masunghoon on 4/20/14.
 */
public class BucketAdapter extends PagerAdapter implements View.OnClickListener{

    protected int mSdkVersion = Build.VERSION.SDK_INT;
    private Context context;
    private Fragment fragment;
    private LayoutInflater mInflater;
    private List<BucketGroup> bucketGroupList;
    protected List<Bitmap> mainImages;


    private String title;

    private int deviceDensityDpi;
    private String mFromMonth, mToMonth;

    static public final int PROGRESS_BAR_BASELINE = 270;
    public static final String TAG = "BucketAdapter";
    public static final int TEXT_ID = 0;


    public BucketAdapter(Fragment fragment, List<BucketGroup> bucketGroupList) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketGroupList = new ArrayList<BucketGroup>(bucketGroupList);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        deviceDensityDpi = displayMetrics.densityDpi;

    }

//    public BucketAdapter(Fragment fragment) {
//        this.context = fragment.getActivity();
//        this.fragment = fragment;
//        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.bucketGroupList = new ArrayList<BucketGroup>();
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.main_contents, container, false);
        ButterknifeViewHolder holder = new ButterknifeViewHolder(viewGroup);
        viewGroup.setTag(holder);


        try {
            init(holder, bucketGroupList.get(position), position);
        } catch (IOException e) {
            e.printStackTrace();
        }
        container.addView(viewGroup);

        return viewGroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewUnbindHelper.unbindReferences((View) object);
        container.removeView((View)object);
    }

    @Override
    public int getCount(){
        return bucketGroupList.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return (view == object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void init(final ButterknifeViewHolder holder, BucketGroup bucketGroup, final int pos) throws IOException {
        this.mainImages = new ArrayList<Bitmap>();
        String mUserBirthday = DreamApp.getInstance().getUser().getBirthday();

        /* SET MAIN TITLE */
        holder.mBtnDecade.setTypeface(BaseActionBarActivity.getNanumBarunGothicBoldFont());
        holder.mBtnDecade.setTextSize(22);
        holder.mBtnDecade.setTextColor(Color.WHITE);
        holder.mBtnDecade.getPaint().setAntiAlias(true);

        switch (pos){
            case 0:
                title = DreamApp.getInstance().getUser().getTitle_life();
                break;
            case 1:
                title = DreamApp.getInstance().getUser().getTitle_10();
                break;
            case 2:
                title = DreamApp.getInstance().getUser().getTitle_20();
                break;
            case 3:
                title = DreamApp.getInstance().getUser().getTitle_30();
                break;
            case 4:
                title = DreamApp.getInstance().getUser().getTitle_40();
                break;
            case 5:
                title = DreamApp.getInstance().getUser().getTitle_50();
                break;
            case 6:
                title = DreamApp.getInstance().getUser().getTitle_60();
                break;
        }
        if (title != null) {
            holder.mBtnDecade.setText(title+" ");
        } else {
            holder.mBtnDecade.setText(bucketGroup.getRangeText().equals("60대") ? context.getString(R.string.after_sixties) : bucketGroup.getRangeText()+" ");
        }

        holder.mBtnDecade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                createExampleDialog(holder, pos);
            }
        });


        /* SET MAIN IMAGES */
        int cnt = bucketGroup.getCount();
        if (cnt > 0) {
            for (int i=0; i<cnt; i++){
                if(bucketGroup.getBukets().get(i).getCvrImgUrl() != null) {
                    mainImages.add(ImageLoader.getInstance().loadImageSync(bucketGroup.getBukets().get(i).getCvrImgUrl(), new ImageSize(540,540)));
                }
            }
        }
//        if (cnt > 4) {
//            cnt = 4;
//        }
        if(mainImages.size() == 0){
            switch(pos){
                case 0:
                    mainImages.add(BitmapFactory.decodeResource(DreamApp.getInstance().getResources(), R.drawable.main_default_00));
                    break;
                case 1:
                    mainImages.add(BitmapFactory.decodeResource(DreamApp.getInstance().getResources(), R.drawable.main_default_01));
                    break;
                case 2:
                    mainImages.add(BitmapFactory.decodeResource(DreamApp.getInstance().getResources(), R.drawable.main_default_02));
                    break;
                case 3:
                    mainImages.add(BitmapFactory.decodeResource(DreamApp.getInstance().getResources(), R.drawable.main_default_03));
                    break;
                case 4:
                    mainImages.add(BitmapFactory.decodeResource(DreamApp.getInstance().getResources(), R.drawable.main_default_04));
                    break;
                case 5:
                    mainImages.add(BitmapFactory.decodeResource(DreamApp.getInstance().getResources(), R.drawable.main_default_05));
                    break;
                case 6:
                    mainImages.add(BitmapFactory.decodeResource(DreamApp.getInstance().getResources(), R.drawable.main_default_06));
                    break;
            }
        } else {
            Collections.shuffle(mainImages);
        }
        int imgCnt = mainImages.size() > 4 ? 4 : mainImages.size();
        for (int j=0; j < imgCnt; j++){
            switch (j){
                case 0:
                    holder.mMainImage1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mMainImage1.setBackgroundDrawable(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    } else {
                        holder.mMainImage1.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    }
                    break;
                case 1:
                    holder.mMainImage2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mMainImage2.setBackgroundDrawable(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    } else {
                        holder.mMainImage2.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    }
                    break;
                case 2:
                    holder.mMainImage3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mMainImage3.setBackgroundDrawable(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    } else {
                        holder.mMainImage3.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    }
                    break;
                case 3:
                    holder.mMainImage4.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
                        holder.mMainImage4.setBackgroundDrawable(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    } else {
                        holder.mMainImage4.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    }
                    break;
//                case 4:
//                    holder.mMainImage5.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                    holder.mMainImage5.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, mainImages.size(), deviceDensityDpi, pos));
//                    break;
//                case 5:
//                    holder.mMainImage6.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                    holder.mMainImage6.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, mainImages.size(), deviceDensityDpi, pos));
//                    break;
//                case 6:
//                    holder.mMainImage7.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                    holder.mMainImage7.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, mainImages.size(), deviceDensityDpi, pos));
//                    break;
//                case 7:
//                    holder.mMainImage8.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                    holder.mMainImage8.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, mainImages.size(), deviceDensityDpi, pos));
//                    break;
            }
        }
        mainImages.clear();


        /* SET PROGRESS BAR */
        if (mUserBirthday != null && !mUserBirthday.isEmpty()) {
            String s = mUserBirthday.substring(4, 6);
            if (s.equals("01")) {
                mFromMonth = context.getString(R.string.jan);
                mToMonth = context.getString(R.string.dec);
            } else if (s.equals("02")) {
                mFromMonth = context.getString(R.string.feb);
                mToMonth = context.getString(R.string.jan);
            } else if (s.equals("03")) {
                mFromMonth = context.getString(R.string.mar);
                mToMonth = context.getString(R.string.feb);
            } else if (s.equals("04")) {
                mFromMonth = context.getString(R.string.apr);
                mToMonth = context.getString(R.string.mar);
            } else if (s.equals("05")) {
                mFromMonth = context.getString(R.string.may);
                mToMonth = context.getString(R.string.apr);
            } else if (s.equals("06")) {
                mFromMonth = context.getString(R.string.jun);
                mToMonth = context.getString(R.string.may);
            } else if (s.equals("07")) {
                mFromMonth = context.getString(R.string.jul);
                mToMonth = context.getString(R.string.jun);
            } else if (s.equals("08")) {
                mFromMonth = context.getString(R.string.aug);
                mToMonth = context.getString(R.string.jul);
            } else if (s.equals("09")) {
                mFromMonth = context.getString(R.string.sep);
                mToMonth = context.getString(R.string.aug);
            } else if (s.equals("10")) {
                mFromMonth = context.getString(R.string.oct);
                mToMonth = context.getString(R.string.sep);
            } else if (s.equals("11")) {
                mFromMonth = context.getString(R.string.nov);
                mToMonth = context.getString(R.string.oct);
            } else if (s.equals("12")) {
                mFromMonth = context.getString(R.string.dec);
                mToMonth = context.getString(R.string.nov);
            }
            if (pos > 0) {
                holder.mMainProgress.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                Calendar cal = java.util.Calendar.getInstance();
                int startY = Integer.parseInt(mUserBirthday.substring(0, 4)) + (pos * 10) - 1;
                int endY = Integer.parseInt(mUserBirthday.substring(0, 4)) + ((pos + 1) * 10 - 1) - 1;
                int thisY = cal.get(Calendar.YEAR);

                holder.mPeriod.setTypeface(BaseActionBarActivity.getDenseRegularFont(), Typeface.BOLD);
                holder.mPeriod.setTextSize(25);
                if (pos == 6) {
                    holder.mPeriod.setText(mFromMonth + String.valueOf(startY).charAt(0) + " "
                            + String.valueOf(startY).charAt(1) + " "
                            + String.valueOf(startY).charAt(2) + " "
                            + String.valueOf(startY).charAt(3) + "  ~");
                } else {
                    holder.mPeriod.setText(mFromMonth + String.valueOf(startY).charAt(0) + " "
                            + String.valueOf(startY).charAt(1) + " "
                            + String.valueOf(startY).charAt(2) + " "
                            + String.valueOf(startY).charAt(3) + "  -  " + mToMonth
                            + String.valueOf(endY).charAt(0) + " "
                            + String.valueOf(endY).charAt(1) + " "
                            + String.valueOf(endY).charAt(2) + " "
                            + String.valueOf(endY).charAt(3));
                }


                if (thisY > endY) {
                    holder.mMainProgress.setImageDrawable(new FanShapeDrawable(null, 360, PROGRESS_BAR_BASELINE, deviceDensityDpi, pos));
                } else if (thisY < startY) {
                    holder.mMainProgress.setImageDrawable(new FanShapeDrawable(null, 0, PROGRESS_BAR_BASELINE, deviceDensityDpi, pos));
                } else {
                    int marking = (((DreamApp.getInstance().getUser().getUserAge() % 10) * 365) + cal.get(Calendar.DAY_OF_YEAR)) / 10;
                    holder.mMainProgress.setImageDrawable(new FanShapeDrawable(null, marking, PROGRESS_BAR_BASELINE, deviceDensityDpi, pos));
                }
            } else {
                holder.mMainProgress.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                int marking = (int) ((float) DreamApp.getInstance().getUser().getUserAge() / 100 * 360);

                holder.mPeriod.setTypeface(BaseActionBarActivity.getDenseRegularFont(), Typeface.BOLD);
                holder.mPeriod.setTextSize(25);
                holder.mMainProgress.setImageDrawable(new FanShapeDrawable(null, marking, PROGRESS_BAR_BASELINE, deviceDensityDpi, pos));
                holder.mPeriod.setText(mFromMonth + mUserBirthday.substring(0, 1) + " "
                        + mUserBirthday.substring(1, 2) + " "
                        + mUserBirthday.substring(2, 3) + " "
                        + mUserBirthday.substring(3, 4) + "  ~");
            }
        } else {
            holder.mDecadeBorder.setVisibility(View.GONE);
            holder.mPeriod.setVisibility(View.GONE);
            holder.mMainProgress.setImageDrawable(new FanShapeDrawable(null, 0, PROGRESS_BAR_BASELINE, deviceDensityDpi, 0));
        }

        /* SET ON CLICK LISTENER */
        holder.mMainImage8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent;
                intent = new Intent();
                intent.putExtra(BucketGroupViewActivity.EXTRA_KEY_GROUP_RANGE, getBucketGroupList().get(pos).getRange());
                //intent.setClass(context, CircleBucketListActivity.class);
                intent.setClass(context, BucketGroupViewActivity.class);
                fragment.startActivityForResult(intent, Code.ACT_VIEW_BUCKET_GROUP);
            }

        });
    }

    public List<BucketGroup> getBucketGroupList() {
        return bucketGroupList;
    }

    public void setBucketGroupList(List<BucketGroup> bucketGroupList) {
        this.bucketGroupList = bucketGroupList;
    }

    @Override
    public void onClick(View v){

    }

    private void createExampleDialog(final ButterknifeViewHolder holder, final int pos){
        final InputMethodManager imm = (InputMethodManager) DreamApp.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (pos != 0) {
            builder.setTitle(String.format(context.getString(R.string.mainview_decade_set_title), pos*10));
            builder.setMessage(context.getString(R.string.mainview_decade_set_message));
        } else {
            builder.setTitle(context.getString(R.string.mainview_life_set_title));
            builder.setMessage(context.getString(R.string.mainview_life_set_message));
        }

        final EditText input = new EditText(context);
        input.setId(TEXT_ID);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
        input.setSingleLine();
        builder.setView(input);
        input.setText(holder.mBtnDecade.getText());
        input.selectAll();

        builder.setPositiveButton(context.getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String titleValue = input.getText().toString();
                holder.mBtnDecade.setText(titleValue);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);  //Hide soft keyboard
                saveUser(titleValue, pos);
            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);   //Hide soft keyboard
            }
        });

        AlertDialog ad = builder.create();
        ad.show();

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void saveUser(String title, int pos){
        UserInfoModify userInfoModify = new UserInfoModify();
        User user = new User();
        switch(pos){
            case 0:
                user.setTitle_life(title);
                break;
            case 1:
                user.setTitle_10(title);
                break;
            case 2:
                user.setTitle_20(title);
                break;
            case 3:
                user.setTitle_30(title);
                break;
            case 4:
                user.setTitle_40(title);
                break;
            case 5:
                user.setTitle_50(title);
                break;
            case 6:
                user.setTitle_60(title);
                break;
        }
        userInfoModify.execute(user);
    }

    public class UserInfoModify extends AsyncTask<User, Void, ResponseBodyWrapped<User>> {
        @Override
        protected ResponseBodyWrapped<User> doInBackground(User... params){
            UserInfoConnector userInfoConnector = new UserInfoConnector();
            ResponseBodyWrapped<User> responseBodyWrapped = new ResponseBodyWrapped<User>();

            if(params != null && params.length > 0){
                responseBodyWrapped = userInfoConnector.put(params[0]);
            }

            return responseBodyWrapped;
        }

        @Override
        protected void onPostExecute(ResponseBodyWrapped<User> userResponseBodyWrapped) {
            if(userResponseBodyWrapped.isSuccess()){
                DreamApp.getInstance().setUser(userResponseBodyWrapped.getData());
            } else {
                Toast.makeText(BucketAdapter.this.context, R.string.modification_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ButterknifeViewHolder {
        @InjectView(R.id.btn_decade)
        Button mBtnDecade;
        @InjectView(R.id.decadeBorder)
        ImageView mDecadeBorder;
        @InjectView(R.id.main_image1)
        ImageView mMainImage1;
        @InjectView(R.id.main_image2)
        ImageView mMainImage2;
        @InjectView(R.id.main_image3)
        ImageView mMainImage3;
        @InjectView(R.id.main_image4)
        ImageView mMainImage4;
//        @InjectView(R.id.main_image5)
//        ImageView mMainImage5;
//        @InjectView(R.id.main_image6)
//        ImageView mMainImage6;
//        @InjectView(R.id.main_image7)
//        ImageView mMainImage7;
        @InjectView(R.id.main_image8)
        ImageView mMainImage8;
        @InjectView(R.id.progress_bar)
        ImageView mMainProgress;
        @InjectView(R.id.period)
        TextView mPeriod;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
