package com.flexor.storage.flexorstoragesolution;

import android.app.Application;

import com.flexor.storage.flexorstoragesolution.Models.UserVendor;

public class UserVendorClient extends Application {
    private UserVendor userVendor = null;
    public  UserVendor getUserVendor() {return userVendor;}
    public void setUserVendor(UserVendor userVendor) {
        this.userVendor = userVendor;
    }
}
