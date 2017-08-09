package com.example.android.socialweather.utils;

import android.content.Context;

import com.example.android.socialweather.R;
import com.example.android.socialweather.data.WeatherPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        return " " + humidity + " %";
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

    //formats weather description
    public static String formatDescription(String description) {
        //capitalizes first character in each word in description
        String[] descriptionArray = description.split(" ");
        String returnDescription = "";
        for(int i = 0; i < descriptionArray.length; i++) {
            returnDescription += Character.toUpperCase(descriptionArray[i].charAt(0)) + descriptionArray[i].substring(1) + " ";
        }
        return returnDescription.substring(0, returnDescription.length() - 1);
    }

    //formats weather date
    public static String formatDate(Context context, long timeStamp) {
        Date resultDate = new Date(timeStamp * 1000);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //milliseconds till midnight
        long timeApartFromMidnight = (timeStamp * 1000) - calendar.getTimeInMillis();

        String dateString = "";
        if(timeApartFromMidnight < 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            dateString = context.getString(R.string.date_today) + sdf.format(resultDate);
        } else if(timeApartFromMidnight < TimeUnit.DAYS.toMillis(1)) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            dateString = context.getString(R.string.date_tommorow) + sdf.format(resultDate);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, h:mm a");
            dateString = sdf.format(resultDate);
        }
        return dateString;
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
            return R.color.gray;
        }
    }
}
