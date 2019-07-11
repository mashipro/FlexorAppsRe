package com.flexor.storage.flexorstoragesolution.Utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoxManager {
    private static final String TAG = "BoxManager";

    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    private CollectionReference boxColRef,userBoxColRef, userVendorRef;

    private UserManager userManager;
    private User user;
    private UserVendor userVendor;

    private ArrayList<SingleBox> userSingleBoxArray = new ArrayList<>();
    private ArrayList<SingleBox> vendorSingleBoxArray = new ArrayList<>();
    private ArrayList<Box> boxArray = new ArrayList<>();
    private ArrayList<String> arrayList = new ArrayList<>();

    public BoxManager() {
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        userManager = new UserManager();
        userManager.getInstance();
        user = userManager.getUser();

        boxColRef = mFirestore.collection("Boxes");
        userVendorRef = mFirestore.collection("Vendor");
    }

    public void getUserBox(final SingleBoxListener singleBoxListener){
        userSingleBoxArray.clear();
        Log.d(TAG, "getUserBox: getting user boxes (SingleBox)");
        userBoxColRef = mFirestore.collection("Users").document(user.getUserID()).collection("MyRentedBox");
        userBoxColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<SingleBox> userBoxList = task.getResult().toObjects(SingleBox.class);
                    userSingleBoxArray.addAll(userBoxList);
                    singleBoxListener.onBoxReceived(userSingleBoxArray);
                }
            }
        });
    }

    public void getVendorBox (String userID, final SingleBoxListener singleBoxListener){
        vendorSingleBoxArray.clear();
        CollectionReference documentReference = mFirestore.collection("Vendor").document(userID).collection("MyBox");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<SingleBox> boxList = task.getResult().toObjects(SingleBox.class);
                    vendorSingleBoxArray.addAll(boxList);
                    singleBoxListener.onBoxReceived(vendorSingleBoxArray);
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
    public void boxDataSeparator (ArrayList<SingleBox> boxes, BoxDataSeparatorListener boxDataSeparatorListener){
        Log.d(TAG, "boxDataSeparator: separating this box: "+boxes);
        arrayList.clear();
        Map<String, Set<SingleBox>> map = new HashMap<String, Set<SingleBox>>();
        for (SingleBox x: boxes){
            if (!map.containsKey(x.getBoxVendor())){
                map.put(x.getBoxVendor(), new HashSet<SingleBox>());
            }
            map.get(x.getBoxVendor()).add(x);
            Log.d(TAG, "boxDataSeparator: "+ map);
            boxDataSeparatorListener.onDataSeparated(map);
            for (String y: map.keySet()){
                if (!arrayList.contains(y)){
                    arrayList.add(y);
                    boxDataSeparatorListener.onDataSeparatedArray(arrayList);
                }

            }
        }

    }

    public void getVendorData (String vendorID, final VendorDataListener vendorDataListener){
        userVendorRef.document(vendorID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    userVendor = task.getResult().toObject(UserVendor.class);
                    vendorDataListener.onVendorDataReceived(userVendor);
                }
            }
        });
    }

    public void getBoxWithVendorID (String vendorID, ArrayList<SingleBox> boxes, SingleBoxListener singleBoxListener){
        ArrayList<SingleBox> boxWithQual = new ArrayList<>();
        for (SingleBox thisBox: boxes){
            if (thisBox.getBoxVendor().equals(vendorID)){
                boxWithQual.add(thisBox);
                singleBoxListener.onBoxReceived(boxWithQual);
            }
        }
    }

    public void checkBoxValidity (Context context, Box box, BoxValidityChecker boxValidityChecker){
        Calendar boxDate = Calendar.getInstance();
        boxDate.setTime(box.getBoxRentTimestamp());
        boxValidityChecker.onBoxValidityChecked(boxDate.getTimeInMillis()<=System.currentTimeMillis());
        if (boxDate.getTimeInMillis()<= System.currentTimeMillis()){
            boxValidityChecker.onBoxValidityChecked(false);
        }else {
            boxValidityChecker.onBoxValidityChecked(true);
        }
        boxValidityChecker.boxExpirationDate(boxDate.getTime());
    }
}
