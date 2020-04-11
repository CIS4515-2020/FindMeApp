package edu.temple.findmeapp;

public class FoundItemMessage {
    private int lat;
    private int lon;
    private int foundOn; // Timestamp found
    private String message;

    public FoundItemMessage(int lat, int lon, int foundOn, String message) {
        this.lat = lat;
        this.lon = lon;
        this.foundOn = foundOn;
        this.message = message;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public int getFoundOn() {
        return foundOn;
    }

    public void setFoundOn(int foundOn) {
        this.foundOn = foundOn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
