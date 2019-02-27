package com.flexor.storage.flexorstoragesolution.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Map;

public class UserLogs {
    private @ServerTimestamp Date logsTime;
    private String userLogsID;
    private int userLogsStatsCode;
    private String referenceID;
    private User userHistory;

    public UserLogs() {
    }

    @Override
    public String toString() {
        return "UserLogs{" +
                "logsTime=" + logsTime +
                ", userLogsID='" + userLogsID + '\'' +
                ", userLogsStatsCode=" + userLogsStatsCode +
                ", referenceID='" + referenceID + '\'' +
                ", userHistory=" + userHistory +
                '}';
    }

    public UserLogs(Date logsTime, String userLogsID, int userLogsStatsCode, String referenceID, User userHistory) {
        this.logsTime = logsTime;
        this.userLogsID = userLogsID;
        this.userLogsStatsCode = userLogsStatsCode;
        this.referenceID = referenceID;
        this.userHistory = userHistory;
    }

    public Date getLogsTime() {
        return logsTime;
    }

    public void setLogsTime(Date logsTime) {
        this.logsTime = logsTime;
    }

    public String getUserLogsID() {
        return userLogsID;
    }

    public void setUserLogsID(String userLogsID) {
        this.userLogsID = userLogsID;
    }

    public int getUserLogsStatsCode() {
        return userLogsStatsCode;
    }

    public void setUserLogsStatsCode(int userLogsStatsCode) {
        this.userLogsStatsCode = userLogsStatsCode;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    public User getUserHistory() {
        return userHistory;
    }

    public void setUserHistory(User userHistory) {
        this.userHistory = userHistory;
    }
}
