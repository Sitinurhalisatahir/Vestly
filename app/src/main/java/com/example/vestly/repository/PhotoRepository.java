package com.example.vestly.repository;

import android.content.Context;
import com.example.vestly.model.PhotoResponse;
import com.example.vestly.network.ApiClient;
import com.example.vestly.network.ApiService;
import com.example.vestly.network.NetworkUtils;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoRepository {

    private ApiService apiService;
    private Executor executor;
    private Context context;

    public interface PhotoCallback {
        void onSuccess(PhotoResponse response);
        void onError(String message);
        void onNoConnection();
    }

    public PhotoRepository(Context context) {
        this.context = context;
        this.apiService = ApiClient.getApiService();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void getCuratedPhotos(int page, PhotoCallback callback) {
        executor.execute(() -> {
            if (!NetworkUtils.isConnected(context)) {
                callback.onNoConnection();
                return;
            }

            apiService.getCuratedPhotos(20, page).enqueue(new Callback<PhotoResponse>() {
                @Override
                public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Gagal memuat data");
                    }
                }

                @Override
                public void onFailure(Call<PhotoResponse> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
        });
    }

    public void searchPhotos(String query, int page, PhotoCallback callback) {
        executor.execute(() -> {
            if (!NetworkUtils.isConnected(context)) {
                callback.onNoConnection();
                return;
            }

            apiService.searchPhotos(query, 20, page).enqueue(new Callback<PhotoResponse>() {
                @Override
                public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Gagal memuat data");
                    }
                }

                @Override
                public void onFailure(Call<PhotoResponse> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
        });
    }

    public void getPhotosByCategory(String category, int page, PhotoCallback callback) {
        String query = category + " outfit fashion";
        searchPhotos(query, page, callback);
    }
}