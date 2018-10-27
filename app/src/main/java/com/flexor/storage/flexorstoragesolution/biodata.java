package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class biodata extends AppCompatActivity implements View.OnClickListener{

    EditText userName, userAddress, userGender, userCity;
    Button userSubmit;
    CircleImageView userAvatar;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biodata);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        userName = (EditText) findViewById(R.id.biodata_name);
        userAddress = (EditText) findViewById(R.id.biodata_address);
        userGender = (EditText) findViewById(R.id.biodata_gender);
        userCity = (EditText) findViewById(R.id.biodata_city);
        userSubmit = (Button) findViewById(R.id.button_submit);
        userAvatar = (CircleImageView) findViewById(R.id.user_avatar);

        userSubmit.setOnClickListener(this);

    }

    private void storeUserInfo() {

        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            mFirebaseFirestore.setFirestoreSettings(settings);
            DocumentReference newUserDocuments = mFirebaseFirestore.collection("Users").document(user.getUid());
            String userID = user.getUid();
            String userEmail = user.getEmail();
            String userNameString = userName.getText().toString().trim();
            String userAddressString = userAddress.getText().toString().trim();
            String userGenderString = userGender.getText().toString().trim();
            String userCityString = userCity.getText().toString().trim();
            User users = new User();
            users.setUserID(userID);
            users.setUserEmail(userEmail);
            users.setUserName(userNameString);
            users.setUserAddress(userAddressString);
            users.setUserGender(userGenderString);
            users.setUserCity(userCityString);
            Log.d("TAG", "storeUserInfo: users UID: " + user.getUid() );
            newUserDocuments.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d("TAG", "StoreUserInfoComplete: Users data stored: Firestore. ID: " + user.getUid());
                        Toast.makeText(biodata.this, "Biodata Updated!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(biodata.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }else{
                        Log.d("TAG", "StoreUserInfoIncomplete: CHECK LOG!");
                    }
                }
            });
        }
    }

    private void storeUserInfotes() {

//        final FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null){
            FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            mFirebaseFirestore.setFirestoreSettings(settings);
            DocumentReference newUserDocuments = mFirebaseFirestore.collection("Users").document("Biodata");
//            String userID = user.getUid();
//            String userEmail = user.getEmail();
            String userNameString = userName.getText().toString().trim();
            String userAddressString = userAddress.getText().toString().trim();
            String userGenderString = userGender.getText().toString().trim();
            String userCityString = userCity.getText().toString().trim();
            User users = new User();
//            users.setUserID(userID);
//            users.setUserEmail(userEmail);
            users.setUserName(userNameString);
            users.setUserAddress(userAddressString);
            users.setUserGender(userGenderString);
            users.setUserCity(userCityString);
//            Log.d("TAG", "storeUserInfo: users UID: " + user.getUid() );
            newUserDocuments.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
//                        Log.d("TAG", "StoreUserInfoComplete: Users data stored: Firestore. ID: " + user.getUid());
                        Toast.makeText(biodata.this, "Biodata Updated!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(biodata.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }else{
                        Log.d("TAG", "StoreUserInfoIncomplete: CHECK LOG!");
                    }
                }
            });
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_submit:
                storeUserInfo();
        }

    }
}
