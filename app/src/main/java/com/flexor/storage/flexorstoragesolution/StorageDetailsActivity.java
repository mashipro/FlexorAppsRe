package com.flexor.storage.flexorstoragesolution;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomSpanCount;
import com.flexor.storage.flexorstoragesolution.ViewHolder.BoxesViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class StorageDetailsActivity extends AppCompatActivity {
    private static final String TAG = "StorageDetailsActivity";

    //Firebase Init
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;

    //Reff Init
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    //View Init
    private ImageView headerVendorImage;
    private TextView vendorName;
    private TextView vendorLocation;
    private RecyclerView recyclerViewVendorDetails;

    //Custom Declare
    private UserVendor userVendor;
    private ArrayList<SingleBox> vendorBoxAL = new ArrayList<>();
    private Query mQuery;
    private FirestoreRecyclerAdapter<SingleBox,BoxesViewHolder> mFirestoreRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_details);

        //Firebase onCreate Init
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        mUser = mAuth.getCurrentUser();

        //View onCreate Init
        headerVendorImage =findViewById(R.id.vendor_header_image);
        vendorName = findViewById(R.id.vendor_name);
        vendorLocation = findViewById(R.id.vendor_location);
        recyclerViewVendorDetails = findViewById(R.id.recyclerViewVendorDetails);


        //Getting Vendor
        userVendor = ((UserClient)(getApplicationContext())).getUserVendor();

        //Updating UI
        vendorName.setText(userVendor.getVendorStorageName());
        vendorLocation.setText(userVendor.getVendorStorageLocation());
        //Todo: Updating Vendor Image

        //Getting Vendor Box Info
        Log.d(TAG, "onCreate: getting vendor box");
        collectionReference = mFirestore.collection("Vendor").document(userVendor.getVendorID()).collection("MyBox");
        mQuery = collectionReference.orderBy("boxID",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<SingleBox> recyclerOptions = new FirestoreRecyclerOptions.Builder<SingleBox>()
                .setQuery(mQuery,SingleBox.class)
                .build();
        mFirestoreRecyclerAdapter = new FirestoreRecyclerAdapter<SingleBox, BoxesViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BoxesViewHolder holder, int position, @NonNull SingleBox model) {
                holder.bindBox(model);
                Log.d(TAG, "onBindViewHolder: binding " +model);
            }

            @NonNull
            @Override
            public BoxesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_box, viewGroup,false);
                return new BoxesViewHolder(view);
            }
        };
        int spanNumber = CustomSpanCount.calculateNoOfColumns(this,Constants.SINGLEBOX_SPAN_WIDTH);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,spanNumber);
        recyclerViewVendorDetails.setHasFixedSize(false);
        recyclerViewVendorDetails.setItemViewCacheSize(20);
        recyclerViewVendorDetails.setDrawingCacheEnabled(true);
        recyclerViewVendorDetails.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewVendorDetails.setAdapter(mFirestoreRecyclerAdapter);
        recyclerViewVendorDetails.setLayoutManager(mLayoutManager);

    }
    @Override
    public void onStart() {
        super.onStart();
        mFirestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFirestoreRecyclerAdapter != null){
            mFirestoreRecyclerAdapter.stopListening();
        }
    }

}
