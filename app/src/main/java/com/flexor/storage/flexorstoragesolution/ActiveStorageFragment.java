package com.flexor.storage.flexorstoragesolution;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.ViewHolder.BoxesViewHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ActiveStorageFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ActiveStorageFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private FirebaseUser authUser;
    private FirebaseFirestoreSettings mFirestoreSettings;
    private FirestoreRecyclerAdapter<Box,BoxesViewHolder> mFirestoreRecyclerAdapter;
    private Query mQuery;

    ///view///
    private FloatingActionButton addBox, vendorSettings;
    private ImageView headerImage;
    private TextView vendorNameTest;
    private RecyclerView recyclerViewBoxDetails;

    ///custom declare///
    private User user;
    private UserVendor userVendor;
    private DocumentReference userVendorRef, userVendorBoxRef;
    private CollectionReference userVendorBoxesRef;
    private BoxesViewHolder boxesViewHolder;
    private ArrayList<Box> mBoxArray = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_active_storage,container,false);
        //Firebase Init//
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        authUser = mAuth.getCurrentUser();


        //View Init //
        addBox = view.findViewById(R.id.fab_addBox);
        vendorSettings = view.findViewById(R.id.fab_vendorSettings);
        headerImage = view.findViewById(R.id.headerImage);
        vendorNameTest = view.findViewById(R.id.vendorNameTest);
        recyclerViewBoxDetails = view.findViewById(R.id.recyclerViewBoxDetails);

        //retrieve user//
        user = ((UserClient)(getApplicationContext())).getUser();
        userVendor = ((UserClient)(getApplicationContext())).getUserVendor();
        checkUserVendor(userVendor);
        checkUser(user);

        //Document Refference//
        userVendorRef = mFirestore.collection("Vendor").document(user.getUserID());
//        userVendorBoxRef = userVendorRef.collection("Boxes").document();
        userVendorBoxesRef = mFirestore.collection("Vendor").document(user.getUserID()).collection("Boxes");


        //------------------//
        //FAB BUTTON//
        addBox.setOnClickListener(this);
        vendorSettings.setOnClickListener(this);

        //Work on RecyclerView//
        mQuery = userVendorBoxesRef.orderBy("boxCreatedDate",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Box> recyclerOptions = new FirestoreRecyclerOptions.Builder<Box>()
                .setQuery(mQuery,Box.class)
                .build();
        mFirestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Box, BoxesViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BoxesViewHolder holder, int position, @NonNull Box model) {
                holder.bindBox(model);
            }

            @NonNull
            @Override
            public BoxesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_box, parent, false);
                return new BoxesViewHolder(view);
            }
        };


        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerViewBoxDetails.setHasFixedSize(true);
        recyclerViewBoxDetails.setItemViewCacheSize(20);
        recyclerViewBoxDetails.setDrawingCacheEnabled(true);
        recyclerViewBoxDetails.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewBoxDetails.setLayoutManager(mLayoutManager);
        recyclerViewBoxDetails.setAdapter(mFirestoreRecyclerAdapter);
        
        return view;
    }


    private void checkUser(User user) {
        if (user != null){
            Log.d(TAG, "checkUser: user: "+user.getUserName()+ ", " + user.getUserID());
        }
    }

    private void checkUserVendor(UserVendor userVendor) {
        Log.d(TAG, "checkUserVendor: checking ......");
        if (userVendor != null){
            Log.d(TAG, "checkUserVendor: userVendor is: " +userVendor.getVendorStorageName()+", "+ userVendor.getVendorID());
        }else{
            Log.d(TAG, "checkUserVendor: userVendor not found, getting vendor");
            getUserVendorInfo();
        }
    }

    private void getUserVendorInfo() {
        userVendorRef = mFirestore.collection("Vendor").document(user.getUserID());
        userVendorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d(TAG, "onComplete: userVendor info retrieved from: " +userVendorRef.toString());
                UserVendor currentUserVendor = task.getResult().toObject(UserVendor.class);
                ((UserClient)(getApplicationContext())).setUserVendor(currentUserVendor);
                Log.d(TAG, "onComplete: UserVendor Info retrieved");
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_addBox:
                if (addBox.isPressed()){
                    Log.d(TAG, "onClick: addBox");
                    checkVendorLimit();
                } break;
            case R.id.fab_vendorSettings:
                if (vendorSettings.isPressed()){
                    Log.d(TAG, "onClick: vendorSettings");
                }
        }

    }

    private void checkVendorLimit() {
        //not yet implemented

        //if not limit
        addVendorBox();

    }

    private void addVendorBox() {
        userVendorBoxRef = userVendorRef.collection("Boxes").document();
        final Box newBox = new Box();
        int newStatCode = 301;
        Double newnewStatCode = (double) newStatCode;
        newBox.setUserVendorOwner(user);
        newBox.setBoxID(userVendorBoxRef.getId());
        newBox.setBoxStatCode(newnewStatCode);
        userVendorBoxRef.set(newBox).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Data Uploaded to: " +userVendorBoxRef.getPath());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFirestoreRecyclerAdapter != null){
            mFirestoreRecyclerAdapter.stopListening();
        }
    }
}
