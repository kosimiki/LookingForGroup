package hu.blog.megosztanam.messaging;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import hu.blog.megosztanam.model.shared.messaging.MessageType;


/**
 * Created by Miklós on 2017. 05. 07..
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = MessagingService.class.getName();

    public static final String PACKAGE_NAME = "hu.blog.megosztanam.messaging.MessagingService.";
    public static final String NEW_POST = PACKAGE_NAME + "NEW_POST";
    public static final String DELETED_POST = PACKAGE_NAME + "DELETED_POST";
    public static final String NEW_APPLICATION = PACKAGE_NAME + "NEW_APPLICATION";
    static final public String RESULT = PACKAGE_NAME + "REQUEST_PROCESSED";
    static final public String TOKEN_UPDATE = PACKAGE_NAME + "TOKEN_UPDATE";

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
            intent.putExtra("fromMessageService", "true NEW");
            broadcastManager.sendBroadcast(intent);
            return;
        } else if ("/topics/POST_DELETED".equals(from)) {
            intent = new Intent(DELETED_POST);
            intent.putExtra("method", "DELETE");
            intent.putExtra("postId", remoteMessage.getData().get("postId"));
            intent.putExtra("fromMessageService", "true DEL ");
            broadcastManager.sendBroadcast(intent);
            return;

        } else {
            Log.i(TAG, remoteMessage.getData().toString());
            String messageType = remoteMessage.getData().get("messageType");
            String message = remoteMessage.getData().get("message");
            if (messageType != null) {
                intent = new Intent(messageType);
                intent.putExtra("message", message);
                broadcastManager.sendBroadcast(intent);
                return;
            }
        }


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

}
