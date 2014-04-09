package com.vivavu.dream.activity.bucket.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;
import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.util.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-25.
 */
public class TimelineCalendarActivity extends BaseActionBarActivity {
    public static final String TAG = "com.vivavu.dream.activity.bucket.timeline.TimelineCalendarActivity";
    public static final String dateExtraName = "defaultDate";
    public static final String timelineMetaInfoExtraName = "timelineMetaInfo";
    public static final String selectedDateExtraName = "selectedDate";
    public static final String selectedDateIndexExtraName = "selectedDateIndex";

    @InjectView(R.id.btn_go_today)
    Button mBtnGoToday;
    @InjectView(R.id.calendar_view)
    CalendarPickerView mCalendarView;

    List<Date> dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_calendar);

        //actionbar setting
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(false);

        ButterKnife.inject(this);

        final Intent intent = getIntent();
        Date defaultDate = (Date) intent.getSerializableExtra(dateExtraName);
        final TimelineMetaInfo timelineMetaInfo = (TimelineMetaInfo) intent.getSerializableExtra(timelineMetaInfoExtraName);
        dateList = timelineMetaInfo.getDateList();

        if(defaultDate == null){
            defaultDate = timelineMetaInfo.getMaxDate();
        }

        mCalendarView.init(timelineMetaInfo.getMinDate(), DateUtils.add(new Date(), Calendar.DAY_OF_MONTH, 1)).withSelectedDate(defaultDate);

        mCalendarView.setDateSelectableFilter(new CalendarPickerView.DateSelectableFilter() {
            @Override
            public boolean isDateSelectable(Date date) {
                return dateList.contains(date);
            }
        });
        mCalendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                if(!timelineMetaInfo.getDateList().contains(date)){

                }else {
                    Intent intent1 = new Intent();
                    Integer index = dateList.indexOf(date);
                    intent1.putExtra(selectedDateExtraName, date);
                    intent1.putExtra(selectedDateIndexExtraName, index);
                    setResult(RESULT_OK, intent1);
                    finish();
                }
            }

            @Override
            public void onDateUnselected(Date date) {
                Log.v(TAG, date.toString());
            }
        });

        mCalendarView.highlightDates(dateList);

        mCalendarView.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                Toast.makeText(TimelineCalendarActivity.this, getString(R.string.invalid_selected_date), Toast.LENGTH_LONG).show();
            }
        });

        mBtnGoToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra(selectedDateExtraName, new Date());
                intent1.putExtra(selectedDateIndexExtraName, 0);
                setResult(RESULT_OK, intent1);
                finish();
            }
        });
    }

}
