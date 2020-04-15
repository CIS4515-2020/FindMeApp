package edu.temple.findmeapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class FoundItemMessageListAdapter extends ArrayAdapter {
    private static final String TAG = "FoundItemMessageListAdapter ===>>>";

    public FoundItemMessageListAdapter(Context context, ArrayList<FoundItemMessage> foundItemMessageList) {
        super(context, R.layout.item_founditemmessagelist, foundItemMessageList);
    }
}
