package edu.temple.findmeapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class EditItemActivity extends AppCompatActivity implements
        DatabaseInterface.DbResponseListener,
        ItemListAdapter.ItemClickListener {
    private final static String TAG = "EditItemActivity ===>>>";

    private RecyclerView recyclerView;
    private EditText nameEditText, descEditText;
    private CheckBox lostCheckBox;
    private boolean mStartUp = true;

    private ItemListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Item> itemList = new ArrayList<>();
    private Item mItem;
    private int itemId;

    private DatabaseInterface dbInterface;
    private int userId;
    private String dbcallback;

    private AlertDialog scanNFCDialog;
    private boolean mReadNfc = false;
    private NfcAdapter mNfcAdapter;
    private static final int PENDING_INTENT_NDEF_DISCOVERED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        getSupportActionBar().setTitle("Edit Item");

        recyclerView = findViewById(R.id.reyclerViewEdit);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ItemListAdapter(this, itemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        nameEditText = findViewById(R.id.editNameET);
        descEditText = findViewById(R.id.editDescET);
        lostCheckBox = findViewById(R.id.lostCheckBox);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt(MainActivity.SHARED_PREFS_USERID, 0);
        if (userId == 0) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            dbInterface = new DatabaseInterface(this);
            dbcallback = "getItems";
            dbInterface.getItems(userId);
        }

        setFocusable(mStartUp);

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( (!mItem.getName().equals(nameEditText.getText().toString().trim()))
                    || (!mItem.getDescription().equals(descEditText.getText().toString().trim()))
                    || (mItem.isLost() != lostCheckBox.isChecked())) {
                    EditItemActivity.this.showChangesDialog();
                }
            }
        });

        findViewById(R.id.editScanBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReadNfc = true;
                EditItemActivity.this.showNfcDialog();
            }
        });
    }

    private void setFocusable(boolean isStartup){
        if(isStartup){
            nameEditText.setFocusable(false);
            descEditText.setFocusable(false);
        }
        else{
            nameEditText.setFocusableInTouchMode(true);
            descEditText.setFocusableInTouchMode(true);
        }
    }

    private void showChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
        String title = "Save changes?";
        String message = "Original:\n" +
                "Name: " + mItem.getName()+"\n" +
                "Description: " + mItem.getDescription()+"\n" +
                "Lost: " + mItem.isLost() + "\n \n" +
                "After save: \n" +
                "Name: " + nameEditText.getText().toString()+"\n"+
                "Description: "+ descEditText.getText().toString()+"\n"+
                "Lost: " + lostCheckBox.isChecked();
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mItem.setName(nameEditText.getText().toString());
                        mItem.setDescription(descEditText.getText().toString());
                        int isLostInt = (lostCheckBox.isChecked()) ? 1 : 0;
                        mItem.setLost(isLostInt);
                        dbcallback = "editItem";
                        dbInterface.editItem(mItem);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }

    private void showNfcDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_scantag, null);

        Button writeCancelButton = view.findViewById(R.id.writeCancelBtn);
        writeCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanNFCDialog.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("mWriteNFC Boolean", String.valueOf(mReadNfc));
                mReadNfc = false;
                Log.d("mWriteNFC Boolean after dismiss", String.valueOf(mReadNfc));
            }
        });

        builder.setView(view);
        scanNFCDialog = builder.create();
        scanNFCDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());

        PendingIntent pi = createPendingResult(PENDING_INTENT_NDEF_DISCOVERED, new Intent(), 0);

        mNfcAdapter.enableForegroundDispatch(EditItemActivity.this, pi,
                new IntentFilter[]{ new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)},
                new String[][]{new String[]{ "android.nfc.tech.Ndef"}});
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(getApplicationContext()).disableForegroundDispatch(EditItemActivity.this);
    }

    // TODO: Add PendingIntent so non-foreground calls will be made for EditItem to read tag
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

    protected void resolveIntent(Intent data, boolean foregroundDispatch){
        String action = data.getAction();
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
            // This activity is in foreground dispatch and we want to read URI from Tag
            Tag tag = data.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(foregroundDispatch && mReadNfc){

                mReadNfc = false;

                // Call method to read tag
                readTag(tag);

                scanNFCDialog.dismiss();
            }
        }
    }

    private boolean userIsOwner(int itemId){
        for(int i = 0; i < itemList.size(); i++){
            if(itemList.get(i).getId() == itemId){
                return true;
            }
        }
        return false;
    }

    private void readTag(Tag tag){
        Ndef ndefTag = Ndef.get(tag);
        if(ndefTag != null) {
            try {
                ndefTag.connect();
                NdefMessage ndefMessage = ndefTag.getNdefMessage();
                String payload = new String(ndefMessage.getRecords()[0].getPayload());
                Log.d("Read tag payload", payload);
                String[] tagInfo = payload.split("/");
                itemId = new Integer(tagInfo[tagInfo.length - 1]);
                Log.d("Payload itemID", String.valueOf(itemId));
                boolean isOwner = userIsOwner(itemId);
                if(isOwner){
                    dbcallback = "getItem";
                    dbInterface.getItem(itemId);
                }
                else
                    Toast.makeText(EditItemActivity.this,
                            "Sorry. You are not the owner of this tag.",
                            Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(EditItemActivity.this, "Error. Could not read tag.",
                        Toast.LENGTH_SHORT).show();
            }
            finally {
                try{
                    ndefTag.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else{
            Toast.makeText(EditItemActivity.this, "Tag is blank!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void response(JSONArray data) {
        if (dbcallback.equals("getItems")) {
            itemList.clear();
            for (int i = 0; i < data.length(); i++) {
                try {
                    itemList.add(new Item(data.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter.itemList = itemList;
            adapter.notifyDataSetChanged();
        }
        else if(dbcallback.equals("editItem")){
            finish();
            startActivity(getIntent());
        }
        else if(dbcallback.equals("getItem")){
            try {
                EditItemActivity.this.onItemClick(new Item(data.getJSONObject(0)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void errorResponse(String error) {
        Toast.makeText(EditItemActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Item item) {
        mItem = item.clone();
        nameEditText.setText(item.getName());
        descEditText.setText(item.getDescription());
        lostCheckBox.setChecked(item.isLost());
        mStartUp = false;
        setFocusable(mStartUp);
    }

    @Override
    public void onItemLongClick(Item item) {
        // TODO: Decide if long click should have any functionality
    }
}
