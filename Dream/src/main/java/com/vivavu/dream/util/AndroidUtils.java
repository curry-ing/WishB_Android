package com.vivavu.dream.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.view.CustomPopupWindow;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yuja on 14. 2. 6.
 */
public class AndroidUtils {
    public static void hideSoftInputFromWindow(Context context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return;
    }

    /**
     * show virtual keyboard
     * @param context activity or application context
     * @param view EditText View or other View
     */
    public static void showSoftInputFromWindow(Context context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        return;
    }

    /**
     * if parameter view is instanceof EditText view, show softkeyboard
     * else hide softkeyboard
     * @param context
     * @param view
     */
    public static void autoVisibleSoftInputFromWindow(Context context, View view){
        if( !(view instanceof EditText) ){
            AndroidUtils.hideSoftInputFromWindow(context, view);
        }else{
            AndroidUtils.showSoftInputFromWindow(context, view);
        }
    }

    public static void autoVisibleSoftInputFromWindow(View view){
        if( !(view instanceof EditText) ){
            AndroidUtils.hideSoftInputFromWindow(view.getContext(), view);
        }else{
            AndroidUtils.showSoftInputFromWindow(view.getContext(), view);
        }
    }

    public static View getRootView(Activity activity){
        View view1 = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        View view2 = activity.findViewById(android.R.id.content);
        View view3 = activity.findViewById(android.R.id.content).getRootView();

        return view2;
    }

    public static void getKeyHashes(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.vivavu.dream",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressLint("NewApi")
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (;;) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }

    }

    public static int getPixelFromDIP(final View view, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, DreamApp.getInstance().getResources().getDisplayMetrics());
    }

    public static int getSpFromPx(int px){
        return  (int) (px / DreamApp.getInstance().getResources().getDisplayMetrics().scaledDensity);
    }

    public static CustomPopupWindow makePopupWindow(View contentsView, int width, int height ){
        /**
         * LayoutParams WRAP_CONTENT를 주면 inflate된 View의 사이즈 만큼의
         * PopupWinidow를 생성한다.
         */

        CustomPopupWindow mPopupWindow = new CustomPopupWindow(contentsView, width, height);
        mPopupWindow.setContentView(contentsView);
        mPopupWindow.setWidth(width);
        mPopupWindow.setHeight(height);
        // 팝업 외의것을 터치하면 사라짐
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(contentsView.getResources(), (Bitmap) null));
        /**
         * showAsDropDown(anchor, xoff, yoff)
         * @View anchor : anchor View를 기준으로 바로 아래 왼쪽에 표시.
         * @예외 : 하지만 anchor View가 화면에 가장 하단 View라면 시스템이
         * 자동으로 위쪽으로 표시되게 한다.
         * xoff, yoff : anchor View를 기준으로 PopupWindow가 xoff는 x좌표,
         * yoff는 y좌표 만큼 이동된 위치에 표시되게 한다.
         * @int xoff : -숫자(화면 왼쪽으로 이동), +숫자(화면 오른쪽으로 이동)
         * @int yoff : -숫자(화면 위쪽으로 이동), +숫자(화면 아래쪽으로 이동)
         * achor View 를 덮는 것도 가능.
         * 화면바깥 좌우, 위아래로 이동 가능. (짤린 상태로 표시됨)
         */
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)
        //mPopupWindow.showAsDropDown(mBtnBookSetting, 0, 0);

        /**
         * showAtLocation(parent, gravity, x, y)
         * @praent : PopupWindow가 생성될 parent View 지정
         * View v = (View) findViewById(R.id.btn_click)의 형태로 parent 생성
         * @gravity : parent View의 Gravity 속성 지정 Popupwindow 위치에 영향을 줌.
         * @x : PopupWindow를 (-x, +x) 만큼 좌,우 이동된 위치에 생성
         * @y : PopupWindow를 (-y, +y) 만큼 상,하 이동된 위치에 생성
         */
        //mPopupWindow.showAtLocation(mBtnBookSetting, Gravity.NO_GRAVITY, 0, 0);
        //mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);

        /**
         * update() 메서드를 통해 PopupWindow의 좌우 사이즈, x좌표, y좌표
         * anchor View까지 재설정 해줄수 있습니다.
         */
        //          mPopupWindow.update(anchor, xoff, yoff, width, height)(width, height);
        return mPopupWindow;
    }
    public static CustomPopupWindow makePopupWindow(View contentsView){
        return makePopupWindow(contentsView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void recycleImage(ImageView imageView){
        if(imageView.getDrawable() != null){
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            imageView.setImageDrawable(null);
            Bitmap bitmap = drawable.getBitmap();
            bitmap.recycle();
        }
    }

    public static void recycleImage(View view){
        if(view.getBackground() != null){
            BitmapDrawable drawable = (BitmapDrawable) view.getBackground();
            view.setBackgroundResource(0);
            Bitmap bitmap = drawable.getBitmap();
            bitmap.recycle();
        }
    }

    public static String[] getEmailAccountName(Context context){
        AccountManager am = AccountManager.get(context);
        Account[] accts = am.getAccounts();
        int count = accts.length;
        if(count > 0) {
            String[] accountNames = new String[accts.length];
            for(int i = 0; i < count; i++) {
                accountNames[i] = accts[i].name;
            }
        }
        return null;
    }

    public static String convertContentsToFileSchema(Context context, String contentSchema){
        Cursor c = context.getContentResolver().query(Uri.parse(contentSchema), null, null, null, null);
        c.moveToNext();
        String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

        return path;
    }
}
