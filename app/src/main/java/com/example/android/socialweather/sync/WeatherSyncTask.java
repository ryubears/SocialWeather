package com.example.android.socialweather.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

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


            int unknownIndex = -1; //index at which invalid locations are stored
            int unknownPosition = -1; //position at which invalid locations are stored
            boolean isValid = false;
            for(int i = 0; i < cursor.getCount(); i ++) {
                //checks both weather and photo because there are cases when should-be-invalid cases return a valid json response
                boolean emptyWeather = false; //whether a valid json response was sent back from weather query
                boolean emptyPhoto = false; //whether a valid json response was sent back from place search query
                String invalidFriendName = ""; //friend name of newly added invalid location

                cursor.moveToPosition(i);
                //get row id and location
                int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                int indexLocation = cursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
                int id = cursor.getInt(indexId);
                String locationName = cursor.getString(indexLocation);

                //fetch weather
                ContentValues contentValues = NetworkUtils.fetchWeather(locationName); //NetworkUtils handles empty location, wrong location
                if(contentValues == null) {
                    //if no weather was fetched
                    emptyWeather = true;
                }

                //fetch photo
                String photoUrl = NetworkUtils.fetchPhoto(locationName);
                if(photoUrl.equals(context.getString(R.string.picture_empty))) {
                    //if no photo was fetched
                    emptyPhoto = true;
                }

                //add location photo
                if(contentValues == null) contentValues = new ContentValues();
                contentValues.put(WeatherEntry.COLUMN_LOCATION_PHOTO, photoUrl);

                //update row
                if(isFacebook) {
                    if(!emptyWeather && !emptyPhoto) isValid = true;
                    context.getContentResolver().update(
                            ContentUris.withAppendedId(WeatherEntry.FACEBOOK_CONTENT_URI, id),
                            contentValues,
                            null,
                            null
                    );
                } else {
                    if(emptyWeather || emptyPhoto) {
                        if(locationName.equals(context.getString(R.string.location_empty))) {
                            //save unknown index and position
                            unknownIndex = id;
                            unknownPosition = i;
                        } else {
                            //save invalid friend name
                            int indexFriendNames = cursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
                            invalidFriendName = cursor.getString(indexFriendNames);
                            //delete row
                            context.getContentResolver().delete(
                                    ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, id),
                                    null,
                                    null
                            );
                        }
                    } else {
                        //update row
                        isValid = true;
                        context.getContentResolver().update(
                                ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, id),
                                contentValues,
                                null,
                                null
                        );
                    }
                }

                //if newly added location was invalid
                if(!TextUtils.isEmpty(invalidFriendName)) {
                    ContentValues invalidContentValues = new ContentValues();
                    if(unknownIndex == -1) {
                        //if no invalid location were added before
                        invalidContentValues.put(WeatherEntry.COLUMN_LAST_UPDATE_TIME, System.currentTimeMillis());
                        invalidContentValues.put(WeatherEntry.COLUMN_LOCATION_NAME, context.getString(R.string.location_empty));
                        invalidContentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, invalidFriendName);
                        //insert unknown location
                        context.getContentResolver().insert(
                                WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                                invalidContentValues
                        );
                    } else {
                        //if there already is an unknown(invalid) location
                        cursor.moveToPosition(unknownPosition);
                        int indexFriendNames = cursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
                        String friendNames = cursor.getString(indexFriendNames);
                        friendNames += context.getString(R.string.delimiter) + invalidFriendName;
                        invalidContentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, friendNames);
                        //update friend names at unknown location row
                        context.getContentResolver().update(
                                ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, unknownIndex),
                                invalidContentValues,
                                null,
                                null
                        );
                    }
                }
            }

            //close cursor used to update
            cursor.close();

            //show update notification
            if(isValid) {
                if(WeatherPreferences.isUpdateNotificationEnabled(context)) {
                    String updateNotificationTimeKey = context.getString(R.string.pref_update_notification_time_key);
                    long currentTime = System.currentTimeMillis();
                    long lastNotificationTime = WeatherPreferences.getLastNotificationTime(context, updateNotificationTimeKey);
                    if(currentTime - lastNotificationTime >= TimeUnit.HOURS.toMillis(12)) { //check if there were any update notification in the past 24 hours
                        NotificationUtils.notifyUpdateWeather(context);
                    }
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

            //store friends that are experiencing rain, snow, extreme
            ArrayList<String> rainFriends = new ArrayList<>();
            ArrayList<String> snowFriends = new ArrayList<>();
            ArrayList<String> extremeFriends = new ArrayList<>();
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                //get name and weather id
                int indexNames = cursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
                int indexWeatherIds = cursor.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);
                String names = cursor.getString(indexNames);
                String[] nameArray = names.split(context.getString(R.string.delimiter));
                String[] weatherIds = cursor.getString(indexWeatherIds).split(context.getString(R.string.delimiter));
                
                //check if there is a valid weather id
                int currentWeatherId = -1;
                if(!weatherIds[0].equals(context.getString(R.string.forecast_ids_empty))) {
                    currentWeatherId = Integer.valueOf(weatherIds[0]);
                }

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

            //to trigger loader refresh to remove refresh animation
            if(isFacebook) {
                context.getContentResolver().notifyChange(WeatherEntry.FACEBOOK_CONTENT_URI, null);
            } else {
                context.getContentResolver().notifyChange(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, null);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
