package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.Utility.BoxDataListener;
import com.flexor.storage.flexorstoragesolution.Utility.BoxManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyStorageViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "MyStorageViewHolder";

    View mView;
    Context mContext;
    
    FirebaseStorage storage;
    StorageReference storageReference,vendorImageRef;
    
    ImageView vendorHeaderImage;
    TextView vendorName, vendorAddress;
    Switch vendorBoxSwitch;
    RecyclerView boxRecyclerview;

    RecyclerView.Adapter<MyStorageVendorBoxViewHolder> vendorBoxAdapter;
    ArrayList<Box> vendorBoxCompleteData = new ArrayList<>();
    BoxManager boxManager;

    public MyStorageViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = mView.getContext();
        
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        boxManager = new BoxManager();
        
        vendorHeaderImage = mView.findViewById(R.id.vendor_header_image);
        vendorName = mView.findViewById(R.id.vendor_name);
        vendorAddress = mView.findViewById(R.id.vendor_address);
        vendorBoxSwitch = mView.findViewById(R.id.switcher);
        boxRecyclerview = mView.findViewById(R.id.vendor_box_recycler);
        if (vendorBoxSwitch.isChecked()){
            Log.d(TAG, "MyStorageViewHolder: switchChecked="+vendorBoxSwitch.isChecked());
            boxRecyclerview.setVisibility(View.VISIBLE);
        }else {
            Log.d(TAG, "MyStorageViewHolder: switchChecked="+vendorBoxSwitch.isChecked());
            boxRecyclerview.setVisibility(View.GONE);
        }
    }

    public void bindVendorData(UserVendor vendorData){
        vendorName.setText(vendorData.getVendorName());
        vendorAddress.setText(vendorData.getVendorAddress());
        vendorImageRef= storageReference.child(vendorData.getVendorIDImgPath());
        Glide.with(getApplicationContext())
                .load(vendorImageRef)
                .into(vendorHeaderImage);
    }

    public void bindVendorBox(final ArrayList<SingleBox> boxes){
        vendorBoxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    boxRecyclerview.setVisibility(View.VISIBLE);
                    if (vendorBoxCompleteData.isEmpty()){
                        Log.d(TAG, "bindVendorBox: boxDatasetEmpty!!!");
                        boxManager.getBoxDataFromArray(boxes, new BoxDataListener() {
                            @Override
                            public void onDataReceived(ArrayList<Box> boxes) {
                                vendorBoxCompleteData = boxes;
                                getBoxesData(vendorBoxCompleteData);
                            }
                        });
                    }else {
                        Log.d(TAG, "bindVendorBox: boxDatasetAlreadySet!!!");
                        getBoxesData(vendorBoxCompleteData);
                    }
                }else {
                    boxRecyclerview.setVisibility(View.GONE);
                }
            }
        });

    }

    private void getBoxesData(final ArrayList<Box> boxes) {
        Log.d(TAG, "getBoxesData: "+ boxes);
        vendorBoxAdapter = new RecyclerView.Adapter<MyStorageVendorBoxViewHolder>() {
            @NonNull
            @Override
            public MyStorageVendorBoxViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_mystoragelist_boxitem,viewGroup, false);
                return new MyStorageVendorBoxViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MyStorageVendorBoxViewHolder viewHolder, int i) {
                viewHolder.bindBoxData(boxes.get(i));
            }

            @Override
            public int getItemCount() {
                return boxes.size();
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        boxRecyclerview.setHasFixedSize(true);
        boxRecyclerview.setItemViewCacheSize(20);
        boxRecyclerview.setLayoutManager(layoutManager);
        boxRecyclerview.setAdapter(vendorBoxAdapter);
    }
}
