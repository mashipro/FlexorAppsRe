package com.flexor.storage.flexorstoragesolution;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flexor.storage.flexorstoragesolution.Models.Notification;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomNotificationManager;
import com.flexor.storage.flexorstoragesolution.Utility.NotificationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

import static com.flexor.storage.flexorstoragesolution.Utility.Constants.ERROR_DIALOG_REQUEST;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private DocumentReference docReference;
    private NavigationView navigationView;
    private CollectionReference collectionReference;
    private ArrayList<UserVendor> vendorArrayList = new ArrayList<>();
    private User user;
    private MenuItem item1;

    private boolean mLocationPermissionGranted = false;
    private CustomNotificationManager customNotificationManager;
    private TextView notifCount;

    private CircleImageView circleImageView, showUserProfilePicture;
    private TextView usernameHeader, userCredits;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        circleImageView = findViewById(R.id.showUserProfilePicture);
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        firebaseUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        user = ((UserClient) getApplicationContext()).getUser();
        item1 = findViewById(R.id.admin_page);





        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser authUser = firebaseAuth.getCurrentUser();
                if (authUser != null) {
                    user = ((UserClient) getApplicationContext()).getUser();
                    getUserDetails();
                    authCode();
                }
                else {
                    startActivity(new Intent(MainActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        };

        /**Init Notification listener*/


        drawerLayout = findViewById(R.id.drawer_layout_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (checkMapServices()){
            if (mLocationPermissionGranted){
                getMapsFragment();
//                getUserDetails();
            }else {
                getLocationPermission();
            }
        }

        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_Maps);
        }
        circleImageView  = navigationView.getHeaderView(0).findViewById(R.id.showUserProfilePicture);
        usernameHeader = navigationView.getHeaderView(0).findViewById(R.id.userNameHeader);
        userCredits = navigationView.getHeaderView(0).findViewById(R.id.userCredits);

        notifCount = (TextView) navigationView.getMenu().findItem(R.id.nav_main_notification).getActionView();

        customNotificationManager = new CustomNotificationManager();
        customNotificationManager.notificationListener(new NotificationListener() {
            @Override
            public void onNewNotificationReceived(Notification notification, ArrayList<Notification> activeNotificationArray, int activeNotificationCount) {
                Log.d(TAG, "onNewNotificationReceived: "+ notification);
                Log.d(TAG, "onNewNotificationReceived: "+ activeNotificationCount);
                Log.d(TAG, "onNewNotificationReceived: "+ activeNotificationArray);
                notifCount.setText(activeNotificationCount>0?String.valueOf(activeNotificationCount): null);
            }

        });
    }

    private void getUserDetails() {
        Log.d(TAG, "getUserDetails: getting User Details from: "+firebaseUser.getUid());
        user = ((UserClient) getApplicationContext()).getUser();
//        String userUID = firebaseUser.getUid();
        String userUID = user.getUserID();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference userRef = db.collection("Users").document(userUID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: User data retrieved");
                    User currentUser = task.getResult().toObject(User.class);
                    Log.d(TAG, "onComplete: User Is: "+ currentUser.toString());
                    ((UserClient)getApplicationContext()).setUser(currentUser);
                    User userr = ((UserClient)(getApplicationContext())).getUser();
                    String userID = userr.getUserID();

                        StorageReference storRef = storageReference.child(userr.getUserAvatar());
                        Glide.with(getApplicationContext())
                                .load(storRef)
                                .into(circleImageView);
                    Log.d(TAG, "onComplete: NAMA SAYA ADALAH "+currentUser.getUserName());

                        String userName = currentUser.getUserName();
                        usernameHeader.setText(userName);
                        Integer userBalance = currentUser.getUserBalance();
                        userCredits.setText("IDR"+ Integer.toString(userBalance));


                        Log.d(TAG, "onComplete: avatar uri"+currentUser.getUserAvatar());
                    Log.d(TAG, "onComplete: userUID: "+currentUser.getUserID());


                    ((UserClient)(getApplicationContext())).setUser(currentUser);
                    if (currentUser.getUserAuthCode()==Constants.STATSCODE_USER_VENDOR){
                        DocumentReference vendorRef = mFirestore.collection("Vendor").document(currentUser.getUserID());
                        vendorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    UserVendor userVendor = task.getResult().toObject(UserVendor.class);
                                    ((UserClient)(getApplicationContext())).setUserVendor(userVendor);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void getMapsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsFragment()).addToBackStack(null).commit();
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                        checkMapServices();
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            Log.d(TAG, "isMapsEnabled: GPS Disabled. Getting permission to Enable GPS");
            return false;
        } else {
            Log.d(TAG, "isMapsEnabled: GPS Enabled");
            return true;
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.d(TAG, "getLocationPermission: getting location permission");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermission: location permission Denied");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "getLocationPermission: location permission Granted");
            mLocationPermissionGranted = true;
//            getLastKnownLocation();
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
            case R.id.nav_vendor_dashboard:
                TransitionalStatCode transitionalStatCode = new TransitionalStatCode();
                transitionalStatCode.setDerivedPaging(Constants.TRANSITIONAL_STATS_CODE_IS_VENDOR);
                ((UserClient)(getApplicationContext())).setTransitionalStatCode(transitionalStatCode);
                startActivity(new Intent(getApplicationContext(),VendorActivity.class));
                afterclick();
                break;
            case R.id.nav_main_customerService:

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
//        int id = item.getItemId();

//        if (id == R.id.nav_logout) {
//            mAuth.signOut();
//            Log.d("TAG", "Logout!!");
//        }

        return true;
    }

    private void authCode(){
        user = ((UserClient) getApplicationContext()).getUser();
        if (user.getUserAuthCode()!= 199) {
            navigationView = (NavigationView) findViewById(R.id.nav_view_main);
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.admin_page).setVisible(false);
        }
        if (user.getUserAuthCode() != 201) {
            navigationView = (NavigationView) findViewById(R.id.nav_view_main);
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.nav_vendor_dashboard).setVisible(false);
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
    }

    private void afterclick(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() >0){
                getFragmentManager().popBackStackImmediate();
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
    public void onClick(View view) {
        switch (view.getId()){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()){
            if (mLocationPermissionGranted){
                getMapsFragment();
//                getLastKnownLocation();
            }else {
                getLocationPermission();
            }
        }
    }
}

// TODO: 2/27/2019 penambahan nama alamat foto pada database