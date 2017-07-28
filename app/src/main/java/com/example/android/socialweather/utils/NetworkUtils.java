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

    //base url of open weather map
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

    //api key from open weather map, stored in gradle.properties
    private static final String API_KEY = "&appid=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;

    //creates an url with the location parameter
    private static URL createUrl(String location) {
        Log.d(LOG_TAG, "createUrl");

        String stringUrl = BASE_URL + location + API_KEY;

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch(MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL: " + e);
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
    private static ContentValues extractWeatherFromJSON(String jsonResponse) {
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
            contentValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_DESCRIPTION, description);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_CURRENT_TEMP, currentTemp);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_MIN_TEMP, minTemp);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_MAX_TEMP, maxTemp);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_PRESSURE, pressure);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_HUMIDITY, humidity);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_WIND_SPEED, windSpeed);

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

    //returns content value with location string
    public static ContentValues fetchWeather(String location) {
        Log.d(LOG_TAG, "fetchWeather");

        //create url
        URL url = createUrl(location);

        //get json response
        String jsonResponse = "";
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error with HTTP request: " + e);
        }

        //get content values from json response
        ContentValues contentValues = extractWeatherFromJSON(jsonResponse);
        return contentValues;
    }
}
