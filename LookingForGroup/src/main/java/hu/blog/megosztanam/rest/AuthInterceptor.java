package hu.blog.megosztanam.rest;

import android.content.Context;

import java.io.IOException;

import hu.blog.megosztanam.login.SaveSharedPreference;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        int userId = SaveSharedPreference.getUserId(context);
        String idToken = SaveSharedPreference.getIdToken(context);
        builder.addHeader("userId", String.valueOf(userId));
        builder.addHeader("Authorization", idToken);

        return chain.proceed(builder.build());
    }
}
