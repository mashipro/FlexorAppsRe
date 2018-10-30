package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class UserVendor implements Parcelable {
    private User user;
    private String vendorName;
    private String vendorAddress;
    private String vendorID;
    private String vendorIDNumber;
    private String vendorNPWP;
    private String vendorCompany;
    private String vendorStorageName;
    private String vendorStorageLocation;
    private String vendorIDImgPath;
    private Boolean vendorAccepted;
    private @ServerTimestamp Date vendorRegistrationTimestamp;

    public UserVendor() {
    }

    public UserVendor(User user, String vendorName, String vendorAddress, String vendorID, String vendorIDNumber, String vendorNPWP, String vendorCompany, String vendorStorageName, String vendorStorageLocation, String vendorIDImgPath, Boolean vendorAccepted, Date vendorRegistrationTimestamp) {
        this.user = user;
        this.vendorName = vendorName;
        this.vendorAddress = vendorAddress;
        this.vendorID = vendorID;
        this.vendorIDNumber = vendorIDNumber;
        this.vendorNPWP = vendorNPWP;
        this.vendorCompany = vendorCompany;
        this.vendorStorageName = vendorStorageName;
        this.vendorStorageLocation = vendorStorageLocation;
        this.vendorIDImgPath = vendorIDImgPath;
        this.vendorAccepted = vendorAccepted;
        this.vendorRegistrationTimestamp = vendorRegistrationTimestamp;
    }

    protected UserVendor(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        vendorName = in.readString();
        vendorAddress = in.readString();
        vendorID = in.readString();
        vendorIDNumber = in.readString();
        vendorNPWP = in.readString();
        vendorCompany = in.readString();
        vendorStorageName = in.readString();
        vendorStorageLocation = in.readString();
        vendorIDImgPath = in.readString();
        byte tmpVendorAccepted = in.readByte();
        vendorAccepted = tmpVendorAccepted == 0 ? null : tmpVendorAccepted == 1;
    }

    public static final Creator<UserVendor> CREATOR = new Creator<UserVendor>() {
        @Override
        public UserVendor createFromParcel(Parcel in) {
            return new UserVendor(in);
        }

        @Override
        public UserVendor[] newArray(int size) {
            return new UserVendor[size];
        }
    };

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    public String getVendorIDNumber() {
        return vendorIDNumber;
    }

    public void setVendorIDNumber(String vendorIDNumber) {
        this.vendorIDNumber = vendorIDNumber;
    }

    public String getVendorNPWP() {
        return vendorNPWP;
    }

    public void setVendorNPWP(String vendorNPWP) {
        this.vendorNPWP = vendorNPWP;
    }

    public String getVendorCompany() {
        return vendorCompany;
    }

    public void setVendorCompany(String vendorCompany) {
        this.vendorCompany = vendorCompany;
    }

    public String getVendorStorageName() {
        return vendorStorageName;
    }

    public void setVendorStorageName(String vendorStorageName) {
        this.vendorStorageName = vendorStorageName;
    }

    public String getVendorStorageLocation() {
        return vendorStorageLocation;
    }

    public void setVendorStorageLocation(String vendorStorageLocation) {
        this.vendorStorageLocation = vendorStorageLocation;
    }

    public String getVendorIDImgPath() {
        return vendorIDImgPath;
    }

    public void setVendorIDImgPath(String vendorIDImgPath) {
        this.vendorIDImgPath = vendorIDImgPath;
    }

    public Boolean getVendorAccepted() {
        return vendorAccepted;
    }

    public void setVendorAccepted(Boolean vendorAccepted) {
        this.vendorAccepted = vendorAccepted;
    }

    public Date getVendorRegistrationTimestamp() {
        return vendorRegistrationTimestamp;
    }

    public void setVendorRegistrationTimestamp(Date vendorRegistrationTimestamp) {
        this.vendorRegistrationTimestamp = vendorRegistrationTimestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(user, i);
        parcel.writeString(vendorName);
        parcel.writeString(vendorAddress);
        parcel.writeString(vendorID);
        parcel.writeString(vendorIDNumber);
        parcel.writeString(vendorNPWP);
        parcel.writeString(vendorCompany);
        parcel.writeString(vendorStorageName);
        parcel.writeString(vendorStorageLocation);
        parcel.writeString(vendorIDImgPath);
        parcel.writeByte((byte) (vendorAccepted == null ? 0 : vendorAccepted ? 1 : 2));
    }

    @Override
    public String toString() {
        return "UserVendor{" +
                "user=" + user +
                ", vendorName='" + vendorName + '\'' +
                ", vendorAddress='" + vendorAddress + '\'' +
                ", vendorID='" + vendorID + '\'' +
                ", vendorIDNumber='" + vendorIDNumber + '\'' +
                ", vendorNPWP='" + vendorNPWP + '\'' +
                ", vendorCompany='" + vendorCompany + '\'' +
                ", vendorStorageName='" + vendorStorageName + '\'' +
                ", vendorStorageLocation='" + vendorStorageLocation + '\'' +
                ", vendorIDImgPath='" + vendorIDImgPath + '\'' +
                ", vendorAccepted=" + vendorAccepted +
                ", vendorRegistrationTimestamp=" + vendorRegistrationTimestamp +
                '}';
    }
}
