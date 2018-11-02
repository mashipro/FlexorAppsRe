package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.opencensus.tags.Tag;

public class biodata extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "biodata";
    EditText userName, userAddress, userGender, userCity;
    Button userSubmit;
    CircleImageView userAvatar;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Uri photoURI;
    private String imageStorageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: Result OK! Uri: " + result.getUri());
                photoURI = result.getUri();
                userAvatar.setImageURI(photoURI);
                Log.d(TAG, "onActivityResult: Image Uri Final: " + photoURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: Result ERROR!!!!  " + error);
            }
        }
    }

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
        userAvatar.setOnClickListener(this);

    }

    private void storeUserInfo() {

        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
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
            final User biodataUser = new User();
            biodataUser.setUserID(userID);
            biodataUser.setUserEmail(userEmail);
            biodataUser.setUserName(userNameString);
            biodataUser.setUserAddress(userAddressString);
            biodataUser.setUserGender(userGenderString);
            biodataUser.setUserCity(userCityString);
            biodataUser.setUserAuthCode((double) 101);
            StorageReference imagePath = storageReference.child("Images").child("UserImages").child(newUserDocuments.getId()).child("cropped_" + System.currentTimeMillis() + ".jpg");
            uploadImageandData(photoURI, imagePath, biodataUser, newUserDocuments);
            Log.d(TAG, "storeUserInfo: users UID: " + user.getUid());
            newUserDocuments.set(biodataUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "StoreUserInfoComplete: Users data stored: Firestore. ID: " + user.getUid());
                        Toast.makeText(biodata.this, "Biodata Updated!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(biodata.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else {
                        Log.d(TAG, "StoreUserInfoIncomplete: CHECK LOG!");
                    }
                }
            });
        }
    }

    private void uploadImageandData(Uri uri, final StorageReference storageReference, final User biodataUser, final DocumentReference newVendorReference) {
        Log.d(TAG, "uploadImage: Attempting upload image");
        Log.d(TAG, "uploadImage: Details> Uri: " + uri.toString());
        Log.d(TAG, "uploadImage: Details> Refference: " + storageReference);
        final UploadTask uploadTask = storageReference.putFile(uri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Image Uploaded!!!");
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            imageStorageUri = downloadUrl.getLastPathSegment();
                            biodataUser.setUserAvatar(imageStorageUri);
                            uploadData(newVendorReference, biodataUser);
                            Log.d(TAG, "onComplete: Image Uploaded to path: " + imageStorageUri);
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: Image upload error");
                }
            }
        });
    }

    private void uploadData(DocumentReference newVendorReference, final User biodataUser) {
        newVendorReference.set(biodataUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    User user = ((UserClient) (getApplicationContext())).getUser();
                    Log.d(TAG, "onComplete: user Biodata uploaded Succesfully");
                    Log.d(TAG, "userBiodata: user: " + user.getUserName());
//                    Log.d(TAG, "userVendorData: vendorName: "+userVendor.getVendorName());
//                    Log.d(TAG, "userVendorData: vendorAddress: "+userVendor.getVendorAddress());
//                    Log.d(TAG, "userVendorData: storageName: "+userVendor.getVendorStorageName());
//                    Log.d(TAG, "userVendorData: storageLocation: "+userVendor.getVendorStorageLocation());
                    startActivity(new Intent(biodata.this, MainActivity.class));
                    finish();
                } else {
                    Log.d(TAG, "onComplete: Error Check LOG");
                }
            }
        });

    }

    private void checkuserinfo() {

        String userNameString = userName.getText().toString().trim();
        String userAddressString = userAddress.getText().toString().trim();
        String userGenderString = userGender.getText().toString().trim();
        String userCityString = userCity.getText().toString().trim();


        if (userNameString.isEmpty()){
            userName.setError("Please fill all empty spaces");
            userName.requestFocus();
            return;
        }

        if (userAddressString.isEmpty()){
            userAddress.setError("Please fill all empty spaces");
            userAddress.requestFocus();
            return;
        }

        if (userGenderString.isEmpty()){
            userGender.setError("Please fill all empty spaces");
            userGender.requestFocus();
            return;
        }

        if (userCityString.isEmpty()){
            userCity.setError("Please fill all empty spaces");
            userCity.requestFocus();
            return;
        }

        if (photoURI == null){
            Toast.makeText(getApplicationContext(), R.string.error_form_photo, Toast.LENGTH_SHORT).show();
        }else{
            storeUserInfo();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_submit:
                checkuserinfo();


            case R.id.user_avatar:
                if (userAvatar.isPressed()) {
                    Log.d(TAG, "onClick: User Avatar Clicked");
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this);
                }
        }

    }

}


