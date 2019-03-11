package com.flexor.storage.flexorstoragesolution.Utility;

import com.flexor.storage.flexorstoragesolution.Models.Box;

import java.util.ArrayList;

public interface BoxDataListener {
    void onDataReceived (ArrayList<Box> boxes);
}
