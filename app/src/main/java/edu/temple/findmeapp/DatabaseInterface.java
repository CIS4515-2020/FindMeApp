package edu.temple.findmeapp;

import android.content.Context;

import androidx.annotation.StringDef;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DatabaseInterface {
    private static final String TAG = "DatabaseInterface ===>>>";
    private static final String API_DOMAIN = "https://findmeapp.tech";
    static final String LOGIN_EXT = "/login";
    static final String REGISTER_EXT = "/register-user";
    static final String ITEM_EXT = "/item";
    static final String FOUND_ITEM_MESSAGE_EXT = "/found-item";
    static final String ADD_ACTION = "/add";
    static final String EDIT_ACTION = "/edit";
    static final String DELETE_ACTION = "/delete";
    static final String LIST_ACTION = "/list";

    private final String SUCCESS = "success";
    private final String FAILED = "failed";

    private RequestQueue queue;
    private DbResponseListener callback;

    public DatabaseInterface(Context context){
        this.queue = Volley.newRequestQueue( context );
        this.callback = (DbResponseListener) context;
    }

    /**
     * make login api call
     *
     * @param username
     * @param password
     */
    public void login( String username, String password ){

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        String url = API_DOMAIN + LOGIN_EXT;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make register user api call
     *
     * @param username
     * @param password
     */
    public void registerUser(String username, String password, String email, String fname, String lname ){
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);
        params.put("fname", fname);
        params.put("lname", lname);
        String url = API_DOMAIN + REGISTER_EXT;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make list items api call
     *
     * @param user_id UserId of owner of items
     */
    public void getItems( Integer user_id ){
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        String url = API_DOMAIN + ITEM_EXT + LIST_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make add item api call
     *
     * @param user_id UserId of item owner
     * @param name Name of item
     * @param description item desc
     */
    public void addItem( Integer user_id, String name, String description ){
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("name", name);
        params.put("description", description);
        String url = API_DOMAIN + ITEM_EXT + ADD_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make edit item api call
     *
     * @param item Item to edit
     */
    public void editItem( Item item ){
        Map<String, String> params = new HashMap<>();
        params.put("item_id", String.valueOf(item.getId()));
        params.put("name", item.getName());
        params.put("description", item.getDescription());
        params.put("lost", String.valueOf(item.isLost()));
        String url = API_DOMAIN + ITEM_EXT + EDIT_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make delete item api call
     *
     * @param item Item to delete
     */
    public void deleteItem( Item item ){
        Map<String, String> params = new HashMap<>();
        params.put("item_id", String.valueOf(item.getId()));
        String url = API_DOMAIN + ITEM_EXT + DELETE_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make list found item messages api call
     *
     * @param item
     */
    public void getFoundItemMessages( Item item ){
        Map<String, String> params = new HashMap<>();
        params.put("item_id", String.valueOf(item.getId()));
        String url = API_DOMAIN + FOUND_ITEM_MESSAGE_EXT + LIST_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make add foundItemMessage api call with location
     *
     * @param item_id
     * @param lat coordinate
     * @param lng coordinate
     */
    public void addFoundItemMessage( Integer item_id, double lat, double lng ){
        Map<String, String> params = new HashMap<>();
        params.put("item_id", String.valueOf(item_id));
        params.put("lat", String.valueOf(lat));
        params.put("lng", String.valueOf(lng));
        String url = API_DOMAIN + FOUND_ITEM_MESSAGE_EXT + ADD_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make add foundItemMessage api call with message
     *
     * @param item_id
     * @param message
     */
    public void addFoundItemMessage( Integer item_id, String message ){
        Map<String, String> params = new HashMap<>();
        params.put("item_id", String.valueOf(item_id));
        params.put("message", message);
        String url = API_DOMAIN + FOUND_ITEM_MESSAGE_EXT + ADD_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make add foundItemMessage api call with location and message
     *
     * @param item_id
     * @param lat
     * @param lng
     * @param message
     */
    public void addFoundItemMessage( Integer item_id, double lat, double lng, String message ){
        Map<String, String> params = new HashMap<>();
        params.put("item_id", String.valueOf(item_id));
        params.put("lat", String.valueOf(lat));
        params.put("lng", String.valueOf(lng));
        params.put("message", message);
        String url = API_DOMAIN + FOUND_ITEM_MESSAGE_EXT + ADD_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make delete foundItemMessage api call
     *
     * @param fim FoundItemMessage to delete
     */
    public void deleteFoundItemMessage( FoundItemMessage fim ){
        Map<String, String> params = new HashMap<>();
        params.put("found_item_id", String.valueOf(fim.getId()));
        String url = API_DOMAIN + FOUND_ITEM_MESSAGE_EXT + DELETE_ACTION;
        this.makeVolleyRequest( url, params );
    }

    /**
     * make post request to webserver api
     *
     * @param url
     * @param params
     */
    private void makeVolleyRequest( String url, final Map<String, String> params ){

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    parseResponse( response );
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    callback.errorResponse( error.getMessage() );
                }
            }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        queue.add( postRequest );
    }

    /**
     * parses the response from the api
     * calls response on success
     * calls errorResponse on failure
     *
     * @param response
     */
    private void parseResponse(String response){
        System.out.println(">>> response: " + response);
        JSONObject result = null;
        try {
            result = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assert result != null;
        try {
            if( result.getString("result").equals(FAILED) ){
                String error = result.getString("error");
                if( error.isEmpty() && result.has("exception") ){
                    error = result.getString("exception");
                }
                callback.errorResponse( error );
            }else{
                JSONArray data = null;
                if( result.has("data") ) {
                    data = result.getJSONArray("data");
                }
                callback.response(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interface to get response from api
     * must be implemented by activity that uses the db interface
     */
    public interface DbResponseListener {
        //returns data on success
        void response( JSONArray data );

        //returns error message on error
        void errorResponse( String error );
    }

}
