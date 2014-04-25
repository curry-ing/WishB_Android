package com.vivavu.lib.view.circular;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.vivavu.lib.R;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by yuja on 2014-04-23.
 */
public class SemiCircularList extends AdapterView {
    public static final String TAG = SemiCircularList.class.getSimpleName();
    /* 상수 */
    private static final int ADD_VIEW_AT_FRONT = 1;				// view를 현재 리스트 화면 최상위에 추가
    private static final int ADD_VIEW_AT_REAR = 2;		// view를 현재 리스트 화면 최하단에 추가

    /* 어댑터 */
    private CircularAdapter mAdapter = null;

    /* 리스너 */
    private OnMainItemChangedListener onMainItemChangedListener;

    /* 화면 갱신을 위한 파라미터 */
    private boolean isDrawing = false;				// 화면 drawing 중에 touch 업데이트로 onLayout() 중복 호출을 방지
    private int mFirstItemPosition = 0;					// 화면에 보이는 첫 아이템의 index
    private int mLastItemPosition = 0;					// 화면에 보이는 마지막 아이템의 index
    private int mMainItemPosition = 0;  //메인 아이템의 index

    /* 터치 컨트롤 */
    private double mStartX = 0;							// 터치 이벤트 시작된 기준 좌표 X
    private double mStartY = 0;							// 터치 이벤트 시작된 기준 좌표 Y
    private boolean isProcessed = false;				// 특정 event 처리 후 ACTION_UP 까지 다른 이벤트 처리가 필요 없을 때 설정하는 flag

    /* 사용 후 제거되는 view를 저장하는 cache */
    private final LinkedList<View> mViewCache = new LinkedList<View>();		// 삭제한 뷰를 저장하는 캐시. getView() 를 호출할 때 convertView 파라미터로 사용

    /* 원형으로 사용시에 필요한 변수*/
    private int mainItemRadius;
    private int subItemRadius;
    private int circleRadius;
    private int displaySubItemCount;
    private double mMovedRadian;
    private double degree;
    private double offsetDegree = 90.0;
    private double mChangeItemRadianThreshold;
    private int roundedCenterX;
    private int roundedCenterY;
    private int circleBackground;
    private HashMap<Integer, Double> mappingTable;

    public SemiCircularList(Context context) {
        this(context, null);
    }

