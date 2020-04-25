package edu.temple.findmeapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class FoundItemMessage {

    private final String DB_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SS";
    private final String DATETIME_PATTERN = "MMM dd, YYYY hh:mm a";

    private int id;
    private double lat;
    private double lon;
    private String foundOn; // Timestamp found
    private String message;
    private Calendar foundOnCal;
    private TimeZone dbTZ = TimeZone.getTimeZone("America/Chicago");
    private SimpleDateFormat sdf = new SimpleDateFormat(DB_TIMESTAMP_PATTERN,Locale.ENGLISH);

    public FoundItemMessage(double lat, double lon, String foundOn, String message) {
        this.lat = lat;
        this.lon = lon;
        this.foundOn = foundOn;
        this.message = message;
    }

    public FoundItemMessage(JSONObject args) throws JSONException, ParseException {
        this.id = args.getInt("found_item_id");
        try {
            this.lat = args.getDouble("lat");
            this.lon = args.getDouble("lng");
        } catch (Exception e) {
            this.lat = 0.0;
            this.lon = 0.0;
        }
        try {
            this.message = args.getString("message");
            if (this.message.equals("null") || this.message.equals("")) {
                this.message = "No description";
            }
        } catch (Exception e) {
            this.message = "No description";
        }
        setFoundOnHelper( args.getString("found_on") );
    }

    private void setFoundOnHelper( String foundOn ) throws ParseException {
        if (foundOnCal == null) {
            foundOnCal = Calendar.getInstance(dbTZ);
        }
        sdf.setTimeZone(dbTZ);
        foundOnCal.setTime(sdf.parse(foundOn));

        sdf.applyPattern(DATETIME_PATTERN);
        sdf.setTimeZone(TimeZone.getDefault());
        foundOnCal.setTimeZone(TimeZone.getDefault());
        this.foundOn = sdf.format(foundOnCal.getTime());
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

    public void setFoundOn(String foundOn) throws ParseException {
        setFoundOnHelper( foundOn );
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return    "FoundItemMessage"   + "\n"
                + "message: " +message + "\n"
                + "id:      " +id      + "\n"
                + "foundOn: " +foundOn + "\n"
                + "lat:     " +lat     + "\n"
                + "lon:     " +lon;
    }
}
