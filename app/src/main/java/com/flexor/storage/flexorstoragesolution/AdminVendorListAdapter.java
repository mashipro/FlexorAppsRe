package com.flexor.storage.flexorstoragesolution;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.firebase.firestore.DocumentSnapshot;

public class AdminVendorListAdapter extends FirestoreRecyclerAdapter<UserVendor, AdminVendorListAdapter.AdminVendorListHolder> {

    private OnItemClickListener listener;
    public AdminVendorListAdapter(@NonNull FirestoreRecyclerOptions<UserVendor> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminVendorListHolder holder, int position, @NonNull UserVendor model) {

        if (model.getVendorStatsCode() == 201){
            holder.textViewName.setText(model.getVendorName());
        }else{
            holder.relativeLayout.setVisibility(View.GONE);
        }

    }

    @NonNull
    @Override
    public AdminVendorListHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_vendor_list,
                parent, false);
        return new AdminVendorListAdapter.AdminVendorListHolder(v);
    }

    public class AdminVendorListHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        RelativeLayout relativeLayout;

        public AdminVendorListHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.vendorfixName);
            relativeLayout = itemView.findViewById(R.id.vendorListLay);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onInfoClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onInfoClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
