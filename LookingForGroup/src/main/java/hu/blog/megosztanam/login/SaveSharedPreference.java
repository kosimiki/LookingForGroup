package hu.blog.megosztanam.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    private static final String ID_TOKEN = "idToken";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setTokenId(Context ctx, String tokenId) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(ID_TOKEN, tokenId);
        editor.apply();
    }

    public static String getIdToken(Context ctx) {
        return getSharedPreferences(ctx).getString(ID_TOKEN, "");
    }
}