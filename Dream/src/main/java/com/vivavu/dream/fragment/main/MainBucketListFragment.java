package com.vivavu.dream.fragment.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketAdapter2;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.vivavu.dream.util.AndroidUtils;

import static com.vivavu.dream.common.DreamApp.getInstance;

/**
 * Created by yuja on 14. 2. 27.
 */
public class MainBucketListFragment extends CustomBaseFragment { //} implements PullToRefreshListView.OnRefreshListener<ListView> {
    static public String TAG = "com.vivavu.dream.fragment.main.MainBucketListFragment";
    static public final int REQUEST_CODE_CHANGE_DAY = 0;
    static public final int SEND_REFRESH_START = 0;
    static public final int SEND_REFRESH_STOP = 1;
    static public final int SEND_BUKET_LIST_UPDATE = 2;
    private static final int SEND_NETWORK_DATA = 3;
    static public final int OFF_SCREEN_PAGE_LIMIT = 3;

    private int mShortAnimationDuration, mMediumAnimationDuration, mLongAnimationDuration;

    private Paint mPaints;
    private RectF mBigOval;
    private float mStart, mSweep;
    private static final int SWEEP_INC = 2;
    private static final int START_INC = 15;

    private int i = 0;
    private float mX;


    @InjectView(R.id.main_pager)
    ViewPager mMainPager;
    @InjectView(R.id.main_pager_bg0)
    ImageView mMainPageBg0;
    @InjectView(R.id.main_pager_bg1)
    ImageView mMainPageBg1;

    private List<BucketGroup> bucketGroupList;
    private BucketAdapter2 bucketAdapter2;
    private ProgressDialog progressDialog;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_REFRESH_START:
                    progressDialog.show();
                    break;
                case SEND_REFRESH_STOP:
                    updateContents((List<BucketGroup>) msg.obj);
                    break;
                case SEND_BUKET_LIST_UPDATE:
                    updateContents((List<BucketGroup>) msg.obj);
                    progressDialog.dismiss();
                    break;
                case SEND_NETWORK_DATA:
                    break;
            }
        }
    };

    public MainBucketListFragment() {
        bucketGroupList = new ArrayList<BucketGroup>();
    }

    public MainBucketListFragment(List<BucketGroup> bucketGroupList) {
        this.bucketGroupList = bucketGroupList;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread thread = new Thread(new NetworkThread());
        thread.start();
        final View rootView = inflater.inflate(R.layout.main_row, container, false);
        ButterKnife.inject(this, rootView);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("진행중");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bucketAdapter2 = new BucketAdapter2(this, bucketGroupList);
        mMainPager.setAdapter(bucketAdapter2);
        if (DreamApp.getInstance().getUser().getUserAge() == 0) {
            mMainPager.setCurrentItem(0);
            mMainPageBg0.setBackground((BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0));
        } else {
            mMainPager.setCurrentItem(getInstance().getUser().getUserAge() / 10);
        }
        mMainPager.setOnPageChangeListener(new MainViewPageChangeListener());
        mMainPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
//        mMainPager.setPageTransformer(true, new DepthPageTransformer());
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mLongAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Thread thread = new Thread(new DataThread());
//        thread.start();
    }

    public void updateContents(List<BucketGroup> obj){
        bucketGroupList.clear();
        bucketGroupList.addAll(obj);
        if(bucketAdapter2 == null) {
            bucketAdapter2 = new BucketAdapter2(this, bucketGroupList);
        }
        bucketAdapter2.setBucketGroupList(bucketGroupList);
        bucketAdapter2.notifyDataSetChanged();
        if (DreamApp.getInstance().getUser().getUserAge() == 0) {
            mMainPager.setCurrentItem(0);
            mMainPageBg0.setBackground((BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0));
        } else {
            mMainPager.setCurrentItem(getInstance().getUser().getUserAge() / 10);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bucketAdapter2 != null) {
            bucketAdapter2.notifyDataSetChanged();
        }
    }

