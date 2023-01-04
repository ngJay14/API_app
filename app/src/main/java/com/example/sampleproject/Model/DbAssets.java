package com.example.sampleproject.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbAssets {
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_HUMIDITY = "humidity";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_WINDSPEED = "windSpeed";
    public static final String KEY_TIMESTAMP = "timestamp";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static final String DATABASE_NAME = "Database_Asset";
    private static final String DATABASE_TABLE = "assets";
    private static final int DATABASE_VERSION = 2;
    private final Context context;

    public DbAssets(Context ctx) {
        this.context = ctx;
    }

    public DbAssets open() {
        dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long createAsset(String name, String humidity, String temperature, String windSpeed, String timestamp) {
        ContentValues inititalValues = new ContentValues();
        inititalValues.put(KEY_NAME, name);
        inititalValues.put(KEY_HUMIDITY, humidity);
        inititalValues.put(KEY_TEMPERATURE, temperature);
        inititalValues.put(KEY_WINDSPEED, windSpeed);
        inititalValues.put(KEY_TIMESTAMP, timestamp);
        return sqLiteDatabase.insert(DATABASE_TABLE, null, inititalValues);
    }

    public boolean deleteAsset(long rowId) {
        return sqLiteDatabase.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
    }

    public boolean deleteAllAssets() {

        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + DATABASE_TABLE + "'");
        return sqLiteDatabase.delete(DATABASE_TABLE, null, null) > 0;
    }

    public Cursor getAllAssets() {
        return sqLiteDatabase.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_HUMIDITY, KEY_TEMPERATURE, KEY_WINDSPEED, KEY_TIMESTAMP}, null, null, null, null, null);
    }

    public Cursor getAsset(String name) {
        String[] fields = {KEY_ID, KEY_NAME, KEY_HUMIDITY, KEY_TEMPERATURE, KEY_WINDSPEED, KEY_TIMESTAMP};
        String criterials = KEY_NAME + "=?";
        String[] parameters = {name};
        return sqLiteDatabase.query(DATABASE_TABLE, fields, criterials,
                parameters, null, null, null);
    }
}
