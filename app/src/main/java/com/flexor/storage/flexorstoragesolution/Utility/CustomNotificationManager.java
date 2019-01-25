package com.flexor.storage.flexorstoragesolution.Utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.Models.Notification;
import com.flexor.storage.flexorstoragesolution.Models.NotificationSend;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class CustomNotificationManager {
    private static final String TAG = "CustomNotifManager";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;
    private FirebaseApp mFirebase;

    private StorageReference storageReference;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private DatabaseReference databaseReference;

    public CustomNotificationManager() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mFirebase = FirebaseApp.getInstance();
        databaseReference = mDatabase.getReference().child("UsersData").child(mUser.getUid()).child("NotificationSend");
    }

    public void setNotification (String targetUserID, NotificationSend notificationSend) {
        DatabaseReference notifRefTarget = mDatabase.getReference().child("UsersData").child(targetUserID).child("NotificationSend").push();
        notificationSend.setNotificationID(databaseReference.getKey());
        notificationSend.setNotificationIsActive(checkNotifActive(notificationSend));
        notificationSend.setNotificationTime(ServerValue.TIMESTAMP);
        notifRefTarget.setValue(notificationSend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: notificationSend is Set");
            }
        });
    }

    private boolean checkNotifActive(NotificationSend notificationSend) {
        if (notificationSend.getNotificationIsActive()==null){
            return false;
        } else {
            return notificationSend.getNotificationIsActive();
        }
    }
    public void notificationListener(){
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Notification notification = dataSnapshot.getValue(Notification.class);
//                Log.d(TAG, "onDataChange: new notificationSend posted on user id: "+ mUser.getUid());
//                Log.d(TAG, "onDataChange: with notificationSend ID: "+ notification.getNotificationID());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        databaseReference.addValueEventListener(valueEventListener);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Notification notification = dataSnapshot.getValue(Notification.class);

                Log.d(TAG, "onChildAdded: new NotificationSend: "+ mUser.getUid());
                Log.d(TAG, "onChildAdded: with id: " + dataSnapshot.getKey());
                Log.d(TAG, "onChildAdded: notificationSend Content: "+ notification);
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
}
