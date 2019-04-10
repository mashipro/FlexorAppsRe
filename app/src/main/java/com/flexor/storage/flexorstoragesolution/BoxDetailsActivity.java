package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.BoxItem;
import com.flexor.storage.flexorstoragesolution.Models.Notification;
import com.flexor.storage.flexorstoragesolution.Models.NotificationSend;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.BoxManager;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomNotificationManager;
import com.flexor.storage.flexorstoragesolution.Utility.NotificationListener;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.flexor.storage.flexorstoragesolution.Utility.VendorDataListener;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class BoxDetailsActivity extends AppCompatActivity {
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

    private DocumentReference documentReference;
    private CollectionReference collectionReference;

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
    private CustomNotificationManager customNotificationManager;
    private BoxManager boxManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_details);

        ////Init Firebase////
        mFirestore = FirebaseFirestore.getInstance();

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

        ////Getting Bundle////
        box = ((UserClient) (getApplicationContext())).getBox();
        boxManager = new BoxManager();
        transitionalStatCode = ((UserClient)(getApplicationContext())).getTransitionalStatCode();
        if (((UserClient)(getApplicationContext())).getUserVendor()==null){
            boxManager.getVendorData(box.getUserVendorOwner(), new VendorDataListener() {
                @Override
                public void onVendorDataReceived(UserVendor vendorData) {
                    userVendor = vendorData;
                    populateView();
                }
            });
        }else {
            userVendor = ((UserClient)(getApplicationContext())).getUserVendor();
            populateView();
        }
        Log.d(TAG, "onCreate: transitionCode"+ transitionalStatCode.getDerivedPaging());
        Log.d(TAG, "onCreate: userVendor: "+ userVendor);
        Log.d(TAG, "onCreate: box: "+ box);


        Log.d(TAG, "onCreate: checking User ....");
        user = ((UserClient) (getApplicationContext())).getUser();
        if (user != null) {
            Log.d(TAG, "onCreate: user Found: " + user.getUserName() + " id: " + user.getUserID());
        } else {
            Log.d(TAG, "onCreate: user Not Found");
        }

        /**Getting transition code*/


        /**Init NotificationSend listener*/
        customNotificationManager = new CustomNotificationManager();
