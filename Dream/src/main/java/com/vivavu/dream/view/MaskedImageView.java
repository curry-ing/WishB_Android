package com.vivavu.dream.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuja on 2014-05-04.
 */
public class MaskedImageView extends ImageView {
    private Bitmap srcBitmap;
    private Drawable foregroundDrawable;

    public MaskedImageView(Context context) {
        super(context);
    }

    public MaskedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas onDrawCanvas) {
        Bitmap mutableBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);

        if (srcBitmap != null) {
            canvas.drawBitmap(srcBitmap, 0, 0, paint);
        }

        if (getBackground() != null) {
            NinePatchDrawable background = (NinePatchDrawable) getBackground();
            background.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            background.draw(canvas);
        }

        final Drawable foreground = getForeground();
        if (foreground != null) {
            foreground.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());

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

        onDrawCanvas.drawBitmap(mutableBitmap, 0, 0, paint);
    }

    private Drawable getForeground() {
        return foregroundDrawable;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        srcBitmap = bitmap;

        invalidate();
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
}

