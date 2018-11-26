package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Box implements Parcelable {
    private String userVendorOwner;
    private @ServerTimestamp Date boxCreatedDate;
    private @ServerTimestamp Date boxLastChange;
    private Double boxStatCode;
    private String boxName;
    private String boxTenant;
    private @ServerTimestamp Date boxRentTimestamp;
    private Double boxRentDuration;
    private String boxAccessCode;
    private String boxID;

    public Box() {
    }

    public Box(String userVendorOwner, Date boxCreatedDate, Date boxLastChange, Double boxStatCode, String boxName, String boxTenant, Date boxRentTimestamp, Double boxRentDuration, String boxAccessCode, String boxID) {
        this.userVendorOwner = userVendorOwner;
        this.boxCreatedDate = boxCreatedDate;
        this.boxLastChange = boxLastChange;
        this.boxStatCode = boxStatCode;
        this.boxName = boxName;
        this.boxTenant = boxTenant;
        this.boxRentTimestamp = boxRentTimestamp;
        this.boxRentDuration = boxRentDuration;
        this.boxAccessCode = boxAccessCode;
        this.boxID = boxID;
    }


    protected Box(Parcel in) {
        userVendorOwner = in.readString();
        if (in.readByte() == 0) {
            boxStatCode = null;
        } else {
            boxStatCode = in.readDouble();
        }
        boxName = in.readString();
        boxTenant = in.readString();
        if (in.readByte() == 0) {
            boxRentDuration = null;
        } else {
            boxRentDuration = in.readDouble();
        }
        boxAccessCode = in.readString();
        boxID = in.readString();
    }

    public static final Creator<Box> CREATOR = new Creator<Box>() {
        @Override
        public Box createFromParcel(Parcel in) {
            return new Box(in);
        }

        @Override
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };

    @Override
    public String toString() {
        return "Box{" +
                "userVendorOwner='" + userVendorOwner + '\'' +
                ", boxCreatedDate=" + boxCreatedDate +
                ", boxLastChange=" + boxLastChange +
                ", boxStatCode=" + boxStatCode +
                ", boxName='" + boxName + '\'' +
                ", boxTenant='" + boxTenant + '\'' +
                ", boxRentTimestamp=" + boxRentTimestamp +
                ", boxRentDuration=" + boxRentDuration +
                ", boxAccessCode='" + boxAccessCode + '\'' +
                ", boxID='" + boxID + '\'' +
                '}';
    }

    public String getUserVendorOwner() {
        return userVendorOwner;
    }

    public void setUserVendorOwner(String userVendorOwner) {
        this.userVendorOwner = userVendorOwner;
    }

    public Date getBoxCreatedDate() {
        return boxCreatedDate;
    }

    public void setBoxCreatedDate(Date boxCreatedDate) {
        this.boxCreatedDate = boxCreatedDate;
    }

    public Date getBoxLastChange() {
        return boxLastChange;
    }

    public void setBoxLastChange(Date boxLastChange) {
        this.boxLastChange = boxLastChange;
    }

    public Double getBoxStatCode() {
        return boxStatCode;
    }

    public void setBoxStatCode(Double boxStatCode) {
        this.boxStatCode = boxStatCode;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getBoxTenant() {
        return boxTenant;
    }

    public void setBoxTenant(String boxTenant) {
        this.boxTenant = boxTenant;
    }

    public Date getBoxRentTimestamp() {
        return boxRentTimestamp;
    }

    public void setBoxRentTimestamp(Date boxRentTimestamp) {
        this.boxRentTimestamp = boxRentTimestamp;
    }

    public Double getBoxRentDuration() {
        return boxRentDuration;
    }

    public void setBoxRentDuration(Double boxRentDuration) {
        this.boxRentDuration = boxRentDuration;
    }

    public String getBoxAccessCode() {
        return boxAccessCode;
    }

    public void setBoxAccessCode(String boxAccessCode) {
        this.boxAccessCode = boxAccessCode;
    }

    public String getBoxID() {
        return boxID;
    }

    public void setBoxID(String boxID) {
        this.boxID = boxID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userVendorOwner);
        if (boxStatCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(boxStatCode);
        }
        dest.writeString(boxName);
        dest.writeString(boxTenant);
        if (boxRentDuration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(boxRentDuration);
        }
        dest.writeString(boxAccessCode);
        dest.writeString(boxID);
    }
}
