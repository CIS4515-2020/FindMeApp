package edu.temple.findmeapp;

import java.util.ArrayList;

public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean lost = false;
    private ArrayList<FoundItemMessage> foundItemMessages;

    public Item(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.foundItemMessages = new ArrayList<FoundItemMessage>();
    }

    public Item(int id, String name, String description, ArrayList<FoundItemMessage> foundItemMessages) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.foundItemMessages = foundItemMessages;
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
