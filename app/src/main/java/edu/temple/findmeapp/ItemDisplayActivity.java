package edu.temple.findmeapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemDisplayActivity extends AppCompatActivity implements DatabaseInterface.DbResponseListener, ItemListAdapter.ItemClickListener {
    private final static String TAG = "ItemDisplayActivity ===>>>";

    private RecyclerView recyclerView;
    private ItemListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Item> itemList = new ArrayList<>();

    private DatabaseInterface dbInterface;
    private int userId;
    private String dbcall;

    private AlertDialog lostDialog;
    private TextView lostDialogTextView;
    private Button lostDialogButton;
    private ProgressBar lostDialogProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_display);
        getSupportActionBar().setTitle("Item List");

        recyclerView = findViewById(R.id.recyclerview_item_display);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ItemListAdapter(this, itemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        userId = sharedPreferences.getInt(MainActivity.SHARED_PREFS_USERID, 0);

        if (userId == 0) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            dbInterface = new DatabaseInterface(this);
            dbcall = "getItems";
            dbInterface.getItems(userId);
        }
    }

    @Override
    public void response(JSONArray data) {
        if (dbcall.equals("getItems")) {
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
        } else if (dbcall.equals("editItem")) {
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

    }

    @Override
    public void onItemLongClick(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_lost, null);
        final Item newItem = item.clone();

        lostDialogTextView = view.findViewById(R.id.lostDialogTextView);
        lostDialogProgressBar = view.findViewById(R.id.lostProgressBar);
        lostDialogButton     = view.findViewById(R.id.lostDialogButton);
        if (item.isLost()) {
            lostDialogTextView.setText("Set item to found?");
            lostDialogButton.setText("Found Item");
            newItem.setLost(0);
        } else {
            lostDialogTextView.setText("Set item to lost?");
            lostDialogButton.setText("Lost Item");
            newItem.setLost(1);
        }

        lostDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lostDialogButton.setVisibility(View.GONE);
                lostDialogProgressBar.setVisibility(View.VISIBLE);

                DatabaseInterface dbInterface = new DatabaseInterface( ItemDisplayActivity.this );
                dbcall = "editItem";
                dbInterface.editItem(newItem);
            }
        });

        builder.setView(view);
        lostDialog = builder.create();
        lostDialog.show();
    }
}
