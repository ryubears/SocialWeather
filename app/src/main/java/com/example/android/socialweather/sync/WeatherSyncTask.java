package com.example.android.socialweather.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.example.android.socialweather.R;
import com.example.android.socialweather.data.WeatherContract.WeatherEntry;
import com.example.android.socialweather.data.WeatherPreferences;
import com.example.android.socialweather.utils.NetworkUtils;
import com.example.android.socialweather.utils.NotificationUtils;

import java.util.ArrayList;

/**
 * Created by Yehyun Ryu on 7/26/2017.
 */

public class WeatherSyncTask {

    //performs network call on each row in the database and update it with new weather data
    synchronized static void syncWeather(Context context) {
        try {
            //query for all rows
            Cursor cursor = context.getContentResolver().query(
                    WeatherEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            for(int i = 0; i < cursor.getCount(); i ++) {
                cursor.moveToPosition(i);
                int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                int indexLocation = cursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_LOCATION);

                int id = cursor.getInt(indexId);
                String location = cursor.getString(indexLocation);

                //update row
                ContentValues contentValues = NetworkUtils.fetchWeather(location);
                if(contentValues != null) {
                    context.getContentResolver().update(
                            ContentUris.withAppendedId(WeatherEntry.CONTENT_URI, id),
                            contentValues,
                            null,
                            null
                    );
                }
            }
            cursor.close();

            //show update notification
            if(WeatherPreferences.isUpdateNotificationEnabled(context)) {
                String updateNotificationTimeKey = context.getString(R.string.pref_update_notification_time_key);
                long currentTime = System.currentTimeMillis();
                long lastNotificationTime = WeatherPreferences.getLastNotificationTime(context, updateNotificationTimeKey);
                if(currentTime - lastNotificationTime >= DateUtils.DAY_IN_MILLIS) { //check if there were any update notification in the past 24 hours
                    NotificationUtils.notifyUpdateWeather(context);
                }
            }

            //query weather table to track rain,snow, and extreme weather
            String[] projection = new String[] {WeatherEntry.COLUMN_PERSON_NAME, WeatherEntry.COLUMN_WEATHER_ID};
            cursor = context.getContentResolver().query(
                    WeatherEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );

            ArrayList<String> rainFriends = new ArrayList<>();
            ArrayList<String> snowFriends = new ArrayList<>();
            ArrayList<String> extremeFriends = new ArrayList<>();
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                int indexName = cursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_NAME);
                int indexWeatherId = cursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);

                String name = cursor.getString(indexName);
                int weatherId = cursor.getInt(indexWeatherId);

                //keep track of friend experiencing rain, snow, and extreme weather
                if(weatherId >= 200 && weatherId <= 232) {
                    extremeFriends.add(name);
                } else if(weatherId >= 300 && weatherId <= 531) {
                    rainFriends.add(name);
                } else if(weatherId >= 600 && weatherId <= 622) {
                    snowFriends.add(name);
                } else if(weatherId >= 762 && weatherId <= 781) {
                    extremeFriends.add(name);
                } else if(weatherId >= 900 && weatherId <= 906) {
                    extremeFriends.add(name);
                } else if(weatherId >= 958 && weatherId <= 962) {
                    extremeFriends.add(name);
                }
            }

            if(WeatherPreferences.isRainSnowNotificationEnabled(context)) {
                //show rain notification
                if(rainFriends.size() != 0) {
                    String rainNotificationTimeKey = context.getString(R.string.pref_rain_notification_time_key);
                    long currentTime = System.currentTimeMillis();
                    long lastNotificationTime = WeatherPreferences.getLastNotificationTime(context, rainNotificationTimeKey);
                    if(currentTime - lastNotificationTime >= DateUtils.DAY_IN_MILLIS) { //check if there were any rain notification in the past 24 hours
                        NotificationUtils.notifyRainWeather(context, rainFriends);
                    }
                }

                //show snow notification
                if(snowFriends.size() != 0) {
                    String snowNotificationTimeKey = context.getString(R.string.pref_snow_notification_time_key);
                    long currentTime = System.currentTimeMillis();
                    long lastNotificationTime = WeatherPreferences.getLastNotificationTime(context, snowNotificationTimeKey);
                    if(currentTime - lastNotificationTime >= DateUtils.DAY_IN_MILLIS) { //check if there were any snow notification in the past 24 hours
                        NotificationUtils.notifySnowWeather(context, snowFriends);
                    }
                }
            }

            //show extreme weather notification
            if(WeatherPreferences.isExtremeNotificationEnabled(context)
                    && extremeFriends.size() != 0) {
                String extremeNotificationTimeKey = context.getString(R.string.pref_extreme_notification_time_key);
                long currentTime = System.currentTimeMillis();
                long lastNotificationTime = WeatherPreferences.getLastNotificationTime(context, extremeNotificationTimeKey);
                if(currentTime - lastNotificationTime >= DateUtils.DAY_IN_MILLIS) { //check if there were any extreme notification in the past 24 hours
                    NotificationUtils.notifyExtremeWeather(context, extremeFriends);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
