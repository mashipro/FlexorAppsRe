package com.flexor.storage.flexorstoragesolution.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeExchange {
    public TimeExchange() {
    }
    public String getDateString (Long currenttimemilis){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MM yyyy, HH.mm");
        Date date = new Date(currenttimemilis);
        return sdf.format(date);
    }
}
