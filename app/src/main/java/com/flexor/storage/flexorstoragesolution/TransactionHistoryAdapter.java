package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.TransactionDownload;
import com.flexor.storage.flexorstoragesolution.Models.TransactionMiniUsers;

public class TransactionHistoryAdapter extends FirestoreRecyclerAdapter<TransactionDownload, TransactionHistoryAdapter.TransactionHistoryHolder> {
    private static final String TAG = "TransHistoryAdapter";

//    private OnItemClickListener listener;
    public TransactionHistoryAdapter(@NonNull FirestoreRecyclerOptions<TransactionDownload> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionHistoryHolder holder, int position, @NonNull TransactionDownload model) {
        if (model.getTransactionID() != null) {
            Log.d(TAG, "onBindViewHolder: masukkk data topupnya!!!!");
            holder.transId.setText(model.getTransactionID());
            holder.transDate.setText(String.valueOf(model.getTransactionChangeTime()));
        }else {
            Log.d(TAG, "onBindViewHolder: HOLDER BEKERJAAAAAAAA");
        }
    }

    @NonNull
    @Override
    public TransactionHistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_transaction_history,
                viewGroup, false);
        return new TransactionHistoryHolder(v);
    }

    public class TransactionHistoryHolder extends RecyclerView.ViewHolder {

        TextView transId;
        TextView transDate;
        public Button detailsBtn;

        public TransactionHistoryHolder(View itemView) {

            super(itemView);
            transId = itemView.findViewById(R.id.transID);
            transDate = itemView.findViewById(R.id.transDATE);
            detailsBtn = itemView.findViewById(R.id.detailsBtn);

            detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TransactionDetailsFragment()).addToBackStack(null).commit();
                    }
                }
            });

        }
    }

//    public interface OnItemClickListener{
//
//    }
//    public void setOnItemClickListener(OnItemClickListener listener){
//        this.listener = listener;
//    }
}
