package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SingleBox implements Parcelable {
    private String boxID;

    public SingleBox() {
    }

    public SingleBox(String boxID) {
        this.boxID = boxID;
    }

    protected SingleBox(Parcel in) {
        boxID = in.readString();
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

    public String getBoxID() {
        return boxID;
    }

    public void setBoxID(String boxID) {
        this.boxID = boxID;
    }

    @Override
    public String toString() {
        return "SingleBox{" +
                "boxID='" + boxID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(boxID);
    }
}
