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
import java.text.SimpleDateFormat;
import java.util.*;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by masunghoon on 4/20/14.
 */
public class BucketAdapter2 extends PagerAdapter implements View.OnClickListener{

    private Context context;
    private Fragment fragment;
    private LayoutInflater mInflater;
    private List<BucketGroup> bucketGroupList;
    private List<Bitmap> mainImages;

    private String title;

    private int deviceDensityDpi;
    private String mUserBirthday;
    private String mFromMonth, mToMonth;

    static public final int PROGRESS_BAR_BASELINE = 270;
    public static final String TAG = "DialogActivity";
    public static final int DLG_EXAMPLE1 = 0;
    public static final int TEXT_ID = 0;


    public BucketAdapter2 (Fragment fragment, List<BucketGroup> bucketGroupList) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketGroupList = new ArrayList<BucketGroup>(bucketGroupList);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        deviceDensityDpi = displayMetrics.densityDpi;

    }

    public BucketAdapter2 (Fragment fragment) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketGroupList = new ArrayList<BucketGroup>();
    }

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void init(final ButterknifeViewHolder holder, BucketGroup bucketGroup, final int pos) throws IOException {
        this.mainImages = new ArrayList<Bitmap>();
        this.mUserBirthday = DreamApp.getInstance().getUser().getBirthday();

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
            String imsi = bucketGroup.getRangeText();
            holder.mBtnDecade.setText(bucketGroup.getRangeText().equals("60대") ? "60대 이후" : bucketGroup.getRangeText()+" ");
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
                    holder.mMainImage1.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    break;
                case 1:
                    holder.mMainImage2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    holder.mMainImage2.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    break;
                case 2:
                    holder.mMainImage3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    holder.mMainImage3.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
                    break;
                case 3:
                    holder.mMainImage4.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    holder.mMainImage4.setBackground(new FanShapeDrawable(mainImages.get(j),j+1, imgCnt, deviceDensityDpi, pos));
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
//            holder.mProgressOverlay.setImageDrawable(new MyDrawable());
        }
        mainImages.clear();


        /* SET PROGRESS BAR */
        if (mUserBirthday != null && !mUserBirthday.isEmpty()) {
            String s = mUserBirthday.substring(4, 6);
            if (s.equals("01")) {
                mFromMonth = "J A N  ";
                mToMonth = "D E C  ";
            } else if (s.equals("02")) {
                mFromMonth = "F E B  ";
                mToMonth = "J A N  ";
            } else if (s.equals("03")) {
                mFromMonth = "M A R  ";
                mToMonth = "F E B  ";
            } else if (s.equals("04")) {
                mFromMonth = "A P R  ";
                mToMonth = "M A R  ";
            } else if (s.equals("05")) {
                mFromMonth = "M A Y  ";
                mToMonth = "A P R  ";
            } else if (s.equals("06")) {
                mFromMonth = "J U N  ";
                mToMonth = "M A Y  ";
            } else if (s.equals("07")) {
                mFromMonth = "J U L  ";
                mToMonth = "J U N  ";
            } else if (s.equals("08")) {
                mFromMonth = "A U G  ";
                mToMonth = "J U L  ";
            } else if (s.equals("09")) {
                mFromMonth = "S E P  ";
                mToMonth = "A U G  ";
            } else if (s.equals("10")) {
                mFromMonth = "O C T  ";
                mToMonth = "S E P  ";
            } else if (s.equals("11")) {
                mFromMonth = "N O V  ";
                mToMonth = "O C T  ";
            } else if (s.equals("12")) {
                mFromMonth = "D E C  ";
                mToMonth = "N O V  ";
            }
            if (pos > 0) {
                holder.mMainProgress.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                Calendar cal = java.util.Calendar.getInstance();
                int startY = Integer.parseInt(mUserBirthday.substring(0, 4)) + (pos * 10) - 1;
                int endY = Integer.parseInt(mUserBirthday.substring(0, 4)) + ((pos + 1) * 10 - 1) - 1;
                int thisY = cal.get(cal.YEAR);

                Typeface periodTypeFace = Typeface.createFromAsset(context.getAssets(), "Dense-Regular.mp3");
                holder.mPeriod.setTypeface(periodTypeFace, Typeface.BOLD);
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
                    int marking = (((DreamApp.getInstance().getUser().getUserAge() % 10) * 365) + cal.get(cal.DAY_OF_YEAR)) / 10;
                    holder.mMainProgress.setImageDrawable(new FanShapeDrawable(null, marking, PROGRESS_BAR_BASELINE, deviceDensityDpi, pos));
                }
            } else {
                holder.mMainProgress.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                int marking = (int) ((float) DreamApp.getInstance().getUser().getUserAge() / 100 * 360);

                Typeface periodTypeFace = Typeface.createFromAsset(context.getAssets(), "Dense-Regular.mp3");
                holder.mPeriod.setTypeface(BaseActionBarActivity.getDenseRegularFont(), Typeface.BOLD);
                holder.mPeriod.setTextSize(25);
//            holder.mMainProgress.setImageDrawable(new FanShapeDrawable(null, 0, PROGRESS_BAR_BASELINE, deviceDensityDpi, pos));
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
                intent.putExtra("groupRange", getBucketGroupList().get(pos).getRange());
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
        if (!(pos == 0)) {
            builder.setTitle("Wish B: 당신의 " + pos * 10 + "대");
            builder.setMessage("어떤 10년을 보내고 싶으세요?");
        } else {
            builder.setTitle("Wish B.");
            builder.setMessage("어떤 인생을 꿈꾸시나요?");
        }

        final EditText input = new EditText(context);
        input.setId(TEXT_ID);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
        input.setSingleLine();
        builder.setView(input);
        input.setText(holder.mBtnDecade.getText());
        input.selectAll();
//        input.setSelection(holder.mBtnDecade.getText().length());

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String titleValue = input.getText().toString();
                holder.mBtnDecade.setText(titleValue);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);  //Hide soft keyboard
                saveUser(titleValue, pos);
                return;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);   //Hide soft keyboard
                return;
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
        @InjectView(R.id.main_image5)
        ImageView mMainImage5;
        @InjectView(R.id.main_image6)
        ImageView mMainImage6;
        @InjectView(R.id.main_image7)
        ImageView mMainImage7;
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
