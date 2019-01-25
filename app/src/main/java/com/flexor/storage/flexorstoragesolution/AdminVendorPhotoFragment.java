package com.flexor.storage.flexorstoragesolution;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.UserVendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AdminVendorPhotoFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "AdminVendorPhotoFragment";

    private CircleImageView uploadImage;
    private Button submitImage;

    UserVendor userVendor;

    private Uri photoURI;
    private String imageStorageUri;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_vendor_photo, container, false);

        uploadImage = view.findViewById(R.id.uploadFoto);
        submitImage = view.findViewById(R.id.submitImage);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        userVendor = ((UserClient) getApplicationContext()).getUserVendor();

        uploadImage.setOnClickListener(this);
        submitImage.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


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
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,new VendorApplistFragment()).commit();
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

                        StorageReference imagePath = storageReference.child("Images").child("VendorLocationImages").child(userVendor.getVendorID()).child("cropped_" + System.currentTimeMillis() + ".jpg");
                        uploadImageandData(photoURI, imagePath, userVendor);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new VendorApplistFragment()).commit();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.uploadFoto:
                if (uploadImage.isPressed()){
                    Log.d(TAG, "onClick: Up user ID CARD Clicked");
//                    CropImage.activity()
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .setAspectRatio(1,1)
//                            .start(getApplicationContext(), AdminVendorPhotoFragment.this);
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                    startActivity(intent);
                    getActivity().startActivityForResult(intent, 100);
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
