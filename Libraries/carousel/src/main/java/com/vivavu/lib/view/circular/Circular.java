package com.vivavu.lib.view.circular;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.lib.R;

/**
 * Created by yuja on 2014-04-17.
 */
public class Circular extends FrameLayout implements GestureDetector.OnGestureListener{
    /**
     * Tag for a class logging
     */
    private static final String TAG = Circular.class.getSimpleName();

    /**
     * If logging should be inside class
     */
    private static final boolean localLOGV = false;

    private GestureDetector mGestureDetector;

    private ListAdapter adapter;
    private int mainItemRadius;
    private int subItemRadius;
    private int circleRadius;
    private int circleDiameter;
    private int displaySubItemCount;
    private float degree;
    private SparseArray<View> viewSparseArray;

    public Circular(Context context) {
        this(context, null);
    }

    public Circular(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Circular(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        viewSparseArray = new SparseArray<View>();
        // It's needed to make items with greater value of
        // z coordinate to be behind items with lesser z-coordinate
        setChildrenDrawingOrderEnabled(true);

        // Making user gestures available
        mGestureDetector = new GestureDetector(getContext(), this);
        mGestureDetector.setIsLongpressEnabled(true);

        // It's needed to apply 3D transforms to items
        // before they are drawn
        setStaticTransformationsEnabled(true);

        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();

        // Retrieve settings
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.Circular);
        circleRadius = arr.getDimensionPixelSize(R.styleable.Circular_circleRadius, viewHeight / 4);
        mainItemRadius = arr.getDimensionPixelSize(R.styleable.Circular_mainItemRadius, viewHeight / 4);
        subItemRadius = arr.getDimensionPixelSize(R.styleable.Circular_subItemRadius, mainItemRadius / 4);
        displaySubItemCount = arr.getInteger(R.styleable.Circular_displaySubItemCount, 6);

        // 기본 원의 반지름을 계산함
        if(circleRadius <= 0 ) {
            circleRadius = Math.min(viewWidth - (2 * subItemRadius), viewHeight - (mainItemRadius + subItemRadius)) / 2;
        }
        circleDiameter = circleRadius * 2;

        degree = 180.0f / (displaySubItemCount + 2);

        int numViews = displaySubItemCount + 1;
        for(int i = 0; i < numViews; i++){
            // Create some quick TextViews that can be placed.
            TextView v = new TextView(context);

            // Set a text and center it in each view.
            v.setText("View " + i);
            v.setGravity(Gravity.CENTER);
            int rgb = 0xffff0000 + (i* 70);
            v.setBackgroundColor(rgb);
            // Force the views to a nice size (150x100 px) that fits my display.
            // This should of course be done in a display size independent way.
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(subItemRadius, subItemRadius);
            // Place all views in the center of the layout. We'll transform them
            // away from there in the code below.
            lp.gravity = Gravity.CENTER;
            // Set layout params on view.
            v.setLayoutParams(lp);

            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "aaaaa", Toast.LENGTH_SHORT).show();
                }
            });

            viewSparseArray.put(i, v);
        }
        for(int i = numViews-1; i >= 0; i--)
        {
            if(i == displaySubItemCount / 2){
                continue;
            }

            TextView v = (TextView) viewSparseArray.get(i);

            // Calculate the angle of the current view. Adjust by 90 degrees to
            // get View 0 at the top. We need the angle in degrees and radians.
            float angleDeg = i * 180.0f / numViews - 180.0f + degree/2;
            float angleRad = (float)(angleDeg * Math.PI / 180.0f);
            // Calculate the position of the view, offset from center (300 px from
            // center). Again, this should be done in a display size independent way.
            v.setTranslationX(circleRadius * (float)Math.cos(angleRad));
            v.setTranslationY(circleRadius * (float)Math.sin(angleRad));
            // Set the rotation of the view.
            //v.setRotation(angleDeg + 90.0f);

            this.addView(v);
        }

        TextView v = new TextView(context);
        v.setBackgroundColor(0xFF00FF00);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(2*mainItemRadius, 2*mainItemRadius);
        // Place all views in the center of the layout. We'll transform them
        // away from there in the code below.
        lp.gravity = Gravity.CENTER;
        // Set layout params on view.
        v.setLayoutParams(lp);
        //v.setTranslationX(circleRadius);
        v.setTranslationY(circleRadius);
        v.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        this.addView(v);
     }

    protected void calcPosition(View v){

    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    float oldX;
    float distanceX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean returnValue = mGestureDetector.onTouchEvent(event);
        Log.v(TAG, String.valueOf(returnValue));
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // handle down
            oldX = event.getX ();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // handle move
            distanceX = event.getX() - oldX; // more accurate
            View childAt = this.getChildAt(this.getChildCount() - 1);
            childAt.setTranslationX(event.getX());
            childAt.setTranslationY(event.getY());
            //invalidate();
        }

        return returnValue;
    }

    //// GestureDetector.OnGestureListener implemets start
    @Override
    public boolean onDown(MotionEvent e) {
        Log.v(TAG, "onDown :");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.v(TAG, "onShowPress :");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.v(TAG, "onSingleTapUp :");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.v(TAG, "onScroll :");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.v(TAG, "onLongPress :");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.v(TAG, "onLongPress :");
        return false;
    }
    //// GestureDetector.OnGestureListener implemets end
}
