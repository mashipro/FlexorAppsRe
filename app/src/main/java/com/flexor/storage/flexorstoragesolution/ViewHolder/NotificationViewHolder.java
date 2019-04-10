package com.flexor.storage.flexorstoragesolution.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.BoxDetailsActivity;
import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.Notification;
import com.flexor.storage.flexorstoragesolution.Models.SingleBox;
import com.flexor.storage.flexorstoragesolution.Models.TransitionalStatCode;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.flexor.storage.flexorstoragesolution.R;
import com.flexor.storage.flexorstoragesolution.UserClient;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.CustomNotificationManager;
import com.flexor.storage.flexorstoragesolution.Utility.TimeExchange;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "NotificationViewHolder";
    private View view;
    private Context context;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private FirebaseUser mUser;
    private FirebaseApp mFirebase;

    private StorageReference storageReference;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private DatabaseReference databaseReference;

    private User user;
    private UserVendor userVendor;
    private SingleBox singleBox;

    private CircleImageView notifImg;
    private LinearLayout notifLinearBoxAccessReq;
    private TextView notifTitle, notifBody, notifTime;
    private ConstraintLayout notifBase;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        context = view.getContext();

        mFirestore = FirebaseFirestore.getInstance();
        user = ((UserClient)(getApplicationContext())).getUser();

        notifImg = view.findViewById(R.id.notif_card_img);
        notifLinearBoxAccessReq = view.findViewById(R.id.notif_card_linear_boxAccess);
        notifTitle = view.findViewById(R.id.notif_card_boxreq_title);
        notifBody = view.findViewById(R.id.notif_card_boxreq_body);
        notifTime = view.findViewById(R.id.notif_card_boxreq_time);
        notifBase = view.findViewById(R.id.notif_card_base);
    }

    public void bindItem(final Notification model) {
        if (model.getNotificationIsActive()){
            notifBase.setBackgroundResource(R.drawable.bg_solid_primarybluelight);
            if (model.getNotificationStatsCode().intValue() == Constants.NOTIFICATION_STATS_USERBOXACCESSREQUEST){
                notifImg.setImageResource(R.mipmap.ic_boxreq_white_circle);
                notifTitle.setText(R.string.notification_access_box_req_title);
                notifBody.setText(R.string.notification_access_box_req_body);
                TimeExchange timeExchange = new TimeExchange();
                String dates = timeExchange.getDateString(model.getNotificationTime());
                notifTime.setText(dates);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionalStatCode transitionalStatCode = new TransitionalStatCode();
                        transitionalStatCode.setDerivedPaging(Constants.STATSCODE_USER_VENDOR);
                        ((UserClient)(getApplicationContext())).setTransitionalStatCode(transitionalStatCode);

                        documentReference = mFirestore.collection("Boxes")
                                .document(model.getNotificationReference());
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Box box = task.getResult().toObject(Box.class);
                                    Log.d(TAG, "onComplete: box reached with id: "+ box.getBoxID());
                                    ((UserClient)(getApplicationContext())).setBox(box);
                                    CustomNotificationManager customNotificationManager = new CustomNotificationManager();
                                    customNotificationManager.setNotificationInactive(model.getNotificationID());
                                    Intent intent = new Intent(context, BoxDetailsActivity.class);
                                    moveActivity(intent, true);
                                }
                            }
                        });
                    }
                });
            }else if (model.getNotificationStatsCode().intValue() == Constants.NOTIFICATION_STATS_USERBOXACCESSREQUESTCANCEL){
                notifImg.setImageResource(R.mipmap.ic_declined_white_circle);
                notifTitle.setText(R.string.notification_access_box_req_cancel_title);
                notifBody.setText(R.string.notification_access_box_req_cancel_body);
                TimeExchange timeExchange = new TimeExchange();
                notifTime.setText(timeExchange.getDateString(model.getNotificationTime()));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionalStatCode transitionalStatCode = new TransitionalStatCode();
                        transitionalStatCode.setDerivedPaging(Constants.STATSCODE_USER_VENDOR);
                        ((UserClient)(getApplicationContext())).setTransitionalStatCode(transitionalStatCode);

                        documentReference = mFirestore.collection("Boxes")
                                .document(model.getNotificationReference());
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Box box = task.getResult().toObject(Box.class);
                                    Log.d(TAG, "onComplete: box reached with id: "+ box.getBoxID());
                                    ((UserClient)(getApplicationContext())).setBox(box);
                                    CustomNotificationManager customNotificationManager = new CustomNotificationManager();
                                    customNotificationManager.setNotificationInactive(model.getNotificationID());
                                    Intent intent = new Intent(context, BoxDetailsActivity.class);
                                    moveActivity(intent, true);
                                }
                            }
                        });
                    }
                });
            }else if (model.getNotificationStatsCode().intValue() == Constants.NOTIFICATION_STATS_USERBOXACCESSREQUESTACCEPTED){
                notifImg.setImageResource(R.mipmap.ic_check_circle_green);
                notifTitle.setText(R.string.notification_access_box_req_accepted_title);
                notifBody.setText(R.string.notification_access_box_req_accepted_body);
                TimeExchange timeExchange = new TimeExchange();
                notifTime.setText(timeExchange.getDateString(model.getNotificationTime()));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionalStatCode transitionalStatCode = new TransitionalStatCode();
                        transitionalStatCode.setDerivedPaging(Constants.STATSCODE_USER_USER);
                        ((UserClient)(getApplicationContext())).setTransitionalStatCode(transitionalStatCode);
                        documentReference = mFirestore.collection("Boxes")
                                .document(model.getNotificationReference());
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Box box = task.getResult().toObject(Box.class);
                                    Log.d(TAG, "onComplete: box reached with id: "+ box.getBoxID());
                                    ((UserClient)(getApplicationContext())).setBox(box);
                                    CustomNotificationManager customNotificationManager = new CustomNotificationManager();
                                    customNotificationManager.setNotificationInactive(model.getNotificationID());
                                    Intent intent = new Intent(context, BoxDetailsActivity.class);
                                    moveActivity(intent, true);
                                }
                            }
                        });
                    }
                });
            } else if (model.getNotificationStatsCode().intValue() == Constants.NOTIFICATION_STATS_USERBOXACCESSREQUESTDENIED){
                notifImg.setImageResource(R.mipmap.ic_cross);
                notifImg.setRotation(45);
                notifTitle.setText(R.string.notification_access_box_req_declined_title);
                notifBody.setText(R.string.notification_access_box_req_declined_body);
                TimeExchange timeExchange = new TimeExchange();
                notifTime.setText(timeExchange.getDateString(model.getNotificationTime()));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionalStatCode transitionalStatCode = new TransitionalStatCode();
                        transitionalStatCode.setDerivedPaging(Constants.STATSCODE_USER_USER);
                        ((UserClient)(getApplicationContext())).setTransitionalStatCode(transitionalStatCode);
                        documentReference = mFirestore.collection("Boxes")
                                .document(model.getNotificationReference());
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Box box = task.getResult().toObject(Box.class);
                                    Log.d(TAG, "onComplete: box reached with id: "+ box.getBoxID());
                                    ((UserClient)(getApplicationContext())).setBox(box);
                                    CustomNotificationManager customNotificationManager = new CustomNotificationManager();
                                    customNotificationManager.setNotificationInactive(model.getNotificationID());
                                    Intent intent = new Intent(context, BoxDetailsActivity.class);
                                    moveActivity(intent, true);
                                }
                            }
                        });
                    }
                });
            }
        }else {
            notifImg.setImageResource(R.mipmap.ic_okay_white_circle);
        }
    }

    private void moveActivity(Intent targetIntent, Boolean isAllowed) {
        if (isAllowed){
            context.startActivity(targetIntent);
        }
    }

}
