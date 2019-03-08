package com.flexor.storage.flexorstoragesolution.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.Transaction;
import com.flexor.storage.flexorstoragesolution.Models.TransactionMiniUsers;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ManPaymentManager {
    private static final String TAG = "ManPaymentManager";
    private UserManager userManager;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference,userRef,transactionReference,logRef;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String transactionID;

    public ManPaymentManager() {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Users");
        userRef=firestore.collection("Users");
        transactionReference = firestore.collection("Transactions");
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

    public void makeTransaction(final Context context,
                                final String userID,
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
            Log.d(TAG, "makeTransaction: not elligible. ask user to recharge!");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.alert_payment_error_elligible)
                    .setMessage(R.string.alert_payment_error_elligible_message)
                    .setPositiveButton(R.string.recharge, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: 08/03/2019 connect to recharge page
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void postTransactionLog(final String transactionID, final String sourceID, final String targetID, int transactionStat, String transactionRef, int transactionRefStat, int value){
        Log.d(TAG, "postTransactionLog: id: "+transactionID+" sourceID: "+sourceID+" targetID: "+targetID+" transaction value: "+value);
        Transaction transactionLogStore = new Transaction();
        transactionLogStore.setTransactionID(transactionID);
        transactionLogStore.setSourceID(sourceID);
        transactionLogStore.setTargetID(targetID);
        transactionLogStore.setTransactionValue(value);
        transactionLogStore.setTransactionChangeTime(null);
        transactionLogStore.setTransactionStartTime(null);
        transactionLogStore.setTransactionRef(transactionRef);
        transactionLogStore.setTransactionStats(transactionStat);
        transactionLogStore.setTransactionRefStats(transactionRefStat);
        transactionReference.document(transactionID).set(transactionLogStore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onComplete: transaction complete..." );
                postTransactionLogtoUser(sourceID, transactionID);
                postTransactionLogtoUser(targetID, transactionID);
            }
        });
    }

    private void postTransactionLogtoUser(String userID, String transactionID) {
        Log.d(TAG, "postTransactionLogtoUser: posting log to userID: "+userID+ " with transactionID: "+transactionID);
        TransactionMiniUsers transactionMiniUsers = new TransactionMiniUsers();
        transactionMiniUsers.setTransactionID(transactionID);
        transactionMiniUsers.setTransactionDate(null);
        userRef.document(userID).collection("MyTransaction").document(transactionID).set(transactionMiniUsers).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: task success!!!!");
            }
        });
    }

    public String getTransactionID(int stat) {
        return String.valueOf(stat)+ String.valueOf(System.currentTimeMillis());
    }


}
