package com.vivavu.lib.view.circular;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by yuja on 2014-04-17.
 */
public class CircularItemContainer extends FrameLayout {
    protected int index;
    protected double centerX;
    protected double centerY;
    protected double angleRadian;

    public CircularItemContainer(Context context) {
        super(context);
    }

    public CircularItemContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularItemContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getAngleRadian() {
        return angleRadian;
    }

    public void setAngleRadian(double angleRadian) {
        this.angleRadian = angleRadian;
    }

    public void layout(int r){
        layout((int)centerX - r, (int)centerY - r, (int)centerX + r, (int)centerY + r);
    }
}
