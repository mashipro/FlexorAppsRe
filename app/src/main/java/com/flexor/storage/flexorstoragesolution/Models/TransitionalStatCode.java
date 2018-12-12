package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class TransitionalStatCode implements Parcelable {
    private int derivedPaging;

    public TransitionalStatCode() {
    }

    public TransitionalStatCode(int derivedPaging) {
        this.derivedPaging = derivedPaging;
    }

    protected TransitionalStatCode(Parcel in) {
        derivedPaging = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(derivedPaging);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransitionalStatCode> CREATOR = new Creator<TransitionalStatCode>() {
        @Override
        public TransitionalStatCode createFromParcel(Parcel in) {
            return new TransitionalStatCode(in);
        }

        @Override
        public TransitionalStatCode[] newArray(int size) {
            return new TransitionalStatCode[size];
        }
    };

    public int getDerivedPaging() {
        return derivedPaging;
    }

    public void setDerivedPaging(int derivedPaging) {
        this.derivedPaging = derivedPaging;
    }

    @Override
    public String toString() {
        return "TransitionalStatCode{" +
                "derivedPaging=" + derivedPaging +
                '}';
    }
}
