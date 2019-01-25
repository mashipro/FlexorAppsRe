package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class VendorRegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VendorRegistration";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private FirebaseUser authUser;

    ///view///
    private ConstraintLayout eula, form;
    private Button eulaAccept, formSubmit;
    private TextView eulaTitle, eulaVersion, eulaFill;
    private EditText vendorRegistrationName, vendorRegistrationAddress, vendorRegistrationIDnumber, vendorRegistrationNPWP, vendorRegistrationCompany,vendorRegistrationStorageName, vendorRegistrationLocation;
    private CircleImageView vendorRegistrationUPID;
    private CheckBox eulaAcceptCheck;
    private ProgressBar progressBar;

    ///Custom Init///
    private String vendorName, vendorAddress, vendorIDnumber, vendorNPWP, vendorCompany, vendorStorageName, vendorStorageLocation ;
    private Uri photoURI;
    private String imageStorageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_registration);

        //Firebase Init//
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //User Check Init//
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser authUser = firebaseAuth.getCurrentUser();
                if (authUser==null){
                    startActivity(new Intent(VendorRegistrationActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else {
                    Log.d(TAG, "onAuthStateChanged: getting User Details");
                    getUserDetails(authUser);
                }
            }
        };
        authUser = mAuth.getCurrentUser();

        //View Init//
        eula = findViewById(R.id.eula);
        form = findViewById(R.id.vendor_registration_form);

        eulaAccept = findViewById(R.id.eula_accept_button);
        formSubmit = findViewById(R.id.button_submit);

        eulaTitle = findViewById(R.id.eula_text_title);
        eulaVersion = findViewById(R.id.eula_ver);
        eulaFill = findViewById(R.id.eula_text);

        vendorRegistrationName = findViewById(R.id.vendor_registration_name);
        vendorRegistrationAddress = findViewById(R.id.vendor_registration_address);
        vendorRegistrationIDnumber = findViewById(R.id.vendor_registration_id);
        vendorRegistrationNPWP = findViewById(R.id.vendor_registration_NPWP);
        vendorRegistrationCompany = findViewById(R.id.vendor_registration_company);
        vendorRegistrationStorageName = findViewById(R.id.vendor_registration_storageName);
        vendorRegistrationLocation = findViewById(R.id.vendor_registration_location);

        vendorRegistrationUPID = findViewById(R.id.vendor_registration_uploadID);

        eulaAcceptCheck = findViewById(R.id.eula_accept_check);
        progressBar = findViewById(R.id.progressBar);

        //Default View//
        eula.setVisibility(View.VISIBLE);
        form.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);




        //Eula Accept Button
        eulaAccept.setOnClickListener(this);
        formSubmit.setOnClickListener(this);
        vendorRegistrationUPID.setOnClickListener(this);

    }

//    private void registrationCheck(final User currentUser) {
//        Log.d(TAG, "registrationCheck: checking.....");
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setTimestampsInSnapshotsEnabled(true)
//                .setPersistenceEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);
//        DocumentReference vendorReference = db.collection("Vendor").document(currentUser.getUserID());
//        vendorReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                UserVendor userVendor = task.getResult().toObject(UserVendor.class);
//                ((UserVendorClient)(getApplicationContext())).setUserVendor(userVendor);
//                if (!currentUser.getUserID().equals(userVendor.getUser().getUserID()) ){
//                    Log.d(TAG, "onComplete: User already registered!!!!!");
//                    Toast.makeText(VendorRegistrationActivity.this, R.string.error_already_registered , Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(VendorRegistrationActivity.this, MainActivity.class));
//                }
//            }
//        });

