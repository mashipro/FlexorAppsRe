package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class SingleBoxSend implements Parcelable {
    private String boxID;
    private String boxVendor;


    public SingleBoxSend() {
    }

    public SingleBoxSend(String boxID, String boxVendor) {
        this.boxID = boxID;
        this.boxVendor = boxVendor;
    }


    protected SingleBoxSend(Parcel in) {
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

    public static final Creator<SingleBoxSend> CREATOR = new Creator<SingleBoxSend>() {
        @Override
        public SingleBoxSend createFromParcel(Parcel in) {
            return new SingleBoxSend(in);
        }

        @Override
        public SingleBoxSend[] newArray(int size) {
            return new SingleBoxSend[size];
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SingleBoxSend)) return false;
        SingleBoxSend singleBox = (SingleBoxSend) o;
        return Objects.equals(boxVendor, singleBox.boxVendor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boxID, boxVendor);
    }
}
