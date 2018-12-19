package com.flexor.storage.flexorstoragesolution;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private DocumentReference boxesDocRef;
    private ArrayList<UserVendor> vendorArrayList = new ArrayList<>();
    private ArrayList<SingleBox> userBoxArrayList = new ArrayList<>();
    private ArrayList<SingleBox> vendorBoxArrayList = new ArrayList<>();
    private ArrayList<Box> boxArrayList = new ArrayList<>();
    private boolean giveCondition;

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

                    private void popupShow(final View view) {
                        Log.d(TAG, "popupShow: success");
                        Log.d(TAG, "popupShow: getting vendor box list to compare with users");
                        vendorBoxRef = mFirestore.collection("Vendor").document(mUserVendor.getVendorID()).collection("MyBox");
                        vendorBoxRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<SingleBox> vendorBoxList = task.getResult().toObjects(SingleBox.class);
                                vendorBoxArrayList.addAll(vendorBoxList);
                                Log.d(TAG, "onComplete: vendorBoxList id: "+vendorBoxArrayList);
                                openPopup(view, mUserVendor);
                            }
                        });

                    }
                });

            } catch (NullPointerException e){
                Log.d(TAG, "onMapReady: ERROR "+e.getMessage());
            }
        }
    }

    private void openPopup(View view, final UserVendor mUserVendor) {
        final View popupView = getLayoutInflater().inflate(R.layout.popup_user_vendor_facade, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView vendorImage = popupView.findViewById(R.id.popup_vendor_bg);
        TextView vendorName = popupView.findViewById(R.id.vendor_name);
        TextView vendorLocation = popupView.findViewById(R.id.vendor_location);
        TextView vendorRate = popupView.findViewById(R.id.vendor_rate);
        CircleImageView cancelAction = popupView.findViewById(R.id.cancel_action);
        final Button vendorAccess = popupView.findViewById(R.id.access_vendor);
        Button vendorContact = popupView.findViewById(R.id.contact_vendor);

        //Todo: update vendor detail image
        //Todo: getting vendor availability, capacity and other details
        if (boxRented()){
            vendorAccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vendorAccess.setText(R.string.access_vendor);
                    goToVendorPage(popupWindow, mUserVendor);
                    //Todo: Set Vendor Access method
                    Log.d(TAG, "onClick: vendor Access Request on: "+mUserVendor.getVendorStorageName()+" With ID: "+ mUserVendor.getVendorID());
                }
            });
        } else {
            vendorAccess.setText(R.string.rent_box_from_vendor);
            vendorAccess.setOnClickListener(new View.OnClickListener() {
                private RadioGroup radioGroup;
                private TextView boxRate, totalPrice;
                private Button cancelButton, acceptButton;

                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    User currentUser = ((UserClient)(getApplicationContext())).getUser();
                    Log.d(TAG, "onClick: user clicking: " +currentUser.toString());

//                                    LayoutInflater inflaterLayout = getLayoutInflater();
//                                    View dialogView = inflaterLayout.inflate(R.layout.popup_box_rent,null);
//
//                                    final Dialog dialog = new Dialog(getContext());
//                                    dialog.setContentView(R.layout.popup_box_rent);
//                                    RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);
//                                    RadioButton checkBox3 = dialog.findViewById(R.id.checkbox3day);
//                                    RadioButton checkBox7 = dialog.findViewById(R.id.checkbox7day);
//                                    RadioButton checkBox14 = dialog.findViewById(R.id.checkbox14day);
//                                    RadioButton checkBox30 = dialog.findViewById(R.id.checkbox30day);
//                                    TextView boxRate = dialog.findViewById(R.id.box_rate);
//                                    TextView totalPrice = dialog.findViewById(R.id.bill_total);
//                                    Button cancelButton = dialog.findViewById(R.id.cancel_button);
//                                    Button acceptButton = dialog.findViewById(R.id.accept_button);
//
//                                    long checkBoxValue = 0;
//                                    int checkBox3int = checkBox3.getId();
//                                    int checkBox7int = checkBox7.getId();
//                                    int checkBox14int = checkBox14.getId();
//                                    int checkBox30int = checkBox30.getId();
//
//                                    int checkedBox = radioGroup.getCheckedRadioButtonId();
//                                    if (checkedBox == checkBox3int){
//                                        checkBoxValue= 3;
//
//                                    }else if (checkedBox == checkBox7int){
//                                        checkBoxValue= 7;
//                                    }else if (checkedBox == checkBox14int){
//                                        checkBoxValue = 14;
//                                    }else if (checkedBox == checkBox30int){
//                                        checkBoxValue = 30;
//                                    }
//                                    if (checkBox3.isChecked()){
//                                        checkBoxValue = 3;
//                                    }
//
//                                    boxRate.setText("Rp. "+mUserVendor.getVendorBoxPrice().toString() + "/day");
//                                    long totalBillValue = checkBoxValue*mUserVendor.getVendorBoxPrice();
//                                    totalPrice.setText("Rp. "+totalBillValue);
//
//                                    dialog.show();
//
                    View rentPopupView = getLayoutInflater().inflate(R.layout.popup_box_rent,null);
//                                    final PopupWindow popupWindowAgain = new PopupWindow(rentPopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    final PopupWindow popupWindowAgain = new PopupWindow(getContext());
                    popupWindowAgain.setContentView(rentPopupView);

                    radioGroup = rentPopupView.findViewById(R.id.radio_group);
                    boxRate = rentPopupView.findViewById(R.id.box_rate);
                    totalPrice = rentPopupView.findViewById(R.id.bill_total);
                    cancelButton = rentPopupView.findViewById(R.id.cancel_button);
                    acceptButton = rentPopupView.findViewById(R.id.accept_button);
                    RadioButton radioButton = rentPopupView.findViewById(R.id.checkbox3day);
                    radioButton.setChecked(true);

                    updatePrice(3, mUserVendor, popupWindowAgain);

                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        int duraValue;
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            switch (checkedId){
                                case R.id.checkbox3day:

                                    duraValue = 3;
                                    updatePrice(duraValue, mUserVendor, popupWindowAgain);
                                    break;
                                case R.id.checkbox7day:
                                    duraValue = 7;
                                    updatePrice(duraValue, mUserVendor, popupWindowAgain);
                                    break;
                                case R.id.checkbox14day:
                                    duraValue = 14;
                                    updatePrice(duraValue, mUserVendor, popupWindowAgain);
                                    break;
                                case R.id.checkbox30day:
                                    duraValue = 30;
                                    updatePrice(duraValue, mUserVendor, popupWindowAgain);
                                    break;
                            }
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindowAgain.dismiss();
                        }
                    });



                    popupWindowAgain.showAtLocation(rentPopupView, Gravity.CENTER,0,0);

                }

                private void updatePrice(int duraValue, final UserVendor mUserVendor, final PopupWindow popupWindowAgain) {
                    boxRate.setText("Rp. "+mUserVendor.getVendorBoxPrice().toString() + "/day");
                    final int totalBillValue = duraValue*mUserVendor.getVendorBoxPrice().intValue();
                    totalPrice.setText("Rp. "+totalBillValue);
                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkAvailableBox(mUserVendor, totalBillValue, popupWindowAgain);
                        }
                    });

                }
            });
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

        popupWindow.showAtLocation(view, Gravity.CENTER, 0,0);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        if (popupWindow.isShowing()){
            Log.d(TAG, "onInfoWindowClick: popup show");
        }

    }

    private void checkAvailableBox(final UserVendor mUserVendor, final int totalBillValue, final PopupWindow popupWindowAgain) {
        boxArrayList.clear();

        final ArrayList<Box> newIDArrayList = new ArrayList<>();

        for (int i = 0; i< vendorBoxArrayList.size(); i++){
            Log.d(TAG, "checkAvailableBox: "+ vendorBoxArrayList.get(i).getBoxID());
            boxesDocRef = mFirestore.collection("Boxes").document(vendorBoxArrayList.get(i).getBoxID());
            boxesDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        Box thisBox = task.getResult().toObject(Box.class);
                        if (thisBox.getBoxTenant() == null && boxArrayList.size() == 0){
                            boxArrayList.add(thisBox);
                            newIDArrayList.add(thisBox);
                            Log.d(TAG, "onComplete: adding empty box to list: "+thisBox.getBoxID());
                            prepareSetUpRent(thisBox,mUserVendor, totalBillValue, popupWindowAgain);
                        }
                    }
                }
            });
