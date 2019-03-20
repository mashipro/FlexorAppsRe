package com.flexor.storage.flexorstoragesolution.Utility;

import android.support.annotation.NonNull;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserLogs;
import com.flexor.storage.flexorstoragesolution.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    public Boolean refreshUserData(){
        ((UserClient)(getApplicationContext())).setUser(null);
        getInstance();
        return true;
    }
    public void getAndStoreToken(){
        Log.d(TAG, "getUserToken: getting user token with uid: "+ mUser.getUid());
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String idToken = instanceIdResult.getToken();
                Log.d(TAG, "getUserToken: user token: "+ idToken);
                storeToken(idToken);
            }
        });
    }

    public void storeToken(String idToken) {
        Log.d(TAG, "storeToken: storing token");
        DatabaseReference tokenRef = mDatabase.getReference().child("UsersData").child(mUser.getUid()).child("UNToken");
        tokenRef.setValue(idToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "storeToken: stored!");
            }
        });
    }

    public User getUser(){
        getInstance();
        Log.d(TAG, "getUser: ID: "+((UserClient)(getApplicationContext())).getUser().getUserID());
        return ((UserClient)(getApplicationContext())).getUser();
    }
    
    public void updateUserData(final User newUserData, final Integer statCode, final String referenceID){
        Log.d(TAG, "updateUserData: updating user data client!!!");
        final User userHistories = ((UserClient)(getApplicationContext())).getUser();
        checkDifferences(userHistories.getUserID(), userHistories, newUserData);
        ((UserClient)(getApplicationContext())).setUser(newUserData);
        Log.d(TAG, "updateUserData: updating user data in firebase !!!");
        userRef.document(newUserData.getUserID()).set(newUserData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "updateUserData: operation success!!!");
                generateUserLogs(userHistories,statCode, referenceID);
            }
        });
    }

    public void getUserDataByID(String userID, final GetUserData getUserData){
        Log.d(TAG, "getUserDataByID: "+ userID);
        userRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: "+ task.getResult().toObject(User.class));
                    getUserData.onDataAcquired(task.getResult().toObject(User.class));
                }
            }
        });
    }
    public void updateUserDataByID(User current, final User history, final int statCode, final String referenceID){
        Log.d(TAG, "updateUserDataByID: updating data!!!");
        checkDifferences(current.getUserID(), history, current);
        userRef.document(current.getUserID()).set(current).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "updateUserDataByID: operation success!!!");
                generateUserLogs(history, statCode, referenceID);
            }
        });
    }

    private void checkDifferences(String userID, User userHistories, User newUserData) {
        Log.d(TAG, "checkDifferences: for id: "+userID);
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
            Log.d(TAG, "checkDifferences: userAvatar changed from: "+ userHistories.getUserAvatar()+
                    " to: "+ newUserData.getUserAvatar());
        }
        if (!userHistories.getUserCity().equals(newUserData.getUserCity())){
            Log.d(TAG, "checkDifferences: userCity changed from: "+ userHistories.getUserCity()+
                    " to: "+ newUserData.getUserCity());
        }
        if (!userHistories.getUserPhone().equals(newUserData.getUserPhone())){
            Log.d(TAG, "checkDifferences: userPhone changed from: "+ userHistories.getUserPhone()+
                    " to: "+ newUserData.getUserPhone());
        }
        if (!userHistories.getUserBalance().equals(newUserData.getUserBalance())){
            Log.d(TAG, "checkDifferences: userBalance changed from: "+ userHistories.getUserBalance()+
                    " to: "+ newUserData.getUserBalance());
        }
    }

    private void generateUserLogs(User userHistories, int logsStat, String referenceID) {
        final DocumentReference newRef= userRef.document(userHistories.getUserID()).collection("MyUserHistories").document();
        
        Log.d(TAG, "generateUserLogs: ID: "+userHistories.getUserID());
        UserLogs userLogsStore = new UserLogs();
        userLogsStore.setLogsTime(null);
        userLogsStore.setUserLogsID(newRef.getId());
        userLogsStore.setUserLogsStatsCode(logsStat);
        userLogsStore.setUserHistory(userHistories);
        userLogsStore.setReferenceID(referenceID);
        newRef.set(userLogsStore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "generateUserLogs: upDB success with id: "+ newRef.getId());
            }
        });
    }


}
