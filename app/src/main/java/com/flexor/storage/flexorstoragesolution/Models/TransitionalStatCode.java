package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TransitionalStatCode implements Parcelable {
    private int derivedPaging;
    private ArrayList<SingleBox> singleBoxesContainer;

    public TransitionalStatCode() {
    }

    protected TransitionalStatCode(Parcel in) {
        derivedPaging = in.readInt();
        singleBoxesContainer = in.createTypedArrayList(SingleBox.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(derivedPaging);
        dest.writeTypedList(singleBoxesContainer);
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

    @Override
    public String toString() {
        return "TransitionalStatCode{" +
                "derivedPaging=" + derivedPaging +
                ", singleBoxesContainer=" + singleBoxesContainer +
                '}';
    }

    public TransitionalStatCode(int derivedPaging, ArrayList<SingleBox> singleBoxesContainer) {
        this.derivedPaging = derivedPaging;
        this.singleBoxesContainer = singleBoxesContainer;
    }

    public int getDerivedPaging() {
        return derivedPaging;
    }

    public void setDerivedPaging(int derivedPaging) {
        this.derivedPaging = derivedPaging;
    }

    public ArrayList<SingleBox> getSingleBoxesContainer() {
        return singleBoxesContainer;
    }

    public void setSingleBoxesContainer(ArrayList<SingleBox> singleBoxesContainer) {
        this.singleBoxesContainer = singleBoxesContainer;
    }
}
