package com.flexor.storage.flexorstoragesolution;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;

import java.security.MessageDigest;
import java.util.Arrays;

public class Login extends AppCompatActivity implements View.OnClickListener {
    LinearLayout login_filler, login_gnf_button;
    EditText edit_email, edit_password;
    Button button_google, button_facebook, button_login;
    TextView link_forgot, link_register, text_no_acc;
    ProgressBar progress_bar_main;
    private final static int RC_SIGN_IN = 2;
    private static final String EMAIL = "email";
    private int flag = 0;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "Login";


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);

        /// INIT PHASE ///
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mCallbackManager = CallbackManager.Factory.create();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();

        login_filler = findViewById(R.id.login_filler);
        login_gnf_button = findViewById(R.id.login_gnf_button);
        edit_email = findViewById(R.id.etEmail);
        edit_password = findViewById(R.id.etPassword);
        button_google = findViewById(R.id.button_google);
        button_facebook = findViewById(R.id.button_facebook);
        button_login = findViewById(R.id.button_login);
        link_forgot = findViewById(R.id.tvLinkForgot);
        link_register = findViewById(R.id.tvLinkRegister);
        text_no_acc = findViewById(R.id.text_no_acc);
        progress_bar_main = findViewById(R.id.progress_bar_main);
        progress_bar_main.setVisibility(View.GONE);
        flag = 0;


        link_register.setOnClickListener(this);
        button_login.setOnClickListener(this);
        button_google.setOnClickListener(this);
        button_facebook.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                        startActivity(new Intent(Login.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                }
            }
        };

        // Facebook Check Login Status
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Login.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication Failed :(", Toast.LENGTH_SHORT).show();
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
//
//    public void signOut(){
//        startActivity(new Intent(this, Login.class).setFlags(0).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//    }
//
//    private boolean emailverified() {
//        checkIfEmailIsVerified();
//        return true;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.tvLinkRegister:
                if (link_register.isPressed()) {
                    register();
                    Log.d("TAG", "Register Pressed!");
                }
                break;
            case R.id.button_login:
                if (button_login.isPressed()) {
                    if (flag == 2) {
                        registerMe();
                        Log.d("TAG", "RegisterMe Pressed!");
                    }
                    else if (flag == 0){
                            esignIn();
                            Log.d("TAG", "Login Pressed!");
                    }
                }
                break;
            case R.id.button_google:
                gsignIn();
                break;

            case R.id.button_facebook:
                fsignIn();
                break;

            default:
                break;

        }

    }

    private void check() {
        if (flag == 1) {
            //forgot password state
            login_gnf_button.setVisibility(View.GONE);
            edit_password.setVisibility(View.GONE);
            link_forgot.setText(getString(R.string.login));
            button_login.setText(getString(R.string.submit));
        } else if (flag == 2) {
            //register email state
            login_gnf_button.setVisibility(View.GONE);
            edit_password.setVisibility(View.VISIBLE);
            link_forgot.setText(getString(R.string.login));
            button_login.setText(getString(R.string.register));
        } else if (flag == 0) {
            //normal state
            login_gnf_button.setVisibility(View.VISIBLE);
            edit_password.setVisibility(View.VISIBLE);
            link_forgot.setText(getString(R.string.forgot_password));
            button_login.setText(getString(R.string.login));
        }
    }

    private void home() {
        flag = 0;
        check();
    }

    private void register() {
        if (flag == 0) {
            flag = 2;
            check();
        } else {
            home();
        }
    }

    private void esignIn() {
        String email = edit_email.getText().toString().trim();
        String password = edit_password.getText().toString().trim();

        if (email.isEmpty()) {
            edit_email.setError("Email is required");
            edit_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edit_email.setError("Please enter a valid email");
            edit_email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edit_password.setError("Password is required");
            edit_password.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edit_password.setError("The minimum length of password is 6 characters");
            edit_password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkIfEmailIsVerified();
                            Log.d("TAG", "SignIn with email: Success");
                        } else {
                            Log.d("TAG", "SignIn with email: failed");
                            Toast.makeText(Login.this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void fsignIn(){

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL));
    }

    private void gsignIn() {
//        progressBar.setVisibility(View.VISIBLE);
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void registerMe() {
            String email = edit_email.getText().toString().trim();
            String password = edit_password.getText().toString().trim();

            if (email.isEmpty()) {
                edit_email.setError("Email is required!");
                edit_email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edit_email.setError("Please enter a valid email address!");
                edit_email.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                edit_password.setError("Password is required!");
                edit_password.requestFocus();
                return;
            }

            if (password.length() < 6) {
                edit_password.setError("Minimum lenght of password is 6 characters");
                edit_password.requestFocus();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success");
                                sendEmailVerification();


                                flag = 0;
                                check();
                                FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            } else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                                }

                                // ...
                            }
                        }
                    });

    }

//    private void storeUserInfo() {
//        FirebaseUser newUsers = FirebaseAuth.getInstance().getCurrentUser();
//        String userID = newUsers.getUid();
//        String userEmail = newUsers.getEmail();
//        DocumentReference newUserDocuments = mFirebaseFirestore.collection("Users").document();
//        User users = new User();
//        users.setUserID(userID);
//        users.setUserEmail(userEmail);
//
//        newUserDocuments.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    Log.d(TAG, "StoreUserInfoComplete: Users data stored: Firestore. ID: " + newUsers.getUid());
//                }else{
//                    Log.d(TAG, "StoreUserInfoIncomplete: CHECK LOG!");
//                }
//            }
//        });
//    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                storeUserInfo();
                                Log.d("TAG", "Email sent.");
                                storeUserInfo();
                            }
                        }
                    });
        }

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
            User users = new User();
            users.setUserID(userID);
            users.setUserEmail(userEmail);
            Log.d(TAG, "storeUserInfo: users UID: " + user.getUid() );
            newUserDocuments.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "StoreUserInfoComplete: Users data stored: Firestore. ID: " + user.getUid());
                        mAuth.signOut();
                    }else{
                        Log.d(TAG, "StoreUserInfoIncomplete: CHECK LOG!");
                        mAuth.signOut();
                    }
                }
            });

            ////trying save into firebase database////
            //Todo firebase db save try
            mDatabaseRef.child("Users").child(user.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "onComplete: data uploaded to db");
                }
            });
            


        }
    }

    private void checkIfEmailIsVerified() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()){
                finish();
                Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.signOut();
                Toast.makeText(this, "Please Verify your Email first", Toast.LENGTH_SHORT).show();

            }
        }

    }


}

