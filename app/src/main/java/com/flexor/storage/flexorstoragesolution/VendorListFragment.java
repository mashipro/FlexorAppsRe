package com.flexor.storage.flexorstoragesolution;

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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class VendorListFragment extends Fragment {
    private static final String TAG = "VendorListFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference adminbookRef;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;

    private AdminVendorListAdapter adminVendorListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_vendor_list,container,false);

        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();

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


        adminVendorListAdapter = new AdminVendorListAdapter(options);

        RecyclerView recyclerView = getView().findViewById(R.id.vendorListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adminVendorListAdapter);

        adminVendorListAdapter.setOnItemClickListener(new AdminVendorListAdapter.OnItemClickListener() {
            @Override
            public void onInfoClick(DocumentSnapshot documentSnapshot, int position) {
                final UserVendor userVendor = documentSnapshot.toObject(UserVendor.class);
                DocumentReference db = FirebaseFirestore.getInstance().collection("Vendor").document(userVendor.getVendorID());
                Log.d(TAG, "onInfoClick: terklik");
                Toast.makeText(getContext(), "Stats Code: " + userVendor.getVendorStatsCode() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adminVendorListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adminVendorListAdapter.stopListening();
    }
}
