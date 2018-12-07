package com.flexor.storage.flexorstoragesolution;

import android.app.Application;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.GlobalSettings;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;

public class UserClient extends Application {
    private User user = null;
    public User getUser() {return user;}
    public void setUser(User user) {
        this.user = user;
    }


    private UserVendor userVendor = null;
    public UserVendor getUserVendor(){return userVendor;}
    public void setUserVendor (UserVendor userVendor){
        this.userVendor = userVendor;
    }

    private GlobalSettings globalSettings = null;
    public GlobalSettings getGlobalSettings() {return globalSettings;}
    public void setGlobalSettings(GlobalSettings globalSettings) {
        this.globalSettings = globalSettings;
    }

    private Box box = null;
    public Box getBox() {
        return box;
    }
    public void setBox(Box box) {
        this.box = box;
    }
}
