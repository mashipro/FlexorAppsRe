package com.flexor.storage.flexorstoragesolution.Utility;

import java.util.Date;

public interface BoxValidityChecker {
    void onBoxValidityChecked(Boolean valid);
    void boxExpirationDate (Date date);

}
