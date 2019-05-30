package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Models.VendorDatabase;
import com.flexor.storage.flexorstoragesolution.Utility.BoxDataSeparatorListener;
import com.flexor.storage.flexorstoragesolution.Utility.BoxManager;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomMapInfo;
import com.flexor.storage.flexorstoragesolution.Utility.SingleBoxListener;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.flexor.storage.flexorstoragesolution.Utility.VendorDBUtilities;
import com.flexor.storage.flexorstoragesolution.Utility.VendorDataListener;
import com.flexor.storage.flexorstoragesolution.ViewHolder.MyStorageViewHolder;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.MAPVIEW_BUNDLE_KEY;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MapsFragment";
    //Components
    private Context context;
    private MapView mMapView;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Marker mapMarker;
    private Boolean mLocationPermissionGranted = false;
    private static final int DEFAULT_ZOOM = 15;
    private UserManager userManager;
    private BoxManager boxManager;
    private User user;
    private VendorDatabase vendorDatabase;

    //View
    private CircleImageView center, left, right;
    private ConstraintLayout myStorageWindow;
    private RecyclerView myBoxRecycler;
    private CircleImageView cancelAct;

    private RecyclerView.Adapter<MyStorageViewHolder> myBoxAdapter;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private CollectionReference collectionReference, userBoxRef, vendorRef;
    private DocumentReference boxesDocRef;
    private DatabaseReference vendorDBRef;

    private ArrayList<UserVendor> vendorArrayList = new ArrayList<>();
    private ArrayList<SingleBox> userBoxArrayList = new ArrayList<>();
    private ArrayList<SingleBox> vendorBoxArrayList = new ArrayList<>();
    private ArrayList<VendorDatabase> vendorDBArray = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        context = view.getContext();
        mMapView = view.findViewById(R.id.map);
        center = view.findViewById(R.id.center_button);
        center.isClickable();
        left = view.findViewById(R.id.search_button);
        right = view.findViewById(R.id.mystorage_button);
        myStorageWindow = view.findViewById(R.id.mystorage_window);
        myBoxRecycler = view.findViewById(R.id.myBoxList);
        cancelAct = view.findViewById(R.id.cancel_action);

        left.isClickable();
        right.isClickable();

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
        mStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        /**
         * Getting User from userClient
         */
        userManager = new UserManager();
        userManager.getInstance();
        user = userManager.getUser();
        boxManager = new BoxManager();

//        userBoxRef = mFirestore.collection("Users").document(user.getUserID()).collection("MyRentedBox");
        vendorRef = mFirestore.collection("Vendor");
        vendorDBRef = firebaseDatabase.getReference().child("Accepted Vendor");

        boxManager.getUserBox(new SingleBoxListener() {
            @Override
            public void onBoxReceived(ArrayList<SingleBox> userBoxes) {
                userBoxArrayList = userBoxes;
                getVendorList(new VendorDBUtilities() {
                    @Override
                    public void onDataReceived(ArrayList<VendorDatabase> vendorDBArray) {
                        addMapMarkers(vendorDBArray);
                    }
                });
            }
        });