//    @Override
//    public void onRefresh(final PullToRefreshBase<ListView> listViewPullToRefreshBase) {
//        if(NetworkUtil.isAvaliableNetworkAccess(DreamApp.getInstance())) {
//            Thread thread = new Thread(new NetworkThread());
//            thread.start();
//        }else {
//            Toast.makeText(getActivity(), getText(R.string.no_network_connection_toast), Toast.LENGTH_SHORT).show();
//            mList.onRefreshComplete();
//        }
//    }


    public class NetworkThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(SEND_REFRESH_START);
            BucketConnector bucketConnector = new BucketConnector();
            ResponseBodyWrapped<List<Bucket>> result = bucketConnector.getBucketList();
            if(result != null) {
                DataRepository.saveBuckets(result.getData());
            }

            handler.post(new DataThread());

        }
    }
    public class DataThread implements Runnable {
        @Override
        public void run() {
            List<BucketGroup> bucketGroup = DataRepository.listBucketGroup();
            Message message = handler.obtainMessage(SEND_BUKET_LIST_UPDATE, bucketGroup);
            handler.sendMessage(message);
        }
    }


    public class MainViewPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        private int currPage;

        @Override
        public void onPageSelected(int position){
            BitmapDrawable toBg = null;
            if(position == 0) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0);
//                String imgUri = "drawable://" + R.drawable.mainview_bg0;
//                toBg = ImageLoader.getInstance().loadImageSync(imgUri, new ImageSize(720, 1233));
            } else if (position == 1) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg1);
//                String imgUri = "drawable://" + R.drawable.mainview_bg1;
//                toBg = ImageLoader.getInstance().loadImageSync(imgUri, new ImageSize(720, 1233));
            } else if (position == 2) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg2);
//                String imgUri = "drawable://" + R.drawable.mainview_bg2;
//                toBg = ImageLoader.getInstance().loadImageSync(imgUri, new ImageSize(720, 1233));
            } else if (position == 3) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg3);
//                String imgUri = "drawable://" + R.drawable.mainview_bg3;
//                toBg = ImageLoader.getInstance().loadImageSync(imgUri, new ImageSize(720, 1233));
            } else if (position == 4) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg4);
//                String imgUri = "drawable://" + R.drawable.mainview_bg4;
//                toBg = ImageLoader.getInstance().loadImageSync(imgUri, new ImageSize(720, 1233));
            } else if (position == 5) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg5);
//                String imgUri = "drawable://" + R.drawable.mainview_bg5;
//                toBg = ImageLoader.getInstance().loadImageSync(imgUri, new ImageSize(720, 1233));
            } else if (position == 6) {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg6);
//                String imgUri = "drawable://" + R.drawable.mainview_bg6;
//                toBg = ImageLoader.getInstance().loadImageSync(imgUri, new ImageSize(720, 1233));
            } else {
                toBg = (BitmapDrawable) getResources().getDrawable(R.drawable.mainview_bg0);
//                String imgUri = "drawable://" + R.drawable.mainview_bg0;
//                toBg = ImageLoader.getInstance().loadImageSync(imgUri, new ImageSize(720, 1233));
            }
            if ((position+1)%2 == 1 ) {
                mMainPageBg0.setAlpha(0.5f);
                mMainPageBg0.setVisibility(View.VISIBLE);
                mMainPageBg0.setBackground(toBg);
//                mMainPageBg0.setImageDrawable(new BitmapDrawable(getResources(),toBg));

                mMainPageBg0.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
                mMainPageBg1.animate().alpha(0.5f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mMainPageBg1.setVisibility(View.GONE);
//                        AndroidUtils.recycleImage(mMainPageBg1);
                    }
                });
            } else {
                mMainPageBg1.setAlpha(0.5f);
                mMainPageBg1.setVisibility(View.VISIBLE);
                mMainPageBg1.setBackground(toBg);
//                mMainPageBg1.setImageDrawable(new BitmapDrawable(getResources(), toBg));

                mMainPageBg1.animate().alpha(1f).setDuration(mShortAnimationDuration).setListener(null);
                mMainPageBg0.animate().alpha(0.5f).setDuration(mShortAnimationDuration).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mMainPageBg0.setVisibility(View.GONE);
                    }
                });
            }

