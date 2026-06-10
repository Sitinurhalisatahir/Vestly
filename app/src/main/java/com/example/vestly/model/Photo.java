package com.example.vestly.model;

import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("id")
    private int id;

    @SerializedName("photographer")
    private String photographer;

    @SerializedName("photographer_url")
    private String photographerUrl;

    @SerializedName("src")
    private PhotoSrc src;

    public int getId() { return id; }
    public String getPhotographer() { return photographer; }
    public String getPhotographerUrl() { return photographerUrl; }
    public PhotoSrc getSrc() { return src; }

    public static class PhotoSrc {
        @SerializedName("original")
        private String original;

        @SerializedName("large2x")
        private String large2x;

        @SerializedName("medium")
        private String medium;

        @SerializedName("portrait")
        private String portrait;

        public String getOriginal() { return original; }
        public String getLarge2x() { return large2x; }
        public String getMedium() { return medium; }
        public String getPortrait() { return portrait; }
    }
}