package com.example.vestly.database;

public class DatabaseContract {
    public static final String TABLE_NAME = "favorite";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PHOTO_ID = "photo_id";
    public static final String COLUMN_PHOTOGRAPHER = "photographer";
    public static final String COLUMN_PORTRAIT_URL = "portrait_url";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PHOTO_ID + " INTEGER UNIQUE, " +
                    COLUMN_PHOTOGRAPHER + " TEXT, " +
                    COLUMN_PORTRAIT_URL + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}