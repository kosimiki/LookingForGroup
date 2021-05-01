package hu.blog.megosztanam;

import android.content.Context;

import hu.blog.megosztanam.login.GoogleAuthService;
import hu.blog.megosztanam.rest.ILFGService;
import hu.blog.megosztanam.rest.LFGService;

/**
 * Helper class, holds singleton instances of various Services.
 */
public class ServiceContainer {

    private final GoogleAuthService authService;
    private final ILFGService lfgService;

    public ServiceContainer(Context context) {
        this.authService = new GoogleAuthService(context);
        this.lfgService = new LFGService(context);
    }

    public GoogleAuthService getAuthService() {
        return authService;
    }

    public ILFGService getLfgService() {
        return this.lfgService;
    }
}
