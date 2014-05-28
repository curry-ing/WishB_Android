package com.vivavu.dream.drawable;

import android.content.res.Configuration;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * Created by masunghoon on 4/23/14.
 */
public class RoundedAvatarDrawable extends Drawable {
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


    public RoundedAvatarDrawable(Bitmap bitmap, int num, int cnt, int res){
//        mBitmap = bitmap;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint2 = new Paint();
        mPaint3 = new Paint();
        imgNum = num;
        imgCnt = cnt;

        if (res == 320) {
            CANVAS_WIDTH = 540;
            CANVAS_HEIGHT = 540;
        } else if (res == 480) {
            CANVAS_WIDTH = 810;
            CANVAS_HEIGHT = 810;
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
            mPaint.setShadowLayer(5, 1.0f, 0.0f, Color.DKGRAY);
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
//        canvas.drawOval(mRectF, mPaint2);
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        if(mBitmap==null) {
            mPaint.setColor(Color.rgb(103, 201, 187));
            canvas.drawArc(new RectF(10,10,290,290), imgCnt, imgNum, true, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawArc(new RectF(10,10,290,290), imgNum-90, 360-imgNum, true, mPaint);
            mPaint3.setColor(Color.DKGRAY);
            canvas.drawOval(new RectF(15,15,285,285), mPaint3);
        } else {
            if (imgCnt > 1) {
                mPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.INNER));
                canvas.drawArc(mRectF, (float)360/imgCnt*(imgNum-1), (float)360/imgCnt, true, mPaint);
            } else {
                mPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.INNER));
                canvas.drawOval(mRectF, mPaint);
            }
//            for (int i=0; i<imgCnt; i++){
//                double Rad = Math.toRadians(360/imgCnt * i);
//
//                int x = (int) (cWidth/2 + cWidth/2 * Math.cos(Rad));
//                int y = (int) (cWidth/2 + cWidth/2 * Math.sin(Rad));
//                if (imgCnt != 1) canvas.drawLine(cWidth/2, cHeight/2, x, y, mPaint2);
//            }
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
        } else if (cnt == 2) {
            if (num == 1) {
                rect = new Rect(0, CANVAS_HEIGHT/2, CANVAS_WIDTH, CANVAS_HEIGHT);
            } else if (num == 2) {
                rect = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT/2);
            }
        } else if (cnt == 3) {
            if (num == 1) {
                rect = new Rect(CANVAS_WIDTH/4, CANVAS_HEIGHT/2, CANVAS_WIDTH, CANVAS_HEIGHT);
            } else if (num == 2) {
                rect = new Rect(0, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT);
            } else if (num == 3) {
                rect = new Rect(CANVAS_WIDTH/4, 0, CANVAS_WIDTH, CANVAS_HEIGHT/2);
            }
        } else if (cnt == 4) {
            switch (num) {
                case 1:
                    rect = new Rect(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, CANVAS_WIDTH, CANVAS_HEIGHT);
                    break;
                case 2:
                    rect = new Rect(0, CANVAS_HEIGHT/2, CANVAS_WIDTH/2, CANVAS_HEIGHT);
                    break;
                case 3:
                    rect = new Rect(0, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
                    break;
                case 4:
                    rect = new Rect(CANVAS_WIDTH/2, 0, CANVAS_WIDTH, CANVAS_HEIGHT/2);
                    break;
            }
        } else if (cnt == 5) {
            switch (num) {
                case 1:
                    rect = new Rect(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, CANVAS_WIDTH, CANVAS_HEIGHT);
                    break;
                case 2:
                    rect = new Rect(CANVAS_WIDTH/10, CANVAS_HEIGHT/2, CANVAS_WIDTH/10*7, CANVAS_HEIGHT);
                    break;
                case 3:
                    rect = new Rect(0, CANVAS_HEIGHT/10*2, CANVAS_WIDTH/2, CANVAS_HEIGHT/10*8);
                    break;
                case 4:
                    rect = new Rect(CANVAS_WIDTH/10, 0, CANVAS_WIDTH/10*7, CANVAS_HEIGHT/2);
                    break;
                case 5:
                    rect = new Rect(CANVAS_WIDTH/2, 0, CANVAS_WIDTH, CANVAS_HEIGHT/2);
                    break;
            }
        } else if (cnt == 6) {
            switch (num) {
                case 1:
                    rect = new Rect(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, CANVAS_WIDTH, CANVAS_HEIGHT);
                    break;
                case 2:
                    rect = new Rect(CANVAS_WIDTH/10*2, CANVAS_HEIGHT/2, CANVAS_WIDTH/10*8, CANVAS_HEIGHT);
                    break;
                case 3:
                    rect = new Rect(0, CANVAS_HEIGHT/2, CANVAS_WIDTH/2, CANVAS_HEIGHT);
                    break;
                case 4:
                    rect = new Rect(0, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
                    break;
                case 5:
                    rect = new Rect(CANVAS_WIDTH/10*2, 0, CANVAS_WIDTH/10*8, CANVAS_HEIGHT/2);
                    break;
                case 6:
                    rect = new Rect(CANVAS_WIDTH/2, 0, CANVAS_WIDTH, CANVAS_HEIGHT/2);
                    break;
            }
        } else if (cnt == 7) {
            switch (num) {
                case 1:
                    rect = new Rect(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, CANVAS_WIDTH, CANVAS_HEIGHT/10*9);
                    break;
                case 2:
                    rect = new Rect(CANVAS_WIDTH/10*3, CANVAS_HEIGHT/2, CANVAS_WIDTH/10*9, CANVAS_HEIGHT);
                    break;
                case 3:
                    rect = new Rect(0, CANVAS_HEIGHT/2, CANVAS_WIDTH/2, CANVAS_HEIGHT);
                    break;
                case 4:
                    rect = new Rect(0, CANVAS_HEIGHT/10*3, CANVAS_WIDTH/2, CANVAS_HEIGHT/10*7);
                    break;
                case 5:
                    rect = new Rect(0, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
                    break;
                case 6:
                    rect = new Rect(CANVAS_WIDTH/10*3, 0, CANVAS_WIDTH/10*9, CANVAS_HEIGHT/2);
                    break;
                case 7:
                    rect = new Rect(CANVAS_WIDTH/2, CANVAS_HEIGHT/10, CANVAS_WIDTH, CANVAS_HEIGHT/2);
                    break;
            }
        } else if (cnt == 8) {
            switch (num) {
                case 1:
                    rect = new Rect(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, CANVAS_WIDTH, CANVAS_HEIGHT/10*9);
                    break;
                case 2:
                    rect = new Rect(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, CANVAS_WIDTH/10*9, CANVAS_HEIGHT);
                    break;
                case 3:
                    rect = new Rect(CANVAS_WIDTH/10, CANVAS_HEIGHT/2, CANVAS_WIDTH/2, CANVAS_HEIGHT);
                    break;
                case 4:
                    rect = new Rect(0, CANVAS_HEIGHT/2, CANVAS_WIDTH/2, CANVAS_HEIGHT/10*9);
                    break;
                case 5:
                    rect = new Rect(0, CANVAS_HEIGHT/10, CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
                    break;
                case 6:
                    rect = new Rect(CANVAS_WIDTH/10, 0, CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
                    break;
                case 7:
                    rect = new Rect(CANVAS_WIDTH/2, 0, CANVAS_WIDTH/10*9, CANVAS_HEIGHT/2);
                    break;
                case 8:
                    rect = new Rect(CANVAS_WIDTH/2, CANVAS_HEIGHT/10, CANVAS_WIDTH, CANVAS_HEIGHT/2);
                    break;
            }
        } else {
            rect = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        }

        c.drawBitmap(bitmap, null, rect, null);
        return cs;
    }
}
