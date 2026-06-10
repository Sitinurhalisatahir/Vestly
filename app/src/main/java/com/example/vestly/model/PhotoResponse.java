package com.example.vestly.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PhotoResponse {

    @SerializedName("photos")
    private List<Photo> photos;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("next_page")
    private String nextPage;

    public List<Photo> getPhotos() { return photos; }
    public int getTotalResults() { return totalResults; }
    public String getNextPage() { return nextPage; }
}