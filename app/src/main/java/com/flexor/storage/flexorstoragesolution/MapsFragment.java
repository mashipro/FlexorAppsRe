package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.ClusterMarker;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.ClusterManagerRenderer;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomMapInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.Point;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.MAPVIEW_BUNDLE_KEY;

public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    //Components
    private Context context;
//    private MapView mapView;
    private MapView mMapView;
    private ImageView screenMarkOne;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //Var
    private static final String TAG = "MapsFragment";
    //    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    //    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionGranted = false;
    private static final int DEFAULT_ZOOM = 15;

    private ClusterManagerRenderer clusterManagerRenderer;
    private ClusterManager<ClusterMarker> clusterManager;
    private UserVendor userVendor;
    private FirebaseFirestore mFirestore;
    private CollectionReference collectionReference, userBoxRef, vendorBoxRef;
    private ArrayList<UserVendor> vendorArrayList = new ArrayList<>();
    private ArrayList<SingleBox> userBoxArrayList = new ArrayList<>();
    private ArrayList<SingleBox> vendorBoxArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        context = view.getContext();
        mMapView = view.findViewById(R.id.map);
        screenMarkOne = view.findViewById(R.id.screen_mark_one);

        initGoogleMap(savedInstanceState);

        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.

        Log.d(TAG, "initGoogleMap: Maps Initializing");
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        getLocationPermission();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mFirestore = FirebaseFirestore.getInstance();

        /**
         * Getting User from userClient
         */

        User currentUser = ((UserClient)(getApplicationContext())).getUser();
        String UIDS;
        if (currentUser != null){
            UIDS = currentUser.getUserID();
        } else {
            Log.d(TAG, "onViewCreated: user Not Found from UserClient");
            String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            UIDS = firebaseUser;
        }
        userBoxRef = mFirestore.collection("Users").document(UIDS).collection("MyRentedBox");
        userBoxRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<SingleBox> userRentedBoxList = task.getResult().toObjects(SingleBox.class);
                    userBoxArrayList.addAll(userRentedBoxList);
                    Log.d(TAG, "onComplete: user rented box: "+vendorArrayList);
                    getVendorList();
                }
            }
        });
    }

    private void getVendorList() {
        collectionReference = mFirestore.collection("Vendor");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: getting vendor info completed");
                    List<UserVendor> userVendorList = task.getResult().toObjects(UserVendor.class);
                    vendorArrayList.addAll(userVendorList);
                    Log.d(TAG, "onComplete: vendor list: " + vendorArrayList);
                    addMapMarkers();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady: map is READY");
        if (mLocationPermissionGranted) {
            mMap = map;
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device current location");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
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

    private void moveCamera(LatLng latLng, int zoom, @Nullable int offsetX, @Nullable int offsetY) {
        Log.d(TAG, "moveCamera: Moving the camera to lat:" +latLng.latitude + ", lng:" +latLng.longitude);
        Log.d(TAG, "moveCamera: with offset of: X" + offsetX+ " Y"+ offsetY);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        android.graphics.Point mapPoint = mMap.getProjection().toScreenLocation(latLng);
        mapPoint.set(mapPoint.x+offsetX,mapPoint.y+offsetY);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getProjection().fromScreenLocation(mapPoint),zoom));

    }

    private void addMapMarkers(){
        for (final UserVendor mUserVendor: vendorArrayList){
            try{
                Log.d(TAG, "onMapReady: pin"+ mUserVendor.getVendorGeoLocation().toString());
                final MarkerOptions newMarker = new MarkerOptions()
                        .position(new LatLng(mUserVendor.getVendorGeoLocation().getLatitude(),mUserVendor.getVendorGeoLocation().getLongitude()))
                        .title(mUserVendor.getVendorStorageName())
                        .snippet(mUserVendor.getVendorAddress())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_maps_pin_green));
                mMap.addMarker(newMarker);
                CustomMapInfo customMapInfo = new CustomMapInfo(getActivity());
                mMap.setInfoWindowAdapter(customMapInfo);
                Marker m = mMap.addMarker(newMarker);
                m.setTag(mUserVendor);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Log.d(TAG, "onMarkerClick: "+ marker.getTitle() + " is clicked");
                        marker.showInfoWindow();
                        moveCamera(marker.getPosition(),DEFAULT_ZOOM,0,-250);

                        return true;
                    }
                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        popupShow(getView());
                        Log.d(TAG, "onInfoWindowClick: showing popup window");
                    }

                    private void popupShow(View view) {
                        Log.d(TAG, "popupShow: success");
                        Log.d(TAG, "popupShow: getting vendor box list to compare with users");
                        vendorBoxRef = mFirestore.collection("Vendor").document(mUserVendor.getVendorID()).collection("MyBox");
                        vendorBoxRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<SingleBox> vendorBoxList = task.getResult().toObjects(SingleBox.class);
                                vendorBoxArrayList.addAll(vendorBoxList);
                                Log.d(TAG, "onComplete: vendorBoxList id: "+vendorBoxArrayList);
                            }
                        });


