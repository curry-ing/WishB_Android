package com.vivavu.lib.view.strechable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;

import java.util.LinkedList;

/**
 * 이소스는 http://tortuga.tistory.com/1 에서 제공하는 것을 가져온것입니다.
 * 참조를 위해 프로젝트에 포함합니다.
 * AdapterView를 이용한 커스텀 리스트
 */
public class StretchableList extends AdapterView {

    /* 상수 */
    private static final int ADD_VIEW_AT_TOP = 1;				// view를 현재 리스트 화면 최상위에 추가
    private static final int ADD_VIEW_AT_BOTTOM = 2;		// view를 현재 리스트 화면 최하단에 추가

    private static final float SCALE_FACTOR = 0.05f;

    private static final int UNIT_PIXELS_PER_MILLI = 1;		// Velocity trackter 의 시간 단위 (MILLI SECOND 단위)
    private static final float FRICTION_FACTOR = 0.9f;			// 화면 업데이트 될 때 마다 속도를 늦춰주는 상수
    private static final float VELOCITY_MIN_THRESHOLD = 0.5f;	// 속도가 이 값 이하일 경우 스크롤 중단
    private static final float DISTANCE_CONVERT_FACTOR = 1f;	// 속도로부터 스크롤 거리를 계산할 때 곱하는 상수, 값이 클 수록 스크롤 양이 커짐
    private static final long REDRAW_DELAY_IN_MILLI = 20;		// 다음 화면 갱신을 위한 대기시간 (밀리초)


    /* 어댑터 */
    private Adapter mAdapter = null;

    /* 화면 갱신을 위한 파라미터 */
    private boolean isDrawing = false;				// 화면 drawing 중에 touch 업데이트로 onLayout() 중복 호출을 방지
    private int mScreenTopOffset = 0;				// 전체 화면 길이에서 현재 화면이 시작되는 시작점
    private int mPrevScreenTopOffset = 0;		// 이전 화면의 시작점을 기억
    private int mItemTopOffset = 0;					// 전체 화면 길이에서 화면에 보이는 첫 번째 아이템이 시작되는 시작점
    private int mFirstItemIndex = 0;					// 화면에 보이는 첫 아이템의 index
    private int mLastItemIndex = 0;					// 화면에 보이는 마지막 아이템의 index

    /* 애니메이션 효과 */
    private Matrix mMatrix;
    private Paint mPaint;

    /* 터치 컨트롤 */
    private float mStartX = 0;							// 터치 이벤트 시작된 기준 좌표 X
    private float mStartY = 0;							// 터치 이벤트 시작된 기준 좌표 Y
    private boolean isProcessed = false;				// 특정 event 처리 후 ACTION_UP 까지 다른 이벤트 처리가 필요 없을 때 설정하는 flag

    private VelocityTracker mVelocityTracker;	// 드래그 속도 추척
    private Runnable mScrollingRunnable;		// 드래그 이후에 자동 스크롤을 구현

    private float mVelocity = 0f;			// 드래그 속도 측정값
    private long mPrevTime = 0;			// 이전 스크롤 처리 시간

    /* 사용 후 제거되는 view를 저장하는 cache */
    private final LinkedList<View> mViewCache = new LinkedList<View>();		// 삭제한 뷰를 저장하는 캐시. getView() 를 호출할 때 convertView 파라미터로 사용




    /********************************************************
     * 생성자
     ********************************************************/
    public StretchableList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void setSelection(int position) {
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        removeAllViewsInLayout();	// 모든 자식 뷰 제거
        requestLayout();					// 레이아웃 위치 계산, onLayout() 호출
    }



