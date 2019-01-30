package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SingleBox implements Parcelable {
    private String boxID;
    private String boxVendor;

    public SingleBox() {
    }

    public SingleBox(String boxID, String boxVendor) {
        this.boxID = boxID;
        this.boxVendor = boxVendor;
    }


    protected SingleBox(Parcel in) {
        boxID = in.readString();
        boxVendor = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(boxID);
        dest.writeString(boxVendor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SingleBox> CREATOR = new Creator<SingleBox>() {
        @Override
        public SingleBox createFromParcel(Parcel in) {
            return new SingleBox(in);
        }

        @Override
        public SingleBox[] newArray(int size) {
            return new SingleBox[size];
        }
    };

    @Override
    public String toString() {
        return "SingleBox{" +
                "boxID='" + boxID + '\'' +
                ", boxVendor='" + boxVendor + '\'' +
                '}';
    }

    public String getBoxID() {
        return boxID;
    }

    public void setBoxID(String boxID) {
        this.boxID = boxID;
    }

    public String getBoxVendor() {
        return boxVendor;
    }

    public void setBoxVendor(String boxVendor) {
        this.boxVendor = boxVendor;
    }
}
