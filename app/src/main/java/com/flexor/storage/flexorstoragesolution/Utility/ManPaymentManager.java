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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ManPaymentManager {
    private static final String TAG = "ManPaymentManager";
    private UserManager userManager;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference,userRef,transactionReference;
    private FirebaseFunctions firebaseFunctions;

    private String transactionID;
    private User user;

    public ManPaymentManager() {
        firestore = FirebaseFirestore.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance();
        collectionReference = firestore.collection("Users");
        userRef=firestore.collection("Users");
        transactionReference = firestore.collection("Transactions");
        userManager=new UserManager();
        userManager.getInstance();
        user = userManager.getUser();
    }

    public int getUserBalance(){
        Log.d(TAG, "getUserBalance: "+userManager.getUser().getUserBalance());
        return userManager.getUser().getUserBalance();
    }

    public Boolean transactionEligible(int bill){
        Log.d(TAG, "transactionEligible: "+ (getUserBalance()>= bill));
        return getUserBalance()>= bill;
    }

//    public void makeTransaction(final Context context,
//                                final String targetUserID,
//                                final int bill,
//                                final int transactionStat,
//                                final String transactionRef,
//                                final TransactionManager transactionManager){
//        transactionID = getTransactionID(transactionStat);
//        if (transactionEligible(bill)){
//            Log.d(TAG, "makeTransaction: eligible.... making transaction");
//            int userBalanceFinal = user.getUserBalance()-bill;
//            user.setUserBalance(userBalanceFinal);
//            userManager.updateUserData(user,Constants.STATSCODE_USERDATA_UPDATE_TRANSACTION,targetUserID);
//            userManager.getUserDataByID(targetUserID, new GetUserData() {
//                @Override
//                public void onDataAcquired(User thisUser) {
//                    User targetUser = thisUser;
//                    int targetUserFinalBalance = targetUser.getUserBalance()+bill;
//                    targetUser.setUserBalance(targetUserFinalBalance);
//                    userManager.updateUserDataByID(targetUser, thisUser, Constants.STATSCODE_USERDATA_UPDATE_TRANSACTION, user.getUserID(), new UserDataUpdateState() {
//                        @Override
//                        public void onUserDataUpdated(Boolean updateState, Exception e) {
//                            if (updateState){
//                                Log.d(TAG, "onUserDataUpdated: "+updateState);
//                                postTransactionLog(transactionID,user.getUserID(),targetUserID,transactionStat,transactionRef, Constants.TRANSACTION__REFSTAT_FINISHED, bill);
//                                transactionManager.onTransactionSuccess(true, transactionID);
//                            }else {
//                                Log.d(TAG, "onUserDataUpdated: "+updateState+" //Exception: "+e);
//                                postTransactionLog(transactionID,user.getUserID(),targetUserID,transactionStat,transactionRef, Constants.TRANSACTION__REFSTAT_ERROR, bill);
//                                transactionManager.onTransactionSuccess(false,transactionID);
//                            }
//                        }
//                    });
//
//
////                    postTransactionLog(transactionID,user.getUserID(),targetUserID,transactionStat,transactionRef, transactionRefStat, bill);
////                    transactionManager.onTransactionSuccess(true, transactionID);
//
//                }
//            });
//        }else {
//            Log.d(TAG, "makeTransaction: not eligible. ask user to recharge!");
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle(R.string.alert_payment_error_elligible)
//                    .setMessage(R.string.alert_payment_error_elligible_message)
//                    .setPositiveButton(R.string.recharge, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO: 08/03/2019 connect to recharge page
//                            dialog.dismiss();
//                        }
//                    })
//                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        }
//    }

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
        transactionMiniUsers.setTransactionChangeTime(null);
        userRef.document(userID).collection("MyTransaction").document(transactionID).set(transactionMiniUsers).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: task success!!!!");
            }
        });
    }

    public String getTransactionID(int stat) {
        return String.valueOf(stat)+String.valueOf(System.currentTimeMillis());
    }
     public void makeTransaction(
             int    transStat,
             String sourceID,
             String targetID,
             int    bill,
             String transRef,
             final TransactionManager transactionManager){
         Log.d(TAG, "testTransaction: triggered");
         Transaction transactionData = new Transaction();
         transactionData.setTransactionStats(transStat);
         transactionData.setSourceID(sourceID);
         transactionData.setTargetID(targetID);
         transactionData.setTransactionValue(bill);
         transactionData.setTransactionRef(transRef);
         requestTransaction(transactionData).addOnCompleteListener(new OnCompleteListener<Object>() {
             @Override
             public void onComplete(@NonNull Task<Object> task) {
                 if (task.isSuccessful()){
                     Log.d(TAG, "onComplete: task success with result: "+task.getResult());
                     transactionManager.onTransactionSuccess(true, task.getResult().toString());
                 }else {
                     Log.d(TAG, "onComplete: error: "+task.getException().toString());
                     transactionManager.onTransactionSuccess(false,task.getException().toString());
                 }
             }
         });
     }

     public void requestTopUp(
             String sourceID,
             String transactionRef,
             int    transactionValue,
             final TransactionManager transactionManager
     ){
        Transaction transaction = new Transaction();
        transaction.setTransactionID(getTransactionID(511));
        transaction.setSourceID(sourceID);
        transaction.setTransactionRef(transactionRef);
        transaction.setTransactionValue(transactionValue);
         topUpRequest(transaction).addOnCompleteListener(new OnCompleteListener<Object>() {
             @Override
             public void onComplete(@NonNull Task<Object> task) {
                 if (task.isSuccessful()){
                     Log.d(TAG, "onComplete: task success with result: "+task.getResult());
                     transactionManager.onTransactionSuccess(true, task.getResult().toString());
                 }else {
                     Log.d(TAG, "onComplete: error: "+task.getException().toString());
                     transactionManager.onTransactionSuccess(false,task.getException().toString());
                 }
             }
         });
     }

    public void acceptTopUp(
            String sourceID,
            final TransactionManager transactionManager
    ){
        Transaction transaction = new Transaction();
        transaction.setTransactionID(getTransactionID(511));
        transaction.setSourceID(sourceID);
        topUpRequest(transaction).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: task success with result: "+task.getResult());
                    transactionManager.onTransactionSuccess(true, task.getResult().toString());
                }else {
                    Log.d(TAG, "onComplete: error: "+task.getException().toString());
                    transactionManager.onTransactionSuccess(false,task.getException().toString());
                }
            }
        });
    }



    private Task<Object> requestTransaction(Transaction transactionData){
        Map<String, Object> data = new HashMap<>();
        data.put("transactionID"    ,getTransactionID(transactionData.getTransactionStats()));
        data.put("transactionStats" ,transactionData.getTransactionStats());
        data.put("sourceID"         ,transactionData.getSourceID());
        data.put("targetID"         ,transactionData.getTargetID());
        data.put("transactionRef"   ,transactionData.getTransactionRef());
        data.put("transactionValue" ,transactionData.getTransactionValue());
        return firebaseFunctions
                .getHttpsCallable("transactionRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Object>() {
                    @Override
                    public Object then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Object result = task.getResult().getData();
                        Log.d(TAG, "HTTPS protocol result: "+result);
                        return result;
                    }
                });
    }

    private Task<Object> topUpRequest (Transaction transactionData){
        Map<String, Object> data = new HashMap<>();
        data.put("transactionID"    ,getTransactionID(transactionData.getTransactionStats()));
        data.put("transactionStats" ,Constants.TRANSACTION__BALANCE_RECHARGE);
        data.put("sourceID"         ,transactionData.getSourceID());
        data.put("transactionRef"   ,transactionData.getTransactionRef());
        data.put("transactionValue" ,transactionData.getTransactionValue());
        return firebaseFunctions
                .getHttpsCallable("newTopUpRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Object>() {
                    @Override
                    public Object then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Object result = task.getResult().getData();
                        Log.d(TAG, "HTTPS protocol result: "+result);
                        return result;
                    }
                });
    }

    private Task<Object> acceptTopUpRequest (Transaction transactionData){
        Map<String, Object> data = new HashMap<>();
        data.put("transactionID"    ,transactionData.getTransactionID());
        return firebaseFunctions
                .getHttpsCallable("acceptTopUpRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Object>() {
                    @Override
                    public Object then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Object result = task.getResult().getData();
                        Log.d(TAG, "HTTPS protocol result: "+result);
                        return result;
                    }
                });
    }
}
