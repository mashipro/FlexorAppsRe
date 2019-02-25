package com.flexor.storage.flexorstoragesolution.Utility;

import android.support.annotation.NonNull;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ManPaymentManager {
    private UserManager userManager;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    public ManPaymentManager() {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Users");
    }

    public int getUserBalance(){
        return userManager.getUser().getUserBalance();
    }
    public Boolean transactionEligible(int bill){
        return getUserBalance()>= bill;
    }

    public void makeTransaction(String userID, String targetUserID, final int bill, final TransactionManager transactionManager){
        if (transactionEligible(bill)){
            User user = userManager.getUser();
            int userBalanceFinal = user.getUserBalance()-bill;
            user.setUserBalance(userBalanceFinal);
            userManager.updateUserData(user);

            collectionReference.document(targetUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        User targetUser = task.getResult().toObject(User.class);
                        int targetUserFinalBalance = targetUser.getUserBalance()+bill;
                        targetUser.setUserBalance(targetUserFinalBalance);
                        userManager.updateUserData(targetUser);
                        transactionManager.onTransactionSuccess(true);
                    }else if (task.isCanceled()){
                        transactionManager.onTransactionSuccess(false);
                    }
                }
            });
        }
    }

}