//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.eula_accept_button:
                if (eulaAccept.isPressed()){
                    if (eulaAcceptCheck.isChecked()){
                        eula.setVisibility(View.GONE);
                        form.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onClick: eula Accepted");
                    }else {
                        Toast.makeText(this, R.string.pleaseAccept, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.button_submit:
                if (formSubmit.isPressed()){
                    checkFormCompletion();

                    Log.d(TAG, "onClick: Form Submit Clicked");
                }
                break;
            case R.id.vendor_registration_uploadID:
                if (vendorRegistrationUPID.isPressed()){
                    Log.d(TAG, "onClick: Up user ID CARD Clicked");
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(this);
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Log.d(TAG, "onActivityResult: Result OK! Uri: " +result.getUri());
                photoURI = result.getUri();
                vendorRegistrationUPID.setImageURI(photoURI);
                Log.d(TAG, "onActivityResult: Image Uri Final: " +photoURI);
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: Result ERROR!!!!  "+error);
            }
        }
    }


    private void checkFormCompletion() {
        //Getting Fill Info//
        vendorName = vendorRegistrationName.getText().toString();
        vendorAddress = vendorRegistrationAddress.getText().toString();
        vendorIDnumber = vendorRegistrationIDnumber.getText().toString();
        vendorNPWP = vendorRegistrationNPWP.getText().toString();
        vendorCompany = vendorRegistrationCompany.getText().toString();
        vendorStorageName = vendorRegistrationStorageName.getText().toString();
        vendorStorageLocation = vendorRegistrationLocation.getText().toString();

        if (vendorName.isEmpty() && vendorAddress.isEmpty() && vendorIDnumber.isEmpty() && vendorStorageName.isEmpty() && vendorStorageLocation.isEmpty()){
            Toast.makeText(this, R.string.error_form_input, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "checkFormCompletion: Error data not complete!!!!!");
            return;
        }else {
            Log.d(TAG, "checkFormCompletion: data Complete!");
            if (photoURI == null){
                Toast.makeText(this, R.string.error_form_photo, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "checkFormCompletion: Error photo not selected!!!!");
                return;
            }else {
                Log.d(TAG, "checkFormCompletion: data complete! try to store all data to singleton!");
                User user = ((UserClient)(getApplicationContext())).getUser();
                if (user != null){
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(true)
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
//                    mFirestore.setFirestoreSettings(settings);
                    DocumentReference newVendorReference = mFirestore.collection("Vendor").document(user.getUserID());
                    final UserVendor userVendor = new UserVendor();
                    userVendor.setVendorOwner(user.getUserID());
                    userVendor.setVendorName(vendorName);
                    userVendor.setVendorAddress(vendorAddress);
                    userVendor.setVendorIDNumber(vendorIDnumber);
                    userVendor.setVendorNPWP(vendorNPWP);
                    userVendor.setVendorCompany(vendorCompany);
                    userVendor.setVendorStorageName(vendorStorageName);
                    userVendor.setVendorStorageLocation(vendorStorageLocation);
                    userVendor.setVendorAccepted(Boolean.FALSE);
                    userVendor.setVendorStatsCode((double) 211);
                    userVendor.setVendorID(newVendorReference.getId());
                    StorageReference imagePath = storageReference.child("Images").child("VendorImages").child(newVendorReference.getId()).child("cropped_"+System.currentTimeMillis()+".jpg");
                    uploadImageandData(photoURI, imagePath, userVendor, newVendorReference);
//                    userVendor.setVendorIDImgPath(imageStorageUri);

//                    newVendorReference.set(userVendor).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()){
//                                User user = ((UserClient)(getApplicationContext())).getUser();
//                                Log.d(TAG, "onComplete: userVendor Data uploaded Succesfully");
//                                Log.d(TAG, "userVendorData: user: "+user.getUserName());
//                                Log.d(TAG, "userVendorData: vendorName: "+userVendor.getVendorName());
//                                Log.d(TAG, "userVendorData: vendorAddress: "+userVendor.getVendorAddress());
//                                Log.d(TAG, "userVendorData: IDnumber: "+userVendor.getVendorIDNumber());
//                                Log.d(TAG, "userVendorData: NPWP: "+userVendor.getVendorNPWP());
//                                Log.d(TAG, "userVendorData: company: "+userVendor.getVendorCompany());
//                                Log.d(TAG, "userVendorData: storageName: "+userVendor.getVendorStorageName());
//                                Log.d(TAG, "userVendorData: storageLocation: "+userVendor.getVendorStorageLocation());
//                                Log.d(TAG, "userVendorData: vendorIDImage: "+userVendor.getVendorIDImgPath());
//                                Log.d(TAG, "userVendorData: vendorID: "+userVendor.getVendorName());
//                                startActivity(new Intent(VendorRegistrationActivity.this,MainActivity.class));
//                                finish();
//                            }else{
//                                Log.d(TAG, "onComplete: Error Check LOG");
//                            }
//                        }
//                    });

                }else{
                    Log.d(TAG, "checkFormCompletion: User is NULL!");
                    getUserDetails(authUser);
                }
            }
        }
    }

    private void uploadData(DocumentReference newVendorReference, final UserVendor userVendor) {
        newVendorReference.set(userVendor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    User user = ((UserClient)(getApplicationContext())).getUser();
                    Log.d(TAG, "onComplete: userVendor Data uploaded Succesfully");
                    Log.d(TAG, "userVendorData: user: "+user.getUserName());
                    Log.d(TAG, "userVendorData: vendorName: "+userVendor.getVendorName());
                    Log.d(TAG, "userVendorData: vendorAddress: "+userVendor.getVendorAddress());
                    Log.d(TAG, "userVendorData: IDnumber: "+userVendor.getVendorIDNumber());
                    Log.d(TAG, "userVendorData: NPWP: "+userVendor.getVendorNPWP());
                    Log.d(TAG, "userVendorData: company: "+userVendor.getVendorCompany());
                    Log.d(TAG, "userVendorData: storageName: "+userVendor.getVendorStorageName());
                    Log.d(TAG, "userVendorData: storageLocation: "+userVendor.getVendorStorageLocation());
                    Log.d(TAG, "userVendorData: vendorIDImage: "+userVendor.getVendorIDImgPath());
                    Log.d(TAG, "userVendorData: vendorID: "+userVendor.getVendorID());
                    startActivity(new Intent(VendorRegistrationActivity.this,MainActivity.class));
                    finish();
                }else{
                    Log.d(TAG, "onComplete: Error Check LOG");
                }
            }
        });

    }
    private void uploadImageandData(Uri uri, final StorageReference storageReference, final UserVendor userVendor, final DocumentReference newVendorReference){
        Log.d(TAG, "uploadImage: Attempting upload image");
        Log.d(TAG, "uploadImage: Details> Uri: "+uri.toString());
        Log.d(TAG, "uploadImage: Details> Refference: "+storageReference);
        final UploadTask uploadTask = storageReference.putFile(uri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Image Uploaded!!!");
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            imageStorageUri = downloadUrl.getLastPathSegment();
                            userVendor.setVendorIDImgPath(imageStorageUri);
                            uploadData(newVendorReference, userVendor);
                            Log.d(TAG, "onComplete: Image Uploaded to path: "+imageStorageUri);
                        }
                    });
                }else{
                    Log.d(TAG, "onComplete: Image upload error");
                }
            }
        });
    }

    private void getUserDetails(FirebaseUser user) {
        String userUID = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference userRef = db.collection("Users").document(userUID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: User data retrieved");
                    User currentUser = task.getResult().toObject(User.class);
                    ((UserClient)(getApplicationContext())).setUser(currentUser);
//                    registrationCheck(currentUser);
                }
            }
        });
    }



}
