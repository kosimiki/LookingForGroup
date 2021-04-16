package hu.blog.megosztanam.messaging;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by Miklós on 2017. 05. 07..
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = MessagingService.class.getName();

    public static final String NEW_POST = "hu.blog.megosztanam.messaging.MessagingService.NEW_POST";
    public static final String DELETED_POST = "hu.blog.megosztanam.messaging.MessagingService.DELETED_POST";
    public static final String NEW_APPLICATION = "hu.blog.megosztanam.messaging.MessagingService.NEW_APPLICATION";
    static final public String RESULT = "hu.blog.megosztanam.messaging.MessagingService.REQUEST_PROCESSED";
    static final public String TOKEN_UPDATE = "hu.blog.megosztanam.messaging.MessagingService.TOKEN_UPDATE";

    private LocalBroadcastManager broadcastManager;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Intent intent = new Intent(RESULT);
        intent.putExtra(TOKEN_UPDATE, s);
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        Log.d(TAG, "From: " + from);
        Intent intent;

        if ("/topics/NEW_POST".equals(from)) {
            intent = new Intent(NEW_POST);
            intent.putExtra("method", "POST");
        } else if ("/topics/POST_DELETED".equals(from)) {
            intent = new Intent(DELETED_POST);
            intent.putExtra("method", "DELETE");
            intent.putExtra("postId", remoteMessage.getData().get("postId"));
        } else {
            intent = new Intent(NEW_APPLICATION);
        }
        broadcastManager.sendBroadcast(intent);


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

}
