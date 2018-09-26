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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {
    LinearLayout login_filler, login_gnf_button;
    EditText edit_email, edit_password;
    Button button_google, button_facebook, button_login;
    TextView link_forgot, link_register, text_no_acc;
    ProgressBar progress_bar_main;
    private int flag = 0;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                        startActivity(new Intent(Login.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                }
            }
        };
        test();


        link_register.setOnClickListener(this);
        button_login.setOnClickListener(this);

    }

    private void test() {
        progress_bar_main.setVisibility(View.GONE);
    }

    public void signOut(){
        startActivity(new Intent(this, Login.class).setFlags(0).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private boolean emailverified() {
        checkIfEmailIsVerified();
        return true;
    }

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
                                mAuth.signOut();
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

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "Email sent.");
                            }
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

