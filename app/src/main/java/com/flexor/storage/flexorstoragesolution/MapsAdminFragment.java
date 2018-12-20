package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.CustomMapInfo;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;

import static com.flexor.storage.flexorstoragesolution.Utility.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.MAPVIEW_BUNDLE_KEY;


public class MapsAdminFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        View.OnClickListener, LocationListener {

    //    private MapView mapView;
    private MapView mMapAdminView;
    private GoogleMap mMapAdmin;
    private FusedLocationProviderClient mAdminFusedLocationProviderClient;
    //Var
    private static final String TAG = "MapsFragment";
    //    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    //    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionGranted = false;
    private static final int DEFAULT_ZOOM = 15;
    Marker mCurrLocationMarker = null;
    GoogleApiClient mGoogleApiClient;
    TextView textView;

    private static final int MESSAGE_ID_SAVE_CAMERA_POSITION = 1;
    private static final int MESSAGE_ID_READ_CAMERA_POSITION = 2;
    private CameraPosition lastCameraPosition;
    private Handler handler;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_admin, container, false);
        mMapAdminView = view.findViewById(R.id.mapAdmin);
        initAdminGoogleMap(savedInstanceState);

        textView = view.findViewById(R.id.tv_mapAdmin);

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
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            Toast.makeText(getContext(), "The user tapped something on the map.",
                    Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            Toast.makeText(getContext(), "The app moved the camera.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraMove() {
        Toast.makeText(getContext(), "The camera is moving.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraMoveCanceled() {
        Toast.makeText(getContext(), "Camera movement canceled.",
                Toast.LENGTH_SHORT).show();
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
                textView.setText("Lattitude: " + latLng.latitude + "Longitude: " + latLng.longitude);
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

    @Override
    public void onLocationChanged(Location location) {

        double lattitude = location.getLatitude();
        double longitude = location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(lattitude, longitude);


        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.setPosition(latLng);
        }else{
            mCurrLocationMarker = mMapAdmin.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("I am here"));
        }

        textView.append("Lattitude: " + lattitude + "  Longitude: " + longitude);
        mMapAdmin.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }
}
