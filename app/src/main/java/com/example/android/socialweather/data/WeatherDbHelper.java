package com.example.android.socialweather.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.socialweather.data.WeatherContract.WeatherEntry;

/**
 * Created by Yehyun Ryu on 7/19/2017.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {

    //database name
    public static final String DATABASE_NAME = "weather.db";

    //database version, will be incremented every time there is a change to the database structure
    private static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL statement that will create a table
        final String SQL_CREATE_WEATHER_TABLE = "" +
                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherEntry.COLUMN_PERSON_ID + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_PERSON_NAME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_PERSON_PROFILE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_PERSON_LOCATION + " TEXT NOT NULL, " +
                " UNIQUE (" + WeatherEntry.COLUMN_PERSON_ID + ") ON CONFLICT REPLACE);";

        //execute the SQL statement above
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //empty for now since there is only one database version
        //should be used to update outdated databases
    }
}
