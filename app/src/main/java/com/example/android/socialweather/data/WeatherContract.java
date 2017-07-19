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

        //person id from facebook
        public static final String COLUMN_PERSON_ID = "person_id";

        //person name
        public static final String COLUMN_PERSON_NAME = "person_name";

        //person profile picture url
        public static final String COLUMN_PERSON_PROFILE = "person_profile";

        //person current city location
        public static final String COLUMN_PERSON_LOCATION = "person_location";

    }
}
