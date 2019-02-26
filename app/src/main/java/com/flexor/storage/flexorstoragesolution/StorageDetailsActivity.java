package com.flexor.storage.flexorstoragesolution;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomSpanCount;
import com.flexor.storage.flexorstoragesolution.ViewHolder.BoxesViewHolder;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1beta1.DocumentTransform;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class StorageDetailsActivity extends AppCompatActivity {
    private static final String TAG = "StorageDetailsActivity";

    //Firebase Init
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;

    //Reff Init
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private CollectionReference vendorBoxRef, boxesRef, userBoxRef;

    //View Init
    private ImageView headerVendorImage;
    private TextView vendorName;
    private TextView vendorLocation;
    private RecyclerView recyclerViewVendorDetails;

    //Custom Declare
    private UserVendor userVendor;
    private ArrayList<SingleBox> vendorBoxAL = new ArrayList<>();
    private ArrayList<Box> vendorNonDisabledBox = new ArrayList<>();
    private Query mQuery;
    private FirestoreRecyclerAdapter<SingleBox,BoxesViewHolder> mFirestoreRecyclerAdapter;
    private RecyclerView.Adapter<BoxGlobalViewHolder> boxGlobalViewHolderAdapter;

    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_details);

        //Firebase onCreate Init
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        mUser = mAuth.getCurrentUser();

        //View onCreate Init
        headerVendorImage =findViewById(R.id.vendor_header_image);
        vendorName = findViewById(R.id.vendor_name);
        vendorLocation = findViewById(R.id.vendor_location);
        recyclerViewVendorDetails = findViewById(R.id.recyclerViewVendorDetails);


        //Getting Vendor
        userVendor = ((UserClient)(getApplicationContext())).getUserVendor();

        //Updating UI
        vendorName.setText(userVendor.getVendorStorageName());
        vendorLocation.setText(userVendor.getVendorStorageLocation());
        //Todo: Updating Vendor Image

        vendorBoxRef = mFirestore.collection("Vendor").document(userVendor.getVendorID()).collection("MyBox");
        boxesRef = mFirestore.collection("Boxes");
        userBoxRef = mFirestore.collection("Users").document(mUser.getUid()).collection("MyRentedBox");

        //Getting Vendor Box Info
        getBoxData();


