package com.yehyunryu.android.socialweather;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.yehyunryu.android.socialweather.data.WeatherContract.WeatherEntry;
import com.yehyunryu.android.socialweather.utils.WeatherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

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

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.details_description) TextView mDescriptionTextView;
    @BindView(R.id.details_location) TextView mLocationTextView;
    @BindView(R.id.details_icon) ImageView mIconImageView;
    @BindView(R.id.details_temperature) TextView mTemperatureTextView;
    @BindView(R.id.details_date) TextView mDateTextView;
    @BindView(R.id.details_pressure_value) TextView mPressureTextView;
    @BindView(R.id.details_humidity_value) TextView mHumidityTextView;
    @BindView(R.id.details_wind_value) TextView mWindTextView;

    private static final int DETAILS_LOADER_ID = 55;

    private boolean mIsFacebook;

    private int mId;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //set up button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //bind views
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null) {
            //get row id and position
            mId = intent.getIntExtra(WeatherEntry._ID, -1);
            mPosition = intent.getIntExtra(getString(R.string.forecast_position_key), -1);
            mIsFacebook = intent.getBooleanExtra(getString(R.string.is_facebook_key), false);

            getSupportLoaderManager().initLoader(DETAILS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case DETAILS_LOADER_ID:
                if(mIsFacebook) {
                    return new CursorLoader(
                            this,
                            ContentUris.withAppendedId(WeatherEntry.FACEBOOK_CONTENT_URI, mId),
                            null,
                            null,
                            null,
                            null
                    );
                } else {
                    return new CursorLoader(
                            this,
                            ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, mId),
                            null,
                            null,
                            null,
                            null
                    );
                }
            default:
                throw new RuntimeException("This loader is not yet implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //get data from cursor
        data.moveToFirst();

        int indexWeatherDescriptions = data.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS);
        int indexLocationName = data.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
        int indexWeatherId = data.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);
        int indexMinTemps = data.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS);
        int indexMaxTemps = data.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS);
        int indexWeatherTimes = data.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES);
        int indexPressures = data.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_PRESSURES);
        int indexHumidities = data.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_HUMIDITIES);
        int indexWindSpeeds = data.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_WIND_SPEEDS);

        String[] descriptions = data.getString(indexWeatherDescriptions).split(getString(R.string.delimiter));
        String locationName = data.getString(indexLocationName);
        String[] weatherIds = data.getString(indexWeatherId).split(getString(R.string.delimiter));
        String[] minTemps = data.getString(indexMinTemps).split(getString(R.string.delimiter));
        String[] maxTemps = data.getString(indexMaxTemps).split(getString(R.string.delimiter));
        String[] weatherTimes = data.getString(indexWeatherTimes).split(getString(R.string.delimiter));
        String[] pressures = data.getString(indexPressures).split(getString(R.string.delimiter));
        String[] humidities = data.getString(indexHumidities).split(getString(R.string.delimiter));
        String[] windSpeeds = data.getString(indexWindSpeeds).split(getString(R.string.delimiter));

        //set description
        String description = WeatherUtils.formatDescription(descriptions[mPosition]);
        mDescriptionTextView.setText(description);

        //set location
        mLocationTextView.setText(locationName);

        //set icon
        int weatherId = Integer.valueOf(weatherIds[mPosition]);
        int icon = WeatherUtils.getWhiteWeatherIcon(weatherId);
        mIconImageView.setImageResource(icon);

        //set temperature
        double averageTemp = (Double.valueOf(minTemps[mPosition]) + Double.valueOf(maxTemps[mPosition])) / 2.0;
        String tempString = WeatherUtils.formatTemperature(this, averageTemp);
        mTemperatureTextView.setText(tempString);

        //set time
        long time = Long.valueOf(weatherTimes[mPosition]);
        String dateString = WeatherUtils.formatDate(this, time);
        mDateTextView.setText(dateString);

        //set pressure
        double pressure = Double.valueOf(pressures[mPosition]);
        String pressureString = WeatherUtils.formatPressure(this, pressure);
        mPressureTextView.setText(pressureString);

        //set humidity
        int humidity = Integer.valueOf(humidities[mPosition]);
        String humidityString = WeatherUtils.formatHumidity(this, humidity);
        mHumidityTextView.setText(humidityString);

        //set wind speed
        double windSpeed = Double.valueOf(windSpeeds[mPosition]);
        String windSpeedString = WeatherUtils.formatWindSpeed(this, windSpeed);
        mWindTextView.setText(windSpeedString);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing since cursor is not used anywhere else
    }
}
