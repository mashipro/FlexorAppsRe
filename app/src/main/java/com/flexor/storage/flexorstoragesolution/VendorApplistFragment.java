package com.flexor.storage.flexorstoragesolution;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.concurrent.Executor;

import static com.facebook.FacebookSdk.getApplicationContext;


public class VendorApplistFragment extends Fragment {

    private static final String TAG = "VendorApplistFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference adminbookRef;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;

    private AdminAdapter adminAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_vendor_applist,container,false);

        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();

//        setUpRecyclerView();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = db
                .collection("Vendor");


        FirestoreRecyclerOptions<UserVendor> options = new FirestoreRecyclerOptions.Builder<UserVendor>()
                .setQuery(query, UserVendor.class)
                .build();

        adminAdapter = new AdminAdapter(options);

        RecyclerView recyclerView = getView().findViewById(R.id.vendorlistRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
    public void onStart() {
        super.onStart();
        adminAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adminAdapter.stopListening();
    }

}
