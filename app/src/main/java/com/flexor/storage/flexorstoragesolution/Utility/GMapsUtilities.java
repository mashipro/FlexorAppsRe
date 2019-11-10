package com.flexor.storage.flexorstoragesolution.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.maps.model.LatLng;

import static com.flexor.storage.flexorstoragesolution.Utility.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.flexor.storage.flexorstoragesolution.Utility.Constants.MAPVIEW_BUNDLE_KEY;

public class GMapsUtilities {
    private static final String TAG = "GMapsUtilities";

    private FusedLocationProviderClient clientLocation;
    private static final int DEFAULT_ZOOM = 15;
    private GoogleMap gMaps;

    public GMapsUtilities() {
    }
    public void getMaps(final Context context, final Activity activity, MapView mapView, Bundle savedInstanceState){
        Log.d(TAG, "getMaps: init!!");
        Bundle mapViewBundle = null;
        clientLocation = LocationServices.getFusedLocationProviderClient(context);
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMaps = googleMap;
                if (locationPermission(context)){
                    Log.d(TAG, "onMapReady: location permission granted");
                    if (clientLocation!=null){
                        clientLocation.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()){
                                    Location location = task.getResult();
                                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                    Log.d(TAG, "onComplete: geoLat: "+geoPoint.getLatitude());
                                    Log.d(TAG, "onComplete: geoLat: "+geoPoint.getLongitude());
                                    moveCamera(geoPoint, DEFAULT_ZOOM,0,0);
                                }
                            }
                        });
                    }
                }else {
                    getLocationPermission(activity);
                }
            }
        });

    }

    private void moveCamera(GeoPoint loc, int zoom, int offsetX, int offsetY) {
        LatLng latLng = new LatLng(loc.getLatitude(),loc.getLongitude());
        Log.d(TAG, "moveCamera: to GeoPoint: "+loc+ "zoomDist: "+ zoom + " offsetXY: X>"+ offsetX+ " Y>"+offsetY);
//        gMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        Point mapPoint = gMaps.getProjection().toScreenLocation(latLng);
        mapPoint.set(mapPoint.x+offsetX,mapPoint.y+offsetY);
        gMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(gMaps.getProjection().fromScreenLocation(mapPoint),zoom),2000,null);
    }

    private void getLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean locationPermission(Context context) {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED);
    }

}
