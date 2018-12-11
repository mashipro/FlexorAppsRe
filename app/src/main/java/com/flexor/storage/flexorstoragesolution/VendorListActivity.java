package com.flexor.storage.flexorstoragesolution;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

public class VendorListActivity extends AppCompatActivity {

    private static final String TAG = "VendorListActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference adminbookRef;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;

    private AdminAdapter adminAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_list);

        UserVendor userVendor = (((UserClient) getApplicationContext()).getUserVendor());
        User user = ((UserClient) getApplicationContext()).getUser();


        adminbookRef = db.collection("Vendor").document(user.getUserID());

        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();


        setUpRecyclerView();

    }


    public void setUpRecyclerView() {

        Query query = db
                .collection("Vendor");


        FirestoreRecyclerOptions<UserVendor> options = new FirestoreRecyclerOptions.Builder<UserVendor>()
                .setQuery(query, UserVendor.class)
                .build();

        adminAdapter = new AdminAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminAdapter);

        adminAdapter.setOnItemClickListener(new AdminAdapter.OnItemClickListener() {
            @Override
            public void onAcceptClick(DocumentSnapshot documentSnapshot, int position) {

            }

            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                final UserVendor userVendor = documentSnapshot.toObject(UserVendor.class);
                DocumentReference db = FirebaseFirestore.getInstance().collection("Vendor").document(userVendor.getVendorID());

                db.update("vendorStatsCode", (double)299)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: yo!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "onFailure: sad", e);
                            }
                        });

                //success

//                Map<String, Object> data = new HashMap<>();
//                data.put("vendorStatsCode", 299);
//                db.set(data, SetOptions.merge());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adminAdapter.startListening();
        adminbookRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adminAdapter.stopListening();
    }

}
