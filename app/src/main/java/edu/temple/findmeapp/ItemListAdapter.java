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

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {
    private static final String TAG = "ItemListAdapter ===>>>";

    public ArrayList<Item> itemList;
    private ItemClickListener listener;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView itemName;
        public TextView itemDescription;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image_view);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
        }
    }

    public ItemListAdapter(Context context, ArrayList<Item> itemList) {
        this.itemList = itemList;
        this.listener = (ItemClickListener) context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itemlist, parent, false);
        ItemViewHolder ivh = new ItemViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final Item currentItem = itemList.get(position);
        if (currentItem.isLost()) {
            holder.imageView.setImageResource(R.drawable.ic_lost);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_not_lost);
        }
        holder.itemName.setText(currentItem.getName());
        holder.itemDescription.setText(currentItem.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(currentItem);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongClick(currentItem);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface ItemClickListener {
        void onItemClick(Item item);
        void onItemLongClick(Item item);
    }
}