//        customNotificationManager.notificationListener(new NotificationListener() {
//            @Override
//            public void onNewNotificationReceived(Notification notification, ArrayList<Notification> activeNotificationArray, int activeNotificationCount) {
//                Log.d(TAG, "onCallback: notification Array: "+activeNotificationArray);
//                Log.d(TAG, "onCallback: notification count: "+ activeNotificationCount);
//                Log.d(TAG, "onCallback: new Notification: "+ notification);
//            }
//        });

        //Todo Access box Method Local / Remote
        //Todo Enable disable method
        //Todo Enable disable Rules
        //Todo Contacts Tenants @chats / @VOip

    }

    private void populateView() {
        /**Populate View*/
        //Todo get vendor image

        storageName.setText(userVendor.getVendorStorageName());
        tenantName.setText(box.getBoxTenant());
        vendorLoc.setText(userVendor.getVendorStorageLocation());
        String price = Constants.CURRENCY + userVendor.getVendorBoxPrice();
        rentRate.setText(price);

        viewChecker();
        getDistance();
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

    private void viewChecker(){
        int boxStatCode = box.getBoxStatCode().intValue();
        boolean boxProcess = box.getBoxProcess();
        if (transitionalStatCode.getDerivedPaging() == Constants.TRANSITIONAL_STATS_CODE_IS_USER){
            btnContact.setText(R.string.contact_vendor);
            btnDisable.setVisibility(View.GONE);
            if (boxStatCode == 311){
                if (boxProcess){
                    btnEnable.setVisibility(View.GONE);
                    btnBoxAccess.setText(R.string.cancel_access);
                    boxStatus.setText(R.string.box_stat_wait_empty);
                    btnBoxAccess.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAccess();
                        }
                    });
                }else {
                    btnEnable.setVisibility(View.GONE);
                    btnBoxAccess.setText(R.string.box_access);
                    boxStatus.setText(R.string.box_stat_empty);
                    btnBoxAccess.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestAccess();
                        }
                    });
                }
            } else if (boxStatCode == 312){
                btnEnable.setVisibility(View.VISIBLE);
                btnEnable.setText(R.string.box_view_item);
                btnEnable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkItem();
                    }
                });
                btnEnable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewBoxItem();
                    }
                });
                if (boxProcess){
                    btnBoxAccess.setText(R.string.cancel_access);
                    boxStatus.setText(R.string.box_stat_wait_full);
                    btnBoxAccess.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAccessOnFull();
                        }
                    });
                }else {
                    btnBoxAccess.setText(R.string.box_access);
                    boxStatus.setText(R.string.box_stat_full);
                    btnBoxAccess.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestAccessOnFull();
                        }
                    });
                }
            }

        } else if (transitionalStatCode.getDerivedPaging()== Constants.TRANSITIONAL_STATS_CODE_IS_VENDOR){
            btnContact.setText(R.string.contact_tenant);
            if (boxStatCode == 311){
                btnBoxAccess.setVisibility(View.GONE);
                if (boxProcess){
                    btnEnable.setText(R.string.accept);
                    btnDisable.setText(R.string.decline);
                    boxStatus.setText(R.string.box_stat_wait_empty);
                    btnEnable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acceptRequest();
                        }
                    });
                    btnDisable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            declineRequest();
                        }
                    });
                }else {
                    boxStatus.setText(R.string.box_stat_empty);
                    btnEnable.setVisibility(View.GONE);
                    btnDisable.setVisibility(View.GONE);
                }
            } else if (boxStatCode == 312){
                if (boxProcess){
                    boxStatus.setText(R.string.box_stat_wait_full);
                    btnBoxAccess.setVisibility(View.GONE);
                    btnEnable.setText(R.string.accept);
                    btnDisable.setText(R.string.decline);
                    btnEnable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acceptRequestWhileFull();
                        }
                    });
                    btnDisable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            declineRequestWhileFull();
                        }
                    });
                }else {
                    boxStatus.setText(R.string.box_stat_full);
                    btnBoxAccess.setVisibility(View.GONE);
                    btnEnable.setVisibility(View.GONE);
                    btnDisable.setVisibility(View.GONE);
                }
            }

        } else if (transitionalStatCode.getDerivedPaging() == Constants.TRANSITIONAL_STATS_CODE_IS_MASTER){
            if (boxStatCode == 311){
                if (boxProcess){
                    boxStatus.setText(R.string.box_stat_wait_empty);
                }else {
                    boxStatus.setText(R.string.box_stat_empty);
                }
            } else if (boxStatCode == 312){
                if (boxProcess){
                    boxStatus.setText(R.string.box_stat_wait_full);
                }else {
                    boxStatus.setText(R.string.box_stat_full);
                }
            }

        }
    }

    private void checkItem() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_itemlist_independent, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(R.drawable.bg_color_grey_translucent));

        /**popup view*/
        RecyclerView itemRecycler = popupView.findViewById(R.id.itemlist_recyclerview);
        Button goButton = popupView.findViewById(R.id.buttonOk);


    }

    private void declineRequestWhileFull() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoxDetailsActivity.this);
        builder.setTitle(R.string.alert_boxaccess_denied);
        builder.setMessage(R.string.alert_boxaccess_denied_message);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postNewNotification(Constants.NOTIFICATION_STATS_USERBOXACCESSREQUESTDENIED,box.getBoxTenant());
                changeDBstat(false, box.getBoxStatCode());
                viewChecker();
                dialog.dismiss();

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

    private void acceptRequestWhileFull() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoxDetailsActivity.this);
        builder.setTitle(R.string.alert_access_accept);
        builder.setMessage(R.string.alert_access_accept_message);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postNewNotification(Constants.NOTIFICATION_STATS_USERBOXACCESSREQUESTACCEPTED,box.getBoxTenant());
                deleteBoxItem();
                changeDBstat(false, 311);
                viewChecker();
                dialog.dismiss();
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

    private void cancelAccessOnFull() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoxDetailsActivity.this);
        builder.setTitle(R.string.alert_request_access);
        builder.setMessage(R.string.alert_request_access_message);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postNewNotification(Constants.NOTIFICATION_STATS_USERBOXACCESSREQUESTCANCEL,box.getUserVendorOwner());
                changeDBstat(false, box.getBoxStatCode());
                viewChecker();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestAccessOnFull() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoxDetailsActivity.this);
        builder.setTitle(R.string.alert_request_access);
        builder.setMessage(R.string.alert_request_access_message);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postNewNotification(Constants.NOTIFICATION_STATS_USERBOXACCESSREQUEST,box.getUserVendorOwner());
                changeDBstat(true, box.getBoxStatCode());
                viewChecker();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void declineRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoxDetailsActivity.this);
        builder.setTitle(R.string.alert_boxaccess_denied);
        builder.setMessage(R.string.alert_boxaccess_denied_message);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postNewNotification(Constants.NOTIFICATION_STATS_USERBOXACCESSREQUESTDENIED,box.getBoxTenant());
                deleteBoxItem();
                changeDBstat(false, box.getBoxStatCode());
                viewChecker();
                dialog.dismiss();

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

    private void acceptRequest() {
        startActivity(new Intent(this,BoxItemListActivity.class));
    }

    private void cancelAccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoxDetailsActivity.this);
        builder.setTitle(R.string.alert_request_cancel);
        builder.setMessage(R.string.alert_request_cancel_message);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postNewNotification(Constants.NOTIFICATION_STATS_USERBOXACCESSREQUESTCANCEL,box.getUserVendorOwner());
                deleteBoxItem();
                changeDBstat(false, box.getBoxStatCode());
                viewChecker();
                dialog.dismiss();
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

    private void requestAccess() {
        if (userIsFar()){
            getPopUp(calculateUserDistance(), getAdditionalMessage());
        }else {
            getPopUp(calculateUserDistance(), getAdditionalMessage());
        }
    }

    private void viewBoxItem() {
        //todo: viewbox item method
    }

    //////END OF BUTTON RESOLVER///////
    private void postNewNotification(int notifStat, String notifTarget){
        NotificationSend newNotif = new NotificationSend();
        customNotificationManager.setNotification(notifTarget,box.getBoxID(),notifStat);
    }

    private void changeDBstat(Boolean inProgressState, int boxStatsCode) {
        box.setBoxProcess(inProgressState);
        box.setBoxStatCode(boxStatsCode);
        DocumentReference documentReference = mFirestore.collection("Boxes").document(box.getBoxID());
        documentReference.set(box).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "box process changed!");
                Log.d(TAG, "this box id: "+box.getBoxID());
                Log.d(TAG, "details: "+ box);
                viewChecker();
            }
        });
    }

    private void deleteBoxItem() {
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
    }

    private String getAdditionalMessage() {
        int userDistance = calculateUserDistance();
        return "your distance to Box is "+userDistance+" Meters. please select your access method.";
    }

    private boolean userIsFar() {
        return !(calculateUserDistance() <= Constants.MAXRANGE_METERS_SHORT);
    }

    private void getPopUp(int v, String additionalMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.box_access_request);
        builder.setMessage(additionalMessage);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.access, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: pos clicked");
                startActivity(new Intent(BoxDetailsActivity.this,BoxItemListActivity.class));
                finish();
            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: neut clicked");
            }
        });
//        builder.setNegativeButton(R.string.access_local, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d(TAG, "onClick: neg clicked");
//            }
//        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int calculateUserDistance() {
        int distances = (int) SphericalUtil.computeDistanceBetween(userLocGeo,vendorLocGeo);
        Log.d(TAG, "calculateUserDistance: "+distances);
        return distances;
    }
}