//            Log.d(TAG, "checkAvailableBox: "+ vendorBoxArrayList.get(i).getBoxID());
//            boxesDocRef = mFirestore.collection("Boxes").document(vendorBoxArrayList.get(i).getBoxID());
//            boxesDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()){
//                        Box thisBox = task.getResult().toObject(Box.class);
//                        if (thisBox.getBoxTenant() == null&& newIDArrayList.size() <= 0){
//                            boxArrayList.add(thisBox);
//                            newIDArrayList.add(thisBox.getBoxID());
//                            exist = true;
//                            Log.d(TAG, "onComplete: adding empty box to list: "+thisBox.getBoxID());
//                            newIDArrayLookup(newIDArrayList, stopIterate(newIDArrayList.contains(thisBox.getBoxID())));
//                        }
//                    }
//                }
//            });
        }
//        prepareSetUpRent();
//        Log.d(TAG, "checkAvailableBox: newIDARRAYList: " +newIDArrayList);
//        for (SingleBox vendorBox: vendorBoxArrayList){
//            if (!exist){
//                Log.d(TAG, "checkAvailableBox: exist value is false");
//                boxesDocRef = mFirestore.collection("Boxes").document(vendorBox.getBoxID());
//                boxesDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()){
//                            Box thisBox = task.getResult().toObject(Box.class);
//                            if (thisBox.getBoxTenant() == null){
//                                boxArrayList.add(thisBox);
//                                exist = true;
//                                Log.d(TAG, "onComplete: adding empty box to list: "+thisBox.getBoxID());
//                            }
//                        }
//                    }
//                });
//            }
//
//        }
//        Log.d(TAG, "checkAvailableBox: how many box is empty: " +boxArrayList.size());
    }

    private void prepareSetUpRent(final Box thisBox, final UserVendor mUserVendor, final int totalBillValue, final PopupWindow popupWindowAgain) {
        Log.d(TAG, "prepareSetUpRent: deployed");
        Log.d(TAG, "prepareSetUpRent: boxArrayListSize: "+boxArrayList.size());
        Log.d(TAG, "prepareSetUpRent: id>> " + thisBox.getBoxID() + " ||| on Vendor: " +mUserVendor.getVendorID());
        //todo: Give alert for rent confirmation
        //todo: SetUp box Auto rent

        /**
         * getting user balance
         */

        final User currentUser = ((UserClient)(getApplicationContext())).getUser();

        if (rentConfirmed(currentUser.getUserBalance().intValue(), totalBillValue)){
            Log.d(TAG, "prepareSetUpRent: giveConditon: "+giveCondition);

            final DocumentReference vendorBoxReff = mFirestore.collection("Boxes").document(thisBox.getBoxID());
            final DocumentReference userDocReff = mFirestore.collection("Users").document(currentUser.getUserID());
            userDocReff.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        final User currentUserUpdt = task.getResult().toObject(User.class);
                        ((UserClient)(getApplicationContext())).setUser(currentUserUpdt);
                        Log.d(TAG, "prepareSetUpRent: userBalance is " + currentUserUpdt.getUserBalance());

                        Integer userBalance = currentUserUpdt.getUserBalance().intValue();
                        Log.d(TAG, "prepareSetUpRent: rent proceed confirmed: " + rentConfirmed(userBalance, totalBillValue));

                        /**
                         * getting confirmation and make transaction or cancel
                         */
                        if (rentConfirmed(userBalance, totalBillValue)){
                            Log.d(TAG, "prepareSetUpRent: "+ rentConfirmed(userBalance, totalBillValue));
                            Log.d(TAG, "prepareSetUpRent: begin calculation");
                            int userFinalBalance = userBalance-totalBillValue;
                            Log.d(TAG, "prepareSetUpRent: userFinalBalance: "+userFinalBalance);
                            final SingleBox singleBox = new SingleBox();
                            singleBox.setBoxID(boxArrayList.get(0).getBoxID());
                            currentUserUpdt.setUserBalance(userFinalBalance);
                            userDocReff.set(currentUserUpdt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "onComplete: userBalance now: " + currentUserUpdt.getUserBalance());
                                    thisBox.setBoxTenant(currentUser.getUserID());
                                    thisBox.setBoxRentTimestamp(null);
                                    thisBox.setBoxLastChange(null);
                                    thisBox.setBoxStatCode(Double.valueOf(311));
                                    vendorBoxReff.set(thisBox).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                SingleBox newUserSingleBox = new SingleBox();
                                                newUserSingleBox.setBoxID(thisBox.getBoxID());
                                                userBoxArrayList.add(newUserSingleBox);
                                                goToVendorPage(popupWindowAgain, mUserVendor);
                                            }
                                        }
                                    });
                                    userDocReff.collection("MyRentedBox").document(thisBox.getBoxID()).set(singleBox);
                                }
                            });


                        }else{
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle(R.string.insufficient_balance);
                            alert.setMessage(R.string.please_recharge);
                            alert.setCancelable(false);
                            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    popupWindowAgain.dismiss();
                                }
                            });
                            AlertDialog alertDialog = alert.create();
                            alertDialog.show();
                        }
                    }
                }
            });
        }

