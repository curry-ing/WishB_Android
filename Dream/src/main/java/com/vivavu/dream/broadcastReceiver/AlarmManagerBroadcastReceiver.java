package com.vivavu.dream.broadcastReceiver;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.StartActivity;
import com.vivavu.dream.activity.main.MainActivity;
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

    public AlarmManagerBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
//        //Acquire the lock
//        wl.acquire();
//
//        Format formatter = new SimpleDateFormat("hh:mm:ss a");
//        msgStr.append(formatter.format(new Date()));
//
//        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();
//
//        //Release the lock
//        wl.release();

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
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.main_logo))
//                .setSmallIcon(R.drawable.login_check_ok_icon,1)
//                .setNumber(2)
                .setTicker(NotificationTicker)
                .setContentTitle(NotificationTitle)
                .setContentText(NotificationContent)
                .setSound(sound)
                .setVibrate(new long[] { 3000 })
                .setLights(R.color.white, 3000, 3000)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(context, StartActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    public void SetAlarm(Context context, int alarmType, boolean alarmOn, int UTC_Hour) {
        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, UTC_Hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        if (alarmType == 1) {
//            cal.set(Calendar.MINUTE, cur_cal.get(Calendar.MINUTE) + 1);
//        } else if (alarmType == 2) {
//            cal.set(Calendar.MINUTE, cur_cal.get(Calendar.MINUTE) + 2);
//        }

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
