package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

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
    private TextView userName, user_name, userAddress, userGender, userIDCNumber, userEmail, userCity, userPhoneNumber,userTimeStamp, userUID, userAuthCode, userBalance, user_balance;
    Button updateProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Firebase Init//
        mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //View Init//TextView//
        userName = (TextView) findViewById(R.id.userName);
        user_name = (TextView) findViewById(R.id.user_name);
        userAddress = (TextView) findViewById(R.id.userAddress);
        userGender = (TextView) findViewById(R.id.userGender);
        userIDCNumber = (TextView) findViewById(R.id.userIDCNumber);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userCity = (TextView) findViewById(R.id.userCity);
        userPhoneNumber = (TextView) findViewById(R.id.userPhoneNumber);
        userTimeStamp = (TextView) findViewById(R.id.userTimestamp);
        userUID = (TextView) findViewById(R.id.userUID);
        userAuthCode = (TextView) findViewById(R.id.userAuthCode);
        userBalance = (TextView) findViewById(R.id.userBalance);
        user_balance = (TextView) findViewById(R.id.user_balance);

        //View Init//TextView//
        updateProfile = (Button) findViewById(R.id.updateProfile);

        //View Init//ImageView//
        userAvatar = (CircleImageView) findViewById(R.id.user_avatar);



    }

    @Override
    public void onClick(View view) {

    }
}
