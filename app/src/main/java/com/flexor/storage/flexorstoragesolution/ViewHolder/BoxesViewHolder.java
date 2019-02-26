package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.BoxDetailsActivity;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.NotificationSend;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.UserClient;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomNotificationManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

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
    private Box box;
    private ArrayList<SingleBox> userBoxArrayList = new ArrayList<>();
    private int transitionCode;

    private User user;



    public BoxesViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
        mContext=itemView.getContext();
        itemView.setOnClickListener(this);
    }


    public void bindBox(SingleBox singleBox){
        Log.d(TAG, "bindBox: id: "+ singleBox);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        final TransitionalStatCode transitionalStatCode = ((UserClient)(getApplicationContext())).getTransitionalStatCode();
        userBoxArrayList= transitionalStatCode.getSingleBoxesContainer();
        transitionCode = transitionalStatCode.getDerivedPaging();
        mFirestore = FirebaseFirestore.getInstance();
        mDocumentRef= mFirestore.collection("Boxes").document(singleBox.getBoxID());

        boxExtra = mView.findViewById(R.id.box_extra);
        boxIndividualImage = mView.findViewById(R.id.boxIndividualImage);
        boxName = mView.findViewById(R.id.storage_name);
        boxStatus = mView.findViewById(R.id.box_status);
        boxExtra.setOnClickListener(this);
        updateView();
    }

    private void updateView() {
        mDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    box = new Box();
                    box = task.getResult().toObject(Box.class);
                    boxName.setText(box.getBoxName());
                    getBoxStat(box.getBoxStatCode());
                    if (transitionCode == Constants.STATSCODE_USER_USER){
                        /** USER VIEW*/
                        boxExtra.setVisibility(View.GONE);
                        if (box.getBoxStatCode() == 300){
                            mView.setVisibility(View.GONE);
                        }else if (box.getBoxStatCode() == 301){
                            boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_available);
                        }else if (box.getBoxStatCode() == 311){
                            if (isUserBox()){
                                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_empty);
                            }else {
                                mView.setVisibility(View.GONE);
                            }
                        }else if (box.getBoxStatCode() ==312){
                            if (isUserBox()){
                                boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_full);
                            }else {
                                mView.setVisibility(View.GONE);
                            }
                        }
                    }else {
                        /**VENDOR AND ADMIN VIEW*/
                        boxExtra.setVisibility(View.VISIBLE);
                        if (box.getBoxStatCode() == 300){
                            boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_disabled);
                        }else if (box.getBoxStatCode() == 301){
                            boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_available);
                        }else if (box.getBoxStatCode() == 311){
                            boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_user);
                        }else if (box.getBoxStatCode() ==312){
                            boxIndividualImage.setBackgroundResource(R.mipmap.ic_box_user);
                        }
                    }

                }
            }
        });
    }

    private boolean isUserBox(){
        SingleBox newSingle = new SingleBox();
        newSingle.setBoxID(box.getBoxID());
        newSingle.setBoxVendor(box.getUserVendorOwner());
        return userBoxArrayList.contains(newSingle);
    }


    private void getBoxStat(int i) {
        Log.d(TAG, "getBoxStat: getting box stats is: "+box.getBoxStatCode().toString());
        if (i == 311){
            boxStatus.setText(R.string.box_stat_empty);
        } else if (i == 312){
            boxStatus.setText(R.string.box_stat_full);
        } else {
            boxStatus.setText(R.string.box_stat_available);
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
        } else if (boxIndividualImage.isPressed()){
            accessBoxDetails();
        }

    }

    private class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_box_Details:
                    accessBoxDetails();

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

    private void accessBoxDetails() {

        if (transitionCode == Constants.STATSCODE_USER_USER){
            if (box.getBoxStatCode() == 301){
                rentBox();
            }else {

                ((UserClient)(getApplicationContext())).setBox(box);
                Intent movePage = new Intent(mContext,BoxDetailsActivity.class);
                mContext.startActivity(movePage);
            }
        }else{
            ((UserClient)(getApplicationContext())).setBox(box);
            Intent movePage = new Intent(mContext,BoxDetailsActivity.class);
            mContext.startActivity(movePage);
        }
    }

    private void rentBox() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View popupView = inflater.inflate(R.layout.popup_box_rent, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RadioGroup radioGroup = popupView.findViewById(R.id.radio_group);
        final RadioButton rad3day = popupView.findViewById(R.id.checkbox3day);
        final RadioButton rad7day = popupView.findViewById(R.id.checkbox7day);
        final RadioButton rad14day = popupView.findViewById(R.id.checkbox14day);
        final RadioButton rad30day = popupView.findViewById(R.id.checkbox30day);
        TextView boxRate = popupView.findViewById(R.id.box_rate);
        TextView billTotal = popupView.findViewById(R.id.bill_total);
        Button btnCancel = popupView.findViewById(R.id.cancel_button);
        Button btnAccept = popupView.findViewById(R.id.accept_button);
        // TODO: 31/01/2019 fix this mess
        int i = 0;
        if (rad3day.isChecked()){
            i=3;
        }else if (rad7day.isChecked()){
            i=7;
        }else if (rad14day.isChecked()){
            i=14;
        }else if (rad30day.isChecked()){
            i=30;
        }

//        boxRate.setText(boxPrice);
//        billTotal.setText(totalBill);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                box.setBoxStatCode(311);
                box.setBoxTenant(((UserClient)(getApplicationContext())).getUser().getUserID());
                box.setBoxLastChange(null);
//                if (rad3day.isChecked()){
//                    box.setBoxRentDuration((double) 3);
//                }else if (rad7day.isChecked()){
//                    box.setBoxRentDuration((double) 7);
//                }else if (rad14day.isChecked()){
//                    box.setBoxRentDuration((double) 14);
//                }else if (rad30day.isChecked()){
//                    box.setBoxRentDuration((double) 30);
//                }

                SingleBox singleBox = new SingleBox();
                singleBox.setBoxID(box.getBoxID());
                singleBox.setBoxVendor(box.getUserVendorOwner());
                userBoxArrayList.add(singleBox);

                DocumentReference boxReff = mFirestore.collection("Boxes").document(box.getBoxID());
                DocumentReference userBoxRef = mFirestore
                        .collection("Users")
                        .document(((UserClient)(getApplicationContext())).getUser().getUserID())
                        .collection("MyRentedBox")
                        .document(box.getBoxID());
                boxReff.set(box).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){
                            Log.d(TAG, "onComplete: box info saved");
                        }
                    }
                });
                userBoxRef.set(singleBox).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){
                            Log.d(TAG, "onComplete: user box saved");
                        }
                    }
                });

                CustomNotificationManager notifMan = new CustomNotificationManager();
                NotificationSend notificationSend = new NotificationSend();
                notificationSend.setNotificationStatsCode(Constants.NOTIFICATION_STATS_USERRENTBOX);
                notificationSend.setNotificationReference(box.getBoxID());
                notifMan.setNotification(box.getUserVendorOwner(),notificationSend);

                popupWindow.dismiss();
                updateView();
            }
        });
        popupWindow.showAtLocation(mView, Gravity.CENTER,0,0);
    }

}
