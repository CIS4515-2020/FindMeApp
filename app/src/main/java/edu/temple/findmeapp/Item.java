package edu.temple.findmeapp;

import java.util.ArrayList;

public class Item {
    private int id;
    private String name;
    private String description;
    private ArrayList<FoundItemMessage> foundItemMessageList;

    public Item(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.foundItemMessageList = new ArrayList<FoundItemMessage>();
    }

    public Item(int id, String name, String description, ArrayList<FoundItemMessage> foundItemMessageList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.foundItemMessageList = foundItemMessageList;
    }

    public int getId() {
        return id;
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

    public ArrayList<FoundItemMessage> getFoundItemMessageList() {
        return foundItemMessageList;
    }

    public void setFoundItemMessageList(ArrayList<FoundItemMessage> foundItemMessageList) {
        this.foundItemMessageList = foundItemMessageList;
    }

    public void addFoundItemMessage(FoundItemMessage foundItemMessage) {
        this.foundItemMessageList.add(foundItemMessage);
    }

    public void clearFoundItemMessageList() {
        this.foundItemMessageList.clear();
    }
}
