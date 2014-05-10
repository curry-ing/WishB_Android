package com.vivavu.dream.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by yuja on 2014-05-02.
 *
 * https://github.com/MostafaGazar/CustomShapeImageView 를 가져와서 사용함 커스터마이징이 필요하여 카피하여 만듦
 * 문제의 소지가 있을 경우 출처 및 라이센스 관련 확인 필요
 */
public abstract class BaseImageView extends ImageView {
    public static final String TAG = BaseImageView.class.getSimpleName();

    protected Context mContext;


    private static final Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    //    private BitmapShader mBitmapShader;
    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private WeakReference<Bitmap> mWeakBitmap;
    protected boolean mReady;
    protected boolean mSetupPending;

    public BaseImageView(Context context) {
        super(context);
        sharedConstructor(context);
    }


    public BaseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor(context);
    }


    public BaseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructor(context);
    }


    private void sharedConstructor(Context context) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mReady = true;

        if (mSetupPending) {
            setUp();
            mSetupPending = false;
        }
    }

    @Override
    public void invalidate() {
        mWeakBitmap = null;
        if (mMaskBitmap != null) { mMaskBitmap.recycle(); }
        super.invalidate();
    }

    protected void setUp(){
        if (!mReady) {
            mSetupPending = true;
            return;
        }

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            int i = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            try {
                Bitmap bitmap = mWeakBitmap != null ? mWeakBitmap.get() : null;
                // Bitmap not loaded.
                if (bitmap == null || bitmap.isRecycled()) {
                    Drawable drawable = getDrawable();
                    if (drawable != null) {
                        // Allocation onDraw but it's ok because it will not always be called.
                        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas bitmapCanvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, getWidth(), getHeight());
                        drawable.draw(bitmapCanvas);
                        Log.v(TAG, String.format("bitmap size:%d, %d", bitmap.getWidth(), bitmap.getHeight()));
                        Log.v(TAG, String.format("canvas size:%d, %d", bitmapCanvas.getWidth(), bitmapCanvas.getHeight()));
                        Log.v(TAG, String.format("drawable size:%s", drawable.getBounds().toString()));
                        // If mask is already set, skip and use cached mask.
                        if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
                            mMaskBitmap = getBitmap();
                        }
                        Log.v(TAG, String.format("mMaskBitmap size:%d, %d", mMaskBitmap.getWidth(), mMaskBitmap.getHeight()));
                        // Draw Bitmap.
                        mPaint.reset();
                        mPaint.setAntiAlias(true);
                        mPaint.setFilterBitmap(false);
                        mPaint.setXfermode(sXfermode);
//                        mBitmapShader = new BitmapShader(mMaskBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//                        mPaint.setShader(mBitmapShader);
                        bitmapCanvas.drawBitmap(mMaskBitmap, 0.0f, 0.0f, mPaint);

                        mWeakBitmap = new WeakReference<Bitmap>(bitmap);
                    }
                }

                // Bitmap already loaded.
                if (bitmap != null) {
                    mPaint.reset();
                    mPaint.setAntiAlias(true);
//                    mPaint.setShader(null);
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, mPaint);
                    return;
                }
            } catch (Exception e) {
                System.gc();
                Log.e(TAG, String.format("Failed to draw, Id :: %s. Error occurred :: %s", getId(), e.toString()));
            } finally {
                canvas.restoreToCount(i);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap bitmap = mWeakBitmap != null ? mWeakBitmap.get() : null;
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    public abstract Bitmap getBitmap();
}
