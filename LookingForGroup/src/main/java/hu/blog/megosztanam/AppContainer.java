package hu.blog.megosztanam;

import android.content.Context;

import hu.blog.megosztanam.login.GoogleAuthService;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.rest.LFGService;

public class AppContainer {

    private final GoogleAuthService authService;
    private final ILFGService lfgService;

    public AppContainer(Context context) {
        this.authService = new GoogleAuthService(context);
        this.lfgService = new LFGService();
    }

    public GoogleAuthService getAuthService() {
        return authService;
    }

    public ILFGService getLfgService() {
        return this.lfgService;
    }
}