    /****************************************************
     * 사용자 touch, adapter data 변화에 따라 layout이 변경되는 시작점. 
     ***************************************************/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mAdapter == null)
            return;
        isDrawing = true;					// drawing flag 설정

        if(mScreenTopOffset < 0) { 			// 스크린이 최 상단을 벗어나지 못하게
            mScreenTopOffset = 0;
        }

        redrawLayout();

        // 스크린이 최 하단을 벗어나지 못하게
        if( mPrevScreenTopOffset < mScreenTopOffset &&
                mLastItemIndex == mAdapter.getCount() - 1 &&
                getChildAt(getChildCount()-1).getBottom() < getHeight() ) {

            mPrevScreenTopOffset = mScreenTopOffset;
            mScreenTopOffset -= ( getHeight() - getChildAt(getChildCount()-1).getBottom() );

            if(mScreenTopOffset < 0) { 			// 다시 스크린이 최 상단을 벗어나지 못하게
                mScreenTopOffset = 0;
            }

            redrawLayout();
        }

        invalidate();								// 화면 업데이트

        mPrevScreenTopOffset = mScreenTopOffset;
        isDrawing = false;					// drawing flag 해제
    }

    /**
     * 화면에 표시될 item을 추가/삭제, item의 위치를 설정하는 메인 루틴
     */
    private void redrawLayout() {
        if (getChildCount() == 0)			// 아이템이 없는 경우 변수 초기화 
        {
            mScreenTopOffset = 0;
            mItemTopOffset = 0;
            mFirstItemIndex = 0;
            mLastItemIndex = 0;
        }
        else {										// 아이템이 있는 경우 이동한 화면에서 보이지 않는 아이템 삭제부터 
            removeNonVisibleViews();
        }

        insertListItems();						// 이동한 화면에 필요한 아이템을 채움
        layoutItems();							// 자식 뷰들의 화면 내 위치를 재설정 (child.layout() 으로 조정)
    }

    /**
     * 새롭게 설정된 리스트 영역에서 보이지 않는 아이템 삭제
     */
    private void removeNonVisibleViews() {
        int movedDistance = mScreenTopOffset - mPrevScreenTopOffset;
        int childCount = getChildCount();

        if(movedDistance > 0 && childCount > 1) {		// 위로 드래그, 화면 위에서 부터 가려지는 아이템을 삭제
            View child = getChildAt(0);

            while( child != null && mItemTopOffset + child.getMeasuredHeight() < mScreenTopOffset ) {
                removeViewInLayout(child);
                childCount--;
                mViewCache.addLast(child);			// 삭제한 아이템은 캐시에 저장
                mItemTopOffset += child.getMeasuredHeight();
                mFirstItemIndex++;

                if(childCount > 1)
                    child = getChildAt(0);
                else
                    child = null;
            }
        }
        else if(movedDistance < 0 && childCount > 1) {	// 아래로 드래그, 화면 아래에서 부터 가려지는 아이템을 삭제
            View child = getChildAt(childCount - 1);

            while( child != null && child.getTop() > getHeight() + movedDistance ) {
                removeViewInLayout(child);
                childCount--;
                mViewCache.addLast(child);			// 삭제한 아이템은 캐시에 저장
                mLastItemIndex--;

                if(childCount > 1)
                    child = getChildAt(childCount - 1);
                else
                    child = null;
            }
        }
    }

    /**
     * 새롭게 설정된 리스트 영역에 필요한 아이템을 채움
     */
    private void insertListItems() {
        int movedDistance = mScreenTopOffset - mPrevScreenTopOffset;
        int childCount = getChildCount();

        if( movedDistance > 0 && childCount > 0 ) {		// 위로 드래그, 화면이 아래로 내려옴. 아래쪽에 신규 아이템을 채움.
            View child = getChildAt(getChildCount()-1);
            int bottom = child.getBottom();
            while( getHeight() + movedDistance >  bottom
                    && mLastItemIndex < mAdapter.getCount() - 1) {
                mLastItemIndex++;
                child = mAdapter.getView(mLastItemIndex, getCachedView(), this);
                addViewAndMeasure(child, ADD_VIEW_AT_BOTTOM);
                bottom += child.getMeasuredHeight();
            }
        }
        else if( movedDistance < 0 && childCount > 0 ) {	// 아래로 드래그, 화면이 위로 올라감. 위에 신규 아이템을 채움.
            View child = null;
            while( mScreenTopOffset <  mItemTopOffset
                    && mFirstItemIndex > 0 ) {
                mFirstItemIndex--;
                child = mAdapter.getView(mFirstItemIndex, getCachedView(), this);
                addViewAndMeasure(child, ADD_VIEW_AT_TOP);
                mItemTopOffset -= child.getMeasuredHeight();
            }
        }
        else {
            if(childCount < 1) {		// 리스트 초기화, 가장 상위의 화면을 구성
                View child = null;
                int bottom = 0;
                int index = 0;
                while( getHeight() >  bottom
                        && index < mAdapter.getCount() ) {
                    child = mAdapter.getView(index, getCachedView(), this);
                    addViewAndMeasure(child, ADD_VIEW_AT_BOTTOM);
                    bottom += child.getMeasuredHeight();
                    mLastItemIndex = index;
                    index++;
                }
            }
        }
    }

    private View getCachedView() {			// 캐시에서 아이템을 하나씩 가져옴. getView()를 호출할 때 convertView 파라미터로 사용.
        if (mViewCache.size() != 0) {
            return mViewCache.remove();
        }
        return null;
    }

    /**
     * child view 설정, measuring (부모와 자식 view 간의 size 협상)
     *
     * @param child 			리스트에 추가할 child view
     * @param direction 	Child view가 붙을 방향
     */
    private void addViewAndMeasure(View child, int direction) {		// child view를 리스트에 추가하고 Measuring 수행 
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        int index = 0;
        if(direction == ADD_VIEW_AT_TOP) {
            index = 0;
        } else {
            index = -1;
        }
        child.setDrawingCacheEnabled(true);			// drawing cache 사용. drawChild() 참고.
        addViewInLayout(child, index, params, true);		// (View child, int index, LayoutParams params, boolean preventRequestLayout)
        // If index is negative, it means put it at the end of the list.
        // if preventRequestLayout is true, calling this method will not trigger a layout request on child
        int itemWidth = getWidth();
        child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);		// 가로 화면은 리스트에 꽉 차게, 세로는 child view의 크기대로
    }


    /**
     * 자식 뷰가 표시될 화면 내 위치 설정
     * 자식 뷰 크기가 확정되어야 하므로 measuring 과정이 선행 되어야 함.
     */
    private void layoutItems() {
        int top = mItemTopOffset - mScreenTopOffset;

        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int left = (getWidth() - width) / 2;

            child.layout(left, top, left + width, top + height);
            top += height;
        }
    }




    /****************************************************
     * 자식 뷰를 그리는 루틴. 자식 뷰에 특별한 효과를 넣고 싶다면 이 메소드를 상속해서 수정
     * Drawing cache에서 자식 뷰의 bitmap을 얻어옴
     * child.setDrawingCacheEnabled(true) 설정이 되어 있어야 bitmap 추출 가능
     ***************************************************/
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final Bitmap bitmap = child.getDrawingCache();

        // 비트맵이 추출되지 않았거나, 속도가 임계치 이하인 경우는 효과를 주지 않음.
        if (bitmap == null
                || ( mVelocity < VELOCITY_MIN_THRESHOLD && mVelocity > VELOCITY_MIN_THRESHOLD * -1 ) ) {
            // drawing cache에서 bitmap을 얻지 못하면 super.drawChild() 호출
            return super.drawChild(canvas, child, drawingTime);
        }

        // 계산에 필요한 파라미터 생성
        final int top = child.getTop();
        final int left = child.getLeft();
        int centerX = child.getMeasuredWidth()/2;
        int centerY = child.getMeasuredHeight()/2;

        // 속도가 빠를 수도록 scale 값이 작아지도록  설정. scale factor 를 통해 조절 가능
        // scale factor 가 작을수록 이미지 축소량이 작아짐 
        float scale = 1 / ( 1 + Math.abs(mVelocity) / VELOCITY_MIN_THRESHOLD * SCALE_FACTOR  );

        // Matrix 인스턴스 생성. 이미 생성되어 있다면 reset
        if (mMatrix == null) {
            mMatrix = new Matrix();
        } else {
            mMatrix.reset();
        }

        // Matrix를 이용한 이미지 변환 커맨드 설정
        mMatrix.preTranslate(-centerX, -centerY); 	// 이미지 좌 상단으로 원점 설정 
        mMatrix.postScale(scale, scale);		// 이미지 사이즈 변경 
        mMatrix.postTranslate(left + centerX, top + centerY);		// 아이템의 원래 위치로 이동 

        // Paint 설정
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setAlpha(0xFF);
        }

        // Matrix 에 따라 이미지를 캔버스에 그림.
        canvas.drawBitmap(bitmap, mMatrix, mPaint);

        return false;
    }



    /****************************************************
     * 터치 이벤트 리스너
     ***************************************************/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getChildCount() == 0) {
            return false;
        }

        processTouch(event);			// 터치 동작을 분석 후 드래그 동작 수행

        return true;
    }

    /**
     * Child view에서 터치 이벤트를 사용하더라도 리스트에서 터치 이벤트를 계속 사용할 수 있도록 함.
     */
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        processTouch(event);			// 터치 동작을 분석 후 드래그 동작 수행
        return false;
    }


    /**
     * 터치 이벤트를 처리하는 메인 루틴
     */
    private void processTouch(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            startTouch(event);
        }
        else if(event.getAction()==MotionEvent.ACTION_MOVE && !isProcessed) {
            mVelocityTracker.addMovement(event);
            scrollList(mStartY - event.getY());

            mStartX = event.getX();		// 요청을 처리했으므로 터치 시작점 재설정
            mStartY = event.getY();
        }
        else if(event.getAction()==MotionEvent.ACTION_UP && !isProcessed) {
            endTouch(event);
        }
    }

    /**
     * 터치 이벤트 시작 루틴
     */
    private void startTouch(MotionEvent event) {
        // 기존 동작중인 스크롤용 Runnable이 더 이상 동작하지 않도록 설정
        removeCallbacks(mScrollingRunnable);
        mVelocity = 0f;

        // 터치 시작점 저장
        mStartX = event.getX();
        mStartY = event.getY();
        isProcessed = false;

        // VelocityTracker 초기화
        mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
    }

    /**
     * 스크롤 된 화면으로 update 요청
     */
    private void scrollList(float offset) {
        if( !isDrawing ) {
            mScreenTopOffset += offset;
            requestLayout();
        }
    }


    /**
     * 터치 이벤트 종료 루틴
     * 속도를 측정하고 자동 스크롤이 되도록 Runnable 생성
     */
    private void endTouch(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(UNIT_PIXELS_PER_MILLI);
        mVelocity = mVelocityTracker.getYVelocity();

        mVelocityTracker.recycle();
        mVelocityTracker = null;

        mPrevTime = AnimationUtils.currentAnimationTimeMillis();

        // 자동으로 스크롤이 되도록 Runnable 생성
        if (mScrollingRunnable == null) {
            mScrollingRunnable = new Runnable() {
                public void run() {
                    long time = AnimationUtils.currentAnimationTimeMillis();
                    float distance = mVelocity * (time - mPrevTime) * DISTANCE_CONVERT_FACTOR;

                    scrollList(distance*-1);		// 리스트 화면 업데이트 요청, 드래그 방향과 스크린 이동방향이 반대이므로 부호를 역전 시킴

                    mVelocity = mVelocity * FRICTION_FACTOR;	// 속도를 줄임.
                    mPrevTime = time;				// 시간 업데이트
                    if (mVelocity > VELOCITY_MIN_THRESHOLD || mVelocity < VELOCITY_MIN_THRESHOLD * -1) {
                        postDelayed(this, REDRAW_DELAY_IN_MILLI);
                    }
                }
            };
        }

        if (mVelocity > VELOCITY_MIN_THRESHOLD || mVelocity < VELOCITY_MIN_THRESHOLD * -1) {
            post(mScrollingRunnable);
        }
    }
}
