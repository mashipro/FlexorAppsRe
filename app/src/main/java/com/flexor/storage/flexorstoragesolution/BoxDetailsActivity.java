package com.flexor.storage.flexorstoragesolution;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.Box;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class BoxDetailsActivity extends AppCompatActivity {
    private static final String TAG = "BoxDetailsActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseUser mUser;
    private FirebaseFirestoreSettings mFirestoreSettings;
    private Query mQuery;

    ///View///
    private ImageView boxBG, boxDetails;
    private TextView boxName, boxStatus, vendorLoc, tenantName, duration, rentDue, rentRate;
    private CircleImageView tenantAvatar;
    private Button btnBoxAccess, btnEnable, btnDisable, btnContact;

    ///CustomDeclare///
    private User user;
    private UserVendor userVendor;
    private Box box;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_details);

        ////Getting Bundle////
        box = ((UserClient)(getApplicationContext())).getBox();
        Log.d(TAG, "onCreate: boxData : " +box.getBoxName()+ " boxID: "+box.getBoxID());
//
//        Bundle extra = getIntent().getExtras();
//        Log.d(TAG, "onCreate: id retrieved: "+ extra.getString("boxIDforExtra"));

        ////Checking User////
        Log.d(TAG, "onCreate: checking User ....");
        user = ((UserClient)(getApplicationContext())).getUser();
        if (user != null){
            Log.d(TAG, "onCreate: user Found: " + user.getUserName()+" id: " + user.getUserID());

        }else{
            Log.d(TAG, "onCreate: user Not Found");
        }

    }
}
