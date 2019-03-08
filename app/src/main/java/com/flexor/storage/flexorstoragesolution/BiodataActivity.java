package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class BiodataActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "BiodataActivity";
    EditText userName, userAddress, userPhone;
    Button userSubmit;
    CircleImageView userAvatar;
    Spinner userCity;
    RadioGroup radioGroup;
    RadioButton userGender;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;
    private FirebaseUser firebaseUser;
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
        firebaseUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //EditText
        userName = findViewById(R.id.biodata_name);
        userAddress = findViewById(R.id.biodata_address);
        userPhone = findViewById(R.id.biodata_phoneNumber);
        //Button
        userSubmit = findViewById(R.id.button_submit);
        //CircleImageView
        userAvatar = findViewById(R.id.user_avatar);
        //RadioButton
        radioGroup = findViewById(R.id.radio_gender);
        //Spinner
        userCity = findViewById(R.id.city_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.daftar_kota_vendor, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        userCity.setAdapter(adapter);

        user = ((UserClient) getApplicationContext()).getUser();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                        startActivity(new Intent(BiodataActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                }
            }
        };

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
            String userPhoneString = userPhone.getText().toString().trim();
            int selectedId = radioGroup.getCheckedRadioButtonId();
            userGender = findViewById(selectedId);
            String userGenderString = userGender.getText().toString().trim();
            String userCityString = userCity.getSelectedItem().toString().trim();
            Integer userBalance = 0;
            Integer userAuthcode = 101;

            final User biodataUser = new User();
            biodataUser.setUserID(userID);
            biodataUser.setUserEmail(userEmail);
            biodataUser.setUserName(userNameString);
            biodataUser.setUserAddress(userAddressString);
            biodataUser.setUserPhone(userPhoneString);
            biodataUser.setUserGender(userGenderString);
            biodataUser.setUserCity(userCityString);
            biodataUser.setUserAuthCode(userAuthcode);
            biodataUser.setUserBalance(userBalance);
            biodataUser.setUserIsVendor(false);
            StorageReference imagePath = storageReference.child("Images").child("UserImages").child(newUserDocuments.getId()).child("cropped_" + System.currentTimeMillis() + ".jpg");
            uploadImageandData(photoURI, imagePath, biodataUser, newUserDocuments);
            Log.d(TAG, "storeUserInfo: users UID: " + user.getUid());
        }
    }

    private void uploadImageandData(Uri uri, final StorageReference storageReference,final User biodataUser, final DocumentReference newUserDocuments) {
        Log.d(TAG, "uploadImage: Attempting upload image");
        Log.d(TAG, "uploadImage: Details> Uri: " + uri.toString());
        Log.d(TAG, "uploadImage: Details> Refference: " + storageReference);
        final UploadTask uploadTask = storageReference.putFile(uri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Image Uploaded!!!");
                    Toast.makeText(BiodataActivity.this, "uploadImageandData", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            imageStorageUri = downloadUrl.getLastPathSegment();
                            biodataUser.setUserAvatar(imageStorageUri);
                            uploadData(newUserDocuments, biodataUser);
                            Log.d(TAG, "onComplete: Image Uploaded to path: " + imageStorageUri);
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: Image upload error");
                }
            }
        });
    }

    private void uploadData(DocumentReference newUserDocuments, final User biodataUser) {
        newUserDocuments.set(biodataUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(BiodataActivity.this, LoginCheckerActivity.class));
                    Log.d(TAG, "StoreUserInfoComplete: Users data stored: Firestore. ID: " + user.getUserID());
                    Toast.makeText(BiodataActivity.this, "Biodata Updated!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(BiodataActivity.this, "Please Re-Login", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: biodata update" + biodataUser.getUserAddress());
                    Log.d(TAG, "onComplete: firebaseUser Biodata uploaded Succesfully");
                    Log.d(TAG, "userBiodata: firebaseUser: " + user.getUserName());
                } else {
                    Log.d(TAG, "onComplete: Error Check LOG");
                }
            }
        });

    }

    private void checkuserinfo() {

        String userNameString = userName.getText().toString().trim();
        String userAddressString = userAddress.getText().toString().trim();
        String userPhoneString = userPhone.getText().toString().trim();


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

        if (userPhoneString.isEmpty()){
            userPhone.setError("please fill all empty spaces");
            userPhone.requestFocus();
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pria:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_wanita:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

}

// TODO: 2/26/2019 setting code for each said city

