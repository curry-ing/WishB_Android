package com.vivavu.dream.broadcastReceiver;

import android.annotation.TargetApi;
import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.StartActivity;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.drawable.RoundedAvatarDrawable;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.DataRepository;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by masunghoon on 6/10/14.
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver{

    final public static String ALARM_TYPE = "alarmType";

    private Bitmap mBitmap;

    public AlarmManagerBroadcastReceiver() {
        super();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        int type = bundle.getInt(ALARM_TYPE);

        StringBuilder NotificationTicker = new StringBuilder();
        StringBuilder NotificationTitle = new StringBuilder();
        StringBuilder NotificationContent = new StringBuilder();

        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);

        Calendar calendar = Calendar.getInstance();
        String weekDay = dayFormat.format(calendar.getTime());
        int dayOfWeekInMonth = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        List<Bucket> buckets = DataRepository.ListBucketByRptCndt(weekDay, dayOfWeekInMonth);
        List<Bitmap> images = new ArrayList<Bitmap>();
        if (buckets.size() > 0) {
            for (int i=0; i<buckets.size(); i++){
                if(buckets.get(i).getCvrImgUrl() != null ){
                    images.add(ImageLoader.getInstance().loadImageSync(buckets.get(i).getCvrImgUrl()));
                }
            }
        } else {
            images.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.main_default_01));
            images.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.main_default_02));
            images.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.main_default_03));
            images.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.main_default_04));
            images.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.main_default_05));
            images.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.main_default_06));
        }
        Collections.shuffle(images);
        mBitmap = images.get(0);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (type == 1) {
            NotificationTicker.append("Wish B: Good Morning!");
            NotificationTitle.append("오늘 하루도 화이팅!");
            NotificationContent.append("오늘, " + buckets.size() + "개의 열기구에 불을 지펴보세요.");
        } else if (type == 2) {
            NotificationTicker.append("Wish B: Evening!!!");
            NotificationTitle.append("수고했어요. 오늘도!");
            NotificationContent.append(buckets.size() + "개의 열기구를 확인해보세요.");
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.main_logo))
                .setLargeIcon(mBitmap)
                .setSmallIcon(R.drawable.logo_tiny_bw_t, 1)
//                .setNumber(2)
                .setTicker(NotificationTicker)
                .setContentTitle(NotificationTitle)
                .setContentText(NotificationContent)
                .setSound(sound)
                .setVibrate(new long[] { 3000 })
                .setLights(R.color.white, 3000, 3000)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(context, StartActivity.class);
        resultIntent.putExtra("goToday", true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(type, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    public void setEverydayAlarm(Context context, boolean alarmOn, int type){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean good_morning_alarm_on = sharedPreferences.getBoolean("notification_good_morning_alarm", true);
        Boolean good_night_alarm_on = sharedPreferences.getBoolean("notification_good_night_alarm", true);
        String good_morning_alarm_time = sharedPreferences.getString("notifications_time_morning", null);
        String good_night_alarm_time = sharedPreferences.getString("notifications_time_night", null);
        Integer good_morning_alarm_hour = Integer.parseInt(good_morning_alarm_time.split(":")[0]);
        Integer good_morning_alarm_min = Integer.parseInt(good_morning_alarm_time.split(":")[1]);
        Integer good_night_alarm_hour = Integer.parseInt(good_night_alarm_time.split(":")[0]);
        Integer good_night_alarm_min = Integer.parseInt(good_night_alarm_time.split(":")[1]);

        if (alarmOn) {
            if (type == 1) { // toggle Good Morning Alarm
                SetAlarm(context, 1, !good_morning_alarm_on, good_morning_alarm_hour, good_morning_alarm_min);
            } else if (type == 2){ // toggle Good Night Alarm
                SetAlarm(context, 2, !good_night_alarm_on, good_night_alarm_hour, good_night_alarm_min);
            } else {
                SetAlarm(context, 1, good_morning_alarm_on, good_morning_alarm_hour, good_morning_alarm_min);
                SetAlarm(context, 2, good_night_alarm_on, good_night_alarm_hour, good_night_alarm_min);
            }
        } else {
            SetAlarm(context, 1, false, good_morning_alarm_hour, good_morning_alarm_min);
            SetAlarm(context, 2, false, good_night_alarm_hour, good_night_alarm_min);
        }
    }

    public void setEverydayAlarm(Context context, int hour, int min, int type){
        SetAlarm(context, type, true, hour, min);
    }

    public void SetAlarm(Context context, int alarmType, boolean alarmOn, int hour, int min) {
        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
        if (cal.getTimeInMillis() <= cur_cal.getTimeInMillis()){
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (alarmOn) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, getPendingIntent(context, alarmType));
        } else {
            am.cancel(getPendingIntent(context, alarmType));
        }
    }

    public PendingIntent getPendingIntent(Context context, int type) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class).putExtra(ALARM_TYPE, type);
        return PendingIntent.getBroadcast(context, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
