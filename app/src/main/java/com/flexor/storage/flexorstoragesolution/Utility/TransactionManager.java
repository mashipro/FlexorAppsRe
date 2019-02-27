package com.flexor.storage.flexorstoragesolution.Utility;

public interface TransactionManager {
    void onTransactionSuccess(Boolean success, String transactionID);
    void onTransactionFailure(Boolean failure, String transactionID,String e);
}
