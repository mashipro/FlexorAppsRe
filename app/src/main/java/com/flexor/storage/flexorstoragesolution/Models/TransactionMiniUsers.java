package com.flexor.storage.flexorstoragesolution.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class TransactionMiniUsers {
    private String transactionID;
    private @ServerTimestamp Date transactionDate;

    public TransactionMiniUsers() {
    }

    public TransactionMiniUsers(String transactionID, Date transactionDate) {
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
