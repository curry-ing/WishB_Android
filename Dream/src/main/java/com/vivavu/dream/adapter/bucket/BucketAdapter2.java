package com.vivavu.dream.adapter.bucket;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.vivavu.dream.R;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.drawable.RoundedAvatarDrawable;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.BucketGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.repository.connector.UserInfoConnector;

/**
 * Created by masunghoon on 4/20/14.
 */
public class BucketAdapter2 extends PagerAdapter implements View.OnClickListener{

    private Context context;
    private Fragment fragment;
    private LayoutInflater mInflater;
    private List<BucketGroup> bucketGroupList;
    private List<Bitmap> mainImages;
//    private User user = null;

    private String userBirth;
    private String title;

    static public final int PROGRESS_BAR_BASELINE = 270;
    public static final String TAG = "DialogActivity";
    public static final int DLG_EXAMPLE1 = 0;
    public static final int TEXT_ID = 0;


    public BucketAdapter2 (Fragment fragment, List<BucketGroup> bucketGroupList) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketGroupList = new ArrayList<BucketGroup>(bucketGroupList);
    }

    public BucketAdapter2 (Fragment fragment) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketGroupList = new ArrayList<BucketGroup>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
//        int i = position;
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

        /* SET MAIN TITLE */
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothicBold.mp3");
//        Shader textShader = new LinearGradient(2, 0, 4, 60, new int[]{Color.parseColor("#b4e391"),Color.parseColor("#61c419"),Color.parseColor("#b4e391")},
//                new float[]{0, 3,1}, Shader.TileMode.MIRROR);
//        holder.mBtnDecade.getPaint().setShader(textShader);
        holder.mBtnDecade.setTypeface(typeface);
        holder.mBtnDecade.setTextSize(22);
        holder.mBtnDecade.setTextColor(Color.WHITE);
        holder.mBtnDecade.getPaint().setAntiAlias(true);
//        holder.mBtnDecade.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pencil,0,0,0);

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
            holder.mBtnDecade.setText(bucketGroup.getRangeText());
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
//            holder.mBktCount.setVisibility(View.VISIBLE);
            for (int i=0; i<cnt; i++){
                if(bucketGroup.getBukets().get(i).getCvrImgUrl() != null) {
                    mainImages.add(ImageLoader.getInstance().loadImageSync(bucketGroup.getBukets().get(i).getCvrImgUrl(), new ImageSize(540,540)));
                }
            }
        }
        if(mainImages.size()==0){
            mainImages.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.up_logo));
        } else {
            Collections.shuffle(mainImages);
        }
//        holder.mBktCount.setText(String.valueOf(cnt)+'개');
        for (int j=0; j<mainImages.size(); j++){
            switch (j){
                case 0:
                    holder.mMainImage1.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 1:
                    holder.mMainImage2.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 2:
                    holder.mMainImage3.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 3:
                    holder.mMainImage4.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 4:
                    holder.mMainImage5.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 5:
                    holder.mMainImage6.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 6:
                    holder.mMainImage7.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
                case 7:
                    holder.mMainImage8.setBackground(new RoundedAvatarDrawable(mainImages.get(j),j+1, mainImages.size()));
                    break;
            }
        }


        /* SET PROGRESS BAR */
        if(pos>0){
            holder.mMainProgress.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            Calendar cal = java.util.Calendar.getInstance();
            int startY = Integer.parseInt(DreamApp.getInstance().getUser().getBirthday().substring(0,4))+(pos*10) - 1;
            int endY = Integer.parseInt(DreamApp.getInstance().getUser().getBirthday().substring(0,4))+((pos+1)*10-1) - 1;
            int thisY = cal.get(cal.YEAR);

            Typeface periodTypeFace = Typeface.createFromAsset(context.getAssets(), "Dense-Regular.mp3");
            holder.mPeriod.setTypeface(periodTypeFace, Typeface.BOLD);
            holder.mPeriod.setTextSize(25);
            holder.mPeriod.setText("J A N  "+String.valueOf(startY).charAt(0)+" "
                                            +String.valueOf(startY).charAt(1)+" "
                                            +String.valueOf(startY).charAt(2)+" "
                                            +String.valueOf(startY).charAt(3)+"  -  D E C  "
                                            +String.valueOf(endY).charAt(0)+" "
                                            +String.valueOf(endY).charAt(1)+" "
                                            +String.valueOf(endY).charAt(2)+" "
                                            +String.valueOf(endY).charAt(3));

            if(thisY > endY) {
                holder.mMainProgress.setImageDrawable(new RoundedAvatarDrawable(null, 360, PROGRESS_BAR_BASELINE));
            } else if (thisY < startY) {
                holder.mMainProgress.setImageDrawable(new RoundedAvatarDrawable(null, 0, PROGRESS_BAR_BASELINE));
            } else {
                holder.mMainProgress.setImageDrawable(new RoundedAvatarDrawable(null, cal.get(cal.DAY_OF_YEAR), PROGRESS_BAR_BASELINE));
            }
        } else {
            int imsi = (int) ((float) DreamApp.getInstance().getUser().getUserAge() / 100 * 360);
            holder.mMainProgress.setImageDrawable(new RoundedAvatarDrawable(null, imsi, PROGRESS_BAR_BASELINE));
            holder.mPeriod.setText(DreamApp.getInstance().getUser().getBirthday().substring(0,4) + " ~");
        }

        /* SET ON CLICK LISTENER */
        holder.mMainImage8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setMessage( Html.fromHtml("<b><font color=#ff00ff> HTML View</font></b><br>Android.com"));
                ab.setPositiveButton(android.R.string.ok, null);
                ab.setTitle( "Basic Alert Dialog" );
                ab.show();
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
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!(pos == 0)) {
            builder.setTitle("당신의 " + pos * 10 + "대");
            builder.setMessage("어떤 10년을 보내고 싶으세요?");
        } else {
            builder.setTitle("꿈틀");
            builder.setMessage("당신의 인생을 꾸며보세요");
        }

        final EditText input = new EditText(context);
        input.setId(TEXT_ID);
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
        @InjectView(R.id.bkt_count)
        TextView mBktCount;
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
