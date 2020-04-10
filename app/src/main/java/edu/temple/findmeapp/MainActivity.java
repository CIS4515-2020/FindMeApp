package edu.temple.findmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity ===>>>";

    private Button buttonNewItemActivity;
    private Button buttonEditItemActivity;
    private Button buttonDeleteItemActivity;
    private Button buttonItemDisplayActivity;

    private Button buttonLogin;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNewItemActivity = findViewById(R.id.button_new_item);
        buttonEditItemActivity = findViewById(R.id.button_edit_item);
        buttonDeleteItemActivity = findViewById(R.id.button_delete_item);
        buttonItemDisplayActivity = findViewById(R.id.button_item_display);

        buttonNewItemActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewItemActivity.class);
                startActivity(intent);
            }
        });

        buttonEditItemActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                startActivity(intent);
            }
        });

        buttonDeleteItemActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeleteItemActivity.class);
                startActivity(intent);
            }
        });

        buttonItemDisplayActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemDisplayActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

    }

    private void showLoginDialog() {

    }

    private void showRegisterDialog() {

    }



}
