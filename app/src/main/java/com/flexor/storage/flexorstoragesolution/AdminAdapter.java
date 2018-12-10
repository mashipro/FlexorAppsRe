package com.flexor.storage.flexorstoragesolution;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.model.ResourcePath;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdminAdapter extends FirestoreRecyclerAdapter<UserVendor, AdminAdapter.AdminHolder> {

    private OnItemClickListener listener;
    public AdminAdapter(@NonNull FirestoreRecyclerOptions<UserVendor> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminHolder holder, int position, @NonNull UserVendor model) {

        if (model.getVendorStatsCode() == 211) {
            holder.textViewName.setText(model.getVendorName());
            holder.textViewUID.setText(model.getVendorID());
        }

    }

    @NonNull
    @Override
    public AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_userdetails,
                parent, false);
        return new AdminHolder(v);

    }

    public void rejectItem(int position){
//        getSnapshots().getSnapshot(position).getReference().delete();
        final UserVendor userVendor = (((UserClient) getApplicationContext()).getUserVendor());
        User user = ((UserClient) getApplicationContext()).getUser();
        DocumentReference db = FirebaseFirestore.getInstance().collection("Vendor").document(user.getUserID());
        db.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userVendor.setVendorStatsCode((double)299);
            }
        });
    }

    public class AdminHolder extends RecyclerView.ViewHolder{

            TextView textViewName;
            TextView textViewUID;
            public Button rejectButton, acceptButton;

        public AdminHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.userName);
            textViewUID = itemView.findViewById(R.id.userUID);
            rejectButton = itemView.findViewById(R.id.rejectBtn);
            acceptButton = itemView.findViewById(R.id.acceptBtn);

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
