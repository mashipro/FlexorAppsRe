package com.flexor.storage.flexorstoragesolution.Utility;

import android.util.Log;

import com.flexor.storage.flexorstoragesolution.Models.Notification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NotificationManager {
    private static final String TAG = "NotificationManager";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;

    private StorageReference storageReference;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private DatabaseReference databaseReference;

    public NotificationManager() {
    }

    public void setNotification (String targetUserID, Notification notification) {
        checkNotif(notification);

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("UsersData").child(targetUserID).child("Notification").push();
        notification.setNotificationID(databaseReference.getKey());
        notification.setNotificationIsActive(checkNotif(notification));
        notification.setNotificationTime(ServerValue.TIMESTAMP);
        databaseReference.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: notification is Set");
            }
        });
    }

    private boolean checkNotif(Notification notification) {
        if (notification.getNotificationIsActive()==null){
            return false;
        } else {
            return notification.getNotificationIsActive();
        }
    }
}
