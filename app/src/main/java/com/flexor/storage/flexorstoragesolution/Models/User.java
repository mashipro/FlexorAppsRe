package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

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
    private Double userBalance;
    private Double userAuthCode;
    private String userGender;
    private String userAddress;
    private @ServerTimestamp Date userRegistrationTimestamp;


    public User() {
    }

    public User(String userEmail, String userID, String userName, String userAvatar, String userCity, String userPhone, Double userBalance, Double userAuthCode, String userGender, String userAddress, Date userRegistrationTimestamp) {
        this.userEmail = userEmail;
        this.userID = userID;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.userCity = userCity;
        this.userPhone = userPhone;
        this.userBalance = userBalance;
        this.userAuthCode = userAuthCode;
        this.userGender = userGender;
        this.userAddress = userAddress;
        this.userRegistrationTimestamp = userRegistrationTimestamp;
    }

    protected User(Parcel in) {
        userEmail = in.readString();
        userID = in.readString();
        userName = in.readString();
        userAvatar = in.readString();
        userCity = in.readString();
        userPhone = in.readString();
        if (in.readByte() == 0) {
            userBalance = null;
        } else {
            userBalance = in.readDouble();
        }
        if (in.readByte() == 0) {
            userAuthCode = null;
        } else {
            userAuthCode = in.readDouble();
        }
        userGender = in.readString();
        userAddress = in.readString();
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

    public Double getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(Double userBalance) {
        this.userBalance = userBalance;
    }

    public Double getUserAuthCode() {
        return userAuthCode;
    }

    public void setUserAuthCode(Double userAuthCode) {
        this.userAuthCode = userAuthCode;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Date getUserRegistrationTimestamp() {
        return userRegistrationTimestamp;
    }

    public void setUserRegistrationTimestamp(Date userRegistrationTimestamp) {
        this.userRegistrationTimestamp = userRegistrationTimestamp;
    }

    @Override
    public String toString() {
        return "User{" +
                "userEmail='" + userEmail + '\'' +
                ", userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", userCity='" + userCity + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userBalance=" + userBalance +
                ", userAuthCode=" + userAuthCode +
                ", userGender='" + userGender + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userRegistrationTimestamp=" + userRegistrationTimestamp +
                '}';
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
        if (userBalance == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(userBalance);
        }
        if (userAuthCode == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(userAuthCode);
        }
        parcel.writeString(userGender);
        parcel.writeString(userAddress);
    }
}
