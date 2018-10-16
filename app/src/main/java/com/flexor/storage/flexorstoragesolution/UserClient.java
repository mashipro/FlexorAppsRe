package com.flexor.storage.flexorstoragesolution;

import android.app.Application;

import com.flexor.storage.flexorstoragesolution.Models.User;

public class UserClient extends Application {
    private User user = null;
    public User getUser() {return user;}
    public void setUser(User user) {
        this.user = user;
    }
}
