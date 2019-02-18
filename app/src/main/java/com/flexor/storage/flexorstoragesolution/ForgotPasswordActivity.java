package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";

    private EditText emailPass;
    private Button resetPass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        emailPass = findViewById(R.id.emailPass);
        resetPass = findViewById(R.id.resetPass);

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = emailPass.getText().toString().trim();

                if (userEmail.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ForgotPasswordActivity.this, "Email has been sent", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPasswordActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        Log.d(TAG, "onComplete: email sent");
                                    }else{
                                        Log.d(TAG, "onComplete: error on sending the email");
                                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}
