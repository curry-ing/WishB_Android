package com.vivavu.dream.view;

import android.content.Context;
import android.content.res.TypedArray;
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
import android.util.Log;

import com.vivavu.dream.R;

/**
 * Created by yuja on 2014-05-02.
 */
public class TextImageView extends BaseImageView {
    public static final String TAG = TextImageView.class.getSimpleName();

    protected String text;
    protected int textSize;
    protected int textColor;
    protected int shadowColor;
    protected float shadowDx;
    protected float shadowDy;
    protected float shadowRadius;
    protected String ellipsize;
    protected boolean isMain = false;
    protected float percent = 0;
    protected int padding=10;
    protected int foregroundResId;

    protected Drawable foregroundDrawable;

    Paint textPaint = null;
    Paint progressPaint = null;

    public TextImageView(Context context) {
        this(context, null);
    }

    public TextImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.CustomeImageView);
        text = arr.getString(R.styleable.CustomeImageView_text);
        textSize = arr.getDimensionPixelSize(R.styleable.CustomeImageView_textSize, 20);
        textColor = arr.getColor(R.styleable.CustomeImageView_textColor, Color.WHITE);
        shadowColor = arr.getColor(R.styleable.CustomeImageView_shadowColor, Color.DKGRAY);
        shadowDx = arr.getFloat(R.styleable.CustomeImageView_shadowDx, 1.0f);
        shadowDy = arr.getFloat(R.styleable.CustomeImageView_shadowDy, 1.0f);
        shadowRadius = arr.getFloat(R.styleable.CustomeImageView_shadowRadius, 1.0f);
        ellipsize = arr.getString(R.styleable.CustomeImageView_ellipsize);
        foregroundResId = arr.getResourceId(R.styleable.CustomeImageView_foregroundResId, -1);
        if(foregroundResId > 0) {
            setForegroundResource(foregroundResId);
        }
        if(ellipsize == null){
            ellipsize = "";
        }
        arr.recycle();
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
        drawBackground(canvas);
        drawProgress(canvas);

        super.onDraw(canvas);

        drawText(canvas);

        drawForeground(canvas);

    }

    protected void drawProgress(Canvas canvas) {
        if(isMain && percent > 0){

            if(progressPaint == null) {
                progressPaint = new Paint();
                progressPaint.setAntiAlias(true);
                progressPaint.setStyle(Paint.Style.STROKE);
                progressPaint.setStrokeWidth(10);
                progressPaint.setStrokeCap(Paint.Cap.SQUARE);
                progressPaint.setColor(getResources().getColor(R.color.mint));
            }

            RectF rectF = new RectF(getPaddingLeft(), getPaddingTop(), getWidth()-getPaddingRight(), getHeight()-getPaddingBottom());
            canvas.drawArc(rectF, -90, 360*(percent/100), false, progressPaint );
        }
    }

    protected void drawForeground(Canvas canvas) {
        final Drawable foreground = getForegroundDrawable();
        if (foreground != null) {
            foreground.setBounds(getPaddingLeft(), getPaddingTop(), getWidth()-getPaddingRight(), getHeight()-getPaddingBottom());

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

    protected void drawBackground(Canvas canvas) {
        final Drawable background = getBackground();
        if (background != null) {
            background.setBounds(0, 0, getWidth(), getHeight());

            final int scrollX = getScrollX();
            final int scrollY = getScrollY();

            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            }
            else {
                canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }
    }

    /**
     * 이미지 위에 텍스트를 그린다.
     * @param canvas
     */
    protected void drawText(Canvas canvas){
        if(isMain == false && text != null && text.length() > 0) {
            if(textPaint == null) {
                textPaint = new Paint();

                textPaint.setAntiAlias(true);
                textPaint.setColor(textColor);
                textPaint.setTextSize(textSize);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setStrokeWidth(2.0f);
                textPaint.setStyle(Paint.Style.STROKE);
                textPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
            }

            float centerX = getWidth()/2;
            float centerY = (float) (getHeight()/2*(1+Math.sin(Math.toRadians(45.0))));
            int innerWidth = (int) Math.ceil(getWidth()*(Math.cos(Math.toRadians(45.0))));

            int measuredTextWidth = (int) Math.ceil(textPaint.measureText(text));
            int charWidth = measuredTextWidth / text.length();
            if(measuredTextWidth > innerWidth){

                int overWidth = measuredTextWidth - innerWidth + (int)Math.ceil(textPaint.measureText(ellipsize));
                int overChar = overWidth/charWidth;
                canvas.drawText(text.substring(0, text.length()-overChar)+ ellipsize, centerX, centerY, textPaint);
            } else {
                canvas.drawText(text, centerX, centerY, textPaint);
            }
        }
    }

    /**
     * 이미지에 텍스트영역이 빈상태로 만들어준다.
     * @param canvas
     */
    protected void drawMaskText(Canvas canvas){
        if(isMain == false && text != null && text.length() > 0) {
            // 기본 문자열 출력. 안티 알리아싱을 적용했다.
            Bitmap textBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvasText = new Canvas(textBitmap);
            Paint pnt = new Paint();
            pnt.setStyle(Paint.Style.FILL);
            pnt.setColor(Color.TRANSPARENT);
            canvasText.drawRect(new Rect(0, 0, getWidth(), getHeight()), pnt);

            pnt.setAntiAlias(true);
            pnt.setColor(textColor);
            pnt.setTextSize(textSize);
            pnt.setTextAlign(Paint.Align.CENTER);
            pnt.setStrokeWidth(2.0f);
            pnt.setStyle(Paint.Style.STROKE);
            pnt.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);

            float centerX = getWidth()/2;
            float centerY = (float) (getHeight()/2*(1+Math.sin(Math.toRadians(45.0))));
            int innerWidth = (int) Math.ceil(getWidth()*(Math.cos(Math.toRadians(45.0))));

            int measuredTextWidth = (int) Math.ceil(pnt.measureText(text));
            int charWidth = measuredTextWidth / text.length();
            if(measuredTextWidth > innerWidth){
                int overWidth = measuredTextWidth - innerWidth + (int)Math.ceil(pnt.measureText(ellipsize));
                int overChar = overWidth/charWidth;
                canvasText.drawText(text.substring(0, text.length()-overChar)+ellipsize, centerX, centerY, pnt);
            } else {
                canvasText.drawText(text, centerX, centerY, pnt);
            }

            Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            xferPaint.setColor(Color.BLACK);
            xferPaint.setFilterBitmap(false);
            xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER)); //<== 이부분을 DST_IN으로 바꾸면 Text만 보여지는데, 의미가 없다...

            canvas.drawBitmap(textBitmap, 0, 0, xferPaint);
        }
    }

    @Override
    public Bitmap getBitmap(int width, int height) {
        // 실제로 마스크 영역
        Log.v(TAG, String.format("%s : %d, %d", String.valueOf(text), width, height));
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapOut);
        Paint pnt = new Paint();

        //꽉찬 원형을 그림
        pnt.setStyle(Paint.Style.FILL);
        pnt.setColor(Color.BLACK);
        //canvas.drawOval(new RectF(0.0f + getPaddingLeft(), 0.0f + getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom()), pnt);
        canvas.drawCircle( (float)width / 2, (float)height/2, (float)Math.min(width-getPaddingLeft()-getPaddingRight(), height-getPaddingTop()-getPaddingBottom())/2, pnt);

        return bitmapOut;
    }

    public void setForegroundResource(int resId) {
        setForegroundDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setForegroundDrawable(Drawable d) {
        d.setCallback(this);
        d.setVisible(getVisibility() == VISIBLE, false);

        foregroundDrawable = d;

        invalidate();
    }

    public Drawable getForegroundDrawable() {
        return foregroundDrawable;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public float getShadowDx() {
        return shadowDx;
    }

    public void setShadowDx(float shadowDx) {
        this.shadowDx = shadowDx;
    }

    public float getShadowDy() {
        return shadowDy;
    }

    public void setShadowDy(float shadowDy) {
        this.shadowDy = shadowDy;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public String getEllipsize() {
        return ellipsize;
    }

    public void setEllipsize(String ellipsize) {
        this.ellipsize = ellipsize;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
