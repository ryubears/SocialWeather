package com.example.android.socialweather.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yehyun Ryu on 7/19/2017.
 */

public class WeatherContract {

    //name of the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.socialweather";

    //base uri for any uris that contact this content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //weather path
    public static final String PATH_WEATHER = "weather";

    //contents of weather table
    public static final class WeatherEntry implements BaseColumns {

        //base uri to contact this weather table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        //table name
        public static final String TABLE_NAME = "weather";

        //last data update time
        public static final String COLUMN_LAST_UPDATE_TIME = "last_update_time";

        //location id
        public static final String COLUMN_LOCATION_ID = "location_id";

        //location name
        public static final String COLUMN_LOCATION_NAME = "location_name";

        //list of friend ids living in this location
        public static final String COLUMN_FRIEND_IDS = "friend_ids";

        //list of friend names
        public static final String COLUMN_FRIEND_NAMES = "friend_names";

        //list of friend pictures
        public static final String COLUMN_FRIEND_PICTURES = "friend_pictures";

        /**

        //person id from facebook
        public static final String COLUMN_PERSON_ID = "person_id";

        //person name
        public static final String COLUMN_PERSON_NAME = "person_name";

        //person profile picture url
        public static final String COLUMN_PERSON_PROFILE = "person_profile";

        //person current city location
        public static final String COLUMN_PERSON_LOCATION = "person_location";
         **/

        //weather id
        public static final String COLUMN_WEATHER_ID = "weather_id";

        //weather description
        public static final String COLUMN_WEATHER_DESCRIPTION = "weather_description";

        //weather current temperature
        public static final String COLUMN_WEATHER_CURRENT_TEMP = "weather_current_temp";

        //weather min temperature
        public static final String COLUMN_WEATHER_MIN_TEMP = "weather_min_temp";

        //weather max temperature
        public static final String COLUMN_WEATHER_MAX_TEMP = "weather_max_temp";

        //weather pressure
        public static final String COLUMN_WEATHER_PRESSURE = "weather_pressure";

        //weather humidity
        public static final String COLUMN_WEATHER_HUMIDITY = "weather_humidity";

        //weather wind speed
        public static final String COLUMN_WEATHER_WIND_SPEED = "weather_wind_speed";
    }
}
