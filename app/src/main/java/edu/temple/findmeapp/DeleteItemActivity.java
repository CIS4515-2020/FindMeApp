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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class DeleteItemActivity extends AppCompatActivity implements
        DatabaseInterface.DbResponseListener,
        ItemListAdapter.ItemClickListener{
    private final static String TAG = "DeleteItemActivity ===>>>";

    private RecyclerView recyclerView;
    private TextView nameTextView, descTextView, lostTextView;

    private ItemListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Item> itemList = new ArrayList<>();
    private Item mItem;

    private DatabaseInterface dbInterface;
    private int userId;
    private String dbcallback;

    private AlertDialog scanNFCDialog;
    private boolean mWriteNfc = false;
    private NfcAdapter mNfcAdapter;
    private static final int PENDING_INTENT_NDEF_DISCOVERED = 1;

    private AlertDialog confirmDeleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);
        getSupportActionBar().setTitle("Delete Item");

        recyclerView = findViewById(R.id.recyclerViewDelete);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ItemListAdapter(this, itemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        nameTextView = findViewById(R.id.nameTextView);
        descTextView = findViewById(R.id.descTextView);
        lostTextView = findViewById(R.id.lostTextView);

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

        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mItem != null){
                    DeleteItemActivity.this.showConfirmDialog();
                }
                else{
                    Toast.makeText(DeleteItemActivity.this, "Must first choose an item to delete.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.deleteScanBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWriteNfc = true;
                showNfcDialog();
            }
        });
    }


    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteItemActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_delete, null);

        TextView confirmDeleteDialogText = view.findViewById(R.id.confirmDeleteDialogText);
        final Button confirmDeleteDialogButton = view.findViewById(R.id.confirmDeleteDialogButton);
        final ProgressBar confirmDeleteDialogProgressBar = view.findViewById(R.id.confirmDeleteDialogProgressBar);

        confirmDeleteDialogText.setText(nameTextView.getText().toString());
        confirmDeleteDialogButton.setVisibility(View.VISIBLE);
        confirmDeleteDialogProgressBar.setVisibility(View.GONE);

        confirmDeleteDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbcallback = "deleteItem";
                dbInterface.deleteItem(mItem);
                confirmDeleteDialogButton.setVisibility(View.GONE);
                confirmDeleteDialogProgressBar.setVisibility(View.VISIBLE);
            }
        });

        builder.setView(view);
        confirmDeleteDialog = builder.create();
        confirmDeleteDialog.show();
    }

    private void showNfcDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteItemActivity.this);
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
                Log.d("mWriteNFC Boolean", String.valueOf(mWriteNfc));
                mWriteNfc = false;
                Log.d("mWriteNFC Boolean after dismiss", String.valueOf(mWriteNfc));
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

        mNfcAdapter.enableForegroundDispatch(DeleteItemActivity.this, pi,
                new IntentFilter[]{ new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)},
                new String[][]{new String[]{ "android.nfc.tech.Ndef"}});
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(getApplicationContext()).disableForegroundDispatch(DeleteItemActivity.this);
    }

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
            Tag tag = data.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(foregroundDispatch && mWriteNfc){
                // This activity is in foreground dispatch and we want to write to Tag
                mWriteNfc = false;

                // Call method to write tag
                writeTag(tag);

                scanNFCDialog.dismiss();
            }
        }
    }

    private void writeTag(Tag tag){
        Ndef ndefTag = Ndef.get(tag);
        if(ndefTag != null){

            try{
                ndefTag.connect();

                ndefTag.writeNdefMessage(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY,
                        null, null, null)));
                Log.d("DeleteTag", "Delete successful");
                Toast.makeText(DeleteItemActivity.this, "Tag erased successfully",
                        Toast.LENGTH_SHORT).show();
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
        else {
            // Tag is not yet Ndef formatted.
            Log.d("FormatError", "Not NDEF Formatted!");
            Toast.makeText(DeleteItemActivity.this, "Sorry! " +
                    "Tag is not yet NDEF Formatted.", Toast.LENGTH_SHORT).show();
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
        else if(dbcallback.equals("deleteItem")){
            Toast.makeText(DeleteItemActivity.this, "Item deleted from database.",
                    Toast.LENGTH_SHORT).show();
            nameTextView.setText("");
            descTextView.setText("");
            lostTextView.setText("");
            confirmDeleteDialog.cancel();
            // TODO: Add less overhead way of updating recyclerView
//            itemList.remove(mItem);
//            adapter.itemList = itemList;
//            adapter.notifyDataSetChanged();
            mWriteNfc = true;
            DeleteItemActivity.this.showNfcDialog();
            mItem = null;

            dbcallback = "getItems";
            dbInterface.getItems(userId);
        }
    }

    @Override
    public void errorResponse(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

        if (dbcallback.equals("deleteItem")) {
            nameTextView.setText("");
            descTextView.setText("");
            lostTextView.setText("");
            confirmDeleteDialog.cancel();
            mWriteNfc = true;
            DeleteItemActivity.this.showNfcDialog();
            mItem = null;

            dbcallback = "getItems";
            dbInterface.getItems(userId);
        }
    }

    @Override
    public void onItemClick(Item item) {
        mItem = item.clone();
        nameTextView.setText(item.getName());
        descTextView.setText(item.getDescription());
        lostTextView.setText(String.valueOf(item.isLost()));
        Log.d(TAG, "onItemClick(): " +item.toString());
    }


    @Override
    public void onItemLongClick(Item item) {
        mItem = item.clone();
        nameTextView.setText(item.getName());
        descTextView.setText(item.getDescription());
        lostTextView.setText(String.valueOf(item.isLost()));
        Log.d(TAG, "onItemLongClick(): " +item.toString());
        showConfirmDialog();
    }
}
