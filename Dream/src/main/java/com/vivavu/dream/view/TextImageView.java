package com.vivavu.dream.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Created by yuja on 2014-05-02.
 */
public class TextImageView extends BaseImageView {
    public static final String TAG = TextImageView.class.getSimpleName();

    protected String text;
    protected Drawable foregroundDrawable;

    public TextImageView(Context context) {
        super(context);
    }

    public TextImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final Drawable foreground = getForegroundDrawable();
        if (foreground != null) {
            foreground.setBounds(0, 0, getWidth(), getHeight());

            final int scrollX = getScrollX();
            final int scrollY = getScrollY();

            if ((scrollX | scrollY) == 0) {
                foreground.draw(canvas);
            }
            else {
                canvas.translate(scrollX, scrollY);
                foreground.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }
    }

    private Bitmap getBitmap(int width, int height) {
        // 실제로 마스크 영역
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapOut);
        Paint pnt = new Paint();

        //꽉찬 원형을 그림
        pnt.setStyle(Paint.Style.FILL);
        pnt.setColor(Color.BLACK);
        canvas.drawOval(new RectF(0.0f, 0.0f, width, height), pnt);

        if(text != null && text.length() > 0) {
            // 기본 문자열 출력. 안티 알리아싱을 적용했다.
            Bitmap textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvasText = new Canvas(textBitmap);

            pnt.setStyle(Paint.Style.FILL);
            pnt.setColor(Color.TRANSPARENT);
            canvasText.drawRect(new Rect(0, 0, width, height), pnt);

            pnt.setAntiAlias(true);
            pnt.setColor(Color.BLACK);
            pnt.setTextSize(20);
            pnt.setTextAlign(Paint.Align.CENTER);
            float centerX = getWidth()/2;
            float centerY = (float) (getHeight()/2*(1+Math.sin(Math.toRadians(45.0))));

            int measuredTextWidth = (int) Math.ceil(pnt.measureText(text));
            if(measuredTextWidth > getWidth()){
                canvasText.drawText(text.substring(0, 1), centerX, centerY, pnt);
            } else {
                canvasText.drawText(text, centerX, centerY, pnt);
            }

            Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            xferPaint.setColor(Color.BLACK);
            xferPaint.setFilterBitmap(false);
            xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT)); //<== 이부분을 DST_IN으로 바꾸면 Text만 보여지는데, 의미가 없다...

            canvas.drawBitmap(textBitmap, 0, 0, xferPaint);
        }

        return bitmapOut;
    }

    @Override
    public Bitmap getBitmap() {
        return getBitmap(getWidth(), getHeight());
    }

    public void setForegroundResource(int resId) {
        setForegroundDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setForegroundDrawable(Drawable d) {
        d.setCallback(this);
        d.setVisible(getVisibility() == VISIBLE, false);

        foregroundDrawable = d;

        requestLayout();
        invalidate();
    }

    public Drawable getForegroundDrawable() {
        return foregroundDrawable;
    }
}