    public SemiCircularList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SemiCircularList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //setStaticTransformationsEnabled(true);
        // Retrieve settings
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.Circular);
        circleRadius = arr.getDimensionPixelSize(R.styleable.Circular_circleRadius, 250);
        mainItemRadius = arr.getDimensionPixelSize(R.styleable.Circular_mainItemRadius, 120);
        subItemRadius = arr.getDimensionPixelSize(R.styleable.Circular_subItemRadius, mainItemRadius / 2);
        displaySubItemCount = arr.getInteger(R.styleable.Circular_displaySubItemCount, 6);

        circleBackground = arr.getResourceId(R.styleable.Circular_circleBackground, -1);
        degree = 360.0 / displaySubItemCount;
        mChangeItemRadianThreshold = Math.toRadians(degree);
        Log.v(TAG, String.format("circleRadius:%d, mainItemRadius:%d, subItemRadius:%d, displaySubItemCount:%d", circleRadius, mainItemRadius, subItemRadius, displaySubItemCount));

        mappingTable = new HashMap<Integer, Double>();
        for(int index = 0; index < displaySubItemCount; index++){
            double angleDeg = adjustDegree(index * degree + offsetDegree);
            double angleRad = adjustRadian(Math.toRadians(angleDeg ));//Math함수에서는 radian을 기준으로 입력을 받아서 60분법에 의한 각도를 변환
            mappingTable.put(index, angleRad);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();

        // 기본 원의 반지름을 계산함
        if(circleRadius <= 0 ) {
            circleRadius = Math.min(viewWidth - (2 * subItemRadius), viewHeight - (mainItemRadius + subItemRadius)) / 2;
        }

        roundedCenterX = viewWidth / 2;
        roundedCenterY = circleRadius + subItemRadius;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.mAdapter = (CircularAdapter) adapter;
        removeAllViewsInLayout(); //모든 자식 뷰 제거
        requestLayout();    // 레이아웃 위치 계산, onLayout()호출
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {

    }

    @Override
    public CircularItemContainer getChildAt(int index) {
        return (CircularItemContainer) super.getChildAt(index);
    }

    /****************************************************
     * 사용자 touch, adapter data 변화에 따라 layout이 변경되는 시작점.
     ***************************************************/
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.v(TAG, String.valueOf(changed));
        if (mAdapter == null)
            return;
        isDrawing = true;					// drawing flag 설정

        redrawLayout();

        invalidate();								// 화면 업데이트

        isDrawing = false;					// drawing flag 해제
    }

    /**
     * 화면에 표시될 item을 추가/삭제, item의 위치를 설정하는 메인 루틴
     */
    private void redrawLayout() {
        int count = getMovedItemCount();
        if (getChildCount() == 0)			// 아이템이 없는 경우 변수 초기화
        {
            mFirstItemPosition = 0;
            mLastItemPosition = 0;
        }
        else {										// 아이템이 있는 경우 이동한 화면에서 보이지 않는 아이템 삭제부터
            //removeNonVisibleViews(count);
        }

        //insertListItems(count);						// 이동한 화면에 필요한 아이템을 채움
        insertListItems(0);						// 이동한 화면에 필요한 아이템을 채움
        layoutItems();							// 자식 뷰들의 화면 내 위치를 재설정 (child.layout() 으로 조정)
    }

    private int getMovedItemCount(){

        int movedItemCount = (int) (mMovedRadian / mChangeItemRadianThreshold);
        Log.v(TAG, String.format("getMovedItemCount by Radian : %d", movedItemCount));
        //return direction * movedItemCount;
        return movedItemCount;
    }
    /**
     * 새롭게 설정된 리스트 영역에서 보이지 않는 아이템 삭제
     * @param movedItemCount
     */
    private void removeNonVisibleViews(int movedItemCount) {
        int childCount = getChildCount();
        //int threshold = Math.min(displaySubItemCount, mAdapter.getCount());
        int threshold = mAdapter.getCount();
        if(movedItemCount > 0 && childCount > 1) {		// 위로 드래그, 화면 위에서 부터 가려지는 아이템을 삭제
            CircularItemContainer child = getChildAt(0);
            for(int index = 1; index <= movedItemCount; index++){
                removeViewInLayout(child);
                childCount--;
                mViewCache.addLast(child);			// 삭제한 아이템은 캐시에 저장
                mFirstItemPosition = ++mFirstItemPosition % threshold;

                if(childCount > 1) {
                    child = getChildAt(0);
                } else {
                    child = null;
                }
            }
        } else if(movedItemCount < 0 && childCount > 1) {	// 아래로 드래그, 화면 아래에서 부터 가려지는 아이템을 삭제
            // direction을 따로 구해야할까???
            CircularItemContainer child = getChildAt(childCount - 1);
            int absMovedItemCount = Math.abs(movedItemCount);
            for(int index = 1; index <= absMovedItemCount; index++){
                removeViewInLayout(child);
                childCount--;
                mViewCache.addLast(child);			// 삭제한 아이템은 캐시에 저장
                mFirstItemPosition = ((--mFirstItemPosition > -1 ? mFirstItemPosition : mFirstItemPosition + threshold)) % threshold;

                if(childCount > 1) {
                    child = getChildAt(childCount - 1);
                } else {
                    child = null;
                }
            }
        }
    }

    /**
     * 새롭게 설정된 리스트 영역에 필요한 아이템을 채움
     * @param movedItemCount
     */
    private void insertListItems(int movedItemCount) {
        int childCount = getChildCount();
        //int threshold = Math.min(displaySubItemCount, mAdapter.getCount());
        int threshold = mAdapter.getCount();
        if( movedItemCount > 0 && childCount > 0 ) {
            // 위로 드래그, 화면이 아래로 내려옴. 아래쪽에 신규 아이템을 채움.
            // 기존것은 화면의 사이즈만으로 이동하지만 원형 리스트의 경우는 일정 이동량을 아이템 이동량으로 변환해야함
            // 이동량을 정의하고 이동량의 변화에 따라 일정 이동량에 따라 아이템의 이동을 결정하는 알고리즘이 필요하다.
            CircularItemContainer child = getChildAt(getChildCount() - 1);
            while( movedItemCount > 0 ) {
                mLastItemPosition = ++mLastItemPosition % threshold;
                child = mAdapter.getView(mLastItemPosition, getCachedView(), this);
                addViewAndMeasure(child, ADD_VIEW_AT_REAR);
                movedItemCount--;
            }

        } else if( movedItemCount < 0 && childCount > 0 ) {	// 아래로 드래그, 화면이 위로 올라감. 위에 신규 아이템을 채움.
            CircularItemContainer child = null;
            while( movedItemCount < 0 ) {
                mLastItemPosition = ((--mLastItemPosition > -1 ? mLastItemPosition : mLastItemPosition + threshold)) % threshold;
                child = mAdapter.getView(mFirstItemPosition, getCachedView(), this);
                addViewAndMeasure(child, ADD_VIEW_AT_FRONT);
                movedItemCount++;
            }
        } else {
            //초기에 입력하는 아이템 수에 따라서 원형에 보여주는 아이템 수가 결정된다.
            if(childCount < 1) {		// 리스트 초기화, 가장 상위의 화면을 구성
                CircularItemContainer child = null;
                int index = 0;
                while( index < Math.min(displaySubItemCount, mAdapter.getCount()) ) {
                    child = mAdapter.getView(index, getCachedView(), this);
                    initChildLayout(child, index);
                    addViewAndMeasure(child, ADD_VIEW_AT_REAR);
                    mLastItemPosition = index;
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
            params = new ViewGroup.LayoutParams(subItemRadius*2, subItemRadius*2);
        }
        int index = 0;
        if(direction == ADD_VIEW_AT_FRONT) {
            index = 0;
        } else {
            index = -1;
        }
        child.setDrawingCacheEnabled(true);			// drawing cache 사용. drawChild() 참고.
        addViewInLayout(child, index, params, true);		// (View child, int index, LayoutParams params, boolean preventRequestLayout)
        // If index is negative, it means put it at the end of the list.
        // if preventRequestLayout is true, calling this method will not trigger a layout request on child
        int itemWidth = getWidth();
        int itemHeight = getHeight();
        child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.EXACTLY | itemHeight);		// 가로 화면은 리스트에 꽉 차게, 세로는 child view의 크기대로
    }


    /**
     * 전체 화면안에서 아이템들을 뿌리므로 각 아이템의 크기와 중심점을 기준으로
     * 뷰 가운데 영역을 중심으로 하는 원을 그려 균등분배한다.
     * 시작은
     */
    private void layoutItems() {
        for (int index = 0; index < getChildCount(); index++) {
            CircularItemContainer child = getChildAt(index);
            moveByDelta(child, mMovedRadian);
        }
    }

    private void initChildLayout(CircularItemContainer child, int index){
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(subItemRadius*2, subItemRadius*2);
        child.setLayoutParams(lp);
        moveToIndex(child, index);
    }

    private void scrollToSlot(){
        if(getChildCount() > 0){
            for (int index = 0; index < getChildCount(); index++){
                CircularItemContainer child = getChildAt(index);
                int childPosition = getIndexFromRadian(child.getAngleRadian());
                moveToIndex(child, childPosition);
            }
        }
    }

    private Double getStdRadian(double angleRad){
        return mappingTable.get(getIndexFromRadian(angleRad));
    }

    private Double getRadianFromIndex(int index){
        return mappingTable.get(index%displaySubItemCount);
    }

    private int getIndexFromRadian(double angleRad) {
        //초기에 index 0 인 아이템을 할당한 곳은 offset 만큼 더한 곳임. 인덱스를 역으로 계산할려면 그만큼 빼줘야함...
        /*double angleDeg = adjustDegree(index * degree + offsetDegree);
        double angleRad = adjustRadian(Math.toRadians(angleDeg ));//Math함수에서는 radian을 기준으로 입력을 받아서 60분법에 의한 각도를 변환*/

        int index = ((int) Math.round(adjustRadian(angleRad - Math.toRadians(offsetDegree)) / mChangeItemRadianThreshold)) % displaySubItemCount;
        return index;
    }

    private void inPosition(CircularItemContainer child){
        int index = child.getIndex();
        moveToIndex(child, index);
    }

    private void moveToIndex(CircularItemContainer child, int index) {
        double angleRad = getRadianFromIndex(index);
        moveTo(child, angleRad);
    }

    private void moveByDelta(CircularItemContainer child, double delta) {
        double angleRad = adjustRadian(child.getAngleRadian() + delta);
        moveTo(child, angleRad);
    }

    private void moveTo(CircularItemContainer child, double angleRad) {
        int index = getIndexFromRadian(angleRad);
        double childCenterX = roundedCenterX + circleRadius * (float)Math.cos( angleRad);
        double childCenterY = roundedCenterY + circleRadius * (float)Math.sin( angleRad);
        //child.setIndex(index);
        child.setCenterX(childCenterX);
        child.setCenterY(childCenterY);
        child.setAngleRadian(angleRad);
        child.layout(subItemRadius);
    }

    private int calcQuadrant(Double radian){
        double x = Math.cos(radian);
        double y = Math.sin(radian);

        if(x > 0 && y >= 0){ //0 <= r < 90
            return 1;
        } else if (x <= 0 && y > 0){//90 <= r < 180
            return 2;
        } else if( x < 0 && y <= 0){//180 <= r < 270
            return 3;
        } else { //if( x >= 0 && y < 0){//270 <= r < 0
            return 4;
        }
    }

    private int calcSector(Double radian){
        double x = Math.cos(radian);
        double y = Math.sin(radian);

        if(x > 0 && y >= 0){ //0 <= r < 90
            return 1;
        } else if (x <= 0 && y > 0){//90 <= r < 180
            return 1;
        } else if( x < 0 && y <= 0){//180 <= r < 270
            return -1;
        } else { //if( x >= 0 && y < 0){//270 <= r < 0
            return -1;
        }
    }

    private double adjustDegree(double degree){
        if( degree < 0.0 ){
            return degree + 360.0;
        } else if( degree >= 360.0){
            return degree - 360.0;
        } else {
            return degree;
        }
    }

    private double adjustRadian(double radian){
        if( radian < 0.0 ){
            return radian + Math.toRadians(360.0);
        } else if( radian >= Math.toRadians(360.0)){
            return radian - Math.toRadians(360.0);
        } else {
            return radian;
        }
    }

    private double getRadian(double pointX, double pointY, double centerX, double centerY){
        double radian = Math.atan2(pointY - centerY, pointX - centerX);
        return radian;
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
            scrollList(event);
        }
        else if(event.getAction()==MotionEvent.ACTION_UP && !isProcessed) {
            calcMovement(event);
            endTouch(event);
            scrollToSlot();
        }
    }
    /**
     * 터치 이벤트 시작 루틴
     */
    private void startTouch(MotionEvent event) {
        // 터치 시작점 저장
        mStartX = event.getX();
        mStartY = event.getY();
        isProcessed = false;
    }

    private void scrollList(MotionEvent event){
        if( !isDrawing ) {
            calcMovement(event);
            requestLayout();
        }
    }

    /**
     * 터치 이벤트 종료 루틴
     * 속도를 측정하고 자동 스크롤이 되도록 Runnable 생성
     */
    private void endTouch(MotionEvent event) {

    }

    private void calcMovement(MotionEvent event){
        double startRad = getRadian(mStartX, mStartY, roundedCenterX, roundedCenterY);
        double endRad = getRadian(event.getX(), event.getY(), roundedCenterX, roundedCenterY);
        mMovedRadian = endRad - startRad;
        Log.v(TAG, String.format("mMovedRadian : %f , %f , startRad:%f, endRad:%f", mMovedRadian, Math.toDegrees(mMovedRadian), startRad, endRad));
        mStartX = event.getX();		// 요청을 처리했으므로 터치 시작점 재설정
        mStartY = event.getY();
    }

    /**
     * http://cholol.tistory.com/92
     * 여러개의 자식 뷰들의 형태를 변환(transform)해주는 코드입니다. 이 메소드를 사용하기 위해서는 setStaticTransformationsEnabled()메소드를 true로 설정해줘야 합니다.
     * 이 부분에서 최종적으로 보여지는 자식 뷰의 모습을 제어함.
     * @param child
     * @param transformation
     * @return
     */
    @Override
    protected boolean getChildStaticTransformation(View child, Transformation transformation) {
        CircularItemContainer view = (CircularItemContainer) child;
        //Log.v(TAG, String.format("child view #%d", view.getIndex()));
        Camera mCamera = new Camera();
        transformation.clear();
        transformation.setTransformationType(Transformation.TYPE_MATRIX);

        // Center of the view
        float centerX = (float)getWidth()/2, centerY = (float)getHeight()/2;

        // Save camera
        mCamera.save();

        // Translate the item to it's coordinates
        final Matrix matrix = transformation.getMatrix();

        mCamera.translate(0.0f, 0.0f, 0.0f);

        // Align the item
        mCamera.getMatrix(matrix);

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

        // Restore camera
        mCamera.restore();

        //http://code.google.com/p/android/issues/detail?id=35178
        child.invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(circleBackground >= 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), circleBackground);
            Bitmap sb = Bitmap.createScaledBitmap(bitmap, circleRadius*2, circleRadius*2, false);
            canvas.drawBitmap(sb, roundedCenterX - circleRadius, roundedCenterY -circleRadius, null);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final Bitmap bitmap = child.getDrawingCache();

        if(! (child instanceof CircularItemContainer) && bitmap == null) {
            return super.drawChild(canvas, child, drawingTime);
        }
        CircularItemContainer view = (CircularItemContainer) child;

        // 계산에 필요한 파라미터 생성
        // 계산에 필요한 파라미터 생성
        final int top = child.getTop();
        final int left = child.getLeft();
        int centerX = child.getWidth()/2;
        int centerY = child.getHeight()/2;

        // 속도가 빠를 수도록 scale 값이 작아지도록  설정. scale factor 를 통해 조절 가능
        // scale factor 가 작을수록 이미지 축소량이 작아짐

        float scale = 1.0f;
        if( getIndexFromRadian(view.getAngleRadian()) < 1){
            scale = 2.0f;
        }
        // Matrix 인스턴스 생성. 이미 생성되어 있다면 reset
        Matrix mMatrix = null;
        if (mMatrix == null) {
            mMatrix = new Matrix();
        } else {
            mMatrix.reset();
        }

        // Matrix를 이용한 이미지 변환 커맨드 설정
        mMatrix.preTranslate(-centerX, -centerY); 	// 이미지의 중심을 0,0으로 변경
        mMatrix.postScale(scale, scale);		// 이미지 사이즈 변경
        mMatrix.postTranslate(left + (centerX), top + (centerY));	// 아이템의 원래 위치로 이동
        //mMatrix.postTranslate((float)view.getCenterX() + (centerX/scale), (float)view.getCenterY() + (centerY/scale));	// 아이템의 원래 위치로 이동

        // Paint 설정
        Paint mPaint = null;
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

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        CircularItemContainer child = getChildAt(i);

        return super.getChildDrawingOrder(childCount, i);

    }

    public interface OnMainItemChangedListener{
        public void onMainItemChanged(int position);
    }

    public OnMainItemChangedListener getOnMainItemChangedListener() {
        return onMainItemChangedListener;
    }

    public void setOnMainItemChangedListener(OnMainItemChangedListener onMainItemChangedListener) {
        this.onMainItemChangedListener = onMainItemChangedListener;
    }


}