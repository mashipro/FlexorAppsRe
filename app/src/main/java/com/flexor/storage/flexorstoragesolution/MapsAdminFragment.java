package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.CustomMapInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import static com.flexor.storage.flexorstoragesolution.Utility.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.MAPVIEW_BUNDLE_KEY;


public class MapsAdminFragment extends Fragment
//        implements OnMapReadyCallback {
//
//    private static final String TAG = "MapsAdminFragment";
//
//    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
//    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
//
//    private boolean mLocationPermissionGranted = false;
//    private GoogleMap mMap;
//    private MapView mMapView;
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        Log.d(TAG, "onMapReady: map is ready");
//        Toast.makeText(getContext(), "Map is Ready!", Toast.LENGTH_SHORT).show();
//            mMap = googleMap;
//       }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_maps_admin, container, false);
//        getLocationPermission();
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    private void initMap(Bundle savedInstanceState){
//        Log.d(TAG, "initMap: map initialized");
//        SupportMapFragment mapFragment = (SupportMapFragment)getFragmentManager().findFragmentById(R.id.mapAdmin);
//
//        mapFragment.getMapAsync(MapsAdminFragment.this);
//        Bundle mapViewBundle = null;
//        if (savedInstanceState != null){
//            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
//        }
//
//        mMapView.onCreate(mapViewBundle);
//        mMapView.getMapAsync(this);
//        getLocationPermission();
//
//    }
//
//    private void getLocationPermission(){
//        Log.d(TAG, "getLocationPermission: getting permissions");
//
//        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION};
//
//        if (ContextCompat.checkSelfPermission(this.getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            if (ContextCompat.checkSelfPermission(this.getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                mLocationPermissionGranted = true;
//            }else{
//                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
//            }
//        }
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called");
//        mLocationPermissionGranted = false;
//
//        switch (requestCode){
//            case LOCATION_PERMISSION_REQUEST_CODE:{
//                if (grantResults.length > 0){
//                     for (int i = 0; i < grantResults.length; i++){
//                         if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
//                             mLocationPermissionGranted = false;
//                             Log.d(TAG, "onRequestPermissionsResult: permission failed");
//                             return;
//                         }
//                     }
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
//                    mLocationPermissionGranted = true;
//                    //Initialize Map
////                    initMap();
//
//                }
//            }
//        }
//    }
//}






//?????????????????????????














        implements OnMapReadyCallback, View.OnClickListener {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_admin, container, false);
        mMapAdminView = view.findViewById(R.id.mapAdmin);
        initAdminGoogleMap(savedInstanceState);

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

//    private void addMapMarkers() {
//        final MarkerOptions newMarker = new MarkerOptions()
////                        .position(new LatLng(mUserVendor.getVendorGeoLocation().getLatitude(), mUserVendor.getVendorGeoLocation().getLongitude()))
//                .position(new LatLng(LocationServices))
//                .title(mUserVendor.getVendorStorageName())
//                .snippet(mUserVendor.getVendorAddress())
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_maps_pin_green));
//        mMap.addMarker(newMarker);
//        CustomMapInfo customMapInfo = new CustomMapInfo(getActivity());
//        mMap.setInfoWindowAdapter(customMapInfo);
////                UserVendor userVendorForTags = new UserVendor();
////                userVendorForTags.setVendorIDImgPath(mUserVendor.getVendorIDImgPath());
////                userVendorForTags.setVendorStorageName(mUserVendor.getVendorStorageName());
//        Marker m = mMap.addMarker(newMarker);
//        m.setTag(mUserVendor);
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Log.d(TAG, "onMarkerClick: " + marker.getTitle() + " is clicked");
//                marker.showInfoWindow();
//                moveCamera(marker.getPosition(), DEFAULT_ZOOM, 200, -200);
//                return true;
//            }
//        });
//    }

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
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, 0, 0);
                        Log.d(TAG, "UserLocationDetails: Lat: " + location.getLatitude() + ", long: " + location.getLongitude());

                    }
                }
            });
        } else {
            Log.d(TAG, "getDeviceLocation: Failed to retrieve location");
        }
    }

    private void moveCamera(LatLng latLng, int zoom, int offsetX, int offsetY) {
        Log.d(TAG, "moveCamera: Moving the camera to lat:" + latLng.latitude + ", lng:" + latLng.longitude);
        mMapAdmin.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        android.graphics.Point mapPoint = mMapAdmin.getProjection().toScreenLocation(latLng);
        mapPoint.set(mapPoint.x + offsetX, mapPoint.y + offsetY);
        mMapAdmin.moveCamera(CameraUpdateFactory.newLatLngZoom(mMapAdmin.getProjection().fromScreenLocation(mapPoint), zoom));
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
}
