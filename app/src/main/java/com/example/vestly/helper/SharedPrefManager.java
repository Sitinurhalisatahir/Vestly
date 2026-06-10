package com.example.vestly.helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.vestly.model.FavoritePhoto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefManager {

    private static final String PREF_NAME = "VestlyPref";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_THEME = "isDarkTheme";
    private static final String KEY_BIO = "bio";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_HOME_CACHE = "home_cache";  // ← TAMBAHAN

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

    // ── Favorites ──
    public List<FavoritePhoto> getFavorites() {
        String json = pref.getString(KEY_FAVORITES, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<FavoritePhoto>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public void addFavorite(FavoritePhoto photo) {
        List<FavoritePhoto> list = getFavorites();
        for (FavoritePhoto p : list) {
            if (p.getId() == photo.getId()) return;
        }
        list.add(photo);
        editor.putString(KEY_FAVORITES, new Gson().toJson(list));
        editor.apply();
    }

    public void removeFavorite(int photoId) {
        List<FavoritePhoto> list = getFavorites();
        list.removeIf(p -> p.getId() == photoId);
        editor.putString(KEY_FAVORITES, new Gson().toJson(list));
        editor.apply();
    }

    public boolean isFavorite(int photoId) {
        for (FavoritePhoto p : getFavorites()) {
            if (p.getId() == photoId) return true;
        }
        return false;
    }

    // ── Theme ──
    public void setDarkTheme(boolean isDark) {
        editor.putBoolean(KEY_THEME, isDark);
        editor.apply();
    }

    public boolean isDarkTheme() {
        return pref.getBoolean(KEY_THEME, false);
    }

    // ── Bio (opsional) ──
    public void saveBio(String bio) {
        editor.putString(KEY_BIO, bio);
        editor.apply();
    }

    public String getBio() {
        return pref.getString(KEY_BIO, "");
    }

    // ── Profile Photo (opsional) ──
    public void savePhotoUri(String uri) {
        editor.putString(KEY_PHOTO, uri);
        editor.apply();
    }

    public String getPhotoUri() {
        return pref.getString(KEY_PHOTO, "");
    }

    // ── Home Cache (untuk offline mode) ──
    public void saveHomeCache(String json) {
        editor.putString(KEY_HOME_CACHE, json);
        editor.apply();
    }

    public String getHomeCache() {
        return pref.getString(KEY_HOME_CACHE, "");
    }
}