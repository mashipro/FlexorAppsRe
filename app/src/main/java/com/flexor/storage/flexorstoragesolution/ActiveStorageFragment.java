package com.flexor.storage.flexorstoragesolution;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomSpanCount;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private FirestoreRecyclerAdapter<SingleBox,BoxesViewHolder> mFirestoreRecyclerAdapter;
    private Query mQuery;

    ///view///
    private FloatingActionButton addBox, vendorSettings;
    private ImageView headerImage;
    private TextView vendorNameTest, textNoBox;
    private RecyclerView recyclerViewBoxDetails;

    ///custom declare///
    private DocumentReference userVendorRef, userVendorBoxRef, boxCollectionRef;
    private CollectionReference userVendorBoxesRef;
    private BoxesViewHolder boxesViewHolder;
    private ArrayList<Box> mBoxArray = new ArrayList<>();
    private User user;
    private UserVendor userVendor;
    private int boxLimit = 2;


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
        textNoBox = view.findViewById(R.id.textNoBox);

        //retrieve user//
        user = ((UserClient)(getApplicationContext())).getUser();
//        getUserVendorInfo(user, userVendor);
        checkUser(user);
//        checkUserVendor(user, userVendor);

        //Document Reference//
        userVendorRef = mFirestore.collection("Vendor").document(user.getUserID());
//        userVendorBoxRef = userVendorRef.collection("Boxes").document();
        userVendorBoxesRef = mFirestore.collection("Vendor").document(user.getUserID()).collection("MyBox");

        ////getting userVendor////
        Log.d(TAG, "onCreateView: checking userVendor Info .....");
        if (userVendor == null){
            Log.d(TAG, "onCreateView: userVendor info not found. getting from database!");
            userVendorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "onComplete: userVendor info retrieved from: " +userVendorRef.toString());
                    userVendor = task.getResult().toObject(UserVendor.class);
                    ((UserClient)(getApplicationContext())).setUserVendor(userVendor);
                    Log.d(TAG, "onComplete: UserVendor Info retrieved");
                    Log.d(TAG, "onComplete: UserVendorName: " + userVendor.getVendorStorageName());

                    ////Work on Header////-
                    vendorNameTest.setText(userVendor.getVendorStorageName());

//                    ////Work on Parameter//// c
//                    if (userVendor.getVendorStatsCode().intValue() == 211){
//                        boxLimit = 9;
//                    } else if (userVendor.getVendorStatsCode().intValue() == 212){
//                        boxLimit = 12;
//                    }
                }
            });
        }else {
            userVendor = ((UserClient)(getApplicationContext())).getUserVendor();
        }

        //------------------//
        ////Default view////
        textNoBox.setVisibility(View.VISIBLE);

        //FAB BUTTON//
        addBox.setOnClickListener(this);
        vendorSettings.setOnClickListener(this);

        ////Check whether box document exist yet////
        userVendorBoxesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot: task.getResult()){
                        if (documentSnapshot.exists()){
                            textNoBox.setVisibility(View.GONE);
                        }else {
                            textNoBox.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        //Work on RecyclerView//
        mQuery = userVendorBoxesRef.orderBy("boxID",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<SingleBox> recyclerOptions = new FirestoreRecyclerOptions.Builder<SingleBox>()
                .setQuery(mQuery,SingleBox.class)
                .build();
        mFirestoreRecyclerAdapter = new FirestoreRecyclerAdapter<SingleBox, BoxesViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BoxesViewHolder holder, int position, @NonNull SingleBox model) {
                holder.bindBox(model);
                Log.d(TAG, "onBindViewHolder: binding: "+model);
            }

            @NonNull
            @Override
            public BoxesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_box, parent, false);
                return new BoxesViewHolder(view);

            }
        };
        int spanNumber = CustomSpanCount.calculateNoOfColumns(getApplicationContext(), Constants.SINGLEBOX_SPAN_WIDTH);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), spanNumber);
        recyclerViewBoxDetails.setHasFixedSize(true);
        recyclerViewBoxDetails.setItemViewCacheSize(20);
        recyclerViewBoxDetails.setDrawingCacheEnabled(true);
        recyclerViewBoxDetails.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewBoxDetails.setLayoutManager(mLayoutManager);
        recyclerViewBoxDetails.setAdapter(mFirestoreRecyclerAdapter);
        recyclerViewBoxDetails.getChildCount();

        ////Working on Alert dialog////


        return view;
    }

    private void checkUser(User user) {
        if (user != null){
            Log.d(TAG, "checkUser: user: "+user.getUserName()+ ", " + user.getUserID());
        }
    }

