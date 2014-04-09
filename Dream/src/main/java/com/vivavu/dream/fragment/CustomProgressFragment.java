package com.vivavu.dream.fragment;

import android.app.Activity;
import android.view.View;

import com.devspark.progressfragment.ProgressFragment;
import com.vivavu.dream.util.AndroidUtils;

/**
 * Created by yuja on 14. 1. 24.
 */
public class CustomProgressFragment extends ProgressFragment implements View.OnClickListener{
    protected OnOptionFragmentRemovedListener mListener;

    public static CustomProgressFragment newInstance(){
        return new CustomProgressFragment();
    }

    @Override
    public void onClick(View view) {
        //todo:이게 호출되는지 확인 필요.
        AndroidUtils.autoVisibleSoftInputFromWindow(view);
    }

    public interface OnOptionFragmentRemovedListener{
        public void onOptionFragmentRemoved(String tag);
    }

    public CustomProgressFragment(){
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnOptionFragmentRemovedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }

    }
}
