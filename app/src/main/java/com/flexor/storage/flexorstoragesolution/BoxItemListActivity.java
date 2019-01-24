package com.flexor.storage.flexorstoragesolution;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.BoxItem;
import com.flexor.storage.flexorstoragesolution.Models.Notification;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Utility.NotificationManager;
import com.flexor.storage.flexorstoragesolution.ViewHolder.BoxItemViewHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoxItemListActivity extends AppCompatActivity {
    private static final String TAG = "BoxItemListActivity";
    /**Firebase Init*/

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;

    private StorageReference storageReference;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private DatabaseReference databaseReference;

    ///Custom declare///
    private ArrayList<BoxItem> boxItemsArray = new ArrayList<>();
    private ArrayList<String> spinnerItemsArray = new ArrayList<>();
    private RecyclerView.Adapter<BoxItemViewHolder> boxItemAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_items_accept, fab_items_add;
    private Box boxSaved;
    private User userSaved;
    private TransitionalStatCode transitionalStatCodeSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_item_list);

        /**Firebase onCreate Init*/
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        mUser = mAuth.getCurrentUser();


        //todo: update spinner items from database
        spinnerItemsArray.add("T-Shirt");
        spinnerItemsArray.add("Jacket");
        spinnerItemsArray.add("Skirt");
        spinnerItemsArray.add("Short");
        spinnerItemsArray.add("Other");
        Log.d(TAG, "onCreate: spinner array: "+spinnerItemsArray);

        /**getting the saved instance*/
        Log.d(TAG, "onCreate: getting saved instance...");
        boxSaved = ((UserClient)(getApplicationContext())).getBox();
        userSaved = ((UserClient)(getApplicationContext())).getUser();
        transitionalStatCodeSaved = ((UserClient)(getApplicationContext())).getTransitionalStatCode();
        Log.d(TAG, "onCreate: savepoint check: // boxSaved// "+ boxSaved);
        Log.d(TAG, "onCreate: savepoint check: // userSaved// "+ userSaved);
        Log.d(TAG, "onCreate: savepoint check: // transSaved// "+ transitionalStatCodeSaved);

        /**init Phase
         * View*/
        Log.d(TAG, "onCreate: init phase....");
        recyclerView = findViewById(R.id.manifestList);
        fab_items_accept    = findViewById(R.id.fab_items_accept);
        fab_items_add       = findViewById(R.id.fab_items_add);

        /**init Firebase ref*/
        documentReference = mFirestore.collection("Boxes").document(boxSaved.getBoxID());
        databaseReference = mDatabase.getReference().child("UsersData").child(boxSaved.getBoxTenant())
                .child("Notification").push();
//        collectionReference = documentReference.collection("boxItems");

        initRecyclerView();


        /**FAB METHOD*/
        fab_items_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertAddItem();
            }
        });

        fab_items_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataListManifest();
            }
        });
    }

    private void saveDataListManifest() {
        //Todo: set notification for vendor
        Toast.makeText(this, R.string.saving_items_please_wait, Toast.LENGTH_SHORT).show();

        /** Saving array data into firestore before save into realtimedb*/
        Log.d(TAG, "saveDataListManifest: initiated. Checking array data: .....");
        for (final BoxItem boxItem: boxItemsArray){
            Log.d(TAG, "saveDataListManifest: id= "+ boxItem.getBoxItemIdentifier()
            +" Amount= "+ boxItem.getBoxItemAmmount()
                    );
            final DocumentReference documentReferenceEach = mFirestore.collection("Boxes")
                    .document(boxSaved.getBoxID()).collection("BoxItems").document();
            boxItem.setBoxItemID(documentReferenceEach.getId());
            documentReferenceEach.set(boxItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Firestore onSuccess: Saving box ID: "+boxSaved.getBoxID()+" with item id: "+ documentReferenceEach.getId());
                    Log.d(TAG, "Firestore onSuccess: contain: " + boxItem.getBoxItemIdentifier()+" amount: "+ boxItem.getBoxItemAmmount());
//                    saveToRealtimeDB();
                }
            });
        }
        saveToRealtimeDB();
    }

    private void saveToRealtimeDB() {
        Log.d(TAG, "Save Notification: Saving.......");

        Notification newNotif = new Notification();
        newNotif.setNotificationID(databaseReference.getKey());
        newNotif.setNotificationStatsCode(401);
        newNotif.setNotificationReference(boxSaved.getBoxID());
        newNotif.setNotificationIsActive(true);
//        databaseReference.setValue(newNotif).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "saveToRealtimeDB: Successfully saved");
//                savingNewStats();
//            }
//        });
        NotificationManager notificationManager = new NotificationManager();
        notificationManager.setNotification(boxSaved.getUserVendorOwner(),newNotif);
        savingNewStats();

    }

    private void savingNewStats() {
        Log.d(TAG, "savingNewStats: saving new stats code for box");
        boxSaved.setBoxStatCode((double)321);
        documentReference.set(boxSaved).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: stats on box: "+boxSaved.getBoxID()+" ,is changed to: "+ boxSaved.getBoxID());
                startActivity(new Intent(BoxItemListActivity.this,BoxDetailsActivity.class));
                finish();
            }
        });
    }

    private void initRecyclerView() {
        /** init recycler view */

        boxItemAdapter=new RecyclerView.Adapter<BoxItemViewHolder>() {
            @NonNull
            @Override
            public BoxItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_list, parent, false);
                return new BoxItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull final BoxItemViewHolder holder, final int position) {
                holder.bindBoxItem(boxItemsArray.get(position));
                Button item_delete = holder.itemView.findViewById(R.id.item_delete);
                item_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = position;
                        boxItemsArray.remove(position);
                        Log.d(TAG, "onClick: remove: "+pos);
                        initRecyclerView();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return boxItemsArray.size();
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setAdapter(boxItemAdapter);
        recyclerView.setLayoutManager(layoutManager);
        Log.d(TAG, "initRecyclerView: recyclerview count: " + recyclerView.getChildCount());
    }

    private void showAlertAddItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoxItemListActivity.this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View alertView = layoutInflater.inflate(R.layout.alert_add_box_item,null);
        builder.setView(alertView);

        final Spinner spinner = alertView.findViewById(R.id.spinner);
        final EditText itemAmount = alertView.findViewById(R.id.edit_amount);
        Button buttonAccept = alertView.findViewById(R.id.button_accept);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                BoxItemListActivity.this,R.layout.spinner_box_items,spinnerItemsArray);
        adapter.setDropDownViewResource(R.layout.spinner_box_items);
        spinner.setAdapter(adapter);


        final AlertDialog alertDialog = builder.create();

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoxItem newItem = new BoxItem();
                newItem.setBoxItemIdentifier(spinner.getSelectedItem().toString());
                newItem.setBoxItemAmmount(itemAmount.getText().toString());
                newItem.setBoxItemVerivied(false);
                boxItemsArray.add(newItem);
                refreshItems();
                alertDialog.dismiss();
                //Todo: Set data exist check and notification
            }
        });

        alertDialog.show();
    }

    private void refreshItems() {
        Log.d(TAG, "refreshItems: Items Now: "+boxItemsArray);
        initRecyclerView();
    }
}