//            mMainProgressBar.setImageDrawable(new MyDrawable());

//            mMainProgress.addView(new DrawView(getActivity()));
            currPage = position;
        }

        public final int getCurrPage(){
            return currPage;
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position){
            if (position < -1){
            } else if (position <= 0) {

            } else if (position <= 1) {

            } else {

            }
        }
    }

    public class DrawView extends View {
        public DrawView(Context context) {
            super(context);
            ValueAnimator anim = ValueAnimator.ofFloat(0,300);
            anim.setDuration(1500);
            anim.setInterpolator(new AccelerateInterpolator());
            anim.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener(){
                public void onAnimationUpdate(ValueAnimator animation){
                    mX = (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            anim.start();

        }

        @Override
        public void onDraw(Canvas canvas) {
            mPaints = new Paint();
            mPaints.setAntiAlias(true);
            mPaints.setStyle(Paint.Style.STROKE);
            mPaints.setStrokeWidth(12);
            mPaints.setColor(Color.RED);

//            canvas.drawColor(R.color.transparent);
            canvas.drawArc(new RectF(25, 25, 575, 575), 270, mX, false, mPaints);
//            for (int j=0; j<=i; j++){
//                canvas.drawArc(new RectF(25, 25, 575, 575), j, 1, false, mPaints);
//            }
            mSweep += SWEEP_INC;
//            if (mSweep > 360) {
//                mSweep -= 360;
//                mStart += START_INC;
//                if (mStart >= 360) {
//                    mStart -= 360;
//                }
//            }
//            if (i<135) {
//                invalidate();
//                i++;
//            }

        }
    }

    public class MyDrawable extends Drawable {
        @Override
        public void draw(Canvas canvas){
            mPaints = new Paint();
            mPaints.setAntiAlias(true);
            mPaints.setStyle(Paint.Style.STROKE);
            mPaints.setStrokeWidth(12);
            mPaints.setColor(Color.RED);

//            canvas.drawColor(R.color.action_bar);
//            canvas.drawArc(new RectF(40, 10, 280, 250), mStart, mSweep, false, mPaints);
//            mSweep += SWEEP_INC;

//            for (int i=0; i<24; i++) {
                canvas.drawArc(new RectF(25, 25, 575, 575), i, 2, false, mPaints);
                mSweep += SWEEP_INC;
                if (mSweep > 360) {
                    mSweep -= 360;
                    mStart += START_INC*2;
                    if (mStart >= 360) {
                        mStart -= 360;
                    }
                }
//            }
        }

        @Override
        public void setAlpha(int i) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }


    }


    private static class MyShapeDrawable extends ShapeDrawable {
        private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public MyShapeDrawable(Shape s){
            super(s);
            mStrokePaint.setStyle(Paint.Style.STROKE);
        }

        public Paint getStrokePaint(){
            return mStrokePaint;
        }

        @Override
        protected void onDraw(Shape s, Canvas c, Paint p){
            s.draw(c, p);
            s.draw(c, mStrokePaint);
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case REQUEST_CODE_CHANGE_DAY:
//                if(resultCode == Activity.RESULT_OK){
//                    Date selectedDate = (Date) data.getSerializableExtra(TodayCalendarActivity.selectedDateExtraName);
//                    Integer selectedIndex =  data.getIntExtra(TodayCalendarActivity.selectedDateIndexExtraName, 0);
//                    if(selectedDate != null){
//                        mMainPager.setCurrentItem(selectedIndex);
//                    }
//                    return;
//                }
//        }
//    }
}
