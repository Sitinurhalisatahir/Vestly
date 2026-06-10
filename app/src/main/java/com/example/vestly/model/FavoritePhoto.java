package com.example.vestly.model;

public class FavoritePhoto {

    private int id;
    private String photographer;
    private String portraitUrl;
    private String category;

    public FavoritePhoto(int id, String photographer, String portraitUrl, String category) {
        this.id = id;
        this.photographer = photographer;
        this.portraitUrl = portraitUrl;
        this.category = category;
    }

    public int getId() { return id; }
    public String getPhotographer() { return photographer; }
    public String getPortraitUrl() { return portraitUrl; }
    public String getCategory() { return category; }
}