//        final DocumentReference userDocReff = mFirestore.collection("Users").document(currentUser.getUserID());
//        userDocReff.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()){
//                    final User currentUserUpdt = task.getResult().toObject(User.class);
//                    ((UserClient)(getApplicationContext())).setUser(currentUserUpdt);
//                    Log.d(TAG, "prepareSetUpRent: userBalance is " + currentUserUpdt.getUserBalance());
//
//                    Integer userBalance = currentUserUpdt.getUserBalance().intValue();
//
//                    /**
//                     * getting simple calculation to determine user balance is sufficient to make transaction
//                     */
//                    Log.d(TAG, "prepareSetUpRent: rent proceed confirmed: " + rentConfirmed(userBalance, (int) totalBillValue));
//
//                    /**
//                     * getting confirmation and make transaction or cancel
//                     */
//                    if (rentConfirmed(userBalance, totalBillValue)){
//                        Log.d(TAG, "prepareSetUpRent: "+ rentConfirmed(userBalance, totalBillValue));
//                        Log.d(TAG, "prepareSetUpRent: begin calculation");
//                        int userFinalBalance = userBalance-totalBillValue;
//                        Double userFBDoub =(double) userFinalBalance;
//                        Log.d(TAG, "prepareSetUpRent: userFinalBalance: "+userFinalBalance);
//                        currentUserUpdt.setUserBalance(userFBDoub);
//                        userDocReff.set(currentUserUpdt).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Log.d(TAG, "onComplete: userBalance now: " + currentUserUpdt.getUserBalance());
//                            }
//                        });
//                    }
//                }
//
//            }
//        });


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

    private Boolean rentConfirmed(Integer userBalance, Integer vendorBoxPrice) {
        if (userBalance-1 <= vendorBoxPrice){
            Log.d(TAG, "rentConfirmed: false");
            return false;
        } else {
            Log.d(TAG, "rentConfirmed: true");
            return true;
        }
    }

    private boolean boxRented() {
        Log.d(TAG, "boxRented: comparing");
        Log.d(TAG, "boxRented: checking user box list");
        if (userBoxArrayList.size()>=1){
            Log.d(TAG, "boxRented: user box exist!");
            Log.d(TAG, "boxRented: user box: "+userBoxArrayList);
        }else {
            Log.d(TAG, "boxRented: user Box EMPTY!");
        }
        Log.d(TAG, "boxRented: checking vendor box list");
        if (vendorBoxArrayList.size() >= 1){
            Log.d(TAG, "boxRented: vendor box exist!");
            Log.d(TAG, "boxRented: vendor box: "+vendorBoxArrayList);
        } else {
            Log.d(TAG, "boxRented: vendor Box EMPTY!");
        }
        for (SingleBox compareOne: userBoxArrayList){
            boolean found = false;
            for (SingleBox compareTwo: vendorBoxArrayList){
                if (compareOne.getBoxID().equals(compareTwo.getBoxID())){
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
