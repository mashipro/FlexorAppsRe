package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;

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
    private LatLng userLocGeo, vendorLocGeo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_details);

        ////Init Firebase////
        mFirestore = FirebaseFirestore.getInstance();

        ////Getting Bundle////
        box = ((UserClient) (getApplicationContext())).getBox();
        Log.d(TAG, "onCreate: boxData : " + box.getBoxName() + " boxID: " + box.getBoxID());

        ////Checking User////
        Log.d(TAG, "onCreate: checking User ....");
        user = ((UserClient) (getApplicationContext())).getUser();
        if (user != null) {
            Log.d(TAG, "onCreate: user Found: " + user.getUserName() + " id: " + user.getUserID());
        } else {
            Log.d(TAG, "onCreate: user Not Found");
        }

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
                vendorLoc.setText(userVendor.getVendorStorageLocation());
            }
        });
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

    private void getBoxStatus() {
        int boxStats = box.getBoxStatCode().intValue();
        if (boxStats == 301) {
            boxStatus.setText(R.string.box_stat_available);
        } else if (boxStats == 311) {
            boxStatus.setText(R.string.box_stat_empty);
        } else if (boxStats == 312) {
            boxStatus.setText(R.string.box_stat_full);
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
            calculateUserDistance();
        }

    }

    private void calculateUserDistance() {
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
                    double distance = SphericalUtil.computeDistanceBetween(userLocGeo, vendorLocGeo);
                    Log.d(TAG, "onComplete: distance is: "+ distance);
                }
            }
        });


    }
}
