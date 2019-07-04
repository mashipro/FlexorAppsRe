package com.flexor.storage.flexorstoragesolution;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class KonfirmTransaksiActivity extends AppCompatActivity {
    private static final String TAG = "KonfirmTransaksiActivit";

    Button send;
    Bitmap thumbnail;
    File pic;
    protected static final int CAMERA_PIC_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirm_transaksi);
        send = findViewById(R.id.upload_bukti);

        Button camera = findViewById(R.id.foto_bukti);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"casteluke.cl@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Konfirmasi Pembayaran Flexor");
            }
        });
    }
}
