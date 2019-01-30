package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BoxItemsArray implements Parcelable {
    private ArrayList<BoxItem> BoxItemArray;

    public BoxItemsArray() {
    }

    public BoxItemsArray(ArrayList<BoxItem> boxItemArray) {
        BoxItemArray = boxItemArray;
    }

    protected BoxItemsArray(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BoxItemsArray> CREATOR = new Creator<BoxItemsArray>() {
        @Override
        public BoxItemsArray createFromParcel(Parcel in) {
            return new BoxItemsArray(in);
        }

        @Override
        public BoxItemsArray[] newArray(int size) {
            return new BoxItemsArray[size];
        }
    };

    public ArrayList<BoxItem> getBoxItemArray() {
        return BoxItemArray;
    }

    public void setBoxItemArray(ArrayList<BoxItem> boxItemArray) {
        BoxItemArray = boxItemArray;
    }

    @Override
    public String toString() {
        return "BoxItemsArray{" +
                "BoxItemArray=" + BoxItemArray +
                '}';
    }

}
