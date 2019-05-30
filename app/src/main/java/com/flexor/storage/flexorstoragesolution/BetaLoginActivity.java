package com.flexor.storage.flexorstoragesolution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

public class BetaLoginActivity extends AppCompatActivity {
    private static final String TAG = "BetaLoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private Button button_login, button_signup, button_accept;
    private EditText edit_name, edit_mail, edit_psw;
    private TextView text_forgotpw;

    private Boolean flagRegist = false;
    private Boolean flagForgotPw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beta_login);

        mAuth = FirebaseAuth.getInstance();
//        Init View
        button_login = findViewById(R.id.button_login);
        button_signup = findViewById(R.id.button_signUp);
        button_accept = findViewById(R.id.button_cont);
        edit_name = findViewById(R.id.edit_signUp_name);
        edit_mail = findViewById(R.id.edit_login_email);
        edit_psw = findViewById(R.id.edit_login_password);
        text_forgotpw = findViewById(R.id.link_forgotPW);

        initView();
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
    }

    private void emailRegist() {
    }
}