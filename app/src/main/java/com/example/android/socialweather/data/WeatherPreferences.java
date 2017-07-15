package com.example.android.socialweather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.android.socialweather.R;

/**
 * Created by Yehyun Ryu on 7/15/2017.
 */

public class WeatherPreferences {

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
}
