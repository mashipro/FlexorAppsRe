package com.flexor.storage.flexorstoragesolution.Utility;

import com.flexor.storage.flexorstoragesolution.Models.VendorDatabaseReceive;

import java.util.ArrayList;

public interface VendorDBUtilities {
    void onDataReceived(ArrayList<VendorDatabaseReceive> vendorDBArray);
}
