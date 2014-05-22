package com.vivavu.lib.view.circular;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.vivavu.lib.R;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by yuja on 2014-04-23.
 */
public class SemiCircularList extends AdapterView implements GestureDetector.OnGestureListener {
    public static final String TAG = SemiCircularList.class.getSimpleName();
    /* 상수 */
    private static final int ADD_VIEW_AT_FRONT = 1;				// view를 현재 리스트 화면 최상위에 추가
    private static final int ADD_VIEW_AT_REAR = 2;		// view를 현재 리스트 화면 최하단에 추가
    public static final int INVALID_ITEM_INDEX = -1;

    /* 어댑터 */
    private CircularAdapter mAdapter = null;

    /* 리스너 */
    private OnMainItemChangedListener onMainItemChangedListener;
    private OnRotateEndedListener onRotateEndedListener;
    private OnMainItemSelectedListener onMainItemSelectedListener;

    /* 화면 갱신을 위한 파라미터 */
    private boolean isDrawing = false;				// 화면 drawing 중에 touch 업데이트로 onLayout() 중복 호출을 방지
    private boolean isDrag = false;
    private int mFirstItemPosition = 0;					// 화면에 보이는 첫 아이템의 index
    private int mLastItemPosition = 0;					// 화면에 보이는 마지막 아이템의 index
    private int mMainItemPosition = -1;  //메인 아이템의 index

    /* 터치 컨트롤 */
    protected GestureDetector mGestureDetector;
    private double mStartX = 0;							// 터치 이벤트 시작된 기준 좌표 X
    private double mStartY = 0;							// 터치 이벤트 시작된 기준 좌표 Y
    private boolean isProcessed = false;				// 특정 event 처리 후 ACTION_UP 까지 다른 이벤트 처리가 필요 없을 때 설정하는 flag

    /* 애니매이션*/
    protected AnimationRunable animationRunable = new AnimationRunable();
    /* 사용 후 제거되는 view를 저장하는 cache */
    private final LinkedList<View> mViewCache = new LinkedList<View>();		// 삭제한 뷰를 저장하는 캐시. getView() 를 호출할 때 convertView 파라미터로 사용

    /* 원형으로 사용시에 필요한 변수*/
    private int mainItemRadius;
    private int subItemRadius;
    private int itemRadius;
    private int circleRadius;
    private int displaySubItemCount;
    private double mMovedRadian;
    private double mAccuMovedRadian = 0.0;
    private boolean isRestrict = false;
    private double mLeftAvailableRadian = 0.0;
    private double mRightAvailableRadian = 0.0;
    private double touchSensFactor = 1.0;
    private double degree;
    private double offsetDegree = 90.0;
    private double mChangeItemRadianThreshold;
    private int roundedCenterX;
    private int roundedCenterY;
    private int circleBackground;
    private Drawable circleBackgroundDrawable;
    private HashMap<Integer, Double> mappingTable;

