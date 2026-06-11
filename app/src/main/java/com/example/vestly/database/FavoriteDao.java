package com.example.vestly.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.vestly.model.FavoritePhoto;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDao {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public FavoriteDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertFavorite(FavoritePhoto photo) {
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.COLUMN_PHOTO_ID, photo.getId());
        values.put(DatabaseContract.COLUMN_PHOTOGRAPHER, photo.getPhotographer());
        values.put(DatabaseContract.COLUMN_PORTRAIT_URL, photo.getPortraitUrl());
        values.put(DatabaseContract.COLUMN_CATEGORY, photo.getCategory());
        database.insert(DatabaseContract.TABLE_NAME, null, values);
        close();
    }

    public void deleteFavorite(int photoId) {
        open();
        database.delete(DatabaseContract.TABLE_NAME,
                DatabaseContract.COLUMN_PHOTO_ID + " = ?",
                new String[]{String.valueOf(photoId)});
        close();
    }

    public void deleteAllFavorites() {
        open();
        database.delete(DatabaseContract.TABLE_NAME, null, null);
        close();
    }

    public List<FavoritePhoto> getAllFavorites() {
        List<FavoritePhoto> favoriteList = new ArrayList<>();
        open();
        Cursor cursor = database.query(DatabaseContract.TABLE_NAME, null,
                null, null, null, null, DatabaseContract.COLUMN_CREATED_AT + " DESC");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.COLUMN_PHOTO_ID));
                String photographer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.COLUMN_PHOTOGRAPHER));
                String portraitUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.COLUMN_PORTRAIT_URL));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.COLUMN_CATEGORY));
                favoriteList.add(new FavoritePhoto(id, photographer, portraitUrl, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return favoriteList;
    }

    public boolean isFavorite(int photoId) {
        boolean exists = false;
        open();
        Cursor cursor = database.query(DatabaseContract.TABLE_NAME, null,
                DatabaseContract.COLUMN_PHOTO_ID + " = ?",
                new String[]{String.valueOf(photoId)}, null, null, null);
        if (cursor.moveToFirst()) {
            exists = true;
        }
        cursor.close();
        close();
        return exists;
    }

    public int getFavoritesCount() {
        int count = 0;
        open();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        close();
        return count;
    }
}