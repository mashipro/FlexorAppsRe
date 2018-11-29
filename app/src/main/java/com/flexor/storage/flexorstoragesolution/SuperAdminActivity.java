package com.flexor.storage.flexorstoragesolution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SuperAdminActivity extends AppCompatActivity {

    private static final String TAG = "SuperAdminActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference adminbookRef;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;

    private AdminAdapter adminAdapter;

    private ArrayList<User> mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);

        User user = ((UserClient) getApplicationContext()).getUser();

        adminbookRef = db.collection("Vendor").document(user.getUserID());

        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();

        setUpRecyclerView();

        adminAdapter.setOnItemClickListener(new AdminAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

            }

            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {

                documentSnapshot.getReference().delete();
            }
        });

    }

    public void rejectUser(int position){
        mUser.remove(position);
        adminAdapter.notifyDataSetChanged();
    }


    public void setUpRecyclerView() {

//        Query query = adminbookRef.orderBy("Users", Query.Direction.DESCENDING);
        User user = ((UserClient) getApplicationContext()).getUser();


        Query query = db
                .collection("Users");


        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adminAdapter = new AdminAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminAdapter);

//        adminAdapter.setOnItemClickListener(new AdminAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//
//            }
//
//            @Override
//            public void onDeleteClick(int position) {
//                rejectUser(position);
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adminAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adminAdapter.stopListening();
    }

}
