package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class BetaLoginActivity extends AppCompatActivity {
    private static final String TAG = "BetaLoginActivity";

    /*Firebase Declare*/
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseFirestoreSettings firebaseFirestoreSettings;
    private DocumentReference userDocument;

    /*UI Declare*/
    private Button button_login, button_signup, button_accept;
    private EditText edit_name, edit_mail, edit_psw;
    private TextView text_forgotpw;

    /*Utilities Declare*/
//    private UserManager userManager;

    /*Custom Value Declare*/
    private Boolean flagRegist = false;
    private Boolean flagForgotPw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beta_login);

//        /*Firebase Init*/
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseUser != null){
                    startActivity(new Intent(BetaLoginActivity.this, MainActivity.class));
                }
            }
        };

        /*Firebase User Check*/


        /*UI init*/
        button_login = findViewById(R.id.button_login);
        button_signup = findViewById(R.id.button_signUp);
        button_accept = findViewById(R.id.button_cont);
        edit_name = findViewById(R.id.edit_signUp_name);
        edit_mail = findViewById(R.id.edit_login_email);
        edit_psw = findViewById(R.id.edit_login_password);
        text_forgotpw = findViewById(R.id.link_forgotPW);

        /*Utilities Init*/
//        userManager = new UserManager();
        checkUser();

        initView();
    }

    private void checkUser() {
    }

    private void initView() {
        text_forgotpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: forgot password!");
                // TODO: 31/05/2019 forgotpassword method
            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagRegist = false;
                initView();
            }
        });
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagRegist = true;
                initView();
            }
        });
        if (!flagRegist){
            button_login.setBackground(getDrawable(R.drawable.button_bg_cpad_lblue));
            button_signup.setBackground(getDrawable(R.drawable.button_bg_cpad_empty));
            edit_name.setVisibility(View.GONE);
        }else {
            button_login.setBackground(getDrawable(R.drawable.button_bg_cpad_empty));
            button_signup.setBackground(getDrawable(R.drawable.button_bg_cpad_lblue));
            edit_name.setVisibility(View.VISIBLE);
        }
        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagRegist){
                    Log.d(TAG, "onClick: flagRegist = "+ flagRegist);
                    Log.d(TAG, "onClick: registering using details: "+ edit_name.getText()+" "+edit_mail.getText()+" "+edit_psw.getText());
                    emailRegist();
                }else {
                    Log.d(TAG, "onClick: flagRegist = "+ flagRegist);
                    Log.d(TAG, "onClick: login using details: "+ edit_mail.getText()+ " " + edit_psw.getText());
                    emailLogin();
                }
            }
        });
    }

    private void emailLogin() {
        Log.d(TAG, "emailLogin: init");
        String email = edit_mail.getText().toString().trim();
        String password = edit_psw.getText().toString().trim();
        Log.d(TAG, "emailLogin: email= "+ email+" pass= "+password);

        if (email.isEmpty()){
            edit_mail.setError(getString(R.string.error_emailEmpty));
            edit_mail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edit_mail.setError(getString(R.string.error_emailInvalid));
            edit_mail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            edit_psw.setError(getString(R.string.error_passwordEmpty));
            edit_psw.requestFocus();
            return;
        }
        if (password.length()<6){
            edit_psw.setError(getString(R.string.error_passwordTShort));
            edit_psw.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(edit_mail.getText().toString(),edit_psw.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: email sign in success!");
                    checkEmailVerified();
                } else {
                    Log.d(TAG, "onComplete: email sign in failed");
                    Toast.makeText(BetaLoginActivity.this, getString(R.string.login_email_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkEmailVerified() {
        Log.d(TAG, "checkEmailVerified: init");
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!= null){
            if (!user.isEmailVerified()){
                Log.d(TAG, "checkEmailVerified: false");
                mAuth.signOut();
                Toast.makeText(this, getString(R.string.error_emailNotVerified), Toast.LENGTH_SHORT).show();
            }else {
                Log.d(TAG, "checkEmailVerified: true, go to main!");
                startActivity(new Intent(BetaLoginActivity.this, BetaMainActivity.class));
            }
        }
    }

    private void sendEmailVerification() {
        Log.d(TAG, "sendEmailVerification: init");
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!= null){
            if (!user.isEmailVerified()){
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(BetaLoginActivity.this, getString(R.string.success_emailVerificationSent), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: email sent");
                        }
                    }
                });
            }
        }
    }

    private void emailRegist() {
        String email = edit_mail.getText().toString().trim();
        String password = edit_psw.getText().toString().trim();
        String name = edit_name.getText().toString().trim();
        Log.d(TAG, "emailRegist: name= "+name+ " email= "+email+" pass= "+ password);

        if (email.isEmpty()){
            edit_mail.setError(getString(R.string.error_emailEmpty));
            edit_mail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edit_mail.setError(getString(R.string.error_emailInvalid));
            edit_mail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            edit_psw.setError(getString(R.string.error_passwordEmpty));
            edit_psw.requestFocus();
            return;
        }
        if (password.length()<6){
            edit_psw.setError(getString(R.string.error_passwordTShort));
            edit_psw.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: register success!");
                    sendEmailVerification();
                    uploadUserInfo();
                }
            }
        });
    }

    private void uploadUserInfo() {
        Log.d(TAG, "uploadUserInfo: init");
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null){
            userDocument = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
            User userDataModel = new User();
            userDataModel.setUserID(firebaseUser.getUid());
            userDataModel.setUserEmail(firebaseUser.getEmail());
            userDataModel.setUserName(edit_name.getText().toString());
            userDocument.set(userDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: data stored and logging out!");
                        mAuth.signOut();
                        startActivity(new Intent(BetaLoginActivity.this, BetaLoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
