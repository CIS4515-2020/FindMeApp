package edu.temple.findmeapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Item {
    private int id;
    private int userId;
    private String name;
    private String description;
    private int lost = 0;
    private ArrayList<FoundItemMessage> foundItemMessages = new ArrayList<>();

    public Item(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Item(int id, String name, String description, ArrayList<FoundItemMessage> foundItemMessages) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.foundItemMessages = foundItemMessages;
    }

    public Item clone() {
        return new Item(id, name, description, foundItemMessages);
    }

    public Item(JSONObject args) throws JSONException {
        this.id = args.getInt("item_id");
        this.userId = args.getInt("user_id");
        this.name = args.getString("name");
        this.description = args.getString("description");
        this.lost = args.getInt("lost");
    }

    public int getId() {
        return id;
    }

    public int getUserId(){ return userId; }

    public boolean isLost(){ return lost == 1; }

    public void setLost( int lost ){
        this.lost = lost;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<FoundItemMessage> getFoundItemMessages() {
        return foundItemMessages;
    }

    public void setFoundItemMessages(ArrayList<FoundItemMessage> foundItemMessages) {
        this.foundItemMessages = foundItemMessages;
    }

    public void addFoundItemMessage(FoundItemMessage foundItemMessage) {
        this.foundItemMessages.add(foundItemMessage);
    }

    public void clearFoundItemMessages() {
        this.foundItemMessages.clear();
    }
}
