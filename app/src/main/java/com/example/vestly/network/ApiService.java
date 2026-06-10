package com.example.vestly.network;

import com.example.vestly.model.PhotoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("v1/curated")
    Call<PhotoResponse> getCuratedPhotos(
            @Query("per_page") int perPage,
            @Query("page") int page
    );

    @GET("v1/search")
    Call<PhotoResponse> searchPhotos(
            @Query("query") String query,
            @Query("per_page") int perPage,
            @Query("page") int page
    );
}