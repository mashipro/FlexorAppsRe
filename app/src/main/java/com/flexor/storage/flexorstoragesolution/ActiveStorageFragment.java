package com.flexor.storage.flexorstoragesolution;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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
    private CollectionReference userVendorBoxesRef, boxesRef;
    private ArrayList<Box> boxArray = new ArrayList<>();
    private ArrayList<SingleBox> vendorSingleBoxArray = new ArrayList<>();
    private ArrayList<SingleBox> userBoxArray = new ArrayList<>();
    private ArrayList<Box> userVendorBoxArray = new ArrayList<>();
    private User user;
    private UserVendor userVendor;
    private RecyclerView.Adapter<BoxGlobalViewHolder> boxGlobalViewHolderAdapter;


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
        Log.d(TAG, "onCreateView: checking user info.....");
        Log.d(TAG, "onCreateView: user info: id: " + user.getUserID());

        //Document Reference//
        userVendorRef = mFirestore.collection("Vendor").document(user.getUserID());
        userVendorBoxesRef = mFirestore.collection("Vendor").document(user.getUserID()).collection("MyBox");
        boxesRef = mFirestore.collection("Boxes");

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
                    Log.d(TAG, "onComplete: UserVendor Name: " + userVendor.getVendorStorageName());

                    ////Work on Header////-
                    vendorNameTest.setText(userVendor.getVendorStorageName());

                }
            });
        }else {
            userVendor = ((UserClient)(getApplicationContext())).getUserVendor();
        }
        ////Default view////
        textNoBox.setVisibility(View.VISIBLE);

        //FAB BUTTON//
        addBox.setOnClickListener(this);
        vendorSettings.setOnClickListener(this);

        /**Getting user and vendor  box data
         * user box data: */
        userBoxArray = ((UserClient)(getApplicationContext())).getTransitionalStatCode().getSingleBoxesContainer();
        //RecyclerView Method

//        prepareRecycler();
        getBoxData();
        return view;
    }

    private void getBoxData() {
        // TODO: 18/02/2019 Sort data in arraylist using comparator!!! 
        userVendorBoxArray.clear();
        vendorSingleBoxArray.clear();
        userVendorBoxesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<SingleBox> thisBox = task.getResult().toObjects(SingleBox.class);
                    vendorSingleBoxArray.addAll(thisBox);
                    for (SingleBox thisSingleBox: vendorSingleBoxArray){
                        boxesRef.document(thisSingleBox.getBoxID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    userVendorBoxArray.add(task.getResult().toObject(Box.class));
                                    Log.d(TAG, "onComplete: add box to array id: "+ task.getResult().toObject(Box.class).getBoxID());
                                    initRecyclerView();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void initRecyclerView(){
        /**Initialize Recyclerview and bind model*/
        boxGlobalViewHolderAdapter = new RecyclerView.Adapter<BoxGlobalViewHolder>() {
            @NonNull
            @Override
            public BoxGlobalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_box, parent, false);
                return new BoxGlobalViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull BoxGlobalViewHolder holder, int position) {
                final Box thisBoxBind = userVendorBoxArray.get(position);
                holder.bindData(thisBoxBind);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accessBoxDetails(thisBoxBind);
                    }
                });
                ImageView boxExtra = holder.itemView.findViewById(R.id.box_extra);
                boxExtra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getContext(),v);
                        MenuInflater inflater = popupMenu.getMenuInflater();
                        inflater.inflate(R.menu.menu_box_details, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.nav_box_Details:
                                        accessBoxDetails(thisBoxBind);

                                        return true;
                                    case R.id.nav_box_Access:
                                        Log.d(TAG, "onMenuItemClick: boxAccess pressed");

                                        //Todo boxAccess pressed
                                        return true;
                                    case R.id.nav_box_remove:
                                        Log.d(TAG, "onMenuItemClick: boxRemove pressed");

                                        //Todo boxRemove pressed
                                        return true;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @Override
            public int getItemCount() {
                if (userVendorBoxArray.size() == 0){
                    return 0;
                }else {
                    return userVendorBoxArray.size();
                }
            }
        };
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerViewBoxDetails.setHasFixedSize(false);
        recyclerViewBoxDetails.setItemViewCacheSize(20);
        recyclerViewBoxDetails.setDrawingCacheEnabled(true);
        recyclerViewBoxDetails.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewBoxDetails.setLayoutManager(mLayoutManager);
        recyclerViewBoxDetails.setAdapter(boxGlobalViewHolderAdapter);
        if (recyclerViewBoxDetails.getChildCount() > 0){
            textNoBox.setVisibility(View.VISIBLE);
        } else {
            textNoBox.setVisibility(View.GONE);
        }

    }

    private void accessBoxDetails(Box thisBoxBind) {
        ((UserClient)(getApplicationContext())).setBox(thisBoxBind);
        Intent movePage = new Intent(getContext(),BoxDetailsActivity.class);
        getContext().startActivity(movePage);
    }

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
                    input.setMaxLines(8);

                    alert.setView(input);
                    alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (input.getText().length() <= 1 || input.getText().length() >= 9){
                                Toast.makeText(getApplicationContext(), "Error adding box", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Text need to be filled and less than 8 character", Toast.LENGTH_SHORT).show();
                            }else{
                                checkVendorLimit(input.getText().toString());
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
    private int getBoxLimit(){
        //todo: add more limiter criterion
        if (userVendor.getVendorStatsCode() == Constants.STATSCODE_VENDOR_ACCEPTED){
            Log.d(TAG, "getBoxLimit: box limit is: "+ Constants.BOXMAXLIMITTIER_A);
            return Constants.BOXMAXLIMITTIER_A;
        }else {
            Log.d(TAG, "getBoxLimit: box limit is: "+ Constants.BOXMAXLIMITTIER_A);
            return Constants.BOXMAXLIMITTIER_A;
        }
    }
    private boolean boxIsLimit(){
        if (userVendorBoxArray.size() >= getBoxLimit()){
            Log.d(TAG, "boxIsLimit: true");
            return true;
        }else{
            return false;
        }
    }

    private void checkVendorLimit(final String text) {
        if (!boxIsLimit()){
            addVendorBox(text);
        } else{
            Toast.makeText(getApplicationContext(), R.string.error_box_limit, Toast.LENGTH_SHORT).show();
        }

    }

    private void addVendorBox(String text) {
        ////saving to box collection////
        boxCollectionRef = mFirestore.collection("Boxes").document();
        final Box newBox = new Box();
        newBox.setUserVendorOwner(user.getUserID());
        newBox.setBoxName(text);
        newBox.setBoxID(boxCollectionRef.getId());
        newBox.setBoxStatCode(301);
        newBox.setBoxProcess(false);
        boxCollectionRef.set(newBox).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Data Uploaded to: " +userVendorBoxRef.getPath());
                }
            }
        });
        saveBox(boxCollectionRef.getId(),newBox.getUserVendorOwner());
        textNoBox.setVisibility(View.GONE);
    }

    private void saveBox(String id, String userVendorOwner) {
        userVendorBoxRef = userVendorRef.collection("MyBox").document(id);
        SingleBox singleBox = new SingleBox();
        singleBox.setBoxID(id);
        singleBox.setBoxVendor(userVendorOwner);
        userVendorBoxRef.set(singleBox);
        getBoxData();
    }

    @Override
    public void onStart() {
        super.onStart();
//        mFirestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFirestoreRecyclerAdapter != null){
//            mFirestoreRecyclerAdapter.stopListening();
        }
    }
}
