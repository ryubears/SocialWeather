package com.example.android.socialweather.utils;

import android.content.Context;

import com.example.android.socialweather.R;
import com.example.android.socialweather.data.WeatherPreferences;

/**
 * Created by Yehyun Ryu on 7/26/2017.
 */

public class WeatherUtils {
    //formats kelvin to either celsius or fahrenheit depending on settings
    public static String formatTemperature(Context context, double temperature) {
        if(WeatherPreferences.isCelsius(context)) {
            temperature = temperature - 273.15;
        } else {
            temperature = temperature * 9.0 / 5.0 - 459.67;
        }

        return String.format(context.getString(R.string.format_temperature), temperature);
    }

    //formats pressure
    public static String formatPressure(Context context, double pressure) {
        return String.format(context.getString(R.string.format_pressure), pressure);
    }

    //formats humidity
    public static String formatHumidity(Context context, int humidity) {
        return humidity + " %";
    }

    //formats wind speed
    public static String formatWindSpeed(Context context, double windSpeed) {
        if(!WeatherPreferences.isCelsius(context)) {
            windSpeed = windSpeed * 0.621371;
            return String.format(context.getString(R.string.format_wind_mph), windSpeed);
        } else {
            return String.format(context.getString(R.string.format_wind_kmh), windSpeed);
        }
    }

    //get drawable resource for weather icon with color
    public static int getColorWeatherIcon(int weatherId) {
        if(weatherId >= 200 && weatherId <= 232) {
            return R.drawable.storm_color;
        } else if(weatherId >= 300 && weatherId <= 321) {
            return R.drawable.light_rain_color;
        } else if(weatherId >= 500 && weatherId <= 531) {
            return R.drawable.rain_color;
        } else if(weatherId >= 600 && weatherId <= 622) {
            return R.drawable.snow_color;
        } else if(weatherId >= 701 && weatherId <= 762) {
            return R.drawable.fog_color;
        } else if(weatherId >= 771 && weatherId <= 781) {
            return R.drawable.storm_color;
        } else if(weatherId == 800) {
            return R.drawable.clear_color;
        } else if(weatherId >= 801 && weatherId <= 804) {
            return R.drawable.cloudy_color;
        } else if(weatherId >= 900 && weatherId <= 906) {
            return R.drawable.storm_color;
        } else if(weatherId >= 951 && weatherId <= 959) {
            return R.drawable.windy_color;
        } else if(weatherId >= 960 && weatherId <= 962) {
            return R.drawable.storm_color;
        } else {
            return R.drawable.no_weather_color;
        }
    }

    //get drawable resource for weather icon in white color
    public static int getWhiteWeatherIcon(int weatherId) {
        if(weatherId >= 200 && weatherId <= 232) {
            return R.drawable.storm_white;
        } else if(weatherId >= 300 && weatherId <= 321) {
            return R.drawable.rain_white;
        } else if(weatherId >= 500 && weatherId <= 531) {
            return R.drawable.rain_white;
        } else if(weatherId >= 600 && weatherId <= 622) {
            return R.drawable.snow_white;
        } else if(weatherId >= 701 && weatherId <= 762) {
            return R.drawable.fog_white;
        } else if(weatherId >= 771 && weatherId <= 781) {
            return R.drawable.storm_white;
        } else if(weatherId == 800) {
            return R.drawable.clear_white;
        } else if(weatherId >= 801 && weatherId <= 804) {
            return R.drawable.cloudy_white;
        } else if(weatherId >= 900 && weatherId <= 906) {
            return R.drawable.storm_white;
        } else if(weatherId >= 951 && weatherId <= 959) {
            return R.drawable.windy_white;
        } else if(weatherId >= 960 && weatherId <= 962) {
            return R.drawable.storm_white;
        } else {
            return R.drawable.no_weather_white;
        }
    }

    //get drawable resource for weather background
    public static int getWeatherBackground(int weatherId) {
        if(weatherId >= 200 && weatherId <= 232) {
            return R.drawable.storm_background;
        } else if(weatherId >= 300 && weatherId <= 321) {
            return R.drawable.rain_background;
        } else if(weatherId >= 500 && weatherId <= 531) {
            return R.drawable.rain_background;
        } else if(weatherId >= 600 && weatherId <= 622) {
            return R.drawable.snow_background;
        } else if(weatherId >= 701 && weatherId <= 762) {
            return R.drawable.fog_background;
        } else if(weatherId >= 771 && weatherId <= 781) {
            return R.drawable.storm_background;
        } else if(weatherId == 800) {
            return R.drawable.clear_background;
        } else if(weatherId >= 801 && weatherId <= 804) {
            return R.drawable.cloudy_background;
        } else if(weatherId >= 900 && weatherId <= 906) {
            return R.drawable.storm_background;
        } else if(weatherId >= 951 && weatherId <= 959) {
            return R.drawable.windy_background;
        } else if(weatherId >= 960 && weatherId <= 962) {
            return R.drawable.storm_background;
        } else {
            return R.drawable.no_weather_background;
        }
    }
}
