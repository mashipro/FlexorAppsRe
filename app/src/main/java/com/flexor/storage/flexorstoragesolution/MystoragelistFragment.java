package com.flexor.storage.flexorstoragesolution;

import android.content.Context;
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

import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.Utility.BoxDataSeparatorListener;
import com.flexor.storage.flexorstoragesolution.Utility.BoxManager;
import com.flexor.storage.flexorstoragesolution.Utility.SingleBoxListener;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.flexor.storage.flexorstoragesolution.Utility.VendorDataListener;
import com.flexor.storage.flexorstoragesolution.ViewHolder.MyStorageViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MystoragelistFragment extends Fragment {
    private static final String TAG = "MystoragelistFragment";

    private View view;
    private Context context;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    private StorageReference storageReference;
    private CollectionReference collectionReference;
    private DocumentReference boxesDocRef;
    private DatabaseReference vendorDBRef;

    private UserManager userManager;
    private BoxManager boxManager;

    private RecyclerView recyclerView;

    private User user;
    private UserVendor userVendor;
    private UserClient userClient;

    private ArrayList<SingleBox> userBox = new ArrayList<>();
    private ArrayList<String> vendors = new ArrayList<>();

    private RecyclerView.Adapter<MyStorageViewHolder> recyclerAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mystoragelist,container,false);
        context = view.getContext();

        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        userManager = new UserManager();
        userManager.getInstance();
        boxManager = new BoxManager();
        userClient = ((UserClient)(getApplicationContext()));
        user = userManager.getUser();

        getUserBox();
    }

    private void getUserBox() {
        userBox.clear();
        boxManager.getUserBox(new SingleBoxListener() {
            @Override
            public void onBoxReceived(final ArrayList<SingleBox> userBoxes) {
//                userBox = userBoxes;
                boxManager.boxDataSeparator(userBoxes, new BoxDataSeparatorListener() {
                    @Override
                    public void onDataSeparated(Map<String, Set<SingleBox>> thisMap) {
                        Log.d(TAG, "onDataSeparated: "+thisMap);
                        for (String string: thisMap.keySet()){
                            Log.d(TAG, "onDataSeparated: "+ string);
                        }
                    }

                    @Override
                    public void onDataSeparatedArray(ArrayList<String> mapKeyString) {
                        Log.d(TAG, "onDataSeparatedArray: "+ mapKeyString);
                        for (String string: mapKeyString){
                            Log.d(TAG, "onDataSeparatedArray: "+string);
                        }
                        setupRecycler(mapKeyString, userBoxes);
                    }
                });
            }
        });
    }

    private void setupRecycler(final ArrayList<String> mapKeyString, final ArrayList<SingleBox> userBoxes) {
        Log.d(TAG, "setupRecycler: init...");
        recyclerAdapter = new RecyclerView.Adapter<MyStorageViewHolder>() {
            @NonNull
            @Override
            public MyStorageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(context).inflate(R.layout.cardview_mystoragelist,viewGroup,false);
                return new MyStorageViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull final MyStorageViewHolder myStorageViewHolder, int i) {
                String vendorID = mapKeyString.get(i);
                boxManager.getVendorData(vendorID, new VendorDataListener() {
                    @Override
                    public void onVendorDataReceived(UserVendor vendorData) {
                        Log.d(TAG, "onVendorDataReceived: "+ vendorData);
                        myStorageViewHolder.bindVendorData(vendorData);
                    }
                });
                boxManager.getBoxWithVendorID(vendorID, userBoxes, new SingleBoxListener() {
                    @Override
                    public void onBoxReceived(ArrayList<SingleBox> userBoxes) {
                        Log.d(TAG, "onBoxReceived: "+userBoxes);
                        myStorageViewHolder.bindVendorBox(userBoxes);
                    }
                });
            }
            @Override
            public int getItemCount() {
                return mapKeyString.size();
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
