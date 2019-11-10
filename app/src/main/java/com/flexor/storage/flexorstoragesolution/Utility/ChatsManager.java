package com.flexor.storage.flexorstoragesolution.Utility;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ChatsManager {
    private static final String TAG = "ChatsManager";

    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    public ChatsManager() {
        mDatabase = FirebaseDatabase.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();


    }
}
