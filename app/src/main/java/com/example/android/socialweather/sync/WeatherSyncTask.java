package com.example.android.socialweather.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.android.socialweather.data.WeatherContract.WeatherEntry;
import com.example.android.socialweather.utils.NetworkUtils;

/**
 * Created by Yehyun Ryu on 7/26/2017.
 */

public class WeatherSyncTask {

    //performs network call on each row in the database and update it with new weather data
    synchronized static void syncWeather(Context context) {
        try {
            //query for all rows
            Cursor cursor = context.getContentResolver().query(
                    WeatherEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            for(int i = 0; i < cursor.getCount(); i ++) {
                cursor.moveToPosition(i);
                int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                int indexLocation = cursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_LOCATION);

                int id = cursor.getInt(indexId);
                String location = cursor.getString(indexLocation);

                //update row
                ContentValues contentValues = NetworkUtils.fetchWeather(location);
                if(contentValues != null) {
                    context.getContentResolver().update(
                            ContentUris.withAppendedId(WeatherEntry.CONTENT_URI, id),
                            contentValues,
                            null,
                            null
                    );
                }
            }
            //TODO: display notification
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
