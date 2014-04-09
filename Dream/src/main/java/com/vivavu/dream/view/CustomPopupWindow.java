package com.vivavu.dream.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by yuja on 14. 3. 4.
 */
public class CustomPopupWindow extends PopupWindow {
    private View mContentView;

    public View getContentView() {
        return mContentView;
    }

    public CustomPopupWindow(Context context) {
        super(context);
    }

    public CustomPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPopupWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomPopupWindow() {
        super();
    }

    public CustomPopupWindow(View contentView) {
        super(contentView);
        this.mContentView = contentView;
    }

    public CustomPopupWindow(int width, int height) {
        super(width, height);
    }

    public CustomPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
        this.mContentView = contentView;
    }

    public CustomPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        this.mContentView = contentView;
    }

    public void hide(){
        if( isShowing()){
            dismiss();
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        setContentView(mContentView);
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        setContentView(mContentView);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        setContentView(mContentView);
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        setContentView(mContentView);
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
        this.mContentView = contentView;
    }
}
