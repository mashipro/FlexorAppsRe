package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuperAdminActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SuperAdminActivity";
    private DrawerLayout drawerlayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference storageReference;
    private FirebaseStorage mStorage;

    CircleImageView circleImageView, showUserProfilePicture;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        circleImageView = findViewById(R.id.showUserProfilePicture);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser authUser = mAuth.getCurrentUser();
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(SuperAdminActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else{
                    getUserDetails(authUser);
                }
            }
        };

        drawerlayout = findViewById(R.id.super_admin);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_superAdmin);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view_superAdmin);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerlayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        showUserProfilePicture  = navigationView.getHeaderView(0).findViewById(R.id.showUserProfilePicture);
    }

    private void getUserDetails(FirebaseUser user) {
        String userUID = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setTimestampsInSnapshotsEnabled(true)
//                .setPersistenceEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);

        DocumentReference userRef = db.collection("Users").document(userUID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: User data retrieved");
                    User currentUser = task.getResult().toObject(User.class);
                    ((UserClient)(getApplicationContext())).setUser(currentUser);

                    User sembarang = ((UserClient)(getApplicationContext())).getUser();
                    Log.d(TAG, "onComplete: userUID: "+sembarang.getUserID());

                    String userID = sembarang.getUserID();

                    StorageReference storRef = storageReference.child(sembarang.getUserAvatar());
                    Log.d(TAG, "onComplete: avatar uri"+sembarang.getUserAvatar());

                    //show photo
                    Glide.with(SuperAdminActivity.this)
                            .load(storRef)
                            .into(showUserProfilePicture);

                }
            }
        });
    }

    private void afterclick() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)){
            drawerlayout.closeDrawer(GravityCompat.START);
        }
    }

    private void getVendorAppFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VendorApplistFragment()).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_vendorAppList:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VendorApplistFragment()).commit();
                afterclick();
                break;
            case R.id.nav_vendorList:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VendorListFragment()).commit();
                afterclick();
                break;
            case R.id.nav_vendorSettings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MessageFragment()).commit();
                afterclick();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)){
            drawerlayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getVendorAppFragment();
    }
}
