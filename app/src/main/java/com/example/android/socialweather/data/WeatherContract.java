package com.example.android.socialweather.data;

import android.net.Uri;
import android.provider.BaseColumns;

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

public class WeatherContract {

    //name of the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.socialweather";

    //base uri for any uris that contact this content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //facebook path
    public static final String PATH_FACEBOOK = "facebook";

    //account kit path
    public static final String PATH_ACCOUNT_KIT = "account_kit";

    //contents of weather table
    public static final class WeatherEntry implements BaseColumns {

        //base content uri to contact facebook weather table
        public static final Uri FACEBOOK_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FACEBOOK)
                .build();

        //base content uri to contact account kit weather table
        public static final Uri ACCOUNT_KIT_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ACCOUNT_KIT)
                .build();

        //facebook table name
        public static final String FACEBOOK_TABLE_NAME = "facebook";

        //account kit table name
        public static final String ACCOUNT_KIT_TABLE_NAME = "account_kit";

        //last data update time
        public static final String COLUMN_LAST_UPDATE_TIME = "last_update_time";

        //location id
        public static final String COLUMN_LOCATION_ID = "location_id";

        //location name
        public static final String COLUMN_LOCATION_NAME = "location_name";

        //location photo
        public static final String COLUMN_LOCATION_PHOTO = "location_photo";

        //list of friend ids living in this location
        public static final String COLUMN_FRIEND_IDS = "friend_ids";

        //list of friend names
        public static final String COLUMN_FRIEND_NAMES = "friend_names";

        //list of friend pictures
        public static final String COLUMN_FRIEND_PICTURES = "friend_pictures";

        //forecast weather time
        public static final String COLUMN_FORECAST_WEATHER_TIMES = "forecast_weather_time";

        //forecast weather id
        public static final String COLUMN_FORECAST_WEATHER_IDS = "forecast_weather_ids";

        //forecast weather description
        public static final String COLUMN_FORECAST_WEATHER_DESCRIPTIONS = "forecast_weather_descriptions";

        //forecast weather min temperature
        public static final String COLUMN_FORECAST_WEATHER_MIN_TEMPS = "forecast_weather_min_temps";

        //forecast weather max temperature
        public static final String COLUMN_FORECAST_WEATHER_MAX_TEMPS = "forecast_weather_max_temps";

        //forecast weather pressure
        public static final String COLUMN_FORECAST_WEATHER_PRESSURES = "forecast_weather_pressures";

        //forecast weather humidity
        public static final String COLUMN_FORECAST_WEATHER_HUMIDITIES = "forecast_weather_humidities";

        //forecast weather wind speed
        public static final String COLUMN_FORECAST_WEATHER_WIND_SPEEDS = "forecast_weather_wind_speeds";

    }
}
