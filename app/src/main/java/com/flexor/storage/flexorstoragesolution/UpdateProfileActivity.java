package com.flexor.storage.flexorstoragesolution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference mDatabase;

    EditText Nama, Umur, Alamat, TglLahir;
    CircleImageView ProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Nama = (EditText) findViewById(R.id.etName);
        Umur = (EditText) findViewById(R.id.etAge);
        Alamat = (EditText) findViewById(R.id.etAddress);
        TglLahir = (EditText) findViewById(R.id.etDate);
        ProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDatabase.getRef();

    }

    @Override
    public void onClick(View view) {

    }
}
