package com.flexor.storage.flexorstoragesolution;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class VendorRegistrationActivity extends AppCompatActivity {
    private static final String TAG = "VendorRegistrationActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    ///view///
    private ConstraintLayout eula, form;
    private Button eulaAccept, formSubmit;
    private TextView eulaTitle, eulaVersion, eulaFill;
    private EditText vendorRegistrationName, vendorRegistrationAddress, vendorRegistrationID, vendorRegistrationNPWP, vendorRegistrationCompany,vendorRegistrationStorageName, vendorRegistrationLocation;
    private CircleImageView vendorRegistrationUPID, vendorRegistrationUPPhoto;
    private CheckBox eulaAcceptCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_registration);

    }
}
