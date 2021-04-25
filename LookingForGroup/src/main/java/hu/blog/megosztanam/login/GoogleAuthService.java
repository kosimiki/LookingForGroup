package hu.blog.megosztanam.login;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;

public class GoogleAuthService {
    private static final String CLIENT_ID = "212782821519-vi2k0kd4jnp5ikasas879h3hm22ivtdd.apps.googleusercontent.com";

    private final GoogleSignInClient googleSignInClient;

    public GoogleAuthService(Context context) {
        googleSignInClient = GoogleSignIn.getClient(context, getSignInOptions());
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    private GoogleSignInOptions getSignInOptions() {
        return new GoogleSignInOptions.Builder()
                .requestIdToken(CLIENT_ID)
                .requestId()
                .build();
    }
}
