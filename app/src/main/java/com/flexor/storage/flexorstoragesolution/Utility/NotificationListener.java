package com.flexor.storage.flexorstoragesolution.Utility;

import com.flexor.storage.flexorstoragesolution.Models.Notification;

import java.util.ArrayList;

public interface NotificationListener {
    void onNewNotificationReceived(Notification notification, ArrayList<Notification> activeNotificationArray, int activeNotificationCount);
}
