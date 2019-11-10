package com.flexor.storage.flexorstoragesolution.Utility;

import com.flexor.storage.flexorstoragesolution.Models.User;

public interface UserDataListener {
    void onUserDataReceived (User user);
    void onException (Exception e);
}
