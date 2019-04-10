package com.flexor.storage.flexorstoragesolution.Utility;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.MainActivity;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.Service.BoxScheduledAlert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SchedulerManager {
    private static final String TAG = "SchedulerManager";

    private AlarmManager alarmManager;
    private Context contexts;

    private ArrayList<PendingIntent> intentArrayList = new ArrayList<>();

    public SchedulerManager(Context context) {
        Log.d(TAG, "SchedulerManager: initialized..");
        contexts = context;
        alarmManager = (AlarmManager) contexts.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.boxExpired_notification_channel_id);
            String description = context.getString(R.string.boxExpired_notification_channel_id);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.boxExpired_notification_channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setBoxAlarm(Box newBox){
        Log.d(TAG, "setBoxAlarm: for boxID: "+ newBox.getBoxID());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Long timestamp = calendar.getTimeInMillis();
        Integer reqCode = timestamp.intValue()/1000;

        int duration = newBox.getBoxRentDuration()*24*60*60*1000;

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(System.currentTimeMillis());
        targetCalendar.add(Calendar.DATE,newBox.getBoxRentDuration());
        Log.d(TAG, "setBoxAlarm: alarm at "+ targetCalendar.getTime());

        Intent intent = new Intent(contexts, BoxScheduledAlert.class);
        intent.putExtra("ReqCode",reqCode);
        intent.putExtra("RefID",newBox.getBoxID());
        intent.putExtra("ActionPref",Constants.NOTIFICATION_STATS_USERBOXEXPIRED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(contexts,reqCode,intent,0);
        long minutes = 60*1000;

        // TODO: 10/04/2019 add more alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP,targetCalendar.getTimeInMillis(),pendingIntent);
//        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+minutes,pendingIntent);
        intentArrayList.add(pendingIntent);

//        Intent notificationIntent = new Intent(contexts, BoxScheduledAlert.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(contexts,reqCode,notificationIntent,0);
//
//        String channelID = contexts.getString(R.string.boxExpired_notification_channel_id);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexts,channelID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(contexts.getString(R.string.box_expired_notification))
//                .setContentText(contexts.getString(R.string.box_expired_notification_text))
//                .setContentIntent(pendingIntent);
//        Notification notification = builder.build();
//
//        notificationIntent.putExtra("ReqCode", reqCode);
//        notificationIntent.putExtra("RefID", newBox.getBoxID());
//        notificationIntent.putExtra("ActionPref", Constants.NOTIFICATION_STATS_USERBOXEXPIRED);
//        notificationIntent.putExtra("Notification", notification);
//
//        Long currentTime = System.currentTimeMillis();
//        Long onemin = currentTime+60000;
//        AlarmManager alarmManager = (AlarmManager) contexts.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP,onemin,pendingIntent);
//        Calendar newCal = Calendar.getInstance();
//        newCal.setTimeInMillis(onemin);
//        Log.d(TAG, "setBoxAlarm: alarm set at: "+newCal.getTime());
    }

    public void getAlarm(){

    }

    public void checkAlarm(){

    }
}
