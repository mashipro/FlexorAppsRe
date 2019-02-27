package com.flexor.storage.flexorstoragesolution.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Map;

public class Transaction {
    private String transactionID;
    private String sourceID;
    private String targetID;
    private int transactionStats;
    private int transactionRefStats;
    private String transactionRef;
    private int transactionValue;
    private @ServerTimestamp Date transactionStartTime;
    private @ServerTimestamp Date transactionChangeTime;

    public Transaction() {
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID='" + transactionID + '\'' +
                ", sourceID='" + sourceID + '\'' +
                ", targetID='" + targetID + '\'' +
                ", transactionStats=" + transactionStats +
                ", transactionRefStats=" + transactionRefStats +
                ", transactionRef='" + transactionRef + '\'' +
                ", transactionValue=" + transactionValue +
                ", transactionStartTime=" + transactionStartTime +
                ", transactionChangeTime=" + transactionChangeTime +
                '}';
    }

    public Transaction(String transactionID, String sourceID, String targetID, int transactionStats, int transactionRefStats, String transactionRef, int transactionValue, Date transactionStartTime, Date transactionChangeTime) {
        this.transactionID = transactionID;
        this.sourceID = sourceID;
        this.targetID = targetID;
        this.transactionStats = transactionStats;
        this.transactionRefStats = transactionRefStats;
        this.transactionRef = transactionRef;
        this.transactionValue = transactionValue;
        this.transactionStartTime = transactionStartTime;
        this.transactionChangeTime = transactionChangeTime;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public int getTransactionStats() {
        return transactionStats;
    }

    public void setTransactionStats(int transactionStats) {
        this.transactionStats = transactionStats;
    }

    public int getTransactionRefStats() {
        return transactionRefStats;
    }

    public void setTransactionRefStats(int transactionRefStats) {
        this.transactionRefStats = transactionRefStats;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public int getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(int transactionValue) {
        this.transactionValue = transactionValue;
    }

    public Date getTransactionStartTime() {
        return transactionStartTime;
    }

    public void setTransactionStartTime(Date transactionStartTime) {
        this.transactionStartTime = transactionStartTime;
    }

    public Date getTransactionChangeTime() {
        return transactionChangeTime;
    }

    public void setTransactionChangeTime(Date transactionChangeTime) {
        this.transactionChangeTime = transactionChangeTime;
    }
}
