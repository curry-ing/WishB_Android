package com.vivavu.dream.drawable;

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
    private int imgNum;
    private int imgCnt;

    public RoundedAvatarDrawable(Bitmap bitmap, int num, int cnt){
        mBitmap = bitmap;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint2 = new Paint();
        mPaint3 = new Paint();
        imgNum = num;
        imgCnt = cnt;
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint2.setAntiAlias(true);
        mPaint2.setDither(true);
        mPaint3.setAntiAlias(true);
        mPaint3.setDither(true);
        mPaint3.setColor(Color.TRANSPARENT);
        if(mBitmap==null){
            mPaint.setColor(Color.rgb(103, 201, 187));

            mBitmapWidth = 290;
            mBitmapHeight = 290;
        } else {
            final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
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
            canvas.drawArc(mRectF, imgCnt, imgNum, true, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawArc(mRectF, imgNum-90, 360-imgNum, true, mPaint);
        } else {
            if (imgCnt == 1) {
                canvas.drawOval(mRectF, mPaint);
            } else if (imgCnt == 2) {
                if (imgNum == 1) {
                    canvas.drawArc(mRectF, 0, 180, true, mPaint);
                } else if (imgNum == 2) {
                    canvas.drawArc(mRectF, 180, 180, true, mPaint);
                }
                canvas.drawLine(0,cHeight/2,cWidth,cHeight/2,mPaint2);
            } else if (imgCnt == 3) {
                if (imgNum == 1) {
                    canvas.drawArc(mRectF, 0, 120, true, mPaint);
                } else if (imgNum == 2) {
                    canvas.drawArc(mRectF, 120, 120, true, mPaint);
                } else if (imgNum == 3) {
                    canvas.drawArc(mRectF, 240, 120, true, mPaint);
                }
                for (int i=0; i<imgCnt; i++){
                    double Rad = Math.toRadians(360/imgCnt * i);

                    int x = (int) (cWidth/2 + cWidth/2 * Math.cos(Rad));
                    int y = (int) (cWidth/2 + cWidth/2 * Math.sin(Rad));

                    canvas.drawLine(cWidth/2, cHeight/2, x, y, mPaint2);
                }
            } else if (imgCnt == 4) {
                if (imgNum == 1) {
                    canvas.drawArc(mRectF, 0, 90, true, mPaint);
                } else if (imgNum == 2) {
                    canvas.drawArc(mRectF, 90, 90, true, mPaint);
                } else if (imgNum == 3) {
                    canvas.drawArc(mRectF, 180, 90, true, mPaint);
                } else if (imgNum == 4) {
                    canvas.drawArc(mRectF, 270, 90, true, mPaint);
                }
            } else if (imgCnt == 5) {
                if (imgNum == 1) {
                    canvas.drawArc(mRectF, 0, 72, true, mPaint);
                } else if (imgNum == 2) {
                    canvas.drawArc(mRectF, 72, 72, true, mPaint);
                } else if (imgNum == 3) {
                    canvas.drawArc(mRectF, 144, 72, true, mPaint);
                } else if (imgNum == 4) {
                    canvas.drawArc(mRectF, 216, 72, true, mPaint);
                } else if (imgNum == 5) {
                    canvas.drawArc(mRectF, 288, 72, true, mPaint);
                }
                for (int i=0; i<imgCnt; i++){
                    double Rad = Math.toRadians(360/imgCnt * i);

                    int x = (int) (cWidth/2 + cWidth/2 * Math.cos(Rad));
                    int y = (int) (cWidth/2 + cWidth/2 * Math.sin(Rad));

                    canvas.drawLine(cWidth/2, cHeight/2, x, y, mPaint2);
                }
            } else if (imgCnt == 6) {
                if (imgNum == 1) {
                    canvas.drawArc(mRectF, 0, 60, true, mPaint);
                } else if (imgNum == 2) {
                    canvas.drawArc(mRectF, 60, 60, true, mPaint);
                } else if (imgNum == 3) {
                    canvas.drawArc(mRectF, 120, 60, true, mPaint);
                } else if (imgNum == 4) {
                    canvas.drawArc(mRectF, 180, 60, true, mPaint);
                } else if (imgNum == 5) {
                    canvas.drawArc(mRectF, 240, 60, true, mPaint);
                } else if (imgNum == 6) {
                    canvas.drawArc(mRectF, 300, 60, true, mPaint);
                }
            } else if (imgCnt == 7) {
                if (imgNum == 1) {
                    canvas.drawArc(mRectF, 0, 52, true, mPaint);
                } else if (imgNum == 2) {
                    canvas.drawArc(mRectF, 51, 52, true, mPaint);
                } else if (imgNum == 3) {
                    canvas.drawArc(mRectF, 103, 52, true, mPaint);
                } else if (imgNum == 4) {
                    canvas.drawArc(mRectF, 154, 52, true, mPaint);
                } else if (imgNum == 5) {
                    canvas.drawArc(mRectF, 205, 52, true, mPaint);
                } else if (imgNum == 6) {
                    canvas.drawArc(mRectF, 257, 52, true, mPaint);
                } else if (imgNum == 7) {
                    canvas.drawArc(mRectF, 308, 52, true, mPaint);
                }
            } else if (imgCnt == 8) {
                if (imgNum == 1) {
                    canvas.drawArc(mRectF, 0, 45, true, mPaint);
                } else if (imgNum == 2) {
                    canvas.drawArc(mRectF, 45, 45, true, mPaint);
                } else if (imgNum == 3) {
                    canvas.drawArc(mRectF, 90, 45, true, mPaint);
                } else if (imgNum == 4) {
                    canvas.drawArc(mRectF, 135, 45, true, mPaint);
                } else if (imgNum == 5) {
                    canvas.drawArc(mRectF, 180, 45, true, mPaint);
                } else if (imgNum == 6) {
                    canvas.drawArc(mRectF, 225, 45, true, mPaint);
                } else if (imgNum == 7) {
                    canvas.drawArc(mRectF, 270, 45, true, mPaint);
                } else if (imgNum == 8) {
                    canvas.drawArc(mRectF, 315, 45, true, mPaint);
                }
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

    public Bitmap combineImages(Bitmap bitmap){
        Bitmap cs = null;

        cs = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(cs);

        c.drawBitmap(bitmap, 0f, 0f, null);

        return cs;
    }
}
