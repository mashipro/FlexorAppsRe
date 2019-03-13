package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.Utility.BoxManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyStorageVendorBoxViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "VendorBoxViewHolder";
    View view;
    Context context;

    FirebaseStorage storage;
    StorageReference storageReference;
    BoxManager boxManager;

    ImageView boxImage;
    TextView boxName, boxStatus, boxDue;

    public MyStorageVendorBoxViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        context = view.getContext();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        boxManager = new BoxManager();

        boxImage = view.findViewById(R.id.box_avatar);
        boxName = view.findViewById(R.id.box_name);
        boxStatus = view.findViewById(R.id.box_status);
        boxDue = view.findViewById(R.id.box_due);
    }

    public void bindBoxData(Box box){
        // TODO: 13/03/2019 update box image and status using box statcode checker
        boxName.setText(box.getBoxName());
        boxDue.setText(box.getBoxRentTimestamp().toString());
    }
}
