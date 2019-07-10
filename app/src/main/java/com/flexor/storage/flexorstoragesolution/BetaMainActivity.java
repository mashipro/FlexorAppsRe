package com.flexor.storage.flexorstoragesolution;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.BoxManager;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomNotificationManager;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.flexor.storage.flexorstoragesolution.Utility.VendorDataListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.flexor.storage.flexorstoragesolution.Utility.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class BetaMainActivity extends AppCompatActivity {
    private static final String TAG = "BetaMainActivity";

    /*Firebase Declare*/
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    /*Firebase Reference Declare*/
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    /*View Declare*/
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Menu navMenu;
    private CircleImageView headerUserAvatar;
    private TextView headerUsername, headerUserCredits, notificationCounter;

    /*Models Declare*/
    private User user;
    private UserVendor userVendor;

    /*Components Declare*/
    private UserManager userManager;
    private BoxManager boxManager;
    private CustomNotificationManager customNotificationManager;
    private FragmentManager fragmentManager;

    /*Custom Value Declare*/
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beta_main);

        /*Firebase Init*/
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        firebaseUser = mAuth.getCurrentUser();

        /*Firebase Reference Init*/
        storageReference = mStorage.getReference();

        /*Components Init*/
        userManager = new UserManager();
        userManager.getInstance();
        userManager.getAndStoreToken();
        boxManager = new BoxManager();

        /*View Init*/
        toolbar = findViewById(R.id.toolbar_betafloatmain);
        drawerLayout = findViewById(R.id.drawer_layout_beta_main);
        navigationView = findViewById(R.id.nav_view_beta_main);
        headerUserAvatar = navigationView.getHeaderView(0).findViewById(R.id.circleImageView);
        headerUsername = navigationView.getHeaderView(0).findViewById(R.id.userNameHeader);
        headerUserCredits = navigationView.getHeaderView(0).findViewById(R.id.userCredits);
        navMenu = navigationView.getMenu();
        notificationCounter = (TextView) navMenu.findItem(R.id.nav_main_notification).getActionView();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null){
            navigationView.setCheckedItem(R.id.nav_Maps);
        }

        /*User re-Check*/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseUser != null){
                    getUserDetails();
                }else {
                    startActivity(new Intent(BetaMainActivity.this, BetaLoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        };

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_Maps:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapsFragment()).addToBackStack(null).commit();
                        afterclick();
                        break;
                    case R.id.nav_myStorageList:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MystoragelistFragment()).addToBackStack(null).commit();
                        afterclick();
                        break;
                    case R.id.nav_main_notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).addToBackStack(null).commit();
                        afterclick();
                        break;
                    case R.id.nav_main_message:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MessageFragment()).commit();
                        afterclick();
                        break;
                    case R.id.nav_vendor_register:
                        startActivity(new Intent(getApplicationContext(),VendorRegistrationActivity.class));
                        afterclick();
                        break;
                    case R.id.nav_vendor_signin:
                        TransitionalStatCode transitionalStatCode = new TransitionalStatCode();
                        transitionalStatCode.setDerivedPaging(Constants.TRANSITIONAL_STATS_CODE_IS_VENDOR);
                        ((UserClient)(getApplicationContext())).setTransitionalStatCode(transitionalStatCode);
                        startActivity(new Intent(getApplicationContext(),VendorActivity.class));
                        afterclick();
                        break;
                    case R.id.nav_main_customerService:
//                customNotificationManager.testFCMRUN();
                        startActivity(new Intent(getApplicationContext(),BetaLoginActivity.class));
                        afterclick();
                        break;
                    case R.id.nav_main_settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingsFragment()).addToBackStack(null).commit();
                        afterclick();
                        break;
                    case R.id.admin_page:
                        startActivity(new Intent(getApplicationContext(), SuperAdminActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        afterclick();
                        break;
                    case R.id.nav_logout:
                        logout();
                        afterclick();
                        break;
                }
                return true;
            }
        });

        /*Check Permissions and Resolve Permission*/
        /*Checking GPS permission*/
        if (checkMapsServices()){
            if (mLocationPermissionGranted){
                getMapsFragment();
            }else {
                getLocationPermission();
            }
        }

    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "mapsPermission: denied, asking permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
        }else {
            Log.d(TAG, "mapsPermission: granted");
            mLocationPermissionGranted = true;
        }
    }

    private void getMapsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsFragment()).addToBackStack(null).commit();
    }

    private boolean checkMapsServices() {
        if (isServicesOK()){
            if (isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGPS();
            Log.d(TAG, "isMapsEnabled: GPS DISABLED. Getting Permission to enable GPS");
            return false;
        }else {
            Log.d(TAG, "isMapsEnabled: GPS ENABLED");
            return true;
        }
    }

    private void buildAlertMessageNoGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable GPS?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                        checkMapsServices();
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(BetaMainActivity.this);
        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: error....");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(BetaMainActivity.this, available, Constants.ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this, "MAP Request Error", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(),BetaLoginActivity.class));
    }

    private void afterclick() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void getUserDetails() {
        Log.d(TAG, "getUserDetails: init");
        Log.d(TAG, "getUserDetails: loading user data ....");
        user = userManager.getUser();
        Log.d(TAG, "getUserDetails: UID: "+ user.getUserID());
        Log.d(TAG, "getUserDetails: Name: "+user.getUserName());

        /*get user avatar*/
        storageReference = storageReference.child(user.getUserAvatar());
        Glide.with(getApplicationContext())
                .load(storageReference)
                .into(headerUserAvatar);
        headerUsername.setText(user.getUserName());
        headerUserCredits.setText(String.valueOf(user.getUserBalance()));
        if (user.getUserIsVendor()){
            /*Getting Vendor Details*/
            boxManager.getVendorData(user.getUserID(), new VendorDataListener() {
                @Override
                public void onVendorDataReceived(UserVendor vendorData) {
                    userVendor = vendorData;
                    /*update View for Vendor*/
                    if (userVendor.getVendorAccepted()){
                        navMenu.findItem(R.id.nav_vendor_signin).setVisible(true);
                        navMenu.findItem(R.id.nav_vendor_register).setVisible(false);
                    }else {
                        navMenu.findItem(R.id.nav_vendor_signin).setVisible(false);
                        navMenu.findItem(R.id.nav_vendor_register).setVisible(false);
                    }
                }
            });
        }else {
            navMenu.findItem(R.id.nav_vendor_signin).setVisible(false);
            if (user.getUserAuthCode()!= 199) {
                navMenu.findItem(R.id.admin_page).setVisible(false);
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentManager.getBackStackEntryCount() >1){
                fragmentManager.popBackStackImmediate();
            }else {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
//                super.onBackPressed();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getMapsFragment();
//                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

}
