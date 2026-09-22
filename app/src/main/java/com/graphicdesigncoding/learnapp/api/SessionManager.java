package com.graphicdesigncoding.learnapp.api;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LoginData";
    private static final String KEY_JWT_TOKEN = "UToken";
    private static final String KEY_EMAIL = "UEmail";
    private static final String KEY_PASSWORD = "UPassword";
    private static final String KEY_USERNAME = "UUsername";
    private static final String KEY_IMAGE = "UImage";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String username, String email, String password, String imageLink) {
        SharedPreferences.Editor editor = pref.edit();
        if (token != null) editor.putString(KEY_JWT_TOKEN, token);
        if (username != null) editor.putString(KEY_USERNAME, username);
        if (email != null) editor.putString(KEY_EMAIL, email);
        if (password != null) editor.putString(KEY_PASSWORD, password);
        if (imageLink != null) editor.putString(KEY_IMAGE, imageLink);
        editor.apply();
    }

    public String getToken() {
        return pref.getString(KEY_JWT_TOKEN, null);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    public String getImage() {
        return pref.getString(KEY_IMAGE, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    public void clearSession() {
        pref.edit().clear().apply();
    }
}
