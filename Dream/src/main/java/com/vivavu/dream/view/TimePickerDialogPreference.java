package com.vivavu.dream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by yuja on 2014-06-11.
 */
public class TimePickerDialogPreference extends DialogPreference {

    protected int lastHour=0;
    protected int lastMinute=0;
    protected boolean is24HourFormat;
    protected TimePicker picker=null;
    protected TextView timeDisplay;

    public TimePickerDialogPreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePickerDialogPreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, 0);
    }

    public TimePickerDialogPreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);
        is24HourFormat = DateFormat.is24HourFormat(ctxt);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    public String toString() {
        if(is24HourFormat) {
            return String.format("%02d:%02d", lastHour, lastMinute);
        } else {
            int myHour = lastHour % 12;
            return String.format("%s %02d:%02d", (lastHour >= 12) ? " PM" : " AM", (myHour == 0) ? 12 : lastHour, lastMinute);
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return super.onCreateView(parent);
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());
        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setIs24HourView(is24HourFormat);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    public void onBindView(View view) {
        View widgetLayout;
        int childCounter = 0;
        do {
            widgetLayout = ((ViewGroup) view).getChildAt(childCounter);
            childCounter++;
        } while (widgetLayout.getId() != android.R.id.widget_frame);
        ((ViewGroup) widgetLayout).removeAllViews();
        timeDisplay = new TextView(widgetLayout.getContext());
        timeDisplay.setText(toString());
        ((ViewGroup) widgetLayout).addView(timeDisplay);
        super.onBindView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            picker.clearFocus();
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();

            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
                timeDisplay.setText(toString());
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            if (defaultValue==null) {
                time="00:00";
            }
            else {
                time=defaultValue.toString();
            }
            if (shouldPersist()) {
                persistString(time);
            }
        }

        String[] timeParts=time.split(":");
        lastHour=Integer.parseInt(timeParts[0]);
        lastMinute=Integer.parseInt(timeParts[1]);;
    }
}
