package com.example.vestly.helper;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {

    private static final String PREF_NAME = "VestlyPref";
    private static final String KEY_THEME = "isDarkTheme";
    private static final String KEY_BIO = "bio";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_HOME_CACHE = "home_cache";

    private static SharedPrefManager instance;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private SharedPrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public void setDarkTheme(boolean isDark) {
        editor.putBoolean(KEY_THEME, isDark);
        editor.apply();
    }

    public boolean isDarkTheme() {
        return pref.getBoolean(KEY_THEME, false);
    }

    public void saveBio(String bio) {
        editor.putString(KEY_BIO, bio);
        editor.apply();
    }

    public String getBio() {
        return pref.getString(KEY_BIO, "");
    }

    public void savePhotoUri(String uri) {
        editor.putString(KEY_PHOTO, uri);
        editor.apply();
    }

    public String getPhotoUri() {
        return pref.getString(KEY_PHOTO, "");
    }

    public void saveHomeCache(String json) {
        editor.putString(KEY_HOME_CACHE, json);
        editor.apply();
    }

    public String getHomeCache() {
        return pref.getString(KEY_HOME_CACHE, "");
    }

    private static final String KEY_SEARCH_CACHE = "search_cache_";

    public void saveSearchCache(String query, String json) {
        editor.putString(KEY_SEARCH_CACHE + query.toLowerCase(), json);
        editor.apply();
    }

    public String getSearchCache(String query) {
        return pref.getString(KEY_SEARCH_CACHE + query.toLowerCase(), "");
    }
}