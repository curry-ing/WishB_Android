package com.vivavu.dream.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.vivavu.dream.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuja on 14. 2. 17.
 */
public class ValidationUtils {
    public static Boolean isValidPassword(EditText view){
        Context context = view.getContext();
        String value = view.getText().toString();

        if (TextUtils.isEmpty(value)) {
//            view.setError(context.getString(R.string.error_field_required));
            return false;
        } else if (value.length() < 6) {
//            view.setError(context.getString(R.string.error_invalid_password));
            return false;
        }

        return true;
    }

    public static Boolean isValidEmail(EditText view){
        Context context = view.getContext();
        String value = view.getText().toString();
        String emailRegExp = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[_A-Za-z0-9]+)*(\\.[A-za-z]{2,})$";
        Pattern p = Pattern.compile(emailRegExp);
        Matcher m = p.matcher(value);

        // Check for a valid email address.
        if (TextUtils.isEmpty(value)) {
//            view.setError(context.getString(R.string.error_field_required));
            return false;
        } else if (!m.matches()) {
//            view.setError(context.getString(R.string.error_invalid_email));
            return false;
        }

        return true;
    }

    public static Boolean isValidNotEmpty(EditText view){
        Context context = view.getContext();
        String value = view.getText().toString();

        // Check for a valid email address.
        if (!isNotEmpty(value)) {
            view.setError(context.getString(R.string.error_field_required));
            return false;
        }

        return true;
    }

    public static Boolean isValidRepeatCount(String count){
        if(count != null && count.length() > 0){
            String numberExp = "\\d+";
            Pattern p = Pattern.compile(numberExp);
            Matcher m = p.matcher(count);
            if( m.matches() ){
                int parseInt = Integer.parseInt(count);
                return parseInt > 0;
            }
        }
        return false;
    }

    public static Boolean isNotEmpty(String str){
        if(str != null && str.length() > 0 && str.trim().length() > 0 ){
            return true;
        }
        return false;
    }

    public static Boolean isEmpty(String str){
        return !isNotEmpty(str);
    }
}
