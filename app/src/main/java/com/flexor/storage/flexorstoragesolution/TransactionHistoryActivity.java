package com.flexor.storage.flexorstoragesolution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.TransactionDownload;
import com.flexor.storage.flexorstoragesolution.Models.TransactionMiniUsers;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TransactionDownload transactionDownload;
    private User user;
    private UserManager userManager;
    private TransactionHistoryAdapter transactionHistoryAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        transactionDownload = new TransactionDownload();
        userManager = new UserManager();
        userManager.getInstance();
        user = userManager.getUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = db.collection("Users").document(user.getUserID())
                .collection("MyTransaction");

        FirestoreRecyclerOptions<TransactionDownload> options = new FirestoreRecyclerOptions.Builder<TransactionDownload>()
                .setQuery(query, TransactionDownload.class)
                .build();

        transactionHistoryAdapter = new TransactionHistoryAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.transactionHistoryRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(transactionHistoryAdapter);


    }
    @Override
    protected void onStart() {
        super.onStart();
        transactionHistoryAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        transactionHistoryAdapter.stopListening();
    }
}
