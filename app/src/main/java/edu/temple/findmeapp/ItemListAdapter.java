package edu.temple.findmeapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ItemListAdapter extends ArrayAdapter {
    private static final String TAG = "ItemListAdapter ===>>>";

    public ItemListAdapter(Context context, ArrayList<Item> itemList) {
        super(context, R.layout.item_itemlist, itemList);
    }

}
