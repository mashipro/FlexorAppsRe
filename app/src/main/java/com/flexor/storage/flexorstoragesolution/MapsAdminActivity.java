package com.flexor.storage.flexorstoragesolution;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.flexor.storage.flexorstoragesolution.Utility.Constants.ERROR_DIALOG_REQUEST;

public class MapsAdminActivity extends AppCompatActivity {

    private static final String TAG = "MapsAdminActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private GoogleMap mMapAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_admin);

        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser authUser = firebaseAuth.getCurrentUser();
                if (authUser == null) {
                    startActivity(new Intent(MapsAdminActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        };

        getAdminMapsFragment();


    }

    private void getAdminMapsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsAdminFragment()).commit();
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsAdminActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsAdminActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "isMapsEnabled: GPS Disabled. Getting permission to Enable GPS");
            return false;
        } else {
            Log.d(TAG, "isMapsEnabled: GPS Enabled");
            return true;
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (checkMapServices()) {
            getAdminMapsFragment();
        }
    }
}

