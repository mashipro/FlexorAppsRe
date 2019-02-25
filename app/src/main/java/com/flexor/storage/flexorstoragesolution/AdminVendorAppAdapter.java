package com.flexor.storage.flexorstoragesolution;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCallbackRequestCodeOffset;

public class AdminVendorAppAdapter extends FirestoreRecyclerAdapter<UserVendor, AdminVendorAppAdapter.AdminVendorAppHolder> {

    private OnItemClickListener listener;
    public AdminVendorAppAdapter(@NonNull FirestoreRecyclerOptions<UserVendor> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull AdminVendorAppHolder holder, int position, @NonNull UserVendor model) {

        if (model.getVendorStatsCode() == 211) {
            holder.textViewName.setText(model.getVendorName());
            holder.textViewUID.setText(model.getVendorID());
        }else {
            holder.relativeLayout.setVisibility(View.GONE);
        }

    }

    @NonNull
    @Override
    public AdminVendorAppHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_vendor_app_details,
                parent, false);
        return new AdminVendorAppHolder(v);

    }

    public class AdminVendorAppHolder extends RecyclerView.ViewHolder{

            TextView textViewName;
            TextView textViewUID;
            public Button rejectButton, acceptButton;
            RelativeLayout relativeLayout;

        public AdminVendorAppHolder(View itemView) {
            super(itemView);
            //vendorapplication
            textViewName = itemView.findViewById(R.id.userName);
            textViewUID = itemView.findViewById(R.id.userUID);
            rejectButton = itemView.findViewById(R.id.rejectBtn);
            acceptButton = itemView.findViewById(R.id.acceptBtn);
            relativeLayout = itemView.findViewById(R.id.vendorAppLay);


            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onAcceptClick(getSnapshots().getSnapshot(position), position);
                    }

                }
            });

        }
    }


    public interface OnItemClickListener{
        void onAcceptClick(DocumentSnapshot documentSnapshot, int position);
        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}


