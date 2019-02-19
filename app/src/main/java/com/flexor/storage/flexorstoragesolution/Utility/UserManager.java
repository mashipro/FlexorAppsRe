package com.flexor.storage.flexorstoragesolution.Utility;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private DatabaseReference databaseReference;

    public UserManager() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        boxesRef = mFirestore.collection("Boxes");
        userRef = mFirestore.collection("Users");
        vendorRef = mFirestore.collection("Vendor");
    }

    public void getInstance(){

    }
}
