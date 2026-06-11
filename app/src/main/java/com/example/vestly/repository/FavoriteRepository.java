package com.example.vestly.repository;

import android.content.Context;
import com.example.vestly.database.FavoriteDao;
import com.example.vestly.model.FavoritePhoto;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteRepository {
    private FavoriteDao favoriteDao;
    private ExecutorService executorService;

    public FavoriteRepository(Context context) {
        favoriteDao = new FavoriteDao(context);
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertFavorite(FavoritePhoto photo, OnFavoriteCallback callback) {
        executorService.execute(() -> {
            favoriteDao.insertFavorite(photo);
            if (callback != null) {
                callback.onComplete();
            }
        });
    }

    public void deleteFavorite(int photoId, OnFavoriteCallback callback) {
        executorService.execute(() -> {
            favoriteDao.deleteFavorite(photoId);
            if (callback != null) {
                callback.onComplete();
            }
        });
    }

    public void deleteAllFavorites(OnFavoriteCallback callback) {
        executorService.execute(() -> {
            favoriteDao.deleteAllFavorites();
            if (callback != null) {
                callback.onComplete();
            }
        });
    }

    public void getAllFavorites(OnFavoritesLoadedCallback callback) {
        executorService.execute(() -> {
            List<FavoritePhoto> favorites = favoriteDao.getAllFavorites();
            if (callback != null) {
                callback.onLoaded(favorites);
            }
        });
    }

    public void isFavorite(int photoId, OnFavoriteCheckCallback callback) {
        executorService.execute(() -> {
            boolean exists = favoriteDao.isFavorite(photoId);
            if (callback != null) {
                callback.onChecked(exists);
            }
        });
    }

    public interface OnFavoriteCallback {
        void onComplete();
    }

    public interface OnFavoritesLoadedCallback {
        void onLoaded(List<FavoritePhoto> favorites);
    }

    public interface OnFavoriteCheckCallback {
        void onChecked(boolean isFavorite);
    }
}