package com.flexor.storage.flexorstoragesolution.Models;

public class Notification {
    private String notificationID;
    private Long notificationStatsCode;
    private String notificationReference;
    private Boolean notificationIsActive;
    private Long notificationTime;

    public Notification() {
    }

    public Notification(String notificationID, Long notificationStatsCode, String notificationReference, Boolean notificationIsActive, Long notificationTime) {
        this.notificationID = notificationID;
        this.notificationStatsCode = notificationStatsCode;
        this.notificationReference = notificationReference;
        this.notificationIsActive = notificationIsActive;
        this.notificationTime = notificationTime;
    }

    @Override
    public String toString() {
        return "Notification{" +
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

    public Long getNotificationStatsCode() {
        return notificationStatsCode;
    }

    public void setNotificationStatsCode(Long notificationStatsCode) {
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

    public Long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Long notificationTime) {
        this.notificationTime = notificationTime;
    }
}
