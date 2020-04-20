package edu.temple.findmeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
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
    private Button saveButton;

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
        saveButton = findViewById(R.id.saveButton);

        setFocusable(mStartUp);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangesDialog();
            }
        });
    }

    private void showChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    }

    @Override
    public void errorResponse(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
    }
}
