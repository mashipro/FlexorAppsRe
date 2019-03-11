package com.flexor.storage.flexorstoragesolution.Utility;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;

import java.util.Comparator;

public class CAS {
    /*CUSTOM ARRAY SORTER*/

    public static Comparator<SingleBox> BoxIDSingleSort = new Comparator<SingleBox>() {
        @Override
        public int compare(SingleBox o1, SingleBox o2) {
            String boxid1 = o1.getBoxID();
            String boxid2 = o2.getBoxID();
            return boxid1.compareTo(boxid2);
        }
    };
    public static Comparator<SingleBox> BoxVendorSingleSort = new Comparator<SingleBox>() {
        @Override
        public int compare(SingleBox o1, SingleBox o2) {
            String boxid1 = o1.getBoxVendor();
            String boxid2 = o2.getBoxVendor();
            return boxid1.compareTo(boxid2);
        }
    };

    public static Comparator<Box> boxNameBoxSort = new Comparator<Box>() {
        @Override
        public int compare(Box o1, Box o2) {
            return o1.getBoxName().compareTo(o2.getBoxName());
        }
    };
    public static Comparator<Box> boxStatSingleSort = new Comparator<Box>() {
        @Override
        public int compare(Box o1, Box o2) {
            return o1.getBoxStatCode().compareTo(o2.getBoxStatCode());
        }
    };
    public static Comparator<Box> boxStatSingleSortRev = new Comparator<Box>() {
        @Override
        public int compare(Box o1, Box o2) {
            return o2.getBoxStatCode().compareTo(o1.getBoxStatCode());
        }
    };
}
