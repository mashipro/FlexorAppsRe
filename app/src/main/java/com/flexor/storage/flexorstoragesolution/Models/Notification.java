package com.flexor.storage.flexorstoragesolution.Models;

public class Notification {
    private String notificationID;
    private Integer notificationStatsCode;
    private String notificationReference;
    private Boolean notificationIsActive;

    public Notification() {
    }

    public Notification(String notificationID, Integer notificationStatsCode, String notificationReference, Boolean notificationIsActive) {
        this.notificationID = notificationID;
        this.notificationStatsCode = notificationStatsCode;
        this.notificationReference = notificationReference;
        this.notificationIsActive = notificationIsActive;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationID='" + notificationID + '\'' +
                ", notificationStatsCode=" + notificationStatsCode +
                ", notificationReference='" + notificationReference + '\'' +
                ", notificationIsActive=" + notificationIsActive +
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
}
