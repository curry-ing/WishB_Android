package com.vivavu.dream.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivavu.dream.util.AndroidUtils;

/**
 * Created by yuja on 14. 3. 3.
 */
public class ButtonIncludeCount extends RelativeLayout {
    private Context mContext;
    private Button mButton;
    private TextView mTextView;

    public ButtonIncludeCount(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ButtonIncludeCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ButtonIncludeCount(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextView = new TextView(getContext());

        int width = AndroidUtils.getPixelFromDIP(this, 20);
        int height = AndroidUtils.getPixelFromDIP(this, 20);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//설정에서 바꿀수 있도록 할것 우선 왼쪽으로만

        mTextView.setLayoutParams(layoutParams);
        mTextView.setBackgroundColor(0xFFFF00FF);
        mButton = new Button(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(AndroidUtils.getPixelFromDIP(this, 5), AndroidUtils.getPixelFromDIP(this, 5), 0, 0);

        mButton.setLayoutParams(params);
        mButton.setId(AndroidUtils.generateViewId());

        this.addView(mButton);
        this.addView(mTextView);
    }

    public Button getButton() {
        return mButton;
    }

    public void setButton(Button mButton) {
        this.mButton = mButton;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setTextView(TextView mTextView) {
        this.mTextView = mTextView;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        //super.setOnClickListener(l);
        mButton.setOnClickListener(l);
    }
}
