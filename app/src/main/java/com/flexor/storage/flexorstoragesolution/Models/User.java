package com.flexor.storage.flexorstoragesolution.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String userEmail;
    private String userID;
    private String userName;
    private String userAvatar;

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + userEmail + '\'' +
                ", user_id='" + userID + '\'' +
                ", username='" + userName + '\'' +
                ", avatar='" + userAvatar + '\'' +
                '}';
    }

    public User(String userEmail, String userID, String userName, String userAvatar) {

        this.userEmail = userEmail;
        this.userID = userID;
        this.userName = userName;
        this.userAvatar = userAvatar;
    }

    protected User(Parcel in) {
        userEmail = in.readString();
        userID = in.readString();
        userName = in.readString();
        userAvatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userEmail);
        dest.writeString(userID);
        dest.writeString(userName);
        dest.writeString(userAvatar);
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
}
