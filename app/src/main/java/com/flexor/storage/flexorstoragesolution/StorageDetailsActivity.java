package com.flexor.storage.flexorstoragesolution;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.Notification;
import com.flexor.storage.flexorstoragesolution.Models.NotificationSend;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomNotificationManager;
import com.flexor.storage.flexorstoragesolution.Utility.ManPaymentManager;
import com.flexor.storage.flexorstoragesolution.Utility.TransactionManager;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.flexor.storage.flexorstoragesolution.ViewHolder.BoxesViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    private ManPaymentManager manPaymentManager;
    private UserManager userManager;
    private User user;
    private CustomNotificationManager notificationManager;

    private int duration=3;

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

        //GettingUserData
        userManager = new UserManager();
        userManager.getInstance();
        user = userManager.getUser();
        manPaymentManager=new ManPaymentManager();
        notificationManager = new CustomNotificationManager();

        //Getting Vendor
        userVendor = ((UserClient)(getApplicationContext())).getUserVendor();

        //Updating UI
        vendorName.setText(userVendor.getVendorStorageName());
        vendorLocation.setText(userVendor.getVendorStorageLocation());
        storageReference = mStorage.getReference().child(userVendor.getVendorIDImgPath());
        Glide.with(getApplicationContext())
                .load(storageReference)
                .into(headerVendorImage);
        //Todo: Updating Vendor Image

        vendorBoxRef = mFirestore.collection("Vendor").document(userVendor.getVendorID()).collection("MyBox");
        boxesRef = mFirestore.collection("Boxes");
        userBoxRef = mFirestore.collection("Users").document(mUser.getUid()).collection("MyRentedBox");

        getBoxData();

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
        TextView boxRate = popupView.findViewById(R.id.box_rate);
        final TextView boxTotal = popupView.findViewById(R.id.bill_total);
        Button acceptButton = popupView.findViewById(R.id.accept_button);
        Button cancelButton = popupView.findViewById(R.id.cancel_button);

        Log.d(TAG, "rentConfirmation: "+ userVendor.getVendorBoxPrice());
        boxRate.setText(String.valueOf(userVendor.getVendorBoxPrice()));
        daySelection.check(R.id.checkbox3day);
        postLog(boxTotal);

        daySelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.checkbox3day:
                        duration = 3;
                        postLog(boxTotal);
                        break;
                    case R.id.checkbox7day:
                        duration = 7;
                        postLog(boxTotal);
                        break;
                    case R.id.checkbox14day:
                        duration = 14;
                        postLog(boxTotal);
                        break;
                    case R.id.checkbox30day:
                        duration = 30;
                        postLog(boxTotal);
                        break;
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
                        manPaymentManager.makeTransaction(
                                StorageDetailsActivity.this,
                                userVendor.getVendorID(),
                                getTotal(duration),
                                Constants.TRANSACTION__BOX_RENT,
                                thisBoxBinding.getBoxID(),
                                Constants.TRANSACTION__REFSTAT_FINISHED,
                                new TransactionManager() {
                                    @Override
                                    public void onTransactionSuccess(Boolean success, String transactionID) {
                                        Log.d(TAG, "onTransactionSuccess: "+success+" transactionID: "+ transactionID);
                                        updateBox(thisBoxBinding, duration);
                                        saveUserBox(thisBoxBinding);
                                        popupWindow.dismiss();
                                    }
                                }
                        );
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

    private void postLog(TextView boxTotal) {
        boxTotal.setText(String.valueOf(getTotal(duration)));
        Log.d(TAG, "postLog: DURATION: "+duration);
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
                }
            }
        });
    }

    private void saveUserBox(final Box thisBoxBinding) {
        SingleBox singleBox = new SingleBox();
        singleBox.setBoxID(thisBoxBinding.getBoxID());
        singleBox.setBoxVendor(thisBoxBinding.getUserVendorOwner());
        Log.d(TAG, "saveUserBox: "+ singleBox);
        userBoxRef.document(singleBox.getBoxID()).set(singleBox).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: saving box success..!!!");
                NotificationSend notification = new NotificationSend();
                notification.setNotificationStatsCode(Constants.NOTIFICATION_STATS_USERRENTBOX);
                notification.setNotificationReference(thisBoxBinding.getBoxID());
                notificationManager.setNotification(userVendor.getVendorID(),notification);
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