//        mQuery = vendorBoxRef.orderBy("boxID",Query.Direction.ASCENDING);
//        FirestoreRecyclerOptions<SingleBox> recyclerOptions = new FirestoreRecyclerOptions.Builder<SingleBox>()
//                .setQuery(mQuery,SingleBox.class)
//                .build();
//        mFirestoreRecyclerAdapter = new FirestoreRecyclerAdapter<SingleBox, BoxesViewHolder>(recyclerOptions) {
//            @Override
//            protected void onBindViewHolder(@NonNull BoxesViewHolder holder, int position, @NonNull SingleBox model) {
//                holder.bindBox(model);
//                Log.d(TAG, "onBindViewHolder: binding " +model);
//            }
//
//            @NonNull
//            @Override
//            public BoxesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_box, viewGroup,false);
//                return new BoxesViewHolder(view);
//            }
//        };
//        int spanNumber = CustomSpanCount.calculateNoOfColumns(this,Constants.SINGLEBOX_SPAN_WIDTH);
//        GridLayoutManager mLayoutManager = new GridLayoutManager(this,spanNumber);
//        recyclerViewVendorDetails.setHasFixedSize(false);
//        recyclerViewVendorDetails.setItemViewCacheSize(20);
//        recyclerViewVendorDetails.setDrawingCacheEnabled(true);
//        recyclerViewVendorDetails.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        recyclerViewVendorDetails.setAdapter(mFirestoreRecyclerAdapter);
//        recyclerViewVendorDetails.setLayoutManager(mLayoutManager);

    }

    private void getBoxData() {
        // TODO: 19/02/2019 sort array data
        vendorBoxAL.clear();
        vendorNonDisabledBox.clear();
        Log.d(TAG, "getBoxData: getting data....");
        vendorBoxRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<SingleBox> thisSingleBox = task.getResult().toObjects(SingleBox.class);
                    vendorBoxAL.addAll(thisSingleBox);
                    for (SingleBox thisVendorSingleBox: vendorBoxAL){
                        boxesRef.document(thisVendorSingleBox.getBoxID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Box thisBox = task.getResult().toObject(Box.class);
                                    if (boxDisabled(thisBox.getBoxStatCode())){
                                        Log.d(TAG, "this box: "+thisBox.getBoxID()+" is disabled");
                                    } else{
                                        if (boxRentedByOtherTenant(thisBox.getBoxStatCode(), thisBox.getBoxTenant())){
                                            Log.d(TAG, "this box: "+thisBox.getBoxID()+" is rented by other people");
                                        }else {
                                            Log.d(TAG, "this box: "+thisBox.getBoxID()+" is available or user box. stats code: "+ thisBox.getBoxStatCode());
                                            vendorNonDisabledBox.add(thisBox);
                                            initRecyclerView();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    private void initRecyclerView() {

        Log.d(TAG, "initRecyclerView: initialize RecyclerView");
        boxGlobalViewHolderAdapter = new RecyclerView.Adapter<BoxGlobalViewHolder>() {
            @NonNull
            @Override
            public BoxGlobalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_box, parent, false);
                return new BoxGlobalViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull BoxGlobalViewHolder holder, int position) {
                final Box thisBoxBinding = vendorNonDisabledBox.get(position);
                holder.bindData(thisBoxBinding);
                Log.d(TAG, "onBindViewHolder: bind box id: "+ thisBoxBinding.getBoxID());
                ImageView boxExtra = holder.itemView.findViewById(R.id.box_extra);
                boxExtra.setVisibility(View.GONE);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (boxAvailable(thisBoxBinding)){
                            rentConfirmation(thisBoxBinding);
                        }else {
                            moveToBoxDetails(thisBoxBinding);
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return vendorNonDisabledBox.size();
            }
        };
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,3);
        recyclerViewVendorDetails.setHasFixedSize(false);
        recyclerViewVendorDetails.setItemViewCacheSize(20);
        recyclerViewVendorDetails.setDrawingCacheEnabled(true);
        recyclerViewVendorDetails.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewVendorDetails.setAdapter(boxGlobalViewHolderAdapter);
        recyclerViewVendorDetails.setLayoutManager(mLayoutManager);
    }

    private void rentConfirmation(final Box thisBoxBinding) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_box_rent, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        
        popupWindow.setBackgroundDrawable(new ColorDrawable(R.drawable.bg_color_grey_translucent));
        RadioGroup daySelection = popupView.findViewById(R.id.radio_group);
        RadioButton rad3 = popupView.findViewById(R.id.checkbox3day);
        RadioButton rad7 = popupView.findViewById(R.id.checkbox7day);
        RadioButton rad14 = popupView.findViewById(R.id.checkbox14day);
        RadioButton rad30 = popupView.findViewById(R.id.checkbox30day);
        TextView boxRate = popupView.findViewById(R.id.box_rate);
        final TextView boxTotal = popupView.findViewById(R.id.bill_total);
        Button acceptButton = popupView.findViewById(R.id.accept_button);
        Button cancelButton = popupView.findViewById(R.id.cancel_button);

        Log.d(TAG, "rentConfirmation: "+ userVendor.getVendorBoxPrice());
        boxRate.setText(String.valueOf(userVendor.getVendorBoxPrice()));
        daySelection.check(R.id.checkbox3day);
        if (rad3.isChecked()){
            boxTotal.setText(String.valueOf(getTotal(3)));
            duration=3;
        }else if (rad7.isChecked()){
            boxTotal.setText(String.valueOf(getTotal(7)));
            duration=7;
        }else if (rad14.isChecked()){
            boxTotal.setText(String.valueOf(getTotal(14)));
            duration=14;
        }else if (rad30.isChecked()){
            boxTotal.setText(String.valueOf(getTotal(30)));
            duration=30;
        }
        daySelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.checkbox3day){
                    boxTotal.setText(String.valueOf(getTotal(3)));
                }else if (checkedId == R.id.checkbox7day){
                    boxTotal.setText(String.valueOf(getTotal(7)));
                }else if (checkedId == R.id.checkbox14day){
                    boxTotal.setText(String.valueOf(getTotal(14)));
                }else if (checkedId == R.id.checkbox30day){
                    boxTotal.setText(String.valueOf(getTotal(30)));
                }
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StorageDetailsActivity.this);
                builder.setTitle(R.string.alert_rent_confirmation);
                builder.setMessage(R.string.alert_rent_confirmation_message);
                builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateBox(thisBoxBinding, duration);
                        saveUserBox(thisBoxBinding);
                        popupWindow.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);

    }

    private void updateBox(Box thisBoxBinding, int i) {
        Box newBox = thisBoxBinding;
        newBox.setBoxLastChange(null);
        newBox.setBoxRentDuration(duration);
        newBox.setBoxRentTimestamp(null);
        newBox.setBoxTenant(mUser.getUid());
        newBox.setBoxStatCode(Constants.STATSCODE_BOX_EMPTY);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(newBox.getBoxCreatedDate());
//        calendar.add(Calendar.DATE,i);
//        Log.d(TAG, "updateBox: from calendar: "+newBox.getBoxCreatedDate());
//        Log.d(TAG, "updateBox: to date: " + calendar.getTime());
        Log.d(TAG, "updateBox: "+ newBox);
        boxesRef.document(newBox.getBoxID()).set(newBox).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    getBoxData();
                    Log.d(TAG, "onComplete: update box success..!!!");
                    // TODO: 19/02/2019 add notif!
                }
            }
        });
        // TODO: 19/02/2019 balance transaction box rent!

    }

    private void saveUserBox(Box thisBoxBinding) {
        SingleBox singleBox = new SingleBox();
        singleBox.setBoxID(thisBoxBinding.getBoxID());
        singleBox.setBoxVendor(thisBoxBinding.getUserVendorOwner());
        Log.d(TAG, "saveUserBox: "+ singleBox);
        userBoxRef.add(singleBox).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Log.d(TAG, "onComplete: saving box success..!!!");
                // TODO: 19/02/2019 add notif!
            }
        });
    }

    private int getTotal(int i) {
        return userVendor.getVendorBoxPrice()*i;
    }

    private void moveToBoxDetails(Box thisBoxBinding) {
        ((UserClient)(getApplicationContext())).setBox(thisBoxBinding);
        Intent movePage = new Intent(StorageDetailsActivity.this,BoxDetailsActivity.class);
        startActivity(movePage);
    }

    private boolean boxAvailable(Box thisBoxBinding) {
        return thisBoxBinding.getBoxStatCode()==Constants.STATSCODE_BOX_AVAILABLE;
    }

    private boolean boxRentedByOtherTenant(Integer boxStatCode, String boxTenant) {
        return boxStatCode != Constants.STATSCODE_BOX_AVAILABLE && !boxTenant.equals(mUser.getUid());
    }

    private boolean boxDisabled(Integer boxStatCode) {
        return boxStatCode == Constants.STATSCODE_BOX_DISABLED;
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
