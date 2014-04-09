package com.vivavu.dream.common.listener;

import android.view.View;

import com.vivavu.dream.util.AndroidUtils;

/**
 * Created by yuja on 14. 2. 6.
 */
public class BaseOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        AndroidUtils.autoVisibleSoftInputFromWindow(view.getContext(), view);
    }
}
