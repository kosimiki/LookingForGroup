package hu.blog.megosztanam.messaging;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static hu.blog.megosztanam.model.shared.messaging.Messaging.NEW_POST;
import static hu.blog.megosztanam.model.shared.messaging.Messaging.POST_DELETED;


/**
 * Created by Miklós on 2017. 05. 07..
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = MessagingService.class.getName();

    static final public String RESULT = "hu.blog.megosztanam.messaging.MessagingService.REQUEST_PROCESSED";

    static final public String MESSAGE = "hu.blog.megosztanam.messaging.MessagingService.MESSAGE";

    static final public String MESSAGE_TYPE_IS_POST = "hu.blog.megosztanam.messaging.MessagingService.MESSAGE_TYPE_IS_POST";


    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);

    }

    public void sendResult(String message) {
        Intent intent = new Intent(RESULT);
        if (message != null && !message.isEmpty()) {
            intent.putExtra(MESSAGE, message);
            boolean isPost = message.contains(POST_DELETED) || message.contains(NEW_POST);
            intent.putExtra(MESSAGE_TYPE_IS_POST, isPost);
            broadcastManager.sendBroadcast(intent);
        }

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().toString());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendResult(remoteMessage.getNotification().getBody());
        }
    }

}
