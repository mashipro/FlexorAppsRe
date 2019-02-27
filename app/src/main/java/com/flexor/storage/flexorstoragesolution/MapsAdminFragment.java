package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.MAPVIEW_BUNDLE_KEY;


public class MapsAdminFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        View.OnClickListener {
//        , LocationListener {

    //    private MapView mapView;
    private MapView mMapAdminView;
    private GoogleMap mMapAdmin;
    private FusedLocationProviderClient mAdminFusedLocationProviderClient;
    //Var
    private static final String TAG = "MapsFragment";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int DEFAULT_ZOOM = 15;
    Marker mCurrLocationMarker = null;
    GoogleApiClient mGoogleApiClient;
    TextView geoPoint;
    CircleImageView savegeoButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private static final int MESSAGE_ID_SAVE_CAMERA_POSITION = 1;
    private static final int MESSAGE_ID_READ_CAMERA_POSITION = 2;
    private CameraPosition lastCameraPosition;
    private Handler handler;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private UserVendor userVendor;

    private LatLng latLngYo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_admin, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        userVendor = ((UserClient) getApplicationContext()).getUserVendor();

        mMapAdminView = view.findViewById(R.id.mapAdmin);
        initAdminGoogleMap(savedInstanceState);

        geoPoint = view.findViewById(R.id.tv_mapAdmin);
        savegeoButton = view.findViewById(R.id.button_saveGeo);

        // Read from the database
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

//        savegeoButton.setOnClickListener(this);
        savegeoButton.setOnClickListener(this);
        return view;
    }

    private void initAdminGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Log.d(TAG, "initGoogleMap: Maps Initializing");
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapAdminView.onCreate(mapViewBundle);
        mMapAdminView.getMapAsync(this);
        getLocationPermission();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdminFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permission = {FINE_LOCATION, COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermission: location permission Denied");
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "getLocationPermission: location permission Granted");
            mLocationPermissionGranted = true;

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapAdminView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapAdminView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapAdminView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapAdminView.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_saveGeo:
                saveGeo();
                break;
        }

    }



    private void saveGeo() {
        final LatLng latLng = mMapAdmin.getCameraPosition().target;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        DocumentReference db = FirebaseFirestore.getInstance().collection("Vendor").document(userVendor.getVendorID());
                        Query query = db
                                .collection("Vendor");

                        FirestoreRecyclerOptions<UserVendor> options = new FirestoreRecyclerOptions.Builder<UserVendor>()
                                .setQuery(query, UserVendor.class)
                                .build();

                        double latt = latLngYo.latitude;
                        double longg = latLngYo.longitude;
                        Log.d(TAG, "writeGeo: "+latt + ", " + longg);

                        GeoPoint latLong = new GeoPoint((int)(latt*1E6), (int)(longg*1E6));

                        userVendor.setVendorGeoLocation(latLong);

                        mReference.child("Accepted Vendor").child(userVendor.getVendorID()).child("Lattitude").setValue(latt);
                        mReference.child("Accepted Vendor").child(userVendor.getVendorID()).child("Longitude").setValue(longg);
                        startActivity(new Intent(getApplicationContext(), AdminVendorPhotoActivity.class));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Anda yakin dengan koordinat berikut?\n" + "Lattitude: " + latLng.latitude + "\nLongitude: " + latLng.longitude).setPositiveButton("Setuju", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        Log.d(TAG, "onMapReady: map is READY");

        mMapAdmin = map;
        mMapAdmin.setOnCameraIdleListener(onCameraIdleListener);

        mMapAdmin.setOnCameraIdleListener(this);
        mMapAdmin.setOnCameraMoveStartedListener(this);
        mMapAdmin.setOnCameraMoveListener(this);
        mMapAdmin.setOnCameraMoveCanceledListener(this);


        if (mLocationPermissionGranted) {
            mMapAdmin = map;
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMapAdmin.setMyLocationEnabled(true);
            mMapAdmin.getUiSettings().setMyLocationButtonEnabled(false);

        }

    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Toast.makeText(getContext(), "The user gestured on the map.",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCameraMoveStarted: The user gestured on the map.");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            Toast.makeText(getContext(), "The user tapped something on the map.",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCameraMoveStarted: The user tapped something on the map.");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            Toast.makeText(getContext(), "The app moved the camera.",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCameraMoveStarted: The app moved the camera");
        }
    }

    @Override
    public void onCameraMove() {
//        Toast.makeText(getContext(), "The camera is moving.",
//                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCameraMove: The camera is moving");
    }

    @Override
    public void onCameraMoveCanceled() {
//        Toast.makeText(getContext(), "Camera movement canceled.",
//                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCameraMoveCanceled: Camera movement canceled");
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device current location");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mAdminFusedLocationProviderClient != null) {
            mAdminFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, "getDeviceLocation: latitude: " + geoPoint.getLatitude());
                        Log.d(TAG, "getDeviceLocation: longitude: " + geoPoint.getLongitude());
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, 0, 0,
                                "My Location");
                        Log.d(TAG, "UserLocationDetails: Lat: " + location.getLatitude() + ", long: " + location.getLongitude());

                    }
                }
            });
        } else {
            Log.d(TAG, "getDeviceLocation: Failed to retrieve location");
        }
    }

    private void moveCamera(LatLng latLng, int zoom, int offsetX, int offsetY, String title) {
        Log.d(TAG, "moveCamera: Moving the camera to lat:" + latLng.latitude + ", lng:" + latLng.longitude);
        mMapAdmin.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        android.graphics.Point mapPoint = mMapAdmin.getProjection().toScreenLocation(latLng);
        mapPoint.set(mapPoint.x + offsetX, mapPoint.y + offsetY);
        mMapAdmin.moveCamera(CameraUpdateFactory.newLatLngZoom(mMapAdmin.getProjection().fromScreenLocation(mapPoint), zoom));

        lastCameraPosition = mMapAdmin.getCameraPosition();


        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);

        mMapAdmin.addMarker(markerOptions);
//        textView.append("Lattitude: " + latLng.latitude + "  Longitude: " + latLng.longitude);
//        textView.append("Lattitude: " + lastCameraPosition);




    }

    @Override
    public void onCameraIdle() {

        LatLng latLng = mMapAdmin.getCameraPosition().target;
        Geocoder geocoder = new Geocoder(getContext());

        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null && addressList.size() > 0) {
            String locality = addressList.get(0).getAddressLine(0);
            String country = addressList.get(0).getCountryName();
            if (!locality.isEmpty() && !country.isEmpty())
//                textView.setText(locality + "  " + country);
                geoPoint.setText("Lattitude: " + latLng.latitude + "Longitude: " + latLng.longitude);
            latLngYo = latLng;
        }

    }


    @Override
    public void onPause() {
        mMapAdminView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapAdminView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapAdminView.onLowMemory();
    }

//    @Override
//    public void onLocationChanged(Location location) {
//
//        double lattitude = location.getLatitude();
//        double longitude = location.getLongitude();
//
//        //Place current location marker
//        LatLng latLng = new LatLng(lattitude, longitude);
//
//
//        if(mCurrLocationMarker!=null){
//            mCurrLocationMarker.setPosition(latLng);
//        }else{
//            mCurrLocationMarker = mMapAdmin.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    .title("I am here"));
//        }
//
//        textView.append("Lattitude: " + lattitude + "  Longitude: " + longitude);
//        mMapAdmin.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//
//    }
}
