package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class VendorDatabase implements Parcelable {
    private Double Latitude;
    private Double Longitude;
    private String vendorName;
    private String vendorAddress;
    private String vendorImage;
    private String vendorID;
    private String vendorCity;

    public VendorDatabase() {
    }

    public VendorDatabase(Double latitude, Double longitude, String vendorName, String vendorAddress, String vendorImage, String vendorID, String vendorCity) {
        Latitude = latitude;
        Longitude = longitude;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.vendorImage = vendorImage;
        this.vendorID = vendorID;
        this.vendorCity = vendorCity;
    }

    protected VendorDatabase(Parcel in) {
        if (in.readByte() == 0) {
            Latitude = null;
        } else {
            Latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            Longitude = null;
        } else {
            Longitude = in.readDouble();
        }
        vendorName = in.readString();
        vendorAddress = in.readString();
        vendorImage = in.readString();
        vendorID = in.readString();
        vendorCity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (Latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(Latitude);
        }
        if (Longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(Longitude);
        }
        dest.writeString(vendorName);
        dest.writeString(vendorAddress);
        dest.writeString(vendorImage);
        dest.writeString(vendorID);
        dest.writeString(vendorCity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VendorDatabase> CREATOR = new Creator<VendorDatabase>() {
        @Override
        public VendorDatabase createFromParcel(Parcel in) {
            return new VendorDatabase(in);
        }

        @Override
        public VendorDatabase[] newArray(int size) {
            return new VendorDatabase[size];
        }
    };

    public Double getLattitude() {
        return Latitude;
    }

    public void setLattitude(Double lattitude) {
        Latitude = lattitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
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

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    public String getVendorCity() {
        return vendorCity;
    }

    public void setVendorCity(String vendorCity) {
        this.vendorCity = vendorCity;
    }
}
