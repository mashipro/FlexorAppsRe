package com.flexor.storage.flexorstoragesolution.Utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.Models.Notification;
import com.flexor.storage.flexorstoragesolution.Models.NotificationSend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomNotificationManager {
    private static final String TAG = "CustomNotifManager";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private DatabaseReference databaseReference;
    private Notification incomingNotification;

    private ArrayList<Notification> notificationArrayList= new ArrayList<>();

    public CustomNotificationManager() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("UsersData").child(mUser.getUid()).child("Notification");
    }

    public void setNotification (String targetUserID, NotificationSend notificationSend) {
        Log.d(TAG, "setNotification: new notification request with target id: ");
        DatabaseReference notifRefTarget = mDatabase.getReference().child("UsersData").child(targetUserID).child("Notification").push();
        notificationSend.setNotificationID(notifRefTarget.getKey());
        notificationSend.setNotificationIsActive(checkNotifActive(notificationSend));
        notificationSend.setNotificationTime(ServerValue.TIMESTAMP);
        notifRefTarget.setValue(notificationSend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: notificationSend is Set");
                setFCM();
            }
        });
    }

    private void setFCM() {
        //todo: set fcm
    }

    private boolean checkNotifActive(NotificationSend notificationSend) {
        if (notificationSend.getNotificationIsActive()==null){
            return false;
        } else {
            return notificationSend.getNotificationIsActive();
        }
    }


    public void notificationListener(final NotificationListener notificationListener){
        notificationArrayList.clear();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                incomingNotification = dataSnapshot.getValue(Notification.class);
                Log.d(TAG, "notificationListener: incoming notification: "+ incomingNotification);
                if (incomingNotification.getNotificationIsActive()){
                    notificationArrayList.add(incomingNotification);
                    Log.d(TAG, "checkIfNotifIsActive: id: "+ incomingNotification.getNotificationID()+ " is active add to active array");
                    notificationListener.onNewNotificationReceived(incomingNotification,notificationArrayList, notificationArrayList.size());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
    public void setNotificationInactive(String notificationID){
        Log.d(TAG, "setNotificationInactive: id: "+ notificationID);
        databaseReference.child(notificationID).child("notificationIsActive").setValue(false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "operation successful");
                        notificationArrayList.clear();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "ERROR!", e);
            }
        });
    }
}