    Paint mPaint = null;
    Matrix mMatrix = null;


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
        if(circleBackground >= 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), circleBackground);
            Bitmap sb = Bitmap.createScaledBitmap(bitmap, circleRadius*2, circleRadius*2, false);
            circleBackgroundDrawable = new BitmapDrawable(getResources(), sb);
        }
        itemRadius = mainItemRadius;
        arr.recycle();

        degree = 360.0 / displaySubItemCount;
        mChangeItemRadianThreshold = Math.toRadians(degree);
        Log.v(TAG, String.format("circleRadius:%d, mainItemRadius:%d, subItemRadius:%d, displaySubItemCount:%d", circleRadius, mainItemRadius, subItemRadius, displaySubItemCount));

        mappingTable = new HashMap<Integer, Double>();
        for(int index = 0; index < displaySubItemCount; index++){
            double angleDeg = adjustDegree(index * degree + offsetDegree);
            double angleRad = adjustRadian(Math.toRadians(angleDeg ));//Math함수에서는 radian을 기준으로 입력을 받아서 60분법에 의한 각도를 변환
            mappingTable.put(index, angleRad);
        }
        //ondraw를 호출하기 위한 방안
        setWillNotDraw(false);

        // 자식 그리는 순서 커스터마이징
        setChildrenDrawingOrderEnabled(true);

        mGestureDetector = new GestureDetector(getContext(), this);
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
        if (mAdapter == null)
            return;
        isDrawing = true;					// drawing flag 설정

        redrawLayout();

        // 애니메이션 종료, 움직임 종료 일 경우
        /*if(animationRunable.isFinished() && !isDrag()){
            if(!checkValidMainItem()){
                if(mMovedRadian > 0){
                    animationRunable.startAnimationUsingAngle(calcDeltaForMoveToSlot(1));
                }else if(mMovedRadian < 0){
                    animationRunable.startAnimationUsingAngle(calcDeltaForMoveToSlot(-1));
                }
                mMovedRadian = 0.0;
            }
        }*/

        fireEvent();
        invalidate();								// 화면 업데이트

        isDrawing = false;					// drawing flag 해제
    }

    private void fireEvent() {

        for (int index = 0; index < getChildCount(); index++) {
            CircularItemContainer child = getChildAt(index);
            int childPosition = getIndexFromRadian(child.getAngleRadian());
            if(childPosition == 0 && mMainItemPosition != child.getIndex() ){
                mMainItemPosition = child.getIndex();
                // main item이 변경될 경우 발생시킴
                if(getOnMainItemChangedListener() != null){
                    getOnMainItemChangedListener().onMainItemChanged(mMainItemPosition, child);
                }
                // 애니메이션 종료, 움직임 종료 일 경우
                if(animationRunable.isFinished() && !isDrag()){
                    if(getOnRotateEndedListener() != null) {
                        getOnRotateEndedListener().onRotateEnded(mMainItemPosition, child);
                    }
                }
            }
            //child.setMainItem(childPosition == 0);
        }
    }

    /**
     * 화면에 표시될 item을 추가/삭제, item의 위치를 설정하는 메인 루틴
     */
    private void redrawLayout() {
        if (getChildCount() == 0) {		// 아이템이 없는 경우 변수 초기화
            mFirstItemPosition = -1;
            mLastItemPosition = -1;
            mMainItemPosition = -1;
        }

        updateCircular();// 이동한 화면에 필요한 아이템을 채움
        layoutItems();							// 자식 뷰들의 화면 내 위치를 재설정 (child.layout() 으로 조정)
    }

    private void updateCircular(){
        if(getChildCount() > 0){
            CircularItemContainer firstChild = getChildAt(0);
            CircularItemContainer lastChild = getChildAt(getChildCount()-1);
            int threshold = Math.max(displaySubItemCount, getAdapter().getCount());
            if(adjustRadian(firstChild.getAngleRadian()) > adjustRadian(Math.toRadians(offsetDegree + 180.0))){
                //시계방향으로 회전할 경우 0의 위치한 자식 뷰를 삭제하고 꼬리부분에 새로운 뷰를 추가해준다.
                double angleRadian = firstChild.getAngleRadian();
                //1. 삭제
                removeViewInLayout(firstChild);
                mViewCache.addLast(firstChild);			// 삭제한 아이템은 캐시에 저장
                mFirstItemPosition = adjustCircularPosition(++mFirstItemPosition, threshold);

                //2. 추가
                mLastItemPosition = adjustCircularPosition(++mLastItemPosition, threshold);
                CircularItemContainer child = mAdapter.getView(mLastItemPosition, getCachedView(), this);
                //3. 화면 배치를 위한 각도 저장(직전의 것의 기존위치로 대체)
                child.setAngleRadian(angleRadian);
                initChildLayout(child, angleRadian);
                addViewAndMeasure(child, ADD_VIEW_AT_REAR);

            }else if(adjustRadian(lastChild.getAngleRadian()) < adjustRadian(Math.toRadians(offsetDegree + 180.0))){
                //시계방향으로 회전할 경우 getChildCount()-1의 위치한 자식 뷰를 삭제하고 맨 앞에 새로운 뷰를 추가해준다.
                double angleRadian = lastChild.getAngleRadian();
                //1. 삭제
                removeViewInLayout(lastChild);
                mViewCache.addLast(lastChild);			// 삭제한 아이템은 캐시에 저장
                mFirstItemPosition = adjustCircularPosition(--mFirstItemPosition, threshold);

                //2. 추가
                mLastItemPosition = adjustCircularPosition(--mLastItemPosition, threshold);
                CircularItemContainer child = mAdapter.getView(mFirstItemPosition, getCachedView(), this);
                //3. 화면 배치를 위한 각도 저장(직전의 것의 기존위치로 대체)
                child.setAngleRadian(angleRadian);
                initChildLayout(child, angleRadian);
                addViewAndMeasure(child, ADD_VIEW_AT_FRONT);

            } else{
                // 아직 임계값 이상으로 변경이 없을 경우에는 다른 작업을 하지 않는다.
            }

        } else {
            //초기에 입력하는 아이템 수에 따라서 원형에 보여주는 아이템 수가 결정된다.
            // 리스트 초기화, 가장 상위의 화면을 구성
            CircularItemContainer child = null;
            int index = 0;
            int threshold = Math.max(displaySubItemCount, getAdapter().getCount());
            int center = (int)Math.ceil(displaySubItemCount / 2);
            mFirstItemPosition = adjustCircularPosition(index-center, threshold);
            mLastItemPosition = mFirstItemPosition - 1;

            while( index < displaySubItemCount ) {
                int position = adjustCircularPosition(center - index, displaySubItemCount);

                //index-center의 순서가 바뀌면 표출 순서도 바뀜
                // 0번째것을 가운데로 표출하기위해 트릭을 사용함
                mLastItemPosition = adjustCircularPosition(++mLastItemPosition, threshold);
                child = mAdapter.getView(mLastItemPosition, getCachedView(), this);
                initChildLayout(child, position);
                addViewAndMeasure(child, ADD_VIEW_AT_REAR);

                index++;
            }
            Log.v(TAG, String.format("mFirstItemPosition:%d, mLastItemPosition:%d", mFirstItemPosition, mLastItemPosition));
        }
    }

    protected int adjustCircularPosition(int position, int size){
        while(position < 0){
            position += size;
        }

        return position % size;
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
            params = new ViewGroup.LayoutParams(itemRadius*2, itemRadius*2);
        }
        int index = 0;
        if(direction == ADD_VIEW_AT_FRONT) {
            index = 0;
        } else {
            index = -1;
        }
        child.setDrawingCacheEnabled(true);			// drawing cache 사용. drawChild() 참고.
        addViewInLayout(child, index, params, true);		// (View child, int index, LayoutParams params, boolean preventRequestLayout)
        measure(child);
    }

    protected void measure(View child){
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
        if(mMovedRadian != 0.0) {
            layoutItems(mMovedRadian);
        }
    }

    private void layoutItems(double delta){
        for (int index = 0; index < getChildCount(); index++) {
            CircularItemContainer child = getChildAt(index);
            moveByDelta(child, delta);
        }
    }

    private void initChildLayout(CircularItemContainer child, int index){
        moveTo(child, getRadianFromIndex(index));
    }

    private void initChildLayout(CircularItemContainer child, double angleRadian){
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(itemRadius*2, itemRadius*2);
        child.setLayoutParams(lp);
        moveTo(child, angleRadian);
    }

    private void scrollToSlot(){
        if(getChildCount() > 0){
            for (int index = 0; index < getChildCount(); index++){
                CircularItemContainer child = getChildAt(index);
                int childPosition = getIndexFromRadian(child.getAngleRadian());
                moveToIndex(child, childPosition);
            }
        }
        invalidate();
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
        double childCenterX = calcChildCenterX(angleRad);
        double childCenterY = calcChildCenterY(angleRad);

        child.setCenterX(childCenterX);
        child.setCenterY(childCenterY);
        child.setAngleRadian(angleRad);
        //if(child.getAngleRadian() == Math.toRadians(offsetDegree)){
        if(Math.toRadians(0.0) <= child.getAngleRadian() &&  child.getAngleRadian() <= Math.toRadians(180.0)){
            child.layout(mainItemRadius);
            measure(child);
        }else {
            child.layout(subItemRadius);
            measure(child);
        }
        invalidate();//잔상 제거용
    }

    private double calcChildCenterX(double angleRad){

        return roundedCenterX + circleRadius * (float)Math.cos( angleRad);
    }

    private double calcChildCenterY(double angleRad){
        return roundedCenterY + circleRadius * (float)Math.sin( angleRad);
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
        while(degree < 0){
            degree += 360.0;
        }
        while(degree >= 360.0){
            degree -= 360.0;
        }

        return degree;
    }

    private double adjustRadian(double radian){
        while(radian < 0){
            radian += Math.toRadians(360.0);
        }
        while(radian >= Math.toRadians(360.0)){
            radian -= Math.toRadians(360.0);
        }
        return radian;
    }

    private double getRadian(double pointX, double pointY, double centerX, double centerY){
        double radian = Math.atan2(pointY - centerY, pointX - centerX);
        return radian;
    }

    /****************************************************
     * 터치 이벤트 리스너
     ***************************************************//*
    /**
     * Implemented to handle touch screen motion events.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Give everything to the gesture detector
        boolean retValue = mGestureDetector.onTouchEvent(event);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            endTouch(event);
        } else if (action == MotionEvent.ACTION_CANCEL) {
            //onCancel();
        }

        return retValue;
    }

    /**
     * 터치 이벤트 시작 루틴
     */
    private void startTouch(MotionEvent event) {
        // 터치 시작점 저장
        mStartX = event.getX();
        mStartY = event.getY();
        isDrag = true;
        isProcessed = false;

        if(getAdapter() != null && getAdapter().getCount() < 2){
            //3개 이하일 경우에는 좌우로 한칸씩만 이동할 수 있도록 함.
            isRestrict = false;
            mLeftAvailableRadian = mChangeItemRadianThreshold;
            mRightAvailableRadian = -mChangeItemRadianThreshold;
        } else if(getAdapter() != null && getChildCount() <= displaySubItemCount){
            isRestrict = false;
            mLeftAvailableRadian = mChangeItemRadianThreshold * (getAdapter().getCount()-1);
            mRightAvailableRadian = -mChangeItemRadianThreshold * (getAdapter().getCount()-1);
        } else {
            isRestrict = false;
        }
    }

    private void scrollList(MotionEvent event){
        Log.v(TAG, "scroll");
        if( !isDrawing ) {
            calcMovement(event);
            if(mMovedRadian!= 0.0 ){
                requestLayout();
            }
        }
    }

    /**
     * 터치 이벤트 종료 루틴
     * 속도를 측정하고 자동 스크롤이 되도록 Runnable 생성
     */
    private void endTouch(MotionEvent event) {
        calcMovement(event);
        Log.v(TAG, "endTouch");
        if(animationRunable.isFinished() && getChildCount() > 0){
            double delta = calcDeltaForMoveToSlot(0);
            animationRunable.startAnimationUsingAngle(delta);
        }

        isDrag = false;
    }

    private boolean checkValidMainItem(){
        if(getChildCount() > 0 ){
            int center = displaySubItemCount/2;
            CircularItemContainer childAt = getChildAt(center);
            if(childAt != null && getAdapter() != null){
                Object item = getAdapter().getItem( childAt.getIndex() );
                if(item != null){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean isAvailableMove(){
        if(isRestrict && (mLeftAvailableRadian <= mAccuMovedRadian || mAccuMovedRadian <= mRightAvailableRadian) ){
            return false;
        }
        return true;
    }

    private double calcDeltaForMoveToSlot(int moveIndex){
        if(getChildCount() > 0){
            CircularItemContainer child = getChildAt(0);
            int index = getIndexFromRadian(child.getAngleRadian());
            double angle = getRadianFromIndex(index + moveIndex);
            return angle - child.getAngleRadian();
        }
        return 0.0;
    }

    private int calcTouchItem(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        for(int index = 0; index < getChildCount(); index ++){
            CircularItemContainer child = getChildAt(index);
            double convertRadian = convertDisplayRadian(child.getAngleRadian());
            double centerX = calcChildCenterX(convertRadian);
            double centerY = calcChildCenterY(convertRadian);

            float scale = (float) calcScale(convertRadian, child);

            int left = (int) (centerX - (child.getWidth() * scale)/2);
            int top = (int) (centerY - (child.getHeight() * scale)/2);
            RectF rectF = new RectF(left, top, left + (child.getWidth() * scale), top+(child.getHeight() * scale));

            if(rectF.contains(x, y)){
                return index;
            }
        }

        return INVALID_ITEM_INDEX;
    }

    private void calcMovement(MotionEvent event){
        double startRad = getRadian(mStartX, mStartY, roundedCenterX, roundedCenterY);
        double endRad = getRadian(event.getX(), event.getY(), roundedCenterX, roundedCenterY);
        double radian = touchSensFactor * (endRad - startRad);

        checkAvailableRadian(radian);

        Log.v(TAG, String.format("mMovedRadian %f", mMovedRadian));
        mStartX = event.getX();		// 요청을 처리했으므로 터치 시작점 재설정
        mStartY = event.getY();
    }

    private void checkAvailableRadian(double radian) {
        if(mLeftAvailableRadian >= mAccuMovedRadian + radian && mAccuMovedRadian + radian >= mRightAvailableRadian){
            mMovedRadian = radian;
            mAccuMovedRadian += mMovedRadian;//터치시에 움직인 각 누적함(터치를 띄면 다시 0으로 초기화)
        } else {
            mMovedRadian = 0.0;
        }
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

        if(circleBackgroundDrawable != null) {
            circleBackgroundDrawable.setBounds(roundedCenterX - circleRadius, roundedCenterY - circleRadius
                    , roundedCenterX + circleRadius, roundedCenterY + circleRadius);
            circleBackgroundDrawable.draw(canvas);
        }
    }

    protected double convertDisplayRadian(double radian){
        double factor = 1.0;
        double temp = Math.abs(adjustRadian(radian) - adjustRadian(0.0 + Math.toRadians(offsetDegree)));
        if( temp <= Math.toRadians(degree)){
            // 중심의 경우 각을 확대시킨다.
            factor = 180.0/(2*degree);
            if(radian > Math.toRadians(180.0)){
                factor *= -1;
            }
            return adjustRadian((radian - Math.toRadians(offsetDegree)) * factor + Math.toRadians(offsetDegree) );
        } else {
            // 중심 외의 부분의 경우 각을 축소시킨다.
            factor = 180.0/(360.0-2*degree);

            //return adjustRadian((radian - Math.toRadians(180))*factor + Math.toRadians(180.0))  + Math.toRadians(offsetDegree);
            //return adjustRadian((radian - Math.toRadians(offsetDegree))*factor+Math.toRadians(offsetDegree));
            //return radian;
            double v = factor*adjustRadian(radian - Math.toRadians(degree)  - Math.toRadians(offsetDegree) ) + Math.toRadians(90.0)+ Math.toRadians(offsetDegree);
            return adjustRadian(v);
        }
    }

    protected Bitmap getChildDrawingCache(final View child){
        Bitmap bitmap = child.getDrawingCache();
        if (bitmap == null) {
            child.setDrawingCacheEnabled(true);
            child.buildDrawingCache();
            bitmap = child.getDrawingCache();
        }
        return bitmap;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

        if(! (child instanceof CircularItemContainer) ) {
            return super.drawChild(canvas, child, drawingTime);
        }
        CircularItemContainer view = (CircularItemContainer) child;

        if(!isDrag && view.getAngleRadian() == Math.toRadians(offsetDegree)){
            view.setMainItem(true);
            return super.drawChild(canvas, child, drawingTime);
        }
        view.setMainItem(false);

        final Bitmap bitmap = getChildDrawingCache(child);

        // 계산에 필요한 파라미터 생성
        double convertRadian = convertDisplayRadian(view.getAngleRadian());
        final int top = (int) calcChildCenterY(convertRadian) - view.getHeight()/2;
        final int left = (int) calcChildCenterX(convertRadian) - view.getWidth()/2;

        int centerX = child.getWidth()/2;
        int centerY = child.getHeight()/2;

        // 속도가 빠를 수도록 scale 값이 작아지도록  설정. scale factor 를 통해 조절 가능
        // scale factor 가 작을수록 이미지 축소량이 작아짐
        float scale = (float) calcScale(convertRadian, view);

        // Matrix 인스턴스 생성. 이미 생성되어 있다면 reset

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

    private double calcScale(double radian, View view){
        final int minRadius = subItemRadius / 2;
        final int middleRadius = subItemRadius;
        final int maxRadius = mainItemRadius;
        double scaleFactor = 1.0f;
        if(calcSector(radian) > 0){
            scaleFactor = (maxRadius-middleRadius)/(Math.sin(Math.toRadians(90.0))-Math.sin(0.0));
        } else {
            scaleFactor = (middleRadius-minRadius)/(Math.sin(0.0)-Math.sin(Math.toRadians(270.0)));
        }

        return ((scaleFactor * Math.sin(radian) + middleRadius) /(view.getWidth()/2));
    }
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if(i <= childCount / 2){
            return  (i * 2);
        } else {
            return  (childCount - 2*( i- childCount/2 ));
        }
    }

    /* GestureDetector를 이용하기 위한 함수 목록 시작*/

    /**
     * 손대면 무조건 발생
     * @param e
     * @return
     */
    @Override
    public boolean onDown(MotionEvent e) {
        animationRunable.stop(false);
        startTouch(e);
        return true;
    }

    /**
     * 살짝 터치
     * @param e
     */
    @Override
    public void onShowPress(MotionEvent e) {

    }

    /**
     * longPress 발생 보다 짧은 시간에 띄었을 경우
     * @param e
     * @return
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        int index  = calcTouchItem(e);
        if(index > INVALID_ITEM_INDEX && getOnItemClickListener() != null) {
            getOnItemClickListener().onItemClick(this, getChildAt(index), getChildAt(index).getIndex(), getChildAt(index).getIndex());
        }
        if(index == (int)Math.ceil(displaySubItemCount / 2) && getOnMainItemSelectedListener() != null){
            getOnMainItemSelectedListener().onMainItemSelected(getChildAt(index).getIndex(), getChildAt(index));
        }

        return true;
    }

    /**
     * 스크롤 시에 발생
     * @param e1 처음 터치가 발생한 위치
     * @param e2 현재 터치 지점
     * @param distanceX 처음과 현재의 x좌표 거리
     * @param distanceY 처음과 현재의 y좌표 거리
     * @return true일 경우 이벤트 소모
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        getParent().requestDisallowInterceptTouchEvent(true);

        scrollList(e2);

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * 손가락을 슬며시 튕기는 동작
     * @param e1 처음 터치한 지점
     * @param e2 마지막 터치 종료 지점
     * @param velocityX x축 가속도
     * @param velocityY y축 가속도
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        Log.v(TAG, String.format("onFling\n e1:%s \n e2:%s\n vX:%f, vY:%f", e1.toString(), e2.toString(), velocityX, velocityY));
        //todo: 부드러운 애니메이션 효과를 위해서는 터치가 끝난 지점에서부터 종료지점까지 이동이 필요함
        // 회전 방향을 알아야한다.

        if(mMovedRadian > 0){
            animationRunable.startAnimationUsingAngle(calcDeltaForMoveToSlot(1));

        } else if(mMovedRadian < 0) {
            animationRunable.startAnimationUsingAngle(calcDeltaForMoveToSlot(-1));
        }

        return false;
    }

    public boolean isDrag() {
        return isDrag;
    }

    /* GestureDetector를 이용하기 위한 함수 목록 끝*/

    public interface OnMainItemChangedListener{
        public void onMainItemChanged(int position, View view);
    }

    public OnMainItemChangedListener getOnMainItemChangedListener() {
        return onMainItemChangedListener;
    }

    public void setOnMainItemChangedListener(OnMainItemChangedListener onMainItemChangedListener) {
        this.onMainItemChangedListener = onMainItemChangedListener;
    }

    public interface OnRotateEndedListener{
        public void onRotateEnded(int position, View mainItem);
    }

    public OnRotateEndedListener getOnRotateEndedListener() {
        return onRotateEndedListener;
    }

    public void setOnRotateEndedListener(OnRotateEndedListener onRotateEndedListener) {
        this.onRotateEndedListener = onRotateEndedListener;
    }

    public interface OnMainItemSelectedListener{
        public void onMainItemSelected(int position, View mainItem);
    }

    public OnMainItemSelectedListener getOnMainItemSelectedListener() {
        return onMainItemSelectedListener;
    }

    public void setOnMainItemSelectedListener(OnMainItemSelectedListener onMainItemSelectedListener) {
        this.onMainItemSelectedListener = onMainItemSelectedListener;
    }

    private class AnimationRunable implements Runnable{
        protected long mStartTime;
        protected long mLastMoveTime;
        private double mDeltaAngle;
        protected double mMovedAnglePerTik;
        protected double mMoveAngle;

        private boolean mFinished;
        private int mDuration;

        public void startAnimationUsingAngle(double deltaAngle){
            if(deltaAngle == 0.0){
                return;
            }
            startCommon();
            mFinished = false;
            mDeltaAngle = deltaAngle;
            mDuration = 500;
            mMovedAnglePerTik = mDeltaAngle / mDuration;
            Log.v(AnimationRunable.class.getSimpleName(), String.format("startAnima delta:%f", deltaAngle));
            mLastMoveTime = mStartTime = AnimationUtils.currentAnimationTimeMillis();
            post(this);
        }

        private void endFling(boolean scrollIntoSlots) {

            if (scrollIntoSlots) {
                scrollToSlot();
            }
        }

        @Override
        public void run() {
            if(getChildCount() == 0){
                endFling(true);
                return;
            }

            final double angle;
            boolean more;
            synchronized(this){
                more = computeAngleOffset();
                angle = mMoveAngle;
                mDeltaAngle -= angle;
            }

            //////// Shoud be reworked
            checkAvailableRadian(angle);
            layoutItems();

            if (more ) {
                post(this);
            } else {
                endFling(true);
            }
        }

        private void startCommon() {
            // Remove any pending flings
            removeCallbacks(this);
        }

        /**
         * Returns the time elapsed since the beginning of the scrolling.
         *
         * @return The elapsed time in milliseconds.
         */
        public long timePassed() {
            return AnimationUtils.currentAnimationTimeMillis() - mStartTime;
        }

        public long getTimeTik(){
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            long tik = currentTime - mLastMoveTime;
            mLastMoveTime = currentTime;
            return tik;
        }

        /**
         * Call this when you want to know the new location.  If it returns true,
         * the animation is not yet finished.  loc will be altered to provide the
         * new location.
         */
        public boolean computeAngleOffset()
        {
            if (mFinished) {
                return false;
            }

            if (mDuration > 0) {
                //tik을 이용하면 실제 애니메이션이 좀 더 길게 동작할 수 있다.
                long tick = getTimeTik();
                mMoveAngle = mMovedAnglePerTik * tick;
                mDuration -= tick;
                Log.v(TAG, String.format("AnimationRunable mMoveAngle:%f", mMoveAngle));
                return true;
            }

            mFinished = true;
            return false;
        }

        public void stop(boolean scrollIntoSlots) {
            removeCallbacks(this);
            endFling(scrollIntoSlots);
        }

        public boolean isFinished() {
            return mFinished;
        }
    }

}
