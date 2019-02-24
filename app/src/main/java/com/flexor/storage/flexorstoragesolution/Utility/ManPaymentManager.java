package com.flexor.storage.flexorstoragesolution.Utility;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.UserClient;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ManPaymentManager {

    private User user;

    public ManPaymentManager() {
        user = ((UserClient)(getApplicationContext())).getUser();

    }

    public int getUserBalance(){
        return ((UserClient)(getApplicationContext())).getUser().getUserBalance();
    }
}
