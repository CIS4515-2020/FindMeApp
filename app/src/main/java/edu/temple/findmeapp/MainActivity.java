package edu.temple.findmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private ProgressBar registerProgressBar;
    private LinearLayout loginRegisterButtonFrame;
    private Button buttonLoginDialog;
    private Button buttonRegisterDialog;
    private Button buttonLogout;
    private AlertDialog dialogLogin;
    private AlertDialog dialogRegister;

    protected final static String SHARED_PREFS          = "sharedPrefs";
    protected final static String SHARED_PREFS_USERNAME = "sharedPrefsUsername";
    protected final static String SHARED_PREFS_USERID = "sharedPrefsUserId";

    private String dbcall;
    private DatabaseInterface dbInterface;

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checking for NFC component
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if( nfcAdapter == null ){
            Toast.makeText(this, "App requires NFC component.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        buttonNewItemActivity     = findViewById(R.id.button_new_item);
        buttonEditItemActivity    = findViewById(R.id.button_edit_item);
        buttonDeleteItemActivity  = findViewById(R.id.button_delete_item);
        buttonItemDisplayActivity = findViewById(R.id.button_item_display);
        loginRegisterButtonFrame  = findViewById(R.id.login_register_button_frame);
        buttonLogin               = findViewById(R.id.button_login);
        buttonRegister            = findViewById(R.id.button_register);
        buttonLogout              = findViewById(R.id.button_logout);

        dbInterface = new DatabaseInterface( MainActivity.this );
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String username = sharedPreferences.getString(SHARED_PREFS_USERNAME, "");
        if (username.equals("")) { // Not logged in
            getSupportActionBar().setTitle("Find Me");
            loginRegisterButtonFrame.setVisibility(View.VISIBLE);
            buttonLogout.setVisibility(View.GONE);
        } else { // Logged in
            getSupportActionBar().setTitle("Find Me - " +username);
            loginRegisterButtonFrame.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.VISIBLE);
            subscribeToNotifications(false);
        }

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

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsubscribeToNotifications(false);
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHARED_PREFS_USERNAME, "");
                editor.putInt(SHARED_PREFS_USERID, 0);
                editor.commit();
                getSupportActionBar().setTitle("Find Me");
                buttonLogout.setVisibility(View.GONE);
                loginRegisterButtonFrame.setVisibility(View.VISIBLE);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.d("GetInstanceID Failed", task.getException().toString());
                        }

                        String token = task.getResult().getToken();

                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
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

                dbcall = "login";
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

        final EditText editTextRegisterUsername = view.findViewById(R.id.registerDialogUsername);
        final EditText editTextRegisterEmail    = view.findViewById(R.id.registerDialogEmail);
        final EditText editTextRegisterPassword = view.findViewById(R.id.registerDialogPassword);
        final EditText editTextRegisterFirstName = view.findViewById(R.id.registerDialogFirstName);
        final EditText editTextRegisterLastName = view.findViewById(R.id.registerDialogLastName);
        buttonRegisterDialog     = view.findViewById(R.id.registerDialogButton);
        registerProgressBar = view.findViewById(R.id.registerProgressBar);

        buttonRegisterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextRegisterUsername.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editTextRegisterPassword.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editTextRegisterEmail.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(editTextRegisterEmail.getText().toString().trim()).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editTextRegisterFirstName.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editTextRegisterLastName.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                buttonRegisterDialog.setVisibility(View.GONE);
                registerProgressBar.setVisibility(View.VISIBLE);

                dbcall = "register";
                dbInterface.registerUser(
                        editTextRegisterUsername.getText().toString().trim(),
                        editTextRegisterPassword.getText().toString().trim(),
                        editTextRegisterEmail.getText().toString().trim(),
                        editTextRegisterFirstName.getText().toString().trim(),
                        editTextRegisterLastName.getText().toString().trim()

                );
            }
        });

        builder.setView(view);
        dialogRegister = builder.create();
        dialogRegister.show();
    }

    public void subscribeToNotifications(final boolean showToast){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(SHARED_PREFS_USERID, 0);

        if (userId != 0) {
            FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(userId))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscribed successfully to notifications!";
                            if (!task.isSuccessful()) {
                                msg = "Subscription to notifications failed.";
                            }
                            Log.d(TAG, msg);
                            if (showToast) {
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, "Warning: subscribe called with userId 0", Toast.LENGTH_SHORT).show();
        }
    }

    public void unsubscribeToNotifications(final boolean showToast) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(SHARED_PREFS_USERID, 0);

        if (userId == 0) {
            Toast.makeText(MainActivity.this, "Warning: unsubscribe called with userId 0", Toast.LENGTH_SHORT).show();
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(userId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successfully unsubscribed to notifications!";
                        if (!task.isSuccessful()) {
                            msg = "Unsubscribe to notifications failed.";
                        }
                        Log.d(TAG, msg);
                        if (showToast) {
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void response(JSONArray data) {
        try {
            if (dbcall.equals("login")) {
                dbcall = null;
                loginProgressBar.setVisibility(View.GONE);
                buttonLoginDialog.setVisibility(View.VISIBLE);
                User user = new User(data.getJSONObject(0));
                Toast.makeText(getApplicationContext(), "Hello " + user.getUsername(), Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHARED_PREFS_USERNAME, user.getUsername());
                editor.putInt(SHARED_PREFS_USERID, user.getId());
                editor.commit();
                getSupportActionBar().setTitle("Find Me - " + user.getUsername());
                dialogLogin.cancel();
                subscribeToNotifications(false);
                loginRegisterButtonFrame.setVisibility(View.GONE);
                buttonLogout.setVisibility(View.VISIBLE);
            } else if (dbcall.equals("register")){
                dbcall = null;
                registerProgressBar.setVisibility(View.GONE);
                buttonRegisterDialog.setVisibility(View.VISIBLE);
                User user = new User(data.getJSONObject(0));
                Toast.makeText(getApplicationContext(), "Welcome " +user.getUsername(), Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHARED_PREFS_USERNAME, user.getUsername());
                editor.putInt(SHARED_PREFS_USERID, user.getId());
                editor.commit();
                getSupportActionBar().setTitle("Find Me - " + user.getUsername());
                dialogRegister.cancel();
                subscribeToNotifications(false);
                loginRegisterButtonFrame.setVisibility(View.GONE);
                buttonLogout.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorResponse(String error) {
        if (dbcall.equals("login")) {
            loginProgressBar.setVisibility(View.GONE);
            buttonLoginDialog.setVisibility(View.VISIBLE);
        } else if (dbcall.equals("register")) {
            registerProgressBar.setVisibility(View.GONE);
            buttonRegisterDialog.setVisibility(View.VISIBLE);
        }
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbInterface = null;
    }
}
