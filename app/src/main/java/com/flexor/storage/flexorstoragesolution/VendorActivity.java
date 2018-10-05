package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class VendorActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "VendorActivity";
    private DrawerLayout drawerlayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(VendorActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        };

        drawerlayout = findViewById(R.id.drawer_layout_vendor);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_vendor);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view_vendor);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerlayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ActiveStorageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_activeStorage);
        }
    }

    private void afterclick() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)){
            drawerlayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_activeStorage:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ActiveStorageFragment()).commit();
                afterclick();
                break;
            case R.id.nav_vendor_upgrade:

                afterclick();
                break;
            case R.id.nav_main_message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MessageFragment()).commit();
                afterclick();
                break;
            case R.id.nav_vendor_signout:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                afterclick();
                break;
            case R.id.nav_main_customerService:

                afterclick();
                break;
            case R.id.nav_main_settings:

                afterclick();
                break;
            case R.id.nav_logout:
                logout();
                afterclick();
                break;
        }
        return true;
    }

    private void logout() {
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
}
