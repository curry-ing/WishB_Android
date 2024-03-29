package com.vivavu.dream.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.vivavu.dream.R;
import com.vivavu.dream.common.DreamApp;

/**
 * Created by masunghoon on 4/23/14.
 */
public class FanShapeDrawable extends Drawable {
    private final Bitmap mBitmap;
    private final Paint mPaint;
    private final Paint mPaint2;
    private final Paint mPaint3;
    private final RectF mRectF;
    private final int mBitmapWidth;
    private final int mBitmapHeight;
    private int CANVAS_WIDTH;
    private int CANVAS_HEIGHT;
    private int imgNum;
    private int imgCnt;
    private int mPosition;



    public FanShapeDrawable(Bitmap bitmap, int num, int cnt, int res, int position){
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint2 = new Paint();
        mPaint3 = new Paint();
        imgNum = num;
        imgCnt = cnt;
        mPosition = position;

	    //todo : 이부분 교정이 필요함. 이미지 처리는 외부 drawable이 아닌 view에서 처리하도록 수정이 필요함.
	    double fact = (double)540 /320;
	    CANVAS_WIDTH = (int) (fact * res);
	    CANVAS_HEIGHT= (int) (fact * res);

	    if (res == 320) {
            CANVAS_WIDTH = 540;
            CANVAS_HEIGHT = 540;
        } else if (res == 480) {
            CANVAS_WIDTH = 810;
            CANVAS_HEIGHT = 810;
        } else {

	    }

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint2.setAntiAlias(true);
        mPaint2.setDither(true);
        mPaint3.setAntiAlias(true);
        mPaint3.setDither(true);
        mPaint3.setColor(Color.TRANSPARENT);
        if(bitmap==null){
            mBitmap = bitmap;

            mBitmapWidth = 290;
            mBitmapHeight = 290;
//            mPaint.setShadowLayer(5, 1.0f, 0.0f, Color.DKGRAY);
        } else {
            mBitmap = adjustImage(bitmap, imgNum, imgCnt);
            final BitmapShader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);

            mPaint2.setColor(Color.WHITE);
            mPaint2.setStrokeWidth(5.0f);
            mBitmapWidth = mBitmap.getWidth();
            mBitmapHeight = mBitmap.getHeight();
        }
    }

    @Override
    public void draw(Canvas canvas){
        if(mBitmap==null) {
            switch(mPosition){
                case 0:
                    mPaint.setColor(DreamApp.getInstance().getResources().getColor(R.color.progress_lt));
                    mPaint.setShadowLayer(3, 1.0f, 0.0f, DreamApp.getInstance().getResources().getColor(R.color.progress_lt));
                    break;
                case 1:
                    mPaint.setColor(DreamApp.getInstance().getResources().getColor(R.color.progress_10));
                    mPaint.setShadowLayer(3, 1.0f, 0.0f, DreamApp.getInstance().getResources().getColor(R.color.progress_10));
                    break;
                case 2:
                    mPaint.setColor(DreamApp.getInstance().getResources().getColor(R.color.progress_20));
                    mPaint.setShadowLayer(3, 1.0f, 0.0f, DreamApp.getInstance().getResources().getColor(R.color.progress_20));
                    break;
                case 3:
                    mPaint.setColor(DreamApp.getInstance().getResources().getColor(R.color.progress_30));
                    mPaint.setShadowLayer(3, 1.0f, 0.0f, DreamApp.getInstance().getResources().getColor(R.color.progress_30));
                    break;
                case 4:
                    mPaint.setColor(DreamApp.getInstance().getResources().getColor(R.color.progress_40));
                    mPaint.setShadowLayer(3, 1.0f, 0.0f, DreamApp.getInstance().getResources().getColor(R.color.progress_40));
                    break;
                case 5:
                    mPaint.setColor(DreamApp.getInstance().getResources().getColor(R.color.progress_50));
                    mPaint.setShadowLayer(3, 1.0f, 0.0f, DreamApp.getInstance().getResources().getColor(R.color.progress_50));
                    break;
                case 6:
                    mPaint.setColor(DreamApp.getInstance().getResources().getColor(R.color.progress_60));
                    mPaint.setShadowLayer(3, 1.0f, 0.0f, DreamApp.getInstance().getResources().getColor(R.color.progress_60));
                    break;
            }
            canvas.drawArc(new RectF(13.0f,13.0f,287.0f,287.0f), imgCnt, imgNum, true, mPaint);
            mPaint3.setColor(Color.DKGRAY);
            canvas.drawOval(new RectF(15,15,285,285), mPaint3);
        } else {
            if (imgCnt == 1){
                mPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.INNER));
                canvas.drawOval(mRectF, mPaint);
            } else if (imgCnt == 2) {
                mPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.INNER));
                canvas.drawArc(mRectF, (float)360/imgCnt*(imgNum-1)+90, 180, true, mPaint);
            } else if (imgCnt == 3) {
                mPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.INNER));
                canvas.drawArc(mRectF, (float)360/imgCnt*(imgNum-1)+30, (float)360/imgCnt, true, mPaint);
            } else if (imgCnt == 4) {
                mPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.INNER));
                canvas.drawArc(mRectF, (float)360/imgCnt*(imgNum-1), (float)360/imgCnt, true, mPaint);
            }
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRectF.set(bounds);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint.getAlpha() != alpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf){
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity(){
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
//        return mBitmapWidth;
        return 300;
    }

    @Override
    public int getIntrinsicHeight(){
//        return mBitmapHeight;
        return 300;
    }

    public void setAntiAlias(boolean aa){
        mPaint.setAntiAlias(aa);
        invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither){
        mPaint.setDither(dither);
        invalidateSelf();
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }

    public Bitmap adjustImage(Bitmap bitmap, int num, int cnt){
        Bitmap cs = null;
        Rect rect = null;

        cs = Bitmap.createBitmap(CANVAS_WIDTH, CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(cs);

        if (cnt == 1) {
            rect = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            c.drawBitmap(bitmap, null, rect, null);
        } else if (cnt == 2) {
            if (num == 1) {
                rect = new Rect(0, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT);
            } else if (num == 2) {
                rect = new Rect(CANVAS_WIDTH/2, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            }
            c.drawBitmap(Bitmap.createBitmap(bitmap, bitmap.getWidth()/4, 0, bitmap.getWidth()/2, bitmap.getHeight()), null, rect, null);
        } else if (cnt == 3) {
            if (num == 1) {
                rect = new Rect(0, CANVAS_HEIGHT/2, CANVAS_WIDTH, CANVAS_HEIGHT);
                c.drawBitmap(Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()/4, bitmap.getWidth(), bitmap.getHeight()/2), null, rect, null);
            } else if (num == 2) {
                rect = new Rect(0, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT*3/4);
                c.drawBitmap(Bitmap.createBitmap(bitmap, bitmap.getWidth()/6, 0, bitmap.getWidth()*2/3, bitmap.getHeight()), null, rect, null);
            } else if (num == 3) {
                rect = new Rect(CANVAS_WIDTH/2, 0, CANVAS_WIDTH, CANVAS_HEIGHT*3/4);
                c.drawBitmap(Bitmap.createBitmap(bitmap, bitmap.getWidth()/6, 0, bitmap.getWidth()*2/3, bitmap.getHeight()), null, rect, null);
            }
        } else if (cnt == 4) {
            if (num == 1) {
                rect = new Rect(CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, CANVAS_WIDTH, CANVAS_HEIGHT);
            } else if (num == 2) {
                rect = new Rect(0, CANVAS_HEIGHT / 2, CANVAS_WIDTH / 2, CANVAS_HEIGHT);
            } else if (num == 3) {
                rect = new Rect(0, 0, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2);
            } else if (num == 4) {
                rect = new Rect(CANVAS_WIDTH/2, 0, CANVAS_WIDTH, CANVAS_HEIGHT/2);
            }
            c.drawBitmap(bitmap, null, rect, null);
        }

        return cs;
    }
}
