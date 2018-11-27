package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.BoxDetailsActivity;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BoxesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "BoxesViewHolder";
    View mView;
    Context mContext;
    String boxStat;

    private FirebaseFirestore mFirestore;
    private DocumentReference mDocumentRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private ImageView boxIndividualImage, boxExtra;
    private TextView boxName, boxStatus;
    private String boxID;
    private Box boxDetailsSend;
    private Box box;



    public BoxesViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
        mContext=itemView.getContext();
        itemView.setOnClickListener(this);
    }


    public void bindBox (SingleBox singleBox){
        Log.d(TAG, "bindBox: id: "+ singleBox);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        mFirestore = FirebaseFirestore.getInstance();
        mDocumentRef= mFirestore.collection("Boxes").document(singleBox.getBoxID());
        mDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                box = new Box();
                box = task.getResult().toObject(Box.class);

                boxIndividualImage = mView.findViewById(R.id.boxIndividualImage);
                boxName = mView.findViewById(R.id.storage_name);
                boxStatus = mView.findViewById(R.id.box_status);

                boxName.setText(box.getBoxName());
                getBoxStat(box);

                //Todo show Image for box card list
            }
        });
        boxExtra = mView.findViewById(R.id.box_extra);
        boxExtra.setOnClickListener(this);

//        Log.d(TAG, "bindBox: "+ box.getBoxID());
//        Log.d(TAG, "7bindBox: "+box.toString());
//        Log.d(TAG, "bindBox: binding data for box: " +box.getBoxID());

//        boxID = box.getBoxID();
//
//        boxIndividualImage.setImageURI(null);
//        boxExtra.setOnClickListener(this);
//        boxName.setText(box.getBoxID());
//        boxDetailsSend = box;

//        getBoxStat(box);
//        boxStatus.setText(box.getBoxStatCode().toString());

    }

    private void getBoxStat(Box box) {
        Log.d(TAG, "getBoxStat: getting box stats is: "+box.getBoxStatCode().toString());
        if (box.getBoxStatCode().intValue() == 301){
            boxStatus.setText(R.string.box_stat_available);
        } else if(box.getBoxStatCode().intValue() == 311){
            boxStatus.setText(R.string.box_stat_empty);
        } else if (box.getBoxStatCode().intValue() == 312){
            boxStatus.setText(R.string.box_stat_full);
        }
    }

    @Override
    public void onClick(View view) {
        if (boxExtra.isPressed()){
            Log.d(TAG, "onClick: boxExtra pressed");
            PopupMenu popupMenu = new PopupMenu(mContext,view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_box_details, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new MenuItemClickListener());
            popupMenu.show();
        }

    }

    private class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_box_Details:
//                    Log.d(TAG, "boxDetails Pressed with id: "+boxID);
                    boxClickedSaveData();
                    Intent movePage = new Intent(mContext,BoxDetailsActivity.class);
//                    movePage.putExtra("boxIDforExtra",boxID);
//                    movePage.putExtra("boxTypeforExtra","vendor");
                    mContext.startActivity(movePage);


                    return true;
                case R.id.nav_box_Access:

                    Log.d(TAG, "onMenuItemClick: boxAccess pressed");

                    //Todo boxAccess pressed
                    return true;
                case R.id.nav_box_remove:
                    Log.d(TAG, "onMenuItemClick: boxRemove pressed");

                    //Todo boxRemove pressed
                    return true;
            }
            return false;
        }
    }

    private void boxClickedSaveData() {
        ((UserClient)(getApplicationContext())).setBox(box);

    }
}
