package com.flexor.storage.flexorstoragesolution.Models;

import java.util.Date;

public class TransactionDownload {
    private String transactionID;
    private Date transactionChangeTime;
    private Integer transactionRefStats;
    private Integer transactionStats;

    public TransactionDownload() {
    }

    public TransactionDownload(String transactionID, Date transactionChangeTime, Integer transactionRefStats, Integer transactionStats) {
        this.transactionID = transactionID;
        this.transactionChangeTime = transactionChangeTime;
        this.transactionRefStats = transactionRefStats;
        this.transactionStats = transactionStats;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Date getTransactionChangeTime() {
        return transactionChangeTime;
    }

    public void setTransactionChangeTime(Date transactionChangeTime) {
        this.transactionChangeTime = transactionChangeTime;
    }

    public Integer getTransactionRefStats() {
        return transactionRefStats;
    }

    public void setTransactionRefStats(Integer transactionRefStats) {
        this.transactionRefStats = transactionRefStats;
    }

    public Integer getTransactionStats() {
        return transactionStats;
    }

    public void setTransactionStats(Integer transactionStats) {
        this.transactionStats = transactionStats;
    }

    @Override
    public String toString() {
        return "TransactionDownload{" +
                "transactionID='" + transactionID + '\'' +
                ", transactionChangeTime=" + transactionChangeTime +
                ", transactionRefStats=" + transactionRefStats +
                ", transactionStats=" + transactionStats +
                '}';
    }
}
