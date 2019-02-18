package com.flexor.storage.flexorstoragesolution;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdminVendorPhotoActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AdminVendorPhotoActivity";

//    private CircleImageView uploadImage;
    private Button submitImage;
    private ImageView uploadImage;

    UserVendor userVendor;

    private Uri photoURI;
    private String imageStorageUri;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vendor_photo);

        uploadImage = findViewById(R.id.uploadFoto);
        submitImage = findViewById(R.id.submitImage);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        userVendor = ((UserClient) getApplicationContext()).getUserVendor();

        uploadImage.setOnClickListener(this);
        submitImage.setOnClickListener(this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: yoyo1st");
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.d(TAG, "onActivityResult: yoyo2nd");
            if (resultCode == RESULT_OK){
                Log.d(TAG, "onActivityResult: Result OK! Uri: " +result.getUri());
                photoURI = result.getUri();
                uploadImage.setImageURI(photoURI);
                Log.d(TAG, "onActivityResult: Image Uri Final: " +photoURI);
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: Result ERROR!!!!  "+error);
            }
        }
    }

    private void uploadImageandData(Uri uri, final StorageReference storageReference, final UserVendor userVendor){
        Log.d(TAG, "uploadImage: Attempting upload image");
//        Log.d(TAG, "uploadImage: Details> Uri: "+uri.toString());
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
                            imageStorageUri = uri.getLastPathSegment();
                            userVendor.setVendorIDImgPath(imageStorageUri);
                            Log.d(TAG, "onComplete: Image Uploaded to path: "+imageStorageUri);

                        }
                    });
                }else{
                    Log.d(TAG, "onComplete: Image upload error");
                }
            }
        });
    }

    private void uploadImage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        userVendor = ((UserClient) getApplicationContext()).getUserVendor();
                        final DocumentReference db = FirebaseFirestore.getInstance().collection("Vendor").document(userVendor.getVendorID());
                        StorageReference imagePath = storageReference.child("Images").child("VendorLocationImages").child(userVendor.getVendorID()).child("cropped_" + System.currentTimeMillis() + ".jpg");

                        userVendor.setVendorStatsCode((double) 201);
                        userVendor.setVendorAccepted(true);
                        db.set(userVendor)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: yo!");
                                        Log.d(TAG, "onSuccess: " + userVendor.getVendorStatsCode());
                                        ((UserClient) (getApplicationContext())).setUserVendor(userVendor);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "onFailure: sad", e);
                                    }
                                });
                        uploadImageandData(photoURI, imagePath, userVendor);
                        startActivity(new Intent(getApplicationContext(), SuperAdminActivity.class));

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda yakin dengan foto tersebut?").setPositiveButton("Setuju", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.uploadFoto:
                if (uploadImage.isPressed()){
                    Log.d(TAG, "onClick: Up user ID CARD Clicked");
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(this);
//                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                    startActivity(intent);
                }
            case R.id.submitImage:
                if (submitImage.isPressed()){
                    uploadImage();


                    Log.d(TAG, "onClick: Form Submit Clicked");
                }
                break;
        }
    }
}
