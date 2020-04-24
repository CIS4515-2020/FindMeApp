package edu.temple.findmeapp;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = "";
        String content = "";

        if(remoteMessage.getData().size() > 0 && remoteMessage.getNotification() != null){

        }
        // Check if message contains a data payload
        else if(remoteMessage.getData().size() > 0){
            title = remoteMessage.getData().get("title");
            content = remoteMessage.getData().get("message");
            Log.d("Data Payload: ","-----------");
            Log.d("Title: ", title);
            Log.d("Msg: ", content);
        }
        // Check if message contains a notification payload.
        else if(remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            content = remoteMessage.getNotification().getBody();
            Log.d("Notification msg: ","----------");
            Log.d("Title: ", title);
            Log.d("Msg: ", content);
        }


        Log.d(TAG, "From: "+remoteMessage.getFrom());

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int notificationID = random.nextInt();
        String channel_id = "admin_channel";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        }

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, "channel_id");

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: "+token);

    }
}
