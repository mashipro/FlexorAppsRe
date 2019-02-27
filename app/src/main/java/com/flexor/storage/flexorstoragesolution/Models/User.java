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
    private Integer userBalance;
    private Integer userAuthCode;
    private String userGender;
    private String userAddress;
    private @ServerTimestamp Date userRegistrationTimestamp;

    public User() {
    }

    public User(String userEmail, String userID, String userName, String userAvatar, String userCity, String userPhone, Integer userBalance, Integer userAuthCode, String userGender, String userAddress, Date userRegistrationTimestamp) {
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
            userBalance = in.readInt();
        }
        if (in.readByte() == 0) {
            userAuthCode = null;
        } else {
            userAuthCode = in.readInt();
        }
        userGender = in.readString();
        userAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userEmail);
        dest.writeString(userID);
        dest.writeString(userName);
        dest.writeString(userAvatar);
        dest.writeString(userCity);
        dest.writeString(userPhone);
        if (userBalance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userBalance);
        }
        if (userAuthCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userAuthCode);
        }
        dest.writeString(userGender);
        dest.writeString(userAddress);
    }

    @Override
    public int describeContents() {
        return 0;
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
                ", userBalance=" + userBalance +
                ", userAuthCode=" + userAuthCode +
                ", userGender='" + userGender + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userRegistrationTimestamp=" + userRegistrationTimestamp +
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

    public Integer getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(Integer userBalance) {
        this.userBalance = userBalance;
    }

    public Integer getUserAuthCode() {
        return userAuthCode;
    }

    public void setUserAuthCode(Integer userAuthCode) {
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
}
