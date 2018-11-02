package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private FirebaseUser authUser;

    private Uri photoURI;

    private String imageStorageUri;

    private CircleImageView userAvatar;
    private TextView pUserName, pUser_name, pUserAddress, pUserGender, pUserIDCNumber,pUserEmail, pUserCity, pUserPhoneNumber,pUserTimeStamp, pUserUID, pUserAuthCode, pUserBalance, pUser_balance;
    Button updateProfile;

    @Override
    protected void onStart() {
        super.onStart();

        User user = ((UserClient) getApplicationContext()).getUser();
//        pUser_name.setText(user.getUserName());
        pUserName.setText(user.getUserName());
        pUserUID.setText(user.getUserID());
        pUserEmail.setText(user.getUserEmail());
        pUserAddress.setText(user.getUserAddress());
        pUserGender.setText(user.getUserGender());
        pUserCity.setText(user.getUserCity());

    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Firebase Init//
        mAuth = FirebaseAuth.getInstance();
        authUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //View Init//TextView//
        pUserName = (TextView) findViewById(R.id.userName);
        pUser_name = (TextView) findViewById(R.id.user_name);
        pUserAddress = (TextView) findViewById(R.id.userAddress);
        pUserGender = (TextView) findViewById(R.id.userGender);
        pUserIDCNumber = (TextView) findViewById(R.id.userIDCNumber);
        pUserEmail = (TextView) findViewById(R.id.userEmail);
        pUserCity = (TextView) findViewById(R.id.userCity);
        pUserPhoneNumber = (TextView) findViewById(R.id.userPhoneNumber);
        pUserTimeStamp = (TextView) findViewById(R.id.userTimestamp);
        pUserUID = (TextView) findViewById(R.id.userUID);
        pUserAuthCode = (TextView) findViewById(R.id.userAuthCode);
        pUserBalance = (TextView) findViewById(R.id.userBalance);
        pUser_balance = (TextView) findViewById(R.id.user_balance);

        //View Init//TextView//
        updateProfile = (Button) findViewById(R.id.updateProfile);

        //View Init//ImageView//
        userAvatar = (CircleImageView) findViewById(R.id.user_avatar);

        updateProfile.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.updateProfile:
                startActivity(new Intent(ProfileActivity.this, biodata.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }

    }
}