//                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
//                        LayoutInflater inflater = LayoutInflater.from(getContext());
//                        View popupView = inflater.inflate(R.layout.popup_user_vendor_facade,null);
                        View popupView = getLayoutInflater().inflate(R.layout.popup_user_vendor_facade, null);
                        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        ImageView vendorImage = popupView.findViewById(R.id.popup_vendor_bg);
                        TextView vendorName = popupView.findViewById(R.id.vendor_name);
                        TextView vendorLocation = popupView.findViewById(R.id.vendor_location);
                        TextView vendorRate = popupView.findViewById(R.id.vendor_rate);
                        CircleImageView cancelAction = popupView.findViewById(R.id.cancel_action);
                        Button vendorAccess = popupView.findViewById(R.id.access_vendor);
                        Button vendorContact = popupView.findViewById(R.id.contact_vendor);

                        //Todo: update vendor detail image
                        //Todo: getting vendor availability, capacity and other details
                        if (boxRented()){
                            vendorAccess.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TransitionalStatCode transitionalStatCode = new TransitionalStatCode();
                                    transitionalStatCode.setDerivedPaging(Constants.TRANSITIONAL_STATS_CODE_IS_USER);
                                    transitionalStatCode.setSingleBoxesContainer(userBoxArrayList);
                                    ((UserClient)(getApplicationContext())).setUserVendor(mUserVendor);
                                    Intent intent = new Intent(context,StorageDetailsActivity.class);
                                    context.startActivity(intent);
                                    //Todo: Set Vendor Access method
                                    Log.d(TAG, "onClick: vendor Access Request on: "+mUserVendor.getVendorStorageName()+" With ID: "+ mUserVendor.getVendorID());
                                }
                            });
                        } else {
                            vendorAccess.setText(R.string.rent_box_from_vendor);
                            vendorAccess.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    User currentUser = ((UserClient)(getApplicationContext())).getUser();
                                    Log.d(TAG, "onClick: user clicking: " +currentUser.toString());
                                }
                            });
                            //Todo: Rent Method
                        }
                        vendorRate.setText(mUserVendor.getVendorBoxPrice().toString());
                        vendorName.setText(mUserVendor.getVendorStorageName());
                        vendorLocation.setText(mUserVendor.getVendorStorageLocation());
                        cancelAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });

                        vendorContact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Todo: Set Vendor Contact Button to contact vendor
                            }
                        });
                        popupWindow.setBackgroundDrawable(new ColorDrawable());
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0,0);
                        popupWindow.setBackgroundDrawable(new ColorDrawable());
                        if (popupWindow.isShowing()){
                            Log.d(TAG, "onInfoWindowClick: popup show");
                        }
                    }
                });

            } catch (NullPointerException e){
                Log.d(TAG, "onMapReady: ERROR "+e.getMessage());
            }
        }
    }

    private boolean boxRented() {
        Log.d(TAG, "boxRented: comparing");
        Log.d(TAG, "boxRented: checking user box list");
        if (userBoxArrayList!= null){
            Log.d(TAG, "boxRented: user box exist!");
            Log.d(TAG, "boxRented: user box: "+userBoxArrayList);
        }
        Log.d(TAG, "boxRented: checking vendor box list");
        if (vendorBoxArrayList != null){
            Log.d(TAG, "boxRented: vendor box exist!");
            Log.d(TAG, "boxRented: vendor box: "+vendorBoxArrayList);
        }
        ArrayList<SingleBox> results = new ArrayList<>();
        for (SingleBox compareOne: userBoxArrayList){
            boolean found = false;
            for (SingleBox compareTwo: vendorBoxArrayList){
                if (compareOne.getBoxID() == compareTwo.getBoxID()){
                    found = true;
                }
            }
            if (found){
                return true;
            }
        }
        return false;
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermission: location permission Denied");
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "getLocationPermission: location permission Granted");
            mLocationPermissionGranted = true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length>0){
                    for (int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted=false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    mLocationPermissionGranted=true;
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                }

            }
        }
    }

    @Override
    public void onClick(View view) {
        CircleImageView center = view.findViewById(R.id.center_button);
        if (center.isPressed()){
            getDeviceLocation();
        }
        //Todo: Onclick button setup

    }
    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }
}
