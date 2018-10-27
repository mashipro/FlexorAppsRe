package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class User implements Parcelable {
    private String userEmail;
    private String userID;
    private String userName;
    private String userAvatar;
    private String userCity;
    private String userPhone;
    private String userAddress;
    private String userGender;

    private @ServerTimestamp
    Date timestamp;

    public User() {
    }

    public User(String userEmail, String userID, String userName, String userAvatar, String userCity, String userPhone, String userAddress, String userGender, Date timestamp) {
        this.userEmail = userEmail;
        this.userID = userID;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.userCity = userCity;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.userGender = userGender;
        this.timestamp = timestamp;
    }

    protected User(Parcel in) {
        userEmail = in.readString();
        userID = in.readString();
        userName = in.readString();
        userAvatar = in.readString();
        userCity = in.readString();
        userPhone = in.readString();
        userAddress = in.readString();
        userGender = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "userEmail='" + userEmail + '\'' +
                ", userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", userCity='" + userCity + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userGender='" + userGender + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userEmail);
        parcel.writeString(userID);
        parcel.writeString(userName);
        parcel.writeString(userAvatar);
        parcel.writeString(userCity);
        parcel.writeString(userPhone);
        parcel.writeString(userAddress);
        parcel.writeString(userGender);
    }
}