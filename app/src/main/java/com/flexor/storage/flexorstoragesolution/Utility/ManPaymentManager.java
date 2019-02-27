package com.flexor.storage.flexorstoragesolution.Utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.TransactionLogStore;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.cert.TrustAnchor;
import java.util.Random;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ManPaymentManager {
    private static final String TAG = "ManPaymentManager";
    private UserManager userManager;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String transactionID;

    public ManPaymentManager() {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Users");
        database = FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        userManager=new UserManager();
        userManager.getInstance();
    }

    public int getUserBalance(){
        Log.d(TAG, "getUserBalance: "+userManager.getUser().getUserBalance());
        return userManager.getUser().getUserBalance();
    }

    public Boolean transactionEligible(int bill){
        Log.d(TAG, "transactionEligible: "+ (getUserBalance()>= bill));
        return getUserBalance()>= bill;
    }

    public void makeTransaction(final String userID,
                                final String targetUserID,
                                final int bill,
                                final int transactionStat,
                                final String transactionRef,
                                final int transactionRefStat,
                                final TransactionManager transactionManager){
        transactionID = getTransactionID(transactionStat);
        if (transactionEligible(bill)){
            Log.d(TAG, "makeTransaction: eligible.... making transaction");
            User user = userManager.getUser();
            int userBalanceFinal = user.getUserBalance()-bill;
            user.setUserBalance(userBalanceFinal);
            userManager.updateUserData(user,Constants.STATSCODE_USERDATA_UPDATE_TRANSACTION,targetUserID);
            transactionID = getTransactionID(transactionStat);
            collectionReference.document(targetUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        User targetUser = task.getResult().toObject(User.class);
                        int targetUserFinalBalance = targetUser.getUserBalance()+bill;
                        targetUser.setUserBalance(targetUserFinalBalance);
                        userManager.updateUserData(targetUser,Constants.STATSCODE_USERDATA_UPDATE_TRANSACTION,userID);
                        String transactionID = getTransactionID(transactionStat);
                        postTransactionLog(transactionID,userID,targetUserID,transactionStat,transactionRef, transactionRefStat, bill);
                        transactionManager.onTransactionSuccess(true, transactionID);
                    }
                }
            });
        }else {
            transactionManager.onTransactionFailure(true, transactionID, getError());
            Log.d(TAG, "makeTransaction: not eligible... cancling transaction");
            Toast.makeText(getApplicationContext(), R.string.transaction_eligible_not, Toast.LENGTH_SHORT).show();
        }
    }

    private String getError() {
        return getApplicationContext().getString(R.string.transaction_eligible_not);
    }

    public void postTransactionLog(final String transactionID, String sourceID, String targetID, int transactionStat, String transactionRef, int transactionRefStat, int value){
        TransactionLogStore transactionLogStore = new TransactionLogStore();
        transactionLogStore.setTransactionID(transactionID);
        transactionLogStore.setSourceID(sourceID);
        transactionLogStore.setTargetID(targetID);
        transactionLogStore.setTransactionValue(value);
        transactionLogStore.setTransactionChangeTime(ServerValue.TIMESTAMP);
        transactionLogStore.setTransactionStartTime(ServerValue.TIMESTAMP);
        transactionLogStore.setTransactionRef(transactionRef);
        transactionLogStore.setTransactionStats(transactionStat);
        transactionLogStore.setTransactionRefStats(transactionRefStat);
        databaseReference.child("transactionList").push().setValue(transactionLogStore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Log id: "+transactionID);
            }
        });
    }

    public String getTransactionID(int stat) {
        return String.valueOf(stat)+ String.valueOf(System.currentTimeMillis());
    }


}
