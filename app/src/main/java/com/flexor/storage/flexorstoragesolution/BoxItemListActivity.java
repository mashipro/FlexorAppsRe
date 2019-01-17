package com.flexor.storage.flexorstoragesolution;

import android.content.DialogInterface;
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

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.BoxItem;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.ViewHolder.BoxItemViewHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BoxItemListActivity extends AppCompatActivity {
    private static final String TAG = "BoxItemListActivity";

    ///Custom declare///
    private ArrayList<BoxItem> boxItemsArray = new ArrayList<>();
    private ArrayList<String> spinnerItemsArray = new ArrayList<>();
    private RecyclerView.Adapter<BoxItemViewHolder> boxItemAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_items_accept, fab_items_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_item_list);

        //todo: update spinner items from database
        spinnerItemsArray.add("T-Shirt");
        spinnerItemsArray.add("Jacket");
        spinnerItemsArray.add("Skirt");
        spinnerItemsArray.add("Short");
        spinnerItemsArray.add("Other");
        Log.d(TAG, "onCreate: spinner array: "+spinnerItemsArray);

        /**getting the saved instance*/
        Log.d(TAG, "onCreate: getting saved instance...");
        Box boxSaved = ((UserClient)(getApplicationContext())).getBox();
        User userSaved = ((UserClient)(getApplicationContext())).getUser();
        TransitionalStatCode transitionalStatCodeSaved = ((UserClient)(getApplicationContext())).getTransitionalStatCode();
        Log.d(TAG, "onCreate: savepoint check: // boxSaved// "+ boxSaved);
        Log.d(TAG, "onCreate: savepoint check: // userSaved// "+ userSaved);
        Log.d(TAG, "onCreate: savepoint check: // transSaved// "+ transitionalStatCodeSaved);

        /**init Phase
         * View*/
        Log.d(TAG, "onCreate: init phase....");
        recyclerView = findViewById(R.id.manifestList);
        fab_items_accept    = findViewById(R.id.fab_items_accept);
        fab_items_add       = findViewById(R.id.fab_items_add);

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
                boxItemsArray.add(newItem);
                refreshItems();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void refreshItems() {
        Log.d(TAG, "refreshItems: Items Now: "+boxItemsArray);
        initRecyclerView();
    }
}