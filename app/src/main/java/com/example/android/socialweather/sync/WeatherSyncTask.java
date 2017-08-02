package com.example.android.socialweather.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.android.socialweather.R;
import com.example.android.socialweather.data.WeatherContract.WeatherEntry;
import com.example.android.socialweather.data.WeatherPreferences;
import com.example.android.socialweather.utils.NetworkUtils;
import com.example.android.socialweather.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yehyun Ryu on 7/26/2017.
 */

public class WeatherSyncTask {

    //performs network call on each row in the database and update it with new weather data
    synchronized static void syncWeather(Context context, boolean isFacebook) {
        try {
            Cursor cursor;
            if(isFacebook) {
                cursor = context.getContentResolver().query(
                        WeatherEntry.FACEBOOK_CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            } else {
                //query for all rows
                cursor = context.getContentResolver().query(
                        WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            }

            for(int i = 0; i < cursor.getCount(); i ++) {
                cursor.moveToPosition(i);
                int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                int indexLocation = cursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);

                int id = cursor.getInt(indexId);
                String locationName = cursor.getString(indexLocation);

                ContentValues contentValues = NetworkUtils.fetchWeather(locationName); //NetworkUtils handles empty location, wrong location

                String photoUrl = NetworkUtils.fetchPhoto(locationName);
                contentValues.put(WeatherEntry.COLUMN_LOCATION_PHOTO, photoUrl);

                //update row
                if(isFacebook) {
                    context.getContentResolver().update(
                            ContentUris.withAppendedId(WeatherEntry.FACEBOOK_CONTENT_URI, id),
                            contentValues,
                            null,
                            null
                    );
                } else {
                    context.getContentResolver().update(
                            ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, id),
                            contentValues,
                            null,
                            null
                    );
                }
            }

            //close cursor used to update
            cursor.close();

            //show update notification
            if(WeatherPreferences.isUpdateNotificationEnabled(context)) {
                String updateNotificationTimeKey = context.getString(R.string.pref_update_notification_time_key);
                long currentTime = System.currentTimeMillis();
                long lastNotificationTime = WeatherPreferences.getLastNotificationTime(context, updateNotificationTimeKey);
                if(currentTime - lastNotificationTime >= TimeUnit.HOURS.toMillis(12)) { //check if there were any update notification in the past 24 hours
                    NotificationUtils.notifyUpdateWeather(context);
                }
            }

            //query weather table to track rain,snow, and extreme weather
            String[] projection = new String[] {WeatherEntry.COLUMN_FRIEND_NAMES, WeatherEntry.COLUMN_FORECAST_WEATHER_IDS};
            if(isFacebook) {
                cursor = context.getContentResolver().query(
                        WeatherEntry.FACEBOOK_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null
                );
            } else {
                cursor = context.getContentResolver().query(
                        WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null
                );
            }

            ArrayList<String> rainFriends = new ArrayList<>();
            ArrayList<String> snowFriends = new ArrayList<>();
            ArrayList<String> extremeFriends = new ArrayList<>();
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                int indexNames = cursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
                int indexWeatherIds = cursor.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);

                String names = cursor.getString(indexNames);
                String[] nameArray = names.split(context.getString(R.string.delimiter));
                String[] weatherIds = cursor.getString(indexWeatherIds).split(context.getString(R.string.delimiter));
                int currentWeatherId = Integer.valueOf(weatherIds[0]);

                //keep track of friend experiencing rain, snow, and extreme weather
                if(currentWeatherId >= 200 && currentWeatherId <= 232) {
                    for(int x = 0; x < nameArray.length; x++) {
                        extremeFriends.add(nameArray[x]);
                    }
                } else if(currentWeatherId >= 300 && currentWeatherId <= 531) {
                    for(int x = 0; x < nameArray.length; x++) {
                        rainFriends.add(nameArray[x]);
                    }
                } else if(currentWeatherId >= 600 && currentWeatherId <= 622) {
                    for(int x = 0; x < nameArray.length; x++) {
                        snowFriends.add(nameArray[x]);
                    }
                } else if(currentWeatherId >= 762 && currentWeatherId <= 781) {
                    for(int x = 0; x < nameArray.length; x++) {
                        extremeFriends.add(nameArray[x]);
                    }
                } else if(currentWeatherId >= 900 && currentWeatherId <= 906) {
                    for(int x = 0; x < nameArray.length; x++) {
                        extremeFriends.add(nameArray[x]);
                    }
                } else if(currentWeatherId >= 958 && currentWeatherId <= 962) {
                    for(int x = 0; x < nameArray.length; x++) {
                        extremeFriends.add(nameArray[x]);
                    }
                }
            }

            //close cursor used to track rain, snow, and extreme weather
            cursor.close();

            if(WeatherPreferences.isRainSnowNotificationEnabled(context)) {
                //show rain notification
                if(rainFriends.size() != 0) {
                    String rainNotificationTimeKey = context.getString(R.string.pref_rain_notification_time_key);
                    long currentTime = System.currentTimeMillis();
                    long lastNotificationTime = WeatherPreferences.getLastNotificationTime(context, rainNotificationTimeKey);
                    if(currentTime - lastNotificationTime >= TimeUnit.HOURS.toMillis(12)) { //check if there were any rain notification in the past 24 hours
                        NotificationUtils.notifyRainWeather(context, rainFriends);
                    }
                }

                //show snow notification
                if(snowFriends.size() != 0) {
                    String snowNotificationTimeKey = context.getString(R.string.pref_snow_notification_time_key);
                    long currentTime = System.currentTimeMillis();
                    long lastNotificationTime = WeatherPreferences.getLastNotificationTime(context, snowNotificationTimeKey);
                    if(currentTime - lastNotificationTime >= TimeUnit.HOURS.toMillis(12)) { //check if there were any snow notification in the past 24 hours
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
                if(currentTime - lastNotificationTime >= TimeUnit.HOURS.toMillis(12)) { //check if there were any extreme notification in the past 24 hours
                    NotificationUtils.notifyExtremeWeather(context, extremeFriends);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
