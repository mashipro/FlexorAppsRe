package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Box implements Parcelable {
    private String userVendorOwner;
    private @ServerTimestamp Date boxCreatedDate;
    private @ServerTimestamp Date boxLastChange;
    private Integer boxStatCode;
    private String boxName;
    private String boxTenant;
    private Long boxRentTimestamp;
    private Double boxRentDuration;
    private String boxAccessCode;
    private String boxID;
    private Boolean boxProcess;

    public Box() {
    }

    public Box(String userVendorOwner, Date boxCreatedDate, Date boxLastChange, Integer boxStatCode, String boxName, String boxTenant, Long boxRentTimestamp, Double boxRentDuration, String boxAccessCode, String boxID, Boolean boxProcess) {
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
        this.boxProcess = boxProcess;
    }

    protected Box(Parcel in) {
        userVendorOwner = in.readString();
        if (in.readByte() == 0) {
            boxStatCode = null;
        } else {
            boxStatCode = in.readInt();
        }
        boxName = in.readString();
        boxTenant = in.readString();
        if (in.readByte() == 0) {
            boxRentTimestamp = null;
        } else {
            boxRentTimestamp = in.readLong();
        }
        if (in.readByte() == 0) {
            boxRentDuration = null;
        } else {
            boxRentDuration = in.readDouble();
        }
        boxAccessCode = in.readString();
        boxID = in.readString();
        byte tmpBoxProcess = in.readByte();
        boxProcess = tmpBoxProcess == 0 ? null : tmpBoxProcess == 1;
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
                ", boxProcess=" + boxProcess +
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

    public Integer getBoxStatCode() {
        return boxStatCode;
    }

    public void setBoxStatCode(Integer boxStatCode) {
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

    public Long getBoxRentTimestamp() {
        return boxRentTimestamp;
    }

    public void setBoxRentTimestamp(Long boxRentTimestamp) {
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

    public Boolean getBoxProcess() {
        return boxProcess;
    }

    public void setBoxProcess(Boolean boxProcess) {
        this.boxProcess = boxProcess;
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
            dest.writeInt(boxStatCode);
        }
        dest.writeString(boxName);
        dest.writeString(boxTenant);
        if (boxRentTimestamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(boxRentTimestamp);
        }
        if (boxRentDuration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(boxRentDuration);
        }
        dest.writeString(boxAccessCode);
        dest.writeString(boxID);
        dest.writeByte((byte) (boxProcess == null ? 0 : boxProcess ? 1 : 2));
    }
}
