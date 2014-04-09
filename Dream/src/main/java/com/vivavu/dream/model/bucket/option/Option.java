package com.vivavu.dream.model.bucket.option;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by yuja on 14. 3. 14.
 */
public class Option extends Object implements Cloneable, Serializable {
    @Override
    public Object clone() {
        Object o = null;
        try{
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            Log.e("dream", e.getMessage());
        }
        return o;
    }
}
