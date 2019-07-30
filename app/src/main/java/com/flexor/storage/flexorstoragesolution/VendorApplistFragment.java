package com.flexor.storage.flexorstoragesolution;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.theartofdev.edmodo.cropper.CropImage;

import static com.facebook.FacebookSdk.getApplicationContext;


public class VendorApplistFragment extends Fragment {

    private static final String TAG = "VendorApplistFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference adminbookRef;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;

    private TextView emptyView;
    private View view;

    private AdminVendorAppAdapter adminAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_vendor_applist,container,false);
        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();
        emptyView = (TextView) view.findViewById(R.id.text_empty);

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

        adminAdapter = new AdminVendorAppAdapter(options);


        RecyclerView recyclerView = getView().findViewById(R.id.vendorApplistRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adminAdapter);

            adminAdapter.setOnItemClickListener(new AdminVendorAppAdapter.OnItemClickListener() {
                @Override
                public void onAcceptClick(DocumentSnapshot documentSnapshot, int position) {
                    final UserVendor userVendor = documentSnapshot.toObject(UserVendor.class);
                    final DocumentReference db = FirebaseFirestore.getInstance().collection("Vendor").document(userVendor.getVendorID());

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    ((UserClient) (getApplicationContext())).setUserVendor(userVendor);
                                    UserVendor userVendor1 = ((UserClient)(getApplicationContext())).getUserVendor();
                                    String vendorID = userVendor1.getVendorID();
                                    userVendor1.setVendorID(vendorID);
                                    startActivity(new Intent(getActivity(), MapsAdminActivity.class));
//                                    userVendor.setVendorStatsCode((double) 201);
////                                db.update("vendorStatsCode", (double)201)  //not used
//                                    db.set(userVendor)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Log.d(TAG, "onSuccess: yo!");
//                                                    Log.d(TAG, "onSuccess: " + userVendor.getVendorStatsCode());
//                                                    ((UserClient) (getApplicationContext())).setUserVendor(userVendor);
//                                                    startActivity(new Intent(getActivity(), MapsAdminActivity.class));
//
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Log.w(TAG, "onFailure: sad", e);
//                                                }
//                                            });

                                    //success

//                Map<String, Object> data = new HashMap<>();
//                data.put("vendorStatsCode", 299);
//                db.set(data, SetOptions.merge());
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Setuju dengan Aplikasi Vendor ini?").setPositiveButton("Setuju", dialogClickListener)
                            .setNegativeButton("Tidak", dialogClickListener).show();


                }

                @Override
                public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                    final UserVendor userVendor = documentSnapshot.toObject(UserVendor.class);
                    final DocumentReference db = FirebaseFirestore.getInstance().collection("Vendor").document(userVendor.getVendorID());


                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    db.update("vendorStatsCode", (double) 299)
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
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };


                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Tolak dan Hapus dari daftar?").setPositiveButton("Setuju", dialogClickListener)
                            .setNegativeButton("Pending", dialogClickListener).show();
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
