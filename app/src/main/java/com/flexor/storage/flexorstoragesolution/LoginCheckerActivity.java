package com.flexor.storage.flexorstoragesolution;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginCheckerActivity extends Activity {
    private static final String TAG = "LoginCheckerActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private User user;

    private Uri photoURI;
    private String imageStorageUri;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = ((UserClient) getApplicationContext()).getUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null && checkIfEmailIsVerified()) {
                    checkBiodata();
//                    if (checkBiodata()) {
//                        Log.d(TAG, "onAuthStateChanged: login passed, go to biodata, checkbiodata passed");
//                        startActivity(new Intent(LoginCheckerActivity.this, BiodataActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                        finish();
//                    }
                }
                else if (firebaseAuth.getCurrentUser() == null && checkIfEmailIsVerified()) {
                    Log.d(TAG, "onAuthStateChanged: logout, email verified");
                    startActivity(new Intent(LoginCheckerActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                else{
                    sendEmailVerification();
                    Log.d(TAG, "onAuthStateChanged: login failed, go back to Login");
                    mAuth.signOut();
                    startActivity(new Intent(LoginCheckerActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    Toast.makeText(LoginCheckerActivity.this, "Please Verify your Email first", Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

    private boolean checkBiodata() {
        user = ((UserClient) getApplicationContext()).getUser();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        DocumentReference docRef = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    user = task.getResult().toObject(User.class);
                    Log.d(TAG, "onComplete: user bos" + user);
                    ((UserClient)(getApplicationContext())).setUser(user);
                    if (isExist()){
                        startActivity(new Intent(LoginCheckerActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        Log.d(TAG, "onComplete: masuk mainactivity");
                    }else{
                        Log.d(TAG, "onAuthStateChanged: login passed, go to biodata, checkbiodata passed");
                        startActivity(new Intent(LoginCheckerActivity.this, BiodataActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }
            }
        });

        return true;

    }

    private boolean isExist(){
        user = ((UserClient) getApplicationContext()).getUser();
        return this.user.getUserAddress() != null;
    }

    private boolean checkIfEmailIsVerified() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()){
//                finish();
                Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginCheckerActivity.this, "verification email sent", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Email sent.");
                    }
                }
            });
        }

    }

}
