package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.BoxItem;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BoxDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BoxDetailsActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseUser mUser;
    private FirebaseFirestoreSettings mFirestoreSettings;
    private DocumentReference vendorRef;
    private Query mQuery;

    ///View///
    private ImageView boxBG, boxDetails;
    private TextView storageName, boxName, boxStatus, vendorLoc, tenantName, duration, rentDue, rentRate;
    private CircleImageView tenantAvatar;
    private Button btnBoxAccess, btnEnable, btnDisable, btnContact;

    ///CustomDeclare///
    private User user;
    private UserVendor userVendor;
    private Box box;
    private TransitionalStatCode transitionalStatCode;
    private LatLng userLocGeo, vendorLocGeo;
    private int boxStats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_details);

        ////Init Firebase////
        mFirestore = FirebaseFirestore.getInstance();

        ////Getting Bundle////
        box = ((UserClient) (getApplicationContext())).getBox();
        Log.d(TAG, "onCreate: boxData : " + box.getBoxName() + " boxID: " + box.getBoxID());


        Log.d(TAG, "onCreate: checking User ....");
        user = ((UserClient) (getApplicationContext())).getUser();
        if (user != null) {
            Log.d(TAG, "onCreate: user Found: " + user.getUserName() + " id: " + user.getUserID());
        } else {
            Log.d(TAG, "onCreate: user Not Found");
        }

        /**Getting transition code*/
        transitionalStatCode = ((UserClient)(getApplicationContext())).getTransitionalStatCode();
        Log.d(TAG, "onCreate: getting transitional stat code: " +transitionalStatCode.getDerivedPaging());


        ////Setting View////

        boxBG = findViewById(R.id.box_Head_BG);
        boxDetails = findViewById(R.id.box_details);
        storageName = findViewById(R.id.storage_name);
        boxName = findViewById(R.id.box_name);
        boxStatus = findViewById(R.id.box_status);
        vendorLoc = findViewById(R.id.vendor_registration_location);
        tenantName = findViewById(R.id.tenant_name);
        duration = findViewById(R.id.tenant_box_duration);
        rentDue = findViewById(R.id.tenant_box_due);
        rentRate = findViewById(R.id.tenant_box_rate);
        tenantAvatar = findViewById(R.id.box_avatar);
        btnBoxAccess = findViewById(R.id.btn_vendor_box_access);
        btnEnable = findViewById(R.id.btn_vendor_box_enable);
        btnDisable = findViewById(R.id.btn_vendor_box_disable);
        btnContact = findViewById(R.id.btn_vendor_box_contacts_tenant);

        ////Getting Vendor////
        String vendorUid = box.getUserVendorOwner();
        vendorRef = mFirestore.collection("Vendor").document(vendorUid);
        vendorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userVendor = new UserVendor();
                userVendor = task.getResult().toObject(UserVendor.class);
                storageName.setText(userVendor.getVendorStorageName());
                boxName.setText(box.getBoxName());
                getBoxStatus();
                getDistance();
                vendorLoc.setText(userVendor.getVendorStorageLocation());
            }
        });

        /**modify view based on source of execution*/
        if (executedByUser()){
           btnEnable.setVisibility(View.GONE);
           btnDisable.setVisibility(View.GONE);
           btnContact.setText(getString(R.string.button_contact_vendor));
        }

        /**
         * invoking button method
         */
        btnBoxAccess.setOnClickListener(this);

        ////Filling View////
        //Todo get vendor image
        tenantName.setText(box.getBoxTenant());


