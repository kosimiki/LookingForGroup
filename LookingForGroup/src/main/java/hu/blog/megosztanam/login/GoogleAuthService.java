package hu.blog.megosztanam.login;

import android.content.Context;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleAuthService {
    private static final String CLIENT_ID = "212782821519-vi2k0kd4jnp5ikasas879h3hm22ivtdd.apps.googleusercontent.com";

    private final GoogleApiClient googleApiClient;

    public GoogleAuthService(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, getSignInOptions())
                .build();
        googleApiClient.connect();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    private GoogleSignInOptions getSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail()
                .build();
    }
}
