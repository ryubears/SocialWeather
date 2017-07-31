package com.example.android.socialweather.utils;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.socialweather.BuildConfig;

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

import static com.example.android.socialweather.data.WeatherContract.WeatherEntry;

/**
 * Created by Yehyun Ryu on 7/24/2017.
 */

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final int CURRENT_WEATHER = 11;
    private static final int FORECAST_WEATHER = 12;

    //base url for weather
    private static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/";

    //base url of google places search
    private static final String PLACES_SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";

    //base url of google places photos
    private static final String PLACES_PHOTOS_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxheight=1200&photoreference=";

    //api key from open weather map, stored in gradle.properties
    private static final String WEATHER_API_KEY = "&appid=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;

    //api key from google places api, stored in gradle.properties
    private static final String PLACES_API_KEY = "&key=" + BuildConfig.GOOGLE_PLACES_API_KEY;

    //creates an url for weather from open weather map
    private static URL createWeatherUrl(int queryType, String location) {
        Log.d(LOG_TAG, "createWeatherUrl");

        String stringUrl = "";
        if(queryType == CURRENT_WEATHER) {
            stringUrl = WEATHER_BASE_URL + "weather?q=" + location + WEATHER_API_KEY;
        } else {
            stringUrl = WEATHER_BASE_URL + "forecast?q=" + location + WEATHER_API_KEY;
        }

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

    //extract data from json response
    private static ContentValues extractCurrentWeatherFromJSON(String jsonResponse) {
        Log.d(LOG_TAG, "extractWeatherFromJSON");

        if(TextUtils.isEmpty(jsonResponse)) {
            Log.d(LOG_TAG, "Empty jsonResponse");
            return null;
        }

        //content values to be returned
        ContentValues contentValues = new ContentValues();
        try {
            JSONObject baseJson = new JSONObject(jsonResponse);

            //weather id and description
            JSONArray weatherArray = baseJson.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            int weatherId = weather.getInt("id");
            String description = weather.getString("description");

            //weather current temp, min temp, max temp, pressure, and humidity
            JSONObject main = baseJson.getJSONObject("main");
            double currentTemp = main.getDouble("temp");
            double minTemp = main.getDouble("temp_min");
            double maxTemp = main.getDouble("temp_max");
            double pressure = main.getDouble("pressure");
            int humidity = main.getInt("humidity");

            //weather wind speed
            JSONObject wind = baseJson.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");

            //add to content values
            contentValues.put(WeatherEntry.COLUMN_CURRENT_WEATHER_ID, weatherId);
            contentValues.put(WeatherEntry.COLUMN_CURRENT_WEATHER_DESCRIPTION, description);
            contentValues.put(WeatherEntry.COLUMN_CURRENT_WEATHER_TEMP, currentTemp);
            contentValues.put(WeatherEntry.COLUMN_CURRENT_WEATHER_PRESSURE, pressure);
            contentValues.put(WeatherEntry.COLUMN_CURRENT_WEATHER_HUMIDITY, humidity);
            contentValues.put(WeatherEntry.COLUMN_CURRENT_WEATHER_WIND_SPEED, windSpeed);

            //for debugging
            Log.d(LOG_TAG, String.valueOf(weatherId));
            Log.d(LOG_TAG, String.valueOf(description));
            Log.d(LOG_TAG, String.valueOf(currentTemp));
            Log.d(LOG_TAG, String.valueOf(minTemp));
            Log.d(LOG_TAG, String.valueOf(maxTemp));
            Log.d(LOG_TAG, String.valueOf(pressure));
            Log.d(LOG_TAG, String.valueOf(humidity));
            Log.d(LOG_TAG, String.valueOf(windSpeed));
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Problem extracting data from JSON: " + e);
        }
        return contentValues;
    }

    //extract forecast weather data
    private static ContentValues extractForecastWeatherFromJSON(String jsonResponse) {
        Log.d(LOG_TAG, "extractForecastWeatherFromJSON");

        //return null content value if json response is empty
        if(TextUtils.isEmpty(jsonResponse)) {
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

            //for debugging
            Log.d(LOG_TAG, timeStamps.substring(0, timeStamps.length() - 3));
            Log.d(LOG_TAG, weatherIds.substring(0, weatherIds.length() - 3));
            Log.d(LOG_TAG, weatherDescriptions.substring(0, weatherDescriptions.length() - 3));
            Log.d(LOG_TAG, minTemps.substring(0, minTemps.length() - 3));
            Log.d(LOG_TAG, maxTemps.substring(0, maxTemps.length() - 3));
            Log.d(LOG_TAG, pressures.substring(0, pressures.length() - 3));
            Log.d(LOG_TAG, humidities.substring(0, humidities.length() - 3));
            Log.d(LOG_TAG, windSpeeds.substring(0, windSpeeds.length() - 3));

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
        String photoUrl = "";
        try {
            JSONObject baseJson = new JSONObject(jsonResponse);
            JSONArray results = baseJson.getJSONArray("results");
            JSONObject result = results.getJSONObject(0);
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
        URL currentUrl = createWeatherUrl(CURRENT_WEATHER, location);
        URL forecastUrl = createWeatherUrl(FORECAST_WEATHER, location);

        //get json response
        String currentJsonResponse = "";
        String forecastJsonResponse = "";
        try {
            currentJsonResponse = makeHTTPRequest(currentUrl);
            forecastJsonResponse = makeHTTPRequest(forecastUrl);
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error with HTTP request: " + e);
        }

        //get content values from json response
        ContentValues contentValues = extractCurrentWeatherFromJSON(currentJsonResponse);

        //add forecast values to returning content values
        ContentValues forecastValues = extractForecastWeatherFromJSON(forecastJsonResponse);
        String forecastTimes = forecastValues.getAsString(WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES);
        String forecastWeatherIds = forecastValues.getAsString(WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);
        String forecastWeatherDescriptions = forecastValues.getAsString(WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS);
        String forecastMinTemps = forecastValues.getAsString(WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS);
        String forecastMaxTemps = forecastValues.getAsString(WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS);
        String forecastPressures = forecastValues.getAsString(WeatherEntry.COLUMN_CURRENT_WEATHER_PRESSURE);
        String forecastHumidities = forecastValues.getAsString(WeatherEntry.COLUMN_FORECAST_WEATHER_HUMIDITIES);
        String forecastWindSpeeds = forecastValues.getAsString(WeatherEntry.COLUMN_FORECAST_WEATHER_WIND_SPEEDS);

        contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES, forecastTimes);
        contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_IDS, forecastWeatherIds);
        contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS, forecastWeatherDescriptions);
        contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS, forecastMinTemps);
        contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS, forecastMaxTemps);
        contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_PRESSURES, forecastPressures);
        contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_HUMIDITIES, forecastHumidities);
        contentValues.put(WeatherEntry.COLUMN_FORECAST_WEATHER_WIND_SPEEDS, forecastWindSpeeds);

        return contentValues;
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
        String photoUrl = extractPlacesFromJSON(jsonResponse);
        return photoUrl;
    }
}
