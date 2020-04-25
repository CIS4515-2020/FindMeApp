package edu.temple.findmeapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FoundItemService extends FirebaseMessagingService {

    private static final String TAG = "MyFCMService";

    public FoundItemService() {

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = "";
        String content = "";

        // check if message has a data payload
        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            content = remoteMessage.getData().get("message");
            Log.d("Data Payload: ", "-----------");
            Log.d("Title: ", title);
            Log.d("Msg: ", content);
        }
        // check if message has a notification payload
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            content = remoteMessage.getNotification().getBody();
            Log.d("Notification msg: ", "----------");
            Log.d("Title: ", title);
            Log.d("Msg: ", content);
        }


        Log.d(TAG, "From: " + remoteMessage.getFrom());
        String channel_id = "admin_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Found Item Notifications";
            String description = "Receive a notification if one of your items is found";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int notificationID = random.nextInt();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id");
        //builder.setSmallIcon(R.drawable.ic_small); TODO: set icon
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        manager.notify(notificationID, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

    }
}
