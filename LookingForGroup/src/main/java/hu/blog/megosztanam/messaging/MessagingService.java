package hu.blog.megosztanam.messaging;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


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
        if(message != null && !message.isEmpty()){
            intent.putExtra(MESSAGE, message);
            if(message.contains("looking for teammates")){
                intent.putExtra(MESSAGE_TYPE_IS_POST, true);
            }else{
                intent.putExtra(MESSAGE_TYPE_IS_POST, false);
            }
            broadcastManager.sendBroadcast(intent);
        }

    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().toString());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendResult(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}
