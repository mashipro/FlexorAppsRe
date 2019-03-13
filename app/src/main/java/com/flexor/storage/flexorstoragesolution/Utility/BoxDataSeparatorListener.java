package com.flexor.storage.flexorstoragesolution.Utility;

import com.flexor.storage.flexorstoragesolution.Models.SingleBox;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface BoxDataSeparatorListener {
    void onDataSeparated (Map<String, Set<SingleBox>> thisMap);
    void onDataSeparatedArray (ArrayList<String> mapKeyString);
}
