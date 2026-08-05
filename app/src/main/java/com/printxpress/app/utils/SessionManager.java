package com.printxpress.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    public void saveUser(long id) {
        prefs.edit().putLong("userId", id).apply();
    }

    public long getUserId() {
        return prefs.getLong("userId", -1);
    }

    public boolean isLoggedIn() {
        return getUserId() > 0;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
