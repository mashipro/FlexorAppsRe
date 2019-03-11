package com.flexor.storage.flexorstoragesolution;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.BoxDataListener;
import com.flexor.storage.flexorstoragesolution.Utility.BoxManager;
import com.flexor.storage.flexorstoragesolution.Utility.UserBoxListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MessageFragment extends Fragment {
    private static final String TAG = "MessageFragment";

    private BoxManager boxManager;
    private UserVendor userVendor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message,container,false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boxManager = new BoxManager();
        boxManager.getUserBox(new UserBoxListener() {
            @Override
            public void onBoxReceived(ArrayList<SingleBox> userBoxes) {
                Log.d(TAG, "onBoxReceived: user box");
                for (SingleBox thisBox: userBoxes){
                    Log.d(TAG, "onBoxReceived: "+thisBox);
                }
                boxManager.getBoxDataFromArray(userBoxes, new BoxDataListener() {
                    @Override
                    public void onDataReceived(ArrayList<Box> boxes) {
                        Log.d(TAG, "onDataReceived: complete boxes.");
                        for (Box thisBoxes: boxes){
                            Log.d(TAG, "onDataReceived: "+thisBoxes);
                        }
                    }
                });
            }
        });
        userVendor = ((UserClient)(getApplicationContext())).getUserVendor();
        if (userVendor != null){
            boxManager.getVendorBox(userVendor.getVendorID(), new UserBoxListener() {
                @Override
                public void onBoxReceived(ArrayList<SingleBox> userBoxes) {
                    Log.d(TAG, "onBoxReceived: vendor Box");
                    for(SingleBox thisSingleBox: userBoxes){
                        Log.d(TAG, "onBoxReceived: "+ thisSingleBox);
                    }
                    boxManager.getBoxDataFromArray(userBoxes, new BoxDataListener() {
                        @Override
                        public void onDataReceived(ArrayList<Box> boxes) {
                            Log.d(TAG, "onDataReceived: complete boxes of vendor");
                            for(Box thisBoxData: boxes){
                                Log.d(TAG, "onDataReceived: "+ thisBoxData);
                            }
                        }
                    });
                }
            });

        }
    }
}
