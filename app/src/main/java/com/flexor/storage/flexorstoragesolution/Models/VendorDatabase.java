package com.flexor.storage.flexorstoragesolution.Models;

public class VendorDatabase {
    private Double Lattitude;
    private Double Longitude;
    private String vendorName;
    private String vendorAddress;
    private String vendorImage;
    private String vendorID;
    private String vendorCity;

    public VendorDatabase() {
    }

    public VendorDatabase(Double lattitude, Double longitude, String vendorName, String vendorAddress, String vendorImage, String vendorID, String vendorCity) {
        Lattitude = lattitude;
        Longitude = longitude;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.vendorImage = vendorImage;
        this.vendorID = vendorID;
        this.vendorCity = vendorCity;
    }

    public Double getLattitude() {
        return Lattitude;
    }

    public void setLattitude(Double lattitude) {
        Lattitude = lattitude;
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
