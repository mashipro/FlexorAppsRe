package com.flexor.storage.flexorstoragesolution.Models;

import java.util.Map;

public class UserLogsStore {
    private Map<String, String> logsTime;
    private String userLogsID;
    private int userLogsStatsCode;
    private User userHistory;

    public UserLogsStore() {
    }

    public UserLogsStore(Map<String, String> logsTime, String userLogsID, int userLogsStatsCode, User userHistory) {
        this.logsTime = logsTime;
        this.userLogsID = userLogsID;
        this.userLogsStatsCode = userLogsStatsCode;
        this.userHistory = userHistory;
    }

    @Override
    public String toString() {
        return "UserLogsStore{" +
                "logsTime=" + logsTime +
                ", userLogsID='" + userLogsID + '\'' +
                ", userLogsStatsCode=" + userLogsStatsCode +
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

    public User getUserHistory() {
        return userHistory;
    }

    public void setUserHistory(User userHistory) {
        this.userHistory = userHistory;
    }
}
