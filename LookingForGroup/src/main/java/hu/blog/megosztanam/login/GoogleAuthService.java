package hu.blog.megosztanam.login;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleAuthService {
    private static final String CLIENT_ID = "212782821519-vi2k0kd4jnp5ikasas879h3hm22ivtdd.apps.googleusercontent.com";

    private final GoogleSignInClient googleSignInClient;

    public GoogleAuthService(Context context) {
        googleSignInClient = GoogleSignIn.getClient(context, getSignInOptions());
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public void logoutUser(Activity context) {
        getGoogleSignInClient().signOut();
        SaveSharedPreference.setTokenId(context.getBaseContext(), "");
        final Intent redirect = new Intent(context, LoginActivity.class);
        context.startActivity(redirect);
    }


    private GoogleSignInOptions getSignInOptions() {
        return new GoogleSignInOptions.Builder()
                .requestIdToken(CLIENT_ID)
                .requestId()
                .build();
    }
}
