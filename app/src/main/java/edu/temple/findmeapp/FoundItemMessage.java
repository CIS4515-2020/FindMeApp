package edu.temple.findmeapp;

import org.json.JSONException;
import org.json.JSONObject;

public class FoundItemMessage {
    private int id;
    private double lat;
    private double lon;
    private String foundOn; // Timestamp found
    private String message;

    public FoundItemMessage(double lat, double lon, String foundOn, String message) {
        this.lat = lat;
        this.lon = lon;
        this.foundOn = foundOn;
        this.message = message;
    }

    public FoundItemMessage(JSONObject args) throws JSONException {
        this.id = args.getInt("found_item_id");
        try {
            this.lat = args.getDouble("lat");
            this.lon = args.getDouble("lng");
        } catch (Exception e) {
            this.lat = 0.0;
            this.lon = 0.0;
        }
        this.message = args.getString("message");
        this.foundOn = args.getString("found_on");
    }

    public int getId(){ return id; }

    public double getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public String getFoundOn() {
        return foundOn;
    }

    public void setFoundOn(String foundOn) {
        this.foundOn = foundOn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
