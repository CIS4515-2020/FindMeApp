package edu.temple.findmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

public class NewItemActivity extends AppCompatActivity implements DatabaseInterface.DbResponseListener{
    private final static String TAG = "NewItemActivity ===>>>";

    PendingIntent pi;

    private DatabaseInterface dbInterface;
    private int userId;

    private static final String API_DOMAIN = "https://findmeapp.tech";
    static final String ITEM_EXT = "/item";
    static final String ADD_ACTION = "/add";

    Button addBtn;
    EditText editTextName, editTextDesc;
    boolean mWriteNfc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        getSupportActionBar().setTitle("New Item");

        userId = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE)
                .getInt(MainActivity.SHARED_PREFS_USERID, 0);
        if (userId == 0) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            dbInterface = new DatabaseInterface(this);
        }

        Intent intent = new Intent(NewItemActivity.this, NewItemActivity.class);
        pi = PendingIntent.getActivity(NewItemActivity.this, 0, intent, 0);

        addBtn = findViewById(R.id.addBtn);
        editTextName = findViewById(R.id.newNameET);
        editTextDesc = findViewById(R.id.newDescET);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = editTextName.getText().toString();
                String itemDesc = editTextDesc.getText().toString();
                if ( itemName.isEmpty() || (itemName.trim().length() == 0) ){
                    Toast.makeText(NewItemActivity.this,
                            "Item name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if ( itemDesc.isEmpty() || (itemDesc.trim().length() == 0) ){
                    Toast.makeText(NewItemActivity.this,
                            "Item description cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    dbInterface.addItem(userId, itemName, itemDesc);
                    mWriteNfc = true;

                    // Pending Intent so that tags are delivered to this activity
                    Intent intent = new Intent(NewItemActivity.this, NewItemActivity.class);
                    pi = PendingIntent.getActivity(NewItemActivity.this, 0, intent, 0);
                }
            }
        });
    }

    // Ensuring this app is set on foreground, needed for NFC tag connection
    @Override
    protected void onResume() {
        super.onResume();
        NfcAdapter.getDefaultAdapter(getApplicationContext()).enableForegroundDispatch(this, pi, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(getApplicationContext()).disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            // upon receiving new intent, call method writing to tag
            writeTag((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        }
    }

    // write payload of URI to Tag
    private void writeTag(Tag tag){
        NdefRecord uriRecord = NdefRecord.createUri(API_DOMAIN);
        NdefMessage msg = new NdefMessage( new NdefRecord[]{uriRecord});

        Ndef ndefTag = Ndef.get(tag);
        if(ndefTag != null){

            try{
                ndefTag.connect();

                ndefTag.writeNdefMessage(msg);

            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                try{
                    ndefTag.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else{
            // TODO: Write code to format tag for NDEF
            // Tag is not yet Ndef formatted.
            Toast.makeText(NewItemActivity.this, "Sorry! " +
                    "Tag is not yet NDEF Formatted.", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: Implements dialog box so that if canceled, writing to NFC is cancelled
    private void showNfcDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NewItemActivity.this);
    }

    @Override
    public void response(JSONArray data) {
        Toast.makeText(NewItemActivity.this, "Item added to DB", Toast.LENGTH_SHORT).show();
        Log.d("Add Response", data.toString());
    }

    @Override
    public void errorResponse(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        Log.d("Add ResponseError", error);
    }
}
