package com.yehyunryu.android.socialweather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.yehyunryu.android.socialweather.R;

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

    //saves how user logged in to the app
    public static void saveLoginType(Context context, boolean isFacebook) {
        //save login type
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_login_type_key), isFacebook);
        editor.apply();
    }

    //returns how user logged in to the app
    public static boolean getLoginType(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_login_type_key), false);
    }
}
