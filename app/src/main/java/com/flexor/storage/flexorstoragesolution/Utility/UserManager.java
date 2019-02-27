package com.flexor.storage.flexorstoragesolution.Utility;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserLogsStore;
import com.flexor.storage.flexorstoragesolution.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserManager {
    private static final String TAG = "UserManager";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;
    private FirebaseApp mFirebase;

    private StorageReference storageReference;
    private DocumentReference documentReference;
    private CollectionReference boxesRef, userRef, vendorRef;
    private DatabaseReference databaseReference, userLogsRef;
    
    private User userFromUserClient;

    public UserManager() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        boxesRef = mFirestore.collection("Boxes");
        userRef = mFirestore.collection("Users");
        vendorRef = mFirestore.collection("Vendor");
        databaseReference=mDatabase.getReference();
    }


    public void getInstance(){
        Log.d(TAG, "getInstance: getting user data for the first time....");
        if (((UserClient)(getApplicationContext())).getUser() == null ){
            Log.d(TAG, "getInstance: user data not found, download from firestore");
            userRef.document(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        User newUser = task.getResult().toObject(User.class);
                        ((UserClient)(getApplicationContext())).setUser(newUser);
                        getInstance();
                        Log.d(TAG, "onComplete: user data on firestore found!! UID: "+ newUser.getUserID());
                    }
                }
            });
        }else {
            userFromUserClient = ((UserClient)(getApplicationContext())).getUser();
            Log.d(TAG, "getInstance: user data found!!! user ID: "+userFromUserClient.getUserID());
        }
    }
    
    public User getUser(){
        Log.d(TAG, "getUser: ID: "+((UserClient)(getApplicationContext())).getUser().getUserID());
        return ((UserClient)(getApplicationContext())).getUser();
    }
    
    public void updateUserData(final User newUserData, final Integer statCode, final String referenceID){

        Log.d(TAG, "updateUserData: updating user data client!!!");
        final User userHistories = ((UserClient)(getApplicationContext())).getUser();
        checkDifferences(userHistories, newUserData);
        ((UserClient)(getApplicationContext())).setUser(newUserData);
        Log.d(TAG, "updateUserData: updating user data in firebase !!!");
        userRef.document(newUserData.getUserID()).set(newUserData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: update data success!!!");
                generateUserLogs(userHistories,newUserData.getUserID(),statCode, referenceID);
            }
        });
    }


    private void checkDifferences(User userHistories, User newUserData) {
        if (!userHistories.getUserAuthCode().equals(newUserData.getUserAuthCode())){
            Log.d(TAG, "checkDifferences: userAuthCode changed from: "+ userHistories.getUserAuthCode()+
                    " to: "+newUserData.getUserAuthCode());
        }
        if (!userHistories.getUserAddress().equals(newUserData.getUserAddress())){
            Log.d(TAG, "checkDifferences: userAddress changed from: "+ userHistories.getUserAddress()+
                    " to: "+ newUserData.getUserAddress());
        }
        if (!userHistories.getUserName().equals(newUserData.getUserName())){
            Log.d(TAG, "checkDifferences: userName changed from: "+ userHistories.getUserName()+
                    " to: "+ newUserData.getUserName());
        }
        if (!userHistories.getUserAvatar().equals(newUserData.getUserAvatar())){
            Log.d(TAG, "checkDifferences: userName changed from: "+ userHistories.getUserAvatar()+
                    " to: "+ newUserData.getUserAvatar());
        }
        if (!userHistories.getUserCity().equals(newUserData.getUserCity())){
            Log.d(TAG, "checkDifferences: userName changed from: "+ userHistories.getUserCity()+
                    " to: "+ newUserData.getUserCity());
        }
        if (!userHistories.getUserPhone().equals(newUserData.getUserPhone())){
            Log.d(TAG, "checkDifferences: userName changed from: "+ userHistories.getUserPhone()+
                    " to: "+ newUserData.getUserPhone());
        }
        if (!userHistories.getUserBalance().equals(newUserData.getUserBalance())){
            Log.d(TAG, "checkDifferences: userName changed from: "+ userHistories.getUserBalance()+
                    " to: "+ newUserData.getUserBalance());
        }
    }

    private void generateUserLogs(User userHistories, final String newUserData, int logsStat, String referenceID) {
        Log.d(TAG, "generateUserLogs: ID: "+newUserData);
        UserLogsStore userLogsStore = new UserLogsStore();
        userLogsStore.setLogsTime(ServerValue.TIMESTAMP);
        userLogsStore.setUserLogsID(newUserData);
        userLogsStore.setUserLogsStatsCode(logsStat);
        userLogsStore.setUserHistory(userHistories);
        userLogsStore.setReferenceID(referenceID);
        final DatabaseReference dataref=userLogsRef.child("UsersData").child(newUserData).child("UserLogs");
        dataref.push().setValue(userLogsStore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: user logs generated for id: "+ newUserData+" with logs id: "+dataref.getKey()+" atLocaltime: "+ System.currentTimeMillis());
            }
        });
    }


}
