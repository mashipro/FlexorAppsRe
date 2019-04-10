package com.flexor.storage.flexorstoragesolution.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.MystoragelistFragment;
import com.flexor.storage.flexorstoragesolution.R;

public class BoxScheduledAlert extends BroadcastReceiver {
    private static final String TAG = "BoxScheduledAlert";
    public BoxScheduledAlert() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: fired!");
//        Log.d(TAG, "onReceive: alarm received");
//        Intent i = new Intent(context, MystoragelistFragment.class);
//        i.putExtra("ReqCode", intent.getExtras().getInt("ReqCode"));
//        Log.d(TAG, "onReceive: alarm reqCode: "+ intent.getExtras().getInt("ReqCode"));
//        i.putExtra("RefID", intent.getExtras().getString("RefID"));
//        Log.d(TAG, "onReceive: alarm refID: "+ intent.getExtras().getString("RefID"));
//        context.startService(i);

        int extraReqCode = intent.getExtras().getInt("ReqCode");
        Log.d(TAG, "onReceive: reqCode: "+extraReqCode);
        Log.d(TAG, "onReceive: RefID: "+ intent.getExtras().get("RefID"));
        Log.d(TAG, "onReceive: ActionPref: "+ intent.getExtras().get("ActionPref"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context,extraReqCode,intent,0);
        String channelID = context.getString(R.string.boxExpired_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.box_expired_notification))
                .setContentText(context.getString(R.string.box_expired_notification_text))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelID,
                    context.getString(R.string.boxExpired_notification_channel_id),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(extraReqCode,builder.build());

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = intent.getParcelableExtra("Notification");
//        Integer reqCode = intent.getParcelableExtra("ReqCode");
//        notificationManager.notify(reqCode, notification);
    }
}
