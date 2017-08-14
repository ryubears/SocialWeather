package com.yehyunryu.android.socialweather.utils;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.yehyunryu.android.socialweather.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.yehyunryu.android.socialweather.data.WeatherContract.WeatherEntry;

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

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    //base url for weather
    private static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";

    //base url of google places search
    private static final String PLACES_SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";

    //base url of google places photos
    private static final String PLACES_PHOTOS_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxheight=1200&photoreference=";

    //api key from open weather map, stored in gradle.properties
    private static final String WEATHER_API_KEY = "&appid=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;

    //api key from google places api, stored in gradle.properties
    private static final String PLACES_API_KEY = "&key=" + BuildConfig.GOOGLE_PLACES_API_KEY;

    //creates an url for weather from open weather map
    private static URL createWeatherUrl(String location) {
        Log.d(LOG_TAG, "createWeatherUrl");

        String stringUrl = WEATHER_BASE_URL + location + WEATHER_API_KEY;

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch(MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating weather URL: " + e);
        }
        return url;
    }

    //creates an url for open weather map forecast

    //creates url for google places api
    private static URL createPlacesUrl(String location) {
        Log.d(LOG_TAG, "createPlacesUrl");

        String stringUrl = PLACES_SEARCH_BASE_URL + location + PLACES_API_KEY;

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch(MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating places URL: " + e);
        }
        return url;
    }

    //connects and read from open weather map servers
    private static String makeHTTPRequest(URL url) throws IOException {
        Log.d(LOG_TAG, "makeHTTPRequest");

        //json response to return
        String jsonResponse = "";

        //return early if url is empty
        if(url == null) {
            return jsonResponse;
        }

        //google places api returns an error response of 400 if there is a space in the query
        String stringUrl = url.toString();
        if(stringUrl.contains(" ")) {
            stringUrl = stringUrl.replace(" ", "&20");
            url = new URL(stringUrl);
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //establishes connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.connect();

            if(connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "URL: " + url);
                Log.e(LOG_TAG, "Bad Response Code: " + connection.getResponseCode());
            }
        } catch(Exception e) {
            Log.e(LOG_TAG, "Error making HTTP request: " + e);
        } finally {
            //closes input stream and connection
            if(inputStream != null) inputStream.close();
            if(connection != null) connection.disconnect();

            return jsonResponse;
        }
    }

    //reads from input stream and returns an output of string
    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.d(LOG_TAG, "readFromStream");

        //output string
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //extract forecast weather data
    private static ContentValues extractForecastWeatherFromJSON(String jsonResponse) {
        Log.d(LOG_TAG, "extractForecastWeatherFromJSON");

        //return null content value if json response is empty
        if(TextUtils.isEmpty(jsonResponse)) {
            Log.e(LOG_TAG, "Empty json response");
            return null;
        }

        //Content Values to return
        ContentValues contentValues = new ContentValues();
        try {
            JSONObject baseJson = new JSONObject(jsonResponse);
            JSONArray list = baseJson.getJSONArray("list");

            //to store data
            String timeStamps = "";
            String weatherIds = "";
            String weatherDescriptions = "";
            String minTemps = "";
            String maxTemps = "";
            String pressures = "";
            String humidities = "";
            String windSpeeds = "";

            for(int i = 0; i < list.length(); i++) {
                JSONObject listItem = list.getJSONObject(i);

                //get timestamp
                long time = listItem.getLong("dt");
                timeStamps += String.valueOf(time) + "%%%";

                //get weather id and description
                JSONArray weatherArray = listItem.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                int weatherId = weather.getInt("id");
                String description = weather.getString("description");

                weatherIds += String.valueOf(weatherId) + "%%%";
                weatherDescriptions += description + "%%%";

                //get min max temp, pressure, and humidity
                JSONObject main = listItem.getJSONObject("main");
                double minTemp = main.getDouble("temp_min");
                double maxTemp = main.getDouble("temp_max");
                double pressure = main.getDouble("pressure");
                int humidity = main.getInt("humidity");

                minTemps += String.valueOf(minTemp) + "%%%";
                maxTemps += String.valueOf(maxTemp) + "%%%";
                pressures += String.valueOf(pressure) + "%%%";
                humidities += String.valueOf(humidity) + "%%%";

                //get wind speed
                JSONObject wind = listItem.getJSONObject("wind");
                double windSpeed = wind.getDouble("speed");

                windSpeeds += String.valueOf(windSpeed) + "%%%";
            }

            //put data into content values
            contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES, timeStamps.substring(0, timeStamps.length() - 3));
            contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_IDS, weatherIds.substring(0, weatherIds.length() - 3));
            contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS, weatherDescriptions.substring(0, weatherDescriptions.length() - 3));
            contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS, minTemps.substring(0, minTemps.length() - 3));
            contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS, maxTemps.substring(0, maxTemps.length() - 3));
            contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_PRESSURES, pressures.substring(0, pressures.length() - 3));
            contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_HUMIDITIES, humidities.substring(0, humidities.length() - 3));
            contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_WIND_SPEEDS, windSpeeds.substring(0, windSpeeds.length() - 3));
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Error extracting forecast from JSON: " + e);
        }

        return contentValues;
    }

    private static String extractPlacesFromJSON(String jsonResponse) {
        Log.d(LOG_TAG, "extractPlacesFromJSON");

        //return empty string if json response is empty
        if(TextUtils.isEmpty(jsonResponse)) {
            return "location_photo_empty"; //linked to strings.xml and default value in WeatherDbHelper
        }

        //photo url to return
        String photoUrl = "location_photo_empty"; //linked to strings.xml and default value in WeatherDbHelper
        try {
            JSONObject baseJson = new JSONObject(jsonResponse);

            //check status
            String status = baseJson.getString("status");
            Log.d(LOG_TAG, "Place Query Status: " + status);
            if(!status.equals("OK")) {
                return photoUrl;
            }

            //get results
            JSONArray results = baseJson.getJSONArray("results");
            if(results.length() == 0) { //check if there are results
                return photoUrl;
            }

            //get photo
            JSONObject result = results.getJSONObject(0);
            if(result.isNull("photos")) { //check if there are photos
                return photoUrl;
            }
            JSONArray photos = result.getJSONArray("photos");
            JSONObject photo = photos.getJSONObject(0);

            //extract photo reference to build photo url
            String photoReference = photo.getString("photo_reference");
            photoUrl = PLACES_PHOTOS_BASE_URL + photoReference + PLACES_API_KEY;

            //for debugging
            Log.d(LOG_TAG, photoUrl);
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Error extracting places from JSON: " + e);
        }
        return photoUrl;
    }

    //returns content value of weather with location string
    public static ContentValues fetchWeather(String location) {
        Log.d(LOG_TAG, "fetchWeather");

        //create url
        URL forecastUrl = createWeatherUrl(location);

        //get json response
        String forecastJsonResponse = "";
        try {
            forecastJsonResponse = makeHTTPRequest(forecastUrl);
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error with HTTP request: " + e);
        }

        //get content values from json response
        return extractForecastWeatherFromJSON(forecastJsonResponse);
    }

    //returns content value of place photo
    public static String fetchPhoto(String location) {
        Log.d(LOG_TAG, "fetchPhoto");

        //create url
        URL url = createPlacesUrl(location);

        //get json response
        String jsonResponse = "";
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error with HTTP request: " + e);
        }

        //get content values from json response
        return extractPlacesFromJSON(jsonResponse);
    }
}
