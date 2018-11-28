package com.flexor.storage.flexorstoragesolution.Models;

import com.google.maps.android.clustering.ClusterItem;
import com.google.type.LatLng;

public class ClusterMarker implements ClusterItem {

    private com.google.android.gms.maps.model.LatLng position;
    private String title;
    private String snippet;
    private int iconPicture;
    private UserVendor userVendor;

    public ClusterMarker() {
    }

    public ClusterMarker(com.google.android.gms.maps.model.LatLng position, String title, String snippet, int iconPicture, UserVendor userVendor) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.userVendor = userVendor;
    }

    @Override
    public com.google.android.gms.maps.model.LatLng getPosition() {
        return position;
    }

    public void setPosition(com.google.android.gms.maps.model.LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    public UserVendor getUserVendor() {
        return userVendor;
    }

    public void setUserVendor(UserVendor userVendor) {
        this.userVendor = userVendor;
    }
}
