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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class AdminAdapter extends FirestoreRecyclerAdapter<User, AdminAdapter.AdminHolder> {

    private OnItemClickListener listener;
    public AdminAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminHolder holder, int position, @NonNull User model) {

        holder.textViewName.setText(model.getUserName());
        holder.textViewUID.setText(model.getUserID());

    }

    @NonNull
    @Override
    public AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_userdetails,
                parent, false);
        return new AdminHolder(v);

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onDeleteClick(DocumentSnapshot documentSnapshot,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
