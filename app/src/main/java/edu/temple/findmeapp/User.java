package edu.temple.findmeapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {

    private int id;
    private String username;
    private String email;
    private String fname;
    private String lname;


    public User(JSONObject args) throws JSONException {
        this.id = args.getInt("user_id");
        this.username = args.getString("username");
        this.fname = args.getString("fname");
        this.lname = args.getString("lname");
        this.email = args.getString("email");
    }

    public int getId(){ return id; }

    public String getUsername(){ return username; }

    public String getEmail(){ return email; }

    public String getName(){
        return getFname() + " " + getLname();
    }

    public String getFname(){
        return fname.substring(0, 1).toUpperCase() + fname.substring(1).toLowerCase();
    }

    public String getLname(){
        return lname.substring(0, 1).toUpperCase() + lname.substring(1).toLowerCase();
    }
}
