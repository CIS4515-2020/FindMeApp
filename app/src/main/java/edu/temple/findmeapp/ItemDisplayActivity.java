package edu.temple.findmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class ItemDisplayActivity extends AppCompatActivity {
    private final static String TAG = "ItemDisplayActivity ===>>>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_display);
    }
}
