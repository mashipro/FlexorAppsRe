package com.flexor.storage.flexorstoragesolution.Utility;

import android.support.annotation.NonNull;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BoxManager {
    private static final String TAG = "BoxManager";

    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    private CollectionReference boxColRef,userBoxColRef;

    private UserManager userManager;
    private User user;

    private ArrayList<SingleBox> userSingleBoxArray = new ArrayList<>();
    private ArrayList<SingleBox> vendorSingleBoxArray = new ArrayList<>();
    private ArrayList<Box> boxArray = new ArrayList<>();

    public BoxManager() {
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        userManager = new UserManager();
        userManager.getInstance();

        user = userManager.getUser();

        boxColRef = mFirestore.collection("Boxes");
    }

    public void getUserBox(final UserBoxListener userBoxListener){
        userSingleBoxArray.clear();
        Log.d(TAG, "getUserBox: getting user boxes (SingleBox)");
        userBoxColRef = mFirestore.collection("Users").document(user.getUserID()).collection("MyRentedBox");
        userBoxColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<SingleBox> userBoxList = task.getResult().toObjects(SingleBox.class);
                    userSingleBoxArray.addAll(userBoxList);
                    userBoxListener.onBoxReceived(userSingleBoxArray);
                }
            }
        });
    }

    public void getVendorBox (String userID, final UserBoxListener userBoxListener){
        vendorSingleBoxArray.clear();
        CollectionReference documentReference = mFirestore.collection("Vendor").document(userID).collection("MyBox");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<SingleBox> boxList = task.getResult().toObjects(SingleBox.class);
                    vendorSingleBoxArray.addAll(boxList);
                    userBoxListener.onBoxReceived(vendorSingleBoxArray);
                }
            }
        });
    }

    public void getBoxDataFromArray(ArrayList<SingleBox> boxes, final BoxDataListener boxDataListener){
        boxArray.clear();
        Log.d(TAG, "getUserBoxFromArray: getting Box from this array");
        for (SingleBox thisBox: boxes){
            Log.d(TAG, "getUserBoxFromArray: "+ thisBox);
        }
        for (SingleBox thisBox: boxes){
            boxColRef.document(thisBox.getBoxID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        Box newBox = task.getResult().toObject(Box.class);
                        boxArray.add(newBox);
                        Collections.sort(boxArray, CAS.boxNameBoxSort);
                        boxDataListener.onDataReceived(boxArray);
                    }
                }
            });
        }
    }
    public void boxDataSeparator (ArrayList<Box> boxes){

    }
}
