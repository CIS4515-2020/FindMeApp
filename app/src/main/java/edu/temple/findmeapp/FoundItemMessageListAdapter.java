package edu.temple.findmeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoundItemMessageListAdapter extends RecyclerView.Adapter<FoundItemMessageListAdapter.ItemViewHolder> {
    private static final String TAG = "FoundItemMessageListAdapter ===>>>";

    public ArrayList<FoundItemMessage> foundItemMessageList;
    private FoundItemMessageListAdapter.ItemClickListener listener;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView tvDescription;
        public TextView tvFoundOn;
        public TextView tvLat;
        public TextView tvLon;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.founditemlist_imageView);
            tvDescription = itemView.findViewById(R.id.founditemlist_description);
            tvFoundOn = itemView.findViewById(R.id.founditemlist_foundOn);
            tvLat = itemView.findViewById(R.id.founditemlist_lat);
            tvLon = itemView.findViewById(R.id.founditemlist_lon);
        }
    }

    public FoundItemMessageListAdapter(Context context, ArrayList<FoundItemMessage> foundItemMessageList) {
        this.foundItemMessageList = foundItemMessageList;
        this.listener = (FoundItemMessageListAdapter.ItemClickListener) context;
    }

    @NonNull
    @Override
    public FoundItemMessageListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_founditemmessagelist, parent, false);
        FoundItemMessageListAdapter.ItemViewHolder ivh = new FoundItemMessageListAdapter.ItemViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull FoundItemMessageListAdapter.ItemViewHolder holder, int position) {
        final FoundItemMessage currentItem = foundItemMessageList.get(position);

        holder.imageView.setImageResource(R.drawable.ic_message);
        holder.tvDescription.setText(currentItem.getMessage());
        holder.tvFoundOn.setText("Time found: " + currentItem.getFoundOn());
        holder.tvLat.setText("Latitude: " +String.valueOf(currentItem.getLat()));
        holder.tvLon.setText("Longitude: " + String.valueOf(currentItem.getLon()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageClick(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foundItemMessageList.size();
    }

    public interface ItemClickListener {
        void onMessageClick(FoundItemMessage message);
    }
}
