package hu.blog.megosztanam;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import hu.blog.megosztanam.login.SaveSharedPreference;
import hu.blog.megosztanam.messaging.MessagingService;

public class MainApplication extends Application {

    private ServiceContainer serviceContainer;

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String firebaseToken = intent.getStringExtra(MessagingService.TOKEN_UPDATE);
            SaveSharedPreference.setFirebaseId(getBaseContext(), firebaseToken);
            int userId = SaveSharedPreference.getUserId(getBaseContext());
            serviceContainer.getLfgService().updateFirebaseId(userId, firebaseToken);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        serviceContainer = new ServiceContainer(getApplicationContext());
        this.registerReceiver(mMessageReceiver, new IntentFilter(MessagingService.TOKEN_UPDATE));
    }

    public ServiceContainer getServiceContainer() {
        return serviceContainer;
    }
}
