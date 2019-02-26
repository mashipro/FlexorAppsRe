package com.flexor.storage.flexorstoragesolution.Models;

import java.util.Map;

public class UserLogsStore {
    private Map<String, String> logsTime;
    private String userLogsID;
    private int userLogsStatsCode;
    private String referenceID;
    private User userHistory;

    public UserLogsStore() {
    }

    public UserLogsStore(Map<String, String> logsTime, String userLogsID, int userLogsStatsCode, String referenceID, User userHistory) {
        this.logsTime = logsTime;
        this.userLogsID = userLogsID;
        this.userLogsStatsCode = userLogsStatsCode;
        this.referenceID = referenceID;
        this.userHistory = userHistory;
    }

    @Override
    public String toString() {
        return "UserLogsStore{" +
                "logsTime=" + logsTime +
                ", userLogsID='" + userLogsID + '\'' +
                ", userLogsStatsCode=" + userLogsStatsCode +
                ", referenceID='" + referenceID + '\'' +
                ", userHistory=" + userHistory +
                '}';
    }

    public Map<String, String> getLogsTime() {
        return logsTime;
    }

    public void setLogsTime(Map<String, String> logsTime) {
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
