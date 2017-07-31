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
        final String SQL_CREATE_FACEBOOK_TABLE = "" +
                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +

                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherEntry.COLUMN_LAST_UPDATE_TIME + " INTEGER NOT NULL, " +

                WeatherEntry.COLUMN_LOCATION_ID + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_LOCATION_NAME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_LOCATION_PHOTO + " TEXT DEFAULT 'location_photo_empty', " +

                WeatherEntry.COLUMN_FRIEND_IDS + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_FRIEND_NAMES + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_FRIEND_PICTURES + " TEXT NOT NULL, " +

                WeatherEntry.COLUMN_CURRENT_WEATHER_ID + " INTEGER DEFAULT -1, " +
                WeatherEntry.COLUMN_CURRENT_WEATHER_DESCRIPTION + " TEXT DEFAULT 'description_empty', " + //also linked to strings.xml description_empty
                WeatherEntry.COLUMN_CURRENT_WEATHER_TEMP + " REAL DEFAULT -1," +
                WeatherEntry.COLUMN_CURRENT_WEATHER_PRESSURE + " REAL DEFAULT -1," +
                WeatherEntry.COLUMN_CURRENT_WEATHER_HUMIDITY + " INTEGER DEFAULT -1," +
                WeatherEntry.COLUMN_CURRENT_WEATHER_WIND_SPEED + " REAL DEFAULT -1, " +

                WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES + " STRING DEFAULT 'forecast_times_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_IDS + " STRING DEFAULT 'forecast_ids_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS + " STRING DEFAULT 'forecast_descriptions_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS + " STRING DEFAULT 'forecast_min_temps_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS + " STRING DEFAULT 'forecast_max_temps_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_PRESSURES + " STRING DEFAULT 'forecast_pressures_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_HUMIDITIES + " STRING DEFAULT 'forecast_humidities_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_WIND_SPEEDS + " STRING DEFAULT 'forecast_wind_speeds_empty', " +

                " UNIQUE (" + WeatherEntry.COLUMN_LOCATION_ID + ") ON CONFLICT REPLACE);";

        //execute the SQL statement above
        db.execSQL(SQL_CREATE_FACEBOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //empty for now since there is only one database version
        //should be used to update outdated databases
    }
}
