package edu.temple.findmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class EditItemActivity extends AppCompatActivity {
    private final static String TAG = "EditItemActivity ===>>>";

    ArrayList<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        getSupportActionBar().setTitle("Edit Item");

        // sample item list
        Item item1 = new Item(1, "item1", "My first item");
        Item item2 = new Item(2, "item2", "My second item",
                new ArrayList<FoundItemMessage>(Arrays.asList(
                        new FoundItemMessage(10.0, 10.0, "12345", "i found the second item"),
                        new FoundItemMessage(11.0, 11.0, "23456", "i also found the second item")
                )));
        Item item3 = new Item(3, "item3", "my third item",
                new ArrayList<FoundItemMessage>(Arrays.asList(
                        new FoundItemMessage(15.0, 15.0, "34567", "I found the third item!")
                )));
        itemList = new ArrayList<Item>(Arrays.asList(item1, item2, item3));



    }
}