//    private void checkUserVendor(User user, UserVendor userVendor) {
//        Log.d(TAG, "checkUserVendor: checking ......");
//        if (userVendor != null){
//            Log.d(TAG, "checkUserVendor: userVendor is: " +userVendor.getVendorStorageName()+", "+ userVendor.getVendorID());
//        }else{
//            Log.d(TAG, "checkUserVendor: userVendor not found, getting vendor");
////            getUserVendorInfo(user, userVendor);
//        }
//    }
//    private void getUserVendorInfo(final User user, UserVendor userVendor) {
//        userVendorRef = mFirestore.collection("Vendor").document(user.getUserID());
//        userVendorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                Log.d(TAG, "onComplete: userVendor info retrieved from: " +userVendorRef.toString());
//                UserVendor currentUserVendor = task.getResult().toObject(UserVendor.class);
//                ((UserClient)(getApplicationContext())).setUserVendor(currentUserVendor);
//                Log.d(TAG, "onComplete: UserVendor Info retrieved");
//                Log.d(TAG, "onComplete: UserVendorName: " + currentUserVendor.getVendorStorageName());
//                checkUserVendor(user, currentUserVendor);
//            }
//        });
//
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_addBox:
                if (addBox.isPressed()){
                    Log.d(TAG, "onClick: addBox");
//                    checkVendorLimit();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle(R.string.alert_new_box_title);
                    alert.setMessage(R.string.alert_new_box_message);

                    final EditText input = new EditText(getContext());

                    alert.setView(input);
                    alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (input.getText().length() <= 1 || input.getText().length() >= 8){
                                Toast.makeText(getApplicationContext(), "Error adding box", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Text need to be filled and less than 8 character", Toast.LENGTH_SHORT).show();
                            }else{
                                checkVendorLimit(input.getText());
                            }

                        }
                    });
                    alert.show();
                } break;
            case R.id.fab_vendorSettings:
                if (vendorSettings.isPressed()){
                    Log.d(TAG, "onClick: vendorSettings");
                }
        }

    }

    private void checkVendorLimit(final Editable text) {
        userVendorBoxRef = userVendorRef.collection("Boxes").document();
        userVendorBoxesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int count = 0;
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        count++;
                    }
                    if (count<=boxLimit){
                        addVendorBox(text);
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.error_box_limit, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void addVendorBox(Editable text) {
        ////saving to box collection////
        boxCollectionRef = mFirestore.collection("Boxes").document();
        final Box newBox = new Box();
        int newStatCode = 301;
        Double newnewStatCode = (double) newStatCode;
        newBox.setUserVendorOwner(user.getUserID());
        newBox.setBoxName(text.toString());
        newBox.setBoxID(boxCollectionRef.getId());
        newBox.setBoxStatCode(newnewStatCode);
        boxCollectionRef.set(newBox).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Data Uploaded to: " +userVendorBoxRef.getPath());
                }
            }
        });
        saveBox(boxCollectionRef.getId());

        textNoBox.setVisibility(View.GONE);
    }

    private void saveBox(String id) {
        userVendorBoxRef = userVendorRef.collection("MyBox").document(id);
        Map<String, Object> userID = new HashMap<>();
        userID.put("boxID", id);
        userVendorBoxRef.set(userID);
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
