package com.flexor.storage.flexorstoragesolution.Models;

import java.util.Map;

public class NotificationSend {
    private String notificationID;
    private Integer notificationStatsCode;
    private String notificationReference;
    private Boolean notificationIsActive;
    private Map<String, String> notificationTime;

    public NotificationSend() {
    }

    public NotificationSend(String notificationID, Integer notificationStatsCode, String notificationReference, Boolean notificationIsActive, Map<String, String> notificationTime) {
        this.notificationID = notificationID;
        this.notificationStatsCode = notificationStatsCode;
        this.notificationReference = notificationReference;
        this.notificationIsActive = notificationIsActive;
        this.notificationTime = notificationTime;
    }

    @Override
    public String toString() {
        return "NotificationSend{" +
                "notificationID='" + notificationID + '\'' +
                ", notificationStatsCode=" + notificationStatsCode +
                ", notificationReference='" + notificationReference + '\'' +
                ", notificationIsActive=" + notificationIsActive +
                ", notificationTime=" + notificationTime +
                '}';
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public Integer getNotificationStatsCode() {
        return notificationStatsCode;
    }

    public void setNotificationStatsCode(Integer notificationStatsCode) {
        this.notificationStatsCode = notificationStatsCode;
    }

    public String getNotificationReference() {
        return notificationReference;
    }

    public void setNotificationReference(String notificationReference) {
        this.notificationReference = notificationReference;
    }

    public Boolean getNotificationIsActive() {
        return notificationIsActive;
    }

    public void setNotificationIsActive(Boolean notificationIsActive) {
        this.notificationIsActive = notificationIsActive;
    }

    public Map<String, String> getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Map<String, String> notificationTime) {
        this.notificationTime = notificationTime;
    }
}
