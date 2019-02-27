package com.flexor.storage.flexorstoragesolution.Models;

import java.util.Map;

public class TransactionLogStore {
    private String transactionID;
    private String sourceID;
    private String targetID;
    private int transactionStats;
    private int transactionRefStats;
    private String transactionRef;
    private int transactionValue;
    private Map<String, String> transactionStartTime;
    private Map<String, String> transactionChangeTime;

    public TransactionLogStore() {
    }

    @Override
    public String toString() {
        return "TransactionLogStore{" +
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

    public TransactionLogStore(String transactionID, String sourceID, String targetID, int transactionStats, int transactionRefStats, String transactionRef, int transactionValue, Map<String, String> transactionStartTime, Map<String, String> transactionChangeTime) {
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

    public Map<String, String> getTransactionStartTime() {
        return transactionStartTime;
    }

    public void setTransactionStartTime(Map<String, String> transactionStartTime) {
        this.transactionStartTime = transactionStartTime;
    }

    public Map<String, String> getTransactionChangeTime() {
        return transactionChangeTime;
    }

    public void setTransactionChangeTime(Map<String, String> transactionChangeTime) {
        this.transactionChangeTime = transactionChangeTime;
    }
}
