package com.jizhang.ledger.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static SharedPreferences sp;
    private static final String PREF_NAME = "ledger_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";

    public static void init(Context context) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveToken(String token) {
        sp.edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken() {
        return sp.getString(KEY_TOKEN, null);
    }

    public static void saveUser(Long userId, String userName) {
        sp.edit().putLong(KEY_USER_ID, userId).putString(KEY_USER_NAME, userName).apply();
    }

    public static String getUserName() {
        return sp.getString(KEY_USER_NAME, "");
    }

    public static void clear() {
        sp.edit().clear().apply();
    }

    public static boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }
}
