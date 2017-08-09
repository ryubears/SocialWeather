package com.example.android.socialweather.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.socialweather.data.WeatherContract.WeatherEntry;

/**
 * Copyright 2017 Yehyun Ryu

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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
        //SQL statement that will create facebook table
        final String SQL_CREATE_FACEBOOK_TABLE =
                "CREATE TABLE " + WeatherEntry.FACEBOOK_TABLE_NAME + " (" +

                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherEntry.COLUMN_LAST_UPDATE_TIME + " INTEGER NOT NULL, " +

                WeatherEntry.COLUMN_LOCATION_ID + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_LOCATION_NAME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_LOCATION_PHOTO + " TEXT DEFAULT 'location_photo_empty', " +

                WeatherEntry.COLUMN_FRIEND_IDS + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_FRIEND_NAMES + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_FRIEND_PICTURES + " TEXT NOT NULL, " +

                WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES + " STRING DEFAULT 'forecast_times_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_IDS + " STRING DEFAULT 'forecast_ids_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS + " STRING DEFAULT 'forecast_descriptions_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS + " STRING DEFAULT 'forecast_min_temps_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS + " STRING DEFAULT 'forecast_max_temps_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_PRESSURES + " STRING DEFAULT 'forecast_pressures_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_HUMIDITIES + " STRING DEFAULT 'forecast_humidities_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_WIND_SPEEDS + " STRING DEFAULT 'forecast_wind_speeds_empty', " +

                " UNIQUE (" + WeatherEntry.COLUMN_LOCATION_ID + ") ON CONFLICT REPLACE);";

        //SQL statement that will create account kit table
        final String SQL_CREATE_ACCOUNT_KIT_TABLE =
                "CREATE TABLE " + WeatherEntry.ACCOUNT_KIT_TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherEntry.COLUMN_LAST_UPDATE_TIME + " INTEGER NOT NULL, " +

                WeatherEntry.COLUMN_LOCATION_NAME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_LOCATION_PHOTO + " TEXT DEFAULT 'location_photo_empty', " +

                WeatherEntry.COLUMN_FRIEND_NAMES + " TEXT NOT NULL, " +
                        //TODO: ADD FRIEND PICTURES

                WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES + " STRING DEFAULT 'forecast_times_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_IDS + " STRING DEFAULT 'forecast_ids_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS + " STRING DEFAULT 'forecast_descriptions_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS + " STRING DEFAULT 'forecast_min_temps_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS + " STRING DEFAULT 'forecast_max_temps_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_PRESSURES + " STRING DEFAULT 'forecast_pressures_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_HUMIDITIES + " STRING DEFAULT 'forecast_humidities_empty', " +
                WeatherEntry.COLUMN_FORECAST_WEATHER_WIND_SPEEDS + " STRING DEFAULT 'forecast_wind_speeds_empty', " +

                " UNIQUE (" + WeatherEntry.COLUMN_LOCATION_NAME + ") ON CONFLICT REPLACE);";

        //execute the SQL statement above
        db.execSQL(SQL_CREATE_FACEBOOK_TABLE);
        db.execSQL(SQL_CREATE_ACCOUNT_KIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //empty for now since there is only one database version
        //should be used to update outdated databases
    }
}