//        tenantName.setText(box.getBoxTenant());
//        duration.setText(box.getBoxRentDuration().toString());

        //Todo get tenant avatar and details
        //Todo get rent duration and due date
        //Todo set price rate method
        //Todo Access box Method Local / Remote
        //Todo Enable disable method
        //Todo Enable disable Rules
        //Todo Contacts Tenants @chats / @VOip

    }

    private boolean executedByUser() {
        return transitionalStatCode.getDerivedPaging() == Constants.TRANSITIONAL_STATS_CODE_IS_USER;
    }

    private void getDistance() {
        FusedLocationProviderClient locationServices = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationServices.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()){
                    Location location = task.getResult();
                    userLocGeo= new LatLng(
                            location.getLatitude(),
                            location.getLongitude());
                    vendorLocGeo = new LatLng(
                            userVendor.getVendorGeoLocation().getLatitude(),
                            userVendor.getVendorGeoLocation().getLongitude());
//                    double distance = SphericalUtil.computeDistanceBetween(userLocGeo, vendorLocGeo);
                }
            }
        });
    }

    private void getBoxStatus() {
        boxStats = box.getBoxStatCode().intValue();
        if (boxStats == 301) {
            boxStatus.setText(R.string.box_stat_available);
            Log.d(TAG, "getBoxStatus: available");
        } else if (boxStats == 311) {
            boxStatus.setText(R.string.box_stat_empty);
            Log.d(TAG, "getBoxStatus: empty");
            btnBoxAccess.setText(getString(R.string.box_access));
        } else if (boxStats == 312) {
            boxStatus.setText(R.string.box_stat_full);
            Log.d(TAG, "getBoxStatus: full");
        } else if (boxStats == 321) {
            boxStatus.setText(R.string.box_stat_wait);
            Log.d(TAG, "getBoxStatus: processed");
            if (executedByUser()){
                btnBoxAccess.setText(getString(R.string.cancel_access));
            }else{
                btnBoxAccess.setText(getString(R.string.reject));
            }

        }
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_vendor_box_access:
//                Log.d(TAG, "onClick: box Acces click");
//                calculateUserDistance();
//        }
        if (btnBoxAccess.isPressed()){
            Log.d(TAG, "onClick: box Acces click");
            if (boxStats == 311){
                if (userIsFar()){
                    getPopUp(calculateUserDistance(), getAdditionalMessage());
                }else {
                    getPopUp(calculateUserDistance(), getAdditionalMessage());
                }
            }else if (boxStats == 312){
                Log.d(TAG, "onClick: box full. attempt to access");

            } else if (boxStats == 321){
                if (executedByUser()){
                    cancelAccess();
                }
            }

        }

    }

    private void cancelAccess() {
        final ArrayList<BoxItem> boxItemsArray = new ArrayList<>();
        DocumentReference documentReference = mFirestore.collection("Boxes").document(box.getBoxID());
        final CollectionReference boxItemRef = documentReference.collection("BoxItems");
        boxItemRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isComplete()){
                    List<BoxItem> boxItems = task.getResult().toObjects(BoxItem.class);
                    boxItemsArray.addAll(boxItems);
                    Log.d(TAG, "onComplete: itemsList in box"+ boxItemsArray);
                    for (BoxItem thisBoxItem: boxItemsArray){
                        boxItemRef.document(thisBoxItem.getBoxItemID()).delete();
                        Log.d(TAG, "deleting this item id: "+thisBoxItem.getBoxItemID());
                    }
                }
            }
        });
        box.setBoxStatCode((double)311);
        documentReference.set(box).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Log.d(TAG, "onComplete: boxStatsCode changed to: "+ box.getBoxStatCode());
                    getBoxStatus();
                }
            }
        });
    }

    private String getAdditionalMessage() {
        int userDistance = calculateUserDistance();
        return "your distance to Box is "+userDistance+" Meters. please select your access method.";
    }

    private boolean userIsFar() {
        return !(calculateUserDistance() <= Constants.MAXRANGE_METERS_SHORT);
    }

//    private void userIsMedium() {
//    }

//    private void userIsClose() {
//    }

    private void getPopUp(int v, String additionalMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.box_access_request);
        builder.setMessage(additionalMessage);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.access_remote, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: pos clicked");
                getBoxAccessCheck();
            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: neut clicked");
            }
        });
        builder.setNegativeButton(R.string.access_local, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: neg clicked");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getBoxAccessCheck() {
        if (boxIsEmpty()){
            getManifestInput();
        } else {
            Log.d(TAG, "getBoxAccessCheck: box stats is full.. Getting Access");
        }
        //todo: give condition if box is full access either add item or remove

    }

    private void getManifestInput() {
        startActivity(new Intent(BoxDetailsActivity.this,BoxItemListActivity.class));
        finish();
    }

    private boolean boxIsEmpty() {
        return box.getBoxStatCode().intValue() == 311;
    }

    private int calculateUserDistance() {
        int distances = (int) SphericalUtil.computeDistanceBetween(userLocGeo,vendorLocGeo);
        Log.d(TAG, "calculateUserDistance: "+distances);
        return distances;
    }
}
