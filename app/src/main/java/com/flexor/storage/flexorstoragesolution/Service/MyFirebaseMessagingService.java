package com.flexor.storage.flexorstoragesolution.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.MainActivity;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServic";

    private UserManager userManager;

    public MyFirebaseMessagingService() {
        userManager = new UserManager();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: all data"+ remoteMessage);
//        String title = remoteMessage.getNotification().getTitle();
//        String message = remoteMessage.getNotification().getBody();
//        Log.d(TAG, "onMessageReceived: title: "+title);
//        Log.d(TAG, "onMessageReceived: message:"+message);
        String notifID = remoteMessage.getData().get("id");
        Log.d(TAG, "onMessageReceived: data notificationID: "+notifID);

        String notifRef = remoteMessage.getData().get("reference");
        Log.d(TAG, "onMessageReceived: data ref: "+ notifRef);

        String notifStat = remoteMessage.getData().get("stats");
        Log.d(TAG, "onMessageReceived: data stats: "+notifStat);

//        sendNotification(notifID, notifRef, notifStat);
        prepareNotification(notifID, notifRef, notifStat);
//        sendNotification(title,message);


    }



    @Override
    public void onDeletedMessages() {

    }

    @Override
    public void onMessageSent(String s) {

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: new Token received! token: "+ s);
        userManager. storeToken(s);
    }
    private void prepareNotification(String notifID, String notifRef, String notifStat) {
        String testStat = String.valueOf(Constants.NOTIFICATION_STATS_TEST);
        if (notifStat.equals(testStat)){
            String notifTitle = getString(R.string.notif_test_title);
            String notifMessage = getString(R.string.notif_test_message);
            sendNotification(notifID, notifTitle, notifMessage);
        }else {
            String notifTitle = getString(R.string.notif_simple_new_notification);
            String notifMessage = getString(R.string.notif_simple_new_notification_message);
            sendNotification(notifID, notifTitle, notifMessage);
        }
    }

    private void sendNotification(String id, String title, String message) {
        Log.d(TAG, "sendNotification: building notification");

        int notificationID = buildNotificationID(id);


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 9999 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.default_notification_channel_id),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build());
    }

    private int buildNotificationID(String id) {
        Log.d(TAG, "buildNotificationID");
        int notificationId = 0;
        for(int i = 0; i < 9; i++){
            notificationId = notificationId + id.charAt(0);
        }
        Log.d(TAG, "buildNotificationID: id: "+ id);
        Log.d(TAG, "buildNotificationID: notif id: "+ notificationId);
        return notificationId;
    }

}
