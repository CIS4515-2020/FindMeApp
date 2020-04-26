package edu.temple.findmeapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

public class NewItemActivity extends AppCompatActivity implements DatabaseInterface.DbResponseListener{
    private final static String TAG = "NewItemActivity ===>>>";

    PendingIntent pi;

    private DatabaseInterface dbInterface;
    private String dbcallback;
    private int userId;

    private static final String API_DOMAIN = "https://findmeapp.tech";
    static final String FOUND_ACTION = "/found-item";
    private int itemId;

    private AlertDialog writeNFCDialog, confirmDeleteDialog;
    private static final int PENDING_INTENT_NDEF_DISCOVERED = 1;
    private NfcAdapter mNfcAdapter;
    boolean mWriteNfc = false, confirmWrite = false;

    Button addBtn;
    EditText editTextName, editTextDesc;
    private String itemName, itemDesc;
    private String payload;

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

        addBtn = findViewById(R.id.addBtn);
        editTextName = findViewById(R.id.newNameET);
        editTextDesc = findViewById(R.id.newDescET);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemName = editTextName.getText().toString();
                itemDesc = editTextDesc.getText().toString();
                if ( itemName.isEmpty() || (itemName.trim().length() == 0) ){
                    Toast.makeText(NewItemActivity.this,
                            "Item name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if ( itemDesc.isEmpty() || (itemDesc.trim().length() == 0) ){
                    Toast.makeText(NewItemActivity.this,
                            "Item description cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    mWriteNfc = true;

                    dbcallback = "getNewItemId";
                    dbInterface.getNewItemId();

                    NewItemActivity.this.showWriteDialog();
                }
            }
        });
    }

    // Ensuring this app is set on foreground, needed for NFC tag connection
    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());

        PendingIntent pi = createPendingResult(PENDING_INTENT_NDEF_DISCOVERED, new Intent(), 0);

        mNfcAdapter.enableForegroundDispatch(this, pi,
                new IntentFilter[]{ new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)},
                new String[][]{new String[]{ "android.nfc.tech.Ndef"}});
    }

    // Disabling NFC PendingIntent (PI) if app is not in foreground.
    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(getApplicationContext()).disableForegroundDispatch(this);
    }

    // On discovering NFC tag from the PI results and assigning action
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case PENDING_INTENT_NDEF_DISCOVERED:
                if(data != null) {
                    resolveIntent(data, true);
                    break;
                }
        }
    }

    // Action to take care of PI
    protected void resolveIntent(Intent data, boolean foregroundDispatch){
        String action = data.getAction();
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
            Tag tag = data.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // Checking this app is in foreground and we want to write to Tag
            if(foregroundDispatch && mWriteNfc) {
                mWriteNfc = false;

                writeTag(tag);

                writeNFCDialog.dismiss();
            }
        }
    }

    // write payload of URI to Tag
    private void writeTag(Tag tag){
        Ndef ndefTag = Ndef.get(tag);
        if(ndefTag != null){

            try{
                ndefTag.connect();

                NdefMessage ndefMessage = ndefTag.getNdefMessage();

                if( (ndefMessage == null) || confirmWrite ){
                    NdefRecord uriRecord = NdefRecord.createUri(API_DOMAIN+FOUND_ACTION+"/"+itemId);
                    NdefMessage msg = new NdefMessage( new NdefRecord[]{uriRecord} );
                    ndefTag.writeNdefMessage(msg);
                    Log.d("WriteTAG", "Writing to tag successful");
                    Toast.makeText(NewItemActivity.this, "Writing to tag successful",
                            Toast.LENGTH_SHORT).show();
                    confirmWrite = false;
                    dbInterface = new DatabaseInterface(NewItemActivity.this);
                    dbcallback = "addItem";
                    dbInterface.addItem(itemId, userId, itemName, itemDesc);
                }
                else{
                    payload = new String(ndefMessage.getRecords()[0].getPayload());
                    showConfirmDialog();
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Toast.makeText(NewItemActivity.this, "Error. Could not write to tag.",
                        Toast.LENGTH_SHORT).show();
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
            Log.d("FormatError", "Not NDEF Formatted!");
            Toast.makeText(NewItemActivity.this, "Sorry! " +
                    "Tag is not yet NDEF Formatted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWriteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NewItemActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_scantag, null);

        Button writeCancelButton = view.findViewById(R.id.writeCancelBtn);
        writeCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeNFCDialog.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("mWriteNFC Boolean", String.valueOf(mWriteNfc));
                mWriteNfc = false;
                Log.d("mWriteNFC Boolean after dismiss", String.valueOf(mWriteNfc));
            }
        });

        builder.setView(view);
        writeNFCDialog = builder.create();
        writeNFCDialog.show();

    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewItemActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_delete, null);

        TextView confirmDeleteDialogTitle = view.findViewById(R.id.confirmDeleteDialogTitle);
        TextView confirmDeleteDialogText = view.findViewById(R.id.confirmDeleteDialogText);
        final Button confirmDeleteDialogButton = view.findViewById(R.id.confirmDeleteDialogButton);
        final ProgressBar confirmDeleteDialogProgressBar = view.findViewById(R.id.confirmDeleteDialogProgressBar);

        confirmDeleteDialogTitle.setText("Overwrite Tag?");
        confirmDeleteDialogText.setText("Current data:\n" + payload);
        confirmDeleteDialogButton.setVisibility(View.VISIBLE);
        confirmDeleteDialogProgressBar.setVisibility(View.GONE);

        confirmDeleteDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteDialog.dismiss();
                mWriteNfc = true;
                confirmWrite = true;
                showWriteDialog();
            }
        });

        builder.setView(view);
        confirmDeleteDialog = builder.create();
        confirmDeleteDialog.show();
    }

    @Override
    public void response(JSONArray data) {
        if(dbcallback.equals("getNewItemId")){
            try {
                itemId = Integer.valueOf(data.getJSONObject(0).getString("item_id"));
                Log.d("Data Response itemID", String.valueOf(itemId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(dbcallback.equals("addItem")){
            Log.d("Add item data response", data.toString());
            Toast.makeText(NewItemActivity.this, "Item added to DB!", Toast.LENGTH_SHORT).show();
            editTextName.getText().clear();
            editTextDesc.getText().clear();
        }
        else{
            Log.d("Response NOT FOUND","dbcallback is currently either null or set to" +
                    "not-callable method");
        }
    }

    @Override
    public void errorResponse(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        Log.d("Add ResponseError", error);
    }
}
