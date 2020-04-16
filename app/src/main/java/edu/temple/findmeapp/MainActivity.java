package edu.temple.findmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DatabaseInterface.DbResponseListener {
    private final static String TAG = "MainActivity ===>>>";

    private Button buttonNewItemActivity;
    private Button buttonEditItemActivity;
    private Button buttonDeleteItemActivity;
    private Button buttonItemDisplayActivity;

    private Button      buttonLogin;
    private Button      buttonRegister;
    private ProgressBar loginProgressBar;
    private Button buttonLoginDialog;
    private AlertDialog dialogLogin;
    private AlertDialog dialogRegister;
    private Boolean     loggedIn;

    private String username;

    private final static String SHARED_PREFS          = "sharedPrefs";
    private final static String SHARED_PREFS_USERNAME = "sharedPrefsUsername";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(SHARED_PREFS_USERNAME, "");
        if (username.equals("")) {
            loggedIn = false;
        } else {
            loggedIn = true;
            getSupportActionBar().setTitle("Find Me - " +username);
        }

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

        final EditText editTextLoginUsername = view.findViewById(R.id.loginDialogUsername);
        final EditText editTextLoginPassword = view.findViewById(R.id.loginDialogPassword);
        loginProgressBar = view.findViewById(R.id.loginProgressBar);
        buttonLoginDialog     = view.findViewById(R.id.loginDialogButton);

        buttonLoginDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO send login request to server
                String editTextLoginUsernameText = editTextLoginUsername.getText().toString();
                if (editTextLoginUsernameText.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }else if (editTextLoginPassword.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                buttonLoginDialog.setVisibility(View.GONE);
                loginProgressBar.setVisibility(View.VISIBLE);

                DatabaseInterface dbInterface = new DatabaseInterface( MainActivity.this );
                dbInterface.login( editTextLoginUsernameText, editTextLoginPassword.getText().toString() );
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


    @Override
    public void response(JSONArray data) {
        try {
            loginProgressBar.setVisibility(View.GONE);
            buttonLoginDialog.setVisibility(View.VISIBLE);
            User user = new User( data.getJSONObject(0) );
            Toast.makeText(getApplicationContext(), "Hello " + user.getUsername() , Toast.LENGTH_LONG).show();
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARED_PREFS_USERNAME, user.getUsername());
            editor.commit();
            loggedIn = true;
            getSupportActionBar().setTitle("Find Me - " + user.getUsername());
            dialogLogin.cancel();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorResponse(String error) {
        loginProgressBar.setVisibility(View.GONE);
        buttonLoginDialog.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }
}
