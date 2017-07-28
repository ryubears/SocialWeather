package com.example.android.socialweather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.android.socialweather.R;

/**
 * Created by Yehyun Ryu on 7/15/2017.
 */

public class WeatherPreferences {

    //return which unit user prefers
    public static boolean isCelsius(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_celsius);
        String preferredUnits = sp.getString(keyForUnits, defaultUnits);
        String celsius = context.getString(R.string.pref_units_celsius);

        boolean userPrefersCelsius = false;
        if(celsius.equals(preferredUnits)) {
            userPrefersCelsius = true;
        }

        return userPrefersCelsius;
    }

    //return if update notification is enabled
    public static boolean isUpdateNotificationEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_notification_update_key);
        boolean defaultValue = context.getResources().getBoolean(R.bool.pref_notification_default);

        return sp.getBoolean(key, defaultValue);
    }

    //return if rain/snow notification is enabled
    public static boolean isRainSnowNotificationEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_notification_rain_snow_key);
        boolean defaultValue = context.getResources().getBoolean(R.bool.pref_notification_default);

        return sp.getBoolean(key, defaultValue);
    }

    //return if extreme weather notification is enabled
    public static boolean isExtremeNotificationEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getString(R.string.pref_notification_extreme_key);
        boolean defaultValue = context.getResources().getBoolean(R.bool.pref_notification_default);

        return sp.getBoolean(key, defaultValue);
    }

    //saves the time the notification had been notified
    public static void saveNotificationTime(Context context, long timeOfNotification, String key) {
        if(key.equals(context.getString(R.string.pref_update_notification_time_key))
                || key.equals(context.getString(R.string.pref_rain_notification_time_key))
                || key.equals(context.getString(R.string.pref_snow_notification_time_key))
                || key.equals(context.getString(R.string.pref_extreme_notification_time_key))) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(key, timeOfNotification);
            editor.apply();
        } else {
            throw new RuntimeException("Key not recognized for SharedPreferences: " + key);
        }
    }

    //returns last time the notification had been notified
    public static long getLastNotificationTime(Context context, String key) {
        if(key.equals(context.getString(R.string.pref_update_notification_time_key))
                || key.equals(context.getString(R.string.pref_rain_notification_time_key))
                || key.equals(context.getString(R.string.pref_snow_notification_time_key))
                || key.equals(context.getString(R.string.pref_extreme_notification_time_key))) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getLong(key, 0);
        } else {
            throw new RuntimeException("Key not recognized for SharedPreferences: " + key);
        }
    }


}
