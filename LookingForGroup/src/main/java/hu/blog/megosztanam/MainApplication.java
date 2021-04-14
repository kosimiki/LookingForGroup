package hu.blog.megosztanam;

import android.app.Application;

public class MainApplication extends Application {

    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer(getApplicationContext());
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}
