package com.flexor.storage.flexorstoragesolution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class BoxDetailsActivity extends AppCompatActivity {
    private static final String TAG = "BoxDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extra = getIntent().getExtras();

        Log.d(TAG, "onCreate: id retrieved: "+ extra.getString("boxIDforExtra"));
    }
}
