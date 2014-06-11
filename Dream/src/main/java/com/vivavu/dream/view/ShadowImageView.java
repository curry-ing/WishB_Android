package com.vivavu.dream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.vivavu.dream.R;

/**
 * Created by yuja on 2014-05-02.
 */
public class ShadowImageView extends BaseImageView {
    public static final String TAG = ShadowImageView.class.getSimpleName();

    protected int shadowColor;
    protected float shadowDx;
    protected float shadowDy;
    protected float shadowRadius;
    private int progressBarWidth;
    private int progressBarColor;

    protected float percent = 0;
    protected int padding=10;
    protected int foregroundResId;

    protected Drawable foregroundDrawable;

    Paint progressPaint = null;
    Paint backgroundPaint = null;

    public ShadowImageView(Context context) {
        this(context, null);
    }

    public ShadowImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setDither(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.WHITE);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        progressPaint.setStrokeCap(Paint.Cap.SQUARE);

        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.CustomeImageView);
        if(arr.getBoolean(R.styleable.CustomeImageView_isShadow, false)){
            shadowColor = arr.getColor(R.styleable.CustomeImageView_shadowColor, Color.DKGRAY);
            shadowDx = arr.getFloat(R.styleable.CustomeImageView_shadowDx, 0.0f);
            shadowDy = arr.getFloat(R.styleable.CustomeImageView_shadowDy, 0.0f);
            shadowRadius = arr.getFloat(R.styleable.CustomeImageView_shadowRadius, 1.0f);
            addShadow(shadowRadius, shadowDx, shadowDy, shadowColor);
        }

        setProgressBarWidth(arr.getDimensionPixelSize(R.styleable.CustomeImageView_progressBarWidth, 2));
        setProgressBarColor(arr.getColor(R.styleable.CustomeImageView_progressBarColor, Color.WHITE));

        foregroundResId = arr.getResourceId(R.styleable.CustomeImageView_foregroundResId, -1);
        if(foregroundResId > 0) {
            setForegroundResource(foregroundResId);
        }

        arr.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawProgress(canvas);

        super.onDraw(canvas);

        drawForeground(canvas);
    }

    protected void drawProgress(Canvas canvas) {
        if(percent > 0){
            RectF rectF = new RectF(getPaddingLeft()+getProgressBarWidth()/2
                    , getPaddingTop()+getProgressBarWidth()/2
                    , getWidth()-(getPaddingRight()+getProgressBarWidth()/2)
                    , getHeight()-(getPaddingBottom()+getProgressBarWidth()/2));
            canvas.drawArc(rectF, -90, 360*(percent/100), true, progressPaint );
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
            background.setBounds(getPaddingLeft(), getPaddingTop(), getWidth()-getPaddingRight(), getHeight()-getPaddingBottom());

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
        } else {
            RectF rectf = new RectF(getPaddingLeft(), getPaddingTop(), getWidth()-getPaddingRight(), getHeight()-getPaddingBottom());
            canvas.drawOval(rectf, backgroundPaint);
        }
    }

    public void addShadow(float shadowRadius, float shadowDx, float shadowDy, int shadowColor) {
        setLayerType(LAYER_TYPE_SOFTWARE, backgroundPaint);
        backgroundPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
    }


    @Override
    public Bitmap getBitmap(int width, int height) {
        // 실제로 마스크 영역
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapOut);
        Paint pnt = new Paint();

        //꽉찬 원형을 그림
        pnt.setStyle(Paint.Style.FILL);
        pnt.setColor(Color.BLACK);
        canvas.drawOval(new RectF(0.0f + (getPaddingLeft() + progressBarWidth)
                , 0.0f + (getPaddingTop() + progressBarWidth)
                , width - (getPaddingRight() + progressBarWidth)
                , height - (getPaddingBottom() + progressBarWidth))
                , pnt);

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

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getProgressBarColor() {
        return progressBarColor;
    }

    public int getProgressBarWidth() {
        return progressBarWidth;
    }

    public void setProgressBarWidth(int progressBarWidth) {
        this.progressBarWidth = progressBarWidth;
        progressPaint.setStrokeWidth(progressBarWidth);
    }

    public void setProgressBarColor(int progressBarColor) {
        this.progressBarColor = progressBarColor;
        progressPaint.setColor(progressBarColor);
        progressPaint.setShadowLayer(3, 1.0f, 0.0f, progressBarColor);
    }
}
