package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class VendorDatabaseSend{
    private String vendorName;
    private String vendorAddress;
    private String vendorImage;

    public VendorDatabaseSend() {
    }

    public VendorDatabaseSend(String vendorName, String vendorAddress, String vendorImage) {
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.vendorImage = vendorImage;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getVendorImage() {
        return vendorImage;
    }

    public void setVendorImage(String vendorImage) {
        this.vendorImage = vendorImage;
    }
}
