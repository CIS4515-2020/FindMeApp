package edu.temple.findmeapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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
            // TODO: Finish coding if both location and message are sent
            Log.d("Data and Notification: ", "Has both payload and notification msg.");
        }
        // Check if message contains a data payload
        else if(remoteMessage.getData().size() > 0){
            title = remoteMessage.getData().get("found_item_id");
            content = remoteMessage.getData().get("user_id");
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        Random random = new Random();
//        int notificationID = random.nextInt();
        String channel_id = "admin_channel";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channel_id,
                    "FCM Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        }

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pi);

        manager.notify(0, notifyBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: "+token);

    }
}