//        userBoxRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    List<SingleBox> userRentedBoxList = task.getResult().toObjects(SingleBox.class);
//                    userBoxArrayList.addAll(userRentedBoxList);
//                    Log.d(TAG, "onComplete: user rented box: "+userBoxArrayList);
//                    getVendorList(new VendorDBUtilities() {
//                        @Override
//                        public void onDataReceived(ArrayList<VendorDatabase> vendorDBArray) {
////                addMapMarkers(vendorDBArray);
//                        }
//                    });
//                }
//            }
//        });

    }

    private void getVendorList(final VendorDBUtilities vendorDBUtilities) {
        vendorDBArray.clear();
        vendorDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                vendorDatabase = dataSnapshot.getValue(VendorDatabase.class);
                Log.d(TAG, "onChildAdded: this vendor:"+ vendorDatabase);
                vendorDBArray.add(vendorDatabase);
                Log.d(TAG, "onChildAdded: this array: "+ vendorDBArray);
                Log.d(TAG, "onChildAdded: array size: "+vendorDBArray.size());
                vendorDBUtilities.onDataReceived(vendorDBArray);
//                addMapMarkers(vendorDBArray);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error cause: "+databaseError);
                return;
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
            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDeviceLocation();
                }
            });
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: search clicked");
                    searchNearestBox(v);
                }
            });

            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myStorageWindow.getVisibility()==View.GONE){
                        myStorageWindow.setVisibility(View.VISIBLE);
                        getRecyclerData();
                    }else {
                        myStorageWindow.setVisibility(View.GONE);
                    }
                }
            });
            cancelAct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myStorageWindow.setVisibility(View.GONE);
                }
            });
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void getRecyclerData() {
        boxManager.boxDataSeparator(userBoxArrayList, new BoxDataSeparatorListener() {
            @Override
            public void onDataSeparated(Map<String, Set<SingleBox>> thisMap) {

            }

            @Override
            public void onDataSeparatedArray(ArrayList<String> mapKeyString) {
                setupRecycler(mapKeyString);
            }
        });

    }

    private void setupRecycler(final ArrayList<String> mapKeyString) {

        myBoxAdapter = new RecyclerView.Adapter<MyStorageViewHolder>() {
            @NonNull
            @Override
            public MyStorageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(context).inflate(R.layout.cardview_mystoragelist, viewGroup, false);
                return new MyStorageViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull final MyStorageViewHolder holder, int i) {
                String vendorID = mapKeyString.get(i);
                boxManager.getVendorData(vendorID, new VendorDataListener() {
                    @Override
                    public void onVendorDataReceived(UserVendor vendorData) {
                        holder.bindVendorData(vendorData);
                    }
                });

                boxManager.getBoxWithVendorID(vendorID, userBoxArrayList, new SingleBoxListener() {
                    @Override
                    public void onBoxReceived(ArrayList<SingleBox> userBoxes) {
                        holder.bindVendorBox(userBoxes);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mapKeyString.size();
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        myBoxRecycler.setHasFixedSize(true);
        myBoxRecycler.setItemViewCacheSize(20);
        myBoxRecycler.setLayoutManager(layoutManager);
        myBoxRecycler.setAdapter(myBoxAdapter);

    }


    private void searchNearestBox(View v) {
        // TODO: 01/03/2019 refix this search!!!
        mMap.getCameraPosition();
        Log.d(TAG, "searchNearestBox: camera Position: "+mMap.getCameraPosition());
        for (UserVendor vendorList: vendorArrayList){
            Log.d(TAG, "searchNearestBox: "+ vendorList.getVendorID()+ "Location: " +vendorList.getVendorGeoLocation());
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMap.getProjection().fromScreenLocation(mapPoint),zoom),2000,null);
    }

    private void addMapMarkers(ArrayList<VendorDatabase> vendorDBArray){
        Log.d(TAG, "addMapMarkers: add markers to: "+vendorDBArray);
        for (VendorDatabase thisVendor: vendorDBArray){
            if (boxRented(thisVendor.getVendorID())){
                MarkerOptions markerRented = new MarkerOptions()
                        .position(new LatLng(thisVendor.getLattitude(),thisVendor.getLongitude()))
                        .title(thisVendor.getVendorName())
                        .snippet(thisVendor.getVendorID())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_vendor_rented));
                mapMarker = mMap.addMarker(markerRented);
            }else {
                MarkerOptions markerNormal = new MarkerOptions()
                        .position(new LatLng(thisVendor.getLattitude(),thisVendor.getLongitude()))
                        .title(thisVendor.getVendorName())
                        .snippet(thisVendor.getVendorID())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_vendor_available));
                mapMarker = mMap.addMarker(markerNormal);
            }
            CustomMapInfo customMapInfo = new CustomMapInfo(getActivity());
            mMap.setInfoWindowAdapter(customMapInfo);
            mapMarker.setTag(thisVendor.getVendorID());
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(TAG, "onMarkerClick: "+ marker.getTitle() + " is clicked");
                    marker.showInfoWindow();
                    moveCamera(marker.getPosition(),DEFAULT_ZOOM,0,-250);
                    Log.d(TAG, "onMarkerClick: marker tag: "+ marker.getTag());
                    return true;
                }
            });
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    getVendorData(marker.getSnippet());
                }
            });

        }
    }

    private void getVendorData(String vendorID) {
        vendorRef.document(vendorID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    UserVendor thisVendor = task.getResult().toObject(UserVendor.class);
                    openPopup(mMapView, thisVendor);
                }else{
                    Log.d(TAG, "onComplete: error: "+task.getException());
                }
            }
        });
    }

    private void openPopup(View view, final UserVendor mUserVendor) {
        //Todo: getting vendor availability, capacity and other details

        final View popupView = getLayoutInflater().inflate(R.layout.popup_user_vendor_facade, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(R.drawable.bg_color_grey_translucent));
        ImageView vendorImage = popupView.findViewById(R.id.popup_vendor_bg);
        TextView vendorName = popupView.findViewById(R.id.vendor_name);
        TextView vendorLocation = popupView.findViewById(R.id.vendor_location);
        TextView vendorRate = popupView.findViewById(R.id.vendor_rate);
        CircleImageView cancelAction = popupView.findViewById(R.id.cancel_action);
        final Button vendorAccess = popupView.findViewById(R.id.access_vendor);
        Button vendorContact = popupView.findViewById(R.id.contact_vendor);

        vendorRate.setText(String.valueOf(mUserVendor.getVendorBoxPrice()));
        vendorName.setText(mUserVendor.getVendorStorageName());
        vendorLocation.setText(mUserVendor.getVendorStorageLocation());
        storageReference = mStorage.getReference().child(mUserVendor.getVendorIDImgPath());
        Glide.with(getApplicationContext())
                .load(storageReference)
                .into(vendorImage);

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
        vendorAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vendorAccess.setText(R.string.cobak);
                goToVendorPage(popupWindow, mUserVendor);
                //Todo: Set Vendor Access method
                Log.d(TAG, "onClick: vendor Access Request on: "+mUserVendor.getVendorStorageName()+" With ID: "+ mUserVendor.getVendorID());
            }
        });
        if (boxRented(mUserVendor.getVendorID())){
            vendorAccess.setText(R.string.access_vendor);
        } else {
            vendorAccess.setText(R.string.check_available_box);
        }
        popupWindow.showAtLocation(view, Gravity.CENTER, 0,0);
        if (popupWindow.isShowing()){
            Log.d(TAG, "onInfoWindowClick: popup show");
        }
    }

    private void goToVendorPage(PopupWindow popupWindow, UserVendor mUserVendor) {
        TransitionalStatCode transitionalStatCode = new TransitionalStatCode();
        transitionalStatCode.setDerivedPaging(Constants.TRANSITIONAL_STATS_CODE_IS_USER);
        transitionalStatCode.setSingleBoxesContainer(userBoxArrayList);
        ((UserClient)(getApplicationContext())).setTransitionalStatCode(transitionalStatCode);
        ((UserClient)(getApplicationContext())).setUserVendor(mUserVendor);
        popupWindow.dismiss();
        Intent intent = new Intent(context,StorageDetailsActivity.class);
        context.startActivity(intent);
    }

    private boolean boxRented(String userVendorID) {
        boolean boxRented = false;
        Log.d(TAG, "boxRented: box array: "+ userBoxArrayList);
        Log.d(TAG, "boxRented: compare with: "+userVendorID);
        for (SingleBox thisboxes: userBoxArrayList){
            if (thisboxes.getBoxVendor().equals(userVendorID)){
                boxRented= true;
            }
        }
        return boxRented;
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
