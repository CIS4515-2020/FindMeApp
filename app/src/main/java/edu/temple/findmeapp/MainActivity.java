package edu.temple.findmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity ===>>>";

    private Button buttonNewItemActivity;
    private Button buttonEditItemActivity;
    private Button buttonDeleteItemActivity;
    private Button buttonItemDisplayActivity;

    private Button buttonLogin;
    private Button buttonRegister;
    private AlertDialog dialogLogin;
    private AlertDialog dialogRegister;

    private String email;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNewItemActivity     = findViewById(R.id.button_new_item);
        buttonEditItemActivity    = findViewById(R.id.button_edit_item);
        buttonDeleteItemActivity  = findViewById(R.id.button_delete_item);
        buttonItemDisplayActivity = findViewById(R.id.button_item_display);
        buttonLogin               = findViewById(R.id.button_login);
        buttonRegister            = findViewById(R.id.button_register);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);

        EditText editTextLoginUsername = view.findViewById(R.id.loginDialogUsername);
        EditText editTextLoginPassword = view.findViewById(R.id.loginDialogPassword);
        Button   buttonLoginDialog     = view.findViewById(R.id.loginDialogButton);

        buttonLoginDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO send login request to server
                // TODO save login info
                dialogLogin.cancel();
            }
        });

        builder.setView(view);
        dialogLogin = builder.create();
        dialogLogin.show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_register, null);

        EditText editTextRegisterUsername = view.findViewById(R.id.registerDialogUsername);
        EditText editTextRegisterEmail    = view.findViewById(R.id.registerDialogEmail);
        EditText editTextRegisterPassword = view.findViewById(R.id.registerDialogPassword);
        Button   buttonRegisterDialog     = view.findViewById(R.id.registerDialogButton);

        buttonRegisterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO send registration request to server
                dialogRegister.cancel();
            }
        });

        builder.setView(view);
        dialogRegister = builder.create();
        dialogRegister.show();
    }



}
