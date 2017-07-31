package com.example.android.socialweather;

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

import com.example.android.socialweather.data.WeatherContract.WeatherEntry;
import com.example.android.socialweather.utils.WeatherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.details_location_image) ImageView mLocationImageView;
    @BindView(R.id.details_location_name) TextView mLocationTextView;
    @BindView(R.id.details_weather_icon) ImageView mIconImageView;
    @BindView(R.id.details_weather_description) TextView mDescriptionTextView;
    @BindView(R.id.details_current_temperature) TextView mCurrentTempTextView;
    @BindView(R.id.details_pressure_value) TextView mPressureTextView;
    @BindView(R.id.details_humidity_value) TextView mHumidityTextView;
    @BindView(R.id.details_wind_value) TextView mWindSpeedTextView;

    //loader id to identify loader
    private static final int DETAILS_LOADER_ID = 55;

    //weather and person data
    private int mId;
    private String mProfilePic;
    private String mName;
    private String mLocation;
    private int mWeatherId;
    private String mWeatherDescription;
    private double mCurrentTemp;
    private double mMinTemp;
    private double mMaxTemp;
    private double mPressure;
    private int mHumidity;
    private double mWindSpeed;

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
            //get id to query data
            mId = intent.getIntExtra(WeatherEntry._ID, -1);

            //initialize loader
            getSupportLoaderManager().initLoader(DETAILS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case DETAILS_LOADER_ID:
                return new CursorLoader(
                        this,
                        ContentUris.withAppendedId(WeatherEntry.CONTENT_URI, mId), //query with id
                        null,
                        null,
                        null,
                        null
                );
            default:
                throw new RuntimeException("This loader is not yet implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();

        //extract data
        int indexProfilePic = data.getColumnIndex(WeatherEntry.COLUMN_FRIEND_PICTURES);
        int indexName = data.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
        int indexLocation = data.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
        int indexWeatherId = data.getColumnIndex(WeatherEntry.COLUMN_CURRENT_WEATHER_ID);
        int indexWeatherDescription = data.getColumnIndex(WeatherEntry.COLUMN_CURRENT_WEATHER_DESCRIPTION);
        int indexCurrentTemp = data.getColumnIndex(WeatherEntry.COLUMN_CURRENT_WEATHER_TEMP);
        int indexPressure = data.getColumnIndex(WeatherEntry.COLUMN_CURRENT_WEATHER_PRESSURE);
        int indexHumidity = data.getColumnIndex(WeatherEntry.COLUMN_CURRENT_WEATHER_HUMIDITY);
        int indexWindSpeed = data.getColumnIndex(WeatherEntry.COLUMN_CURRENT_WEATHER_WIND_SPEED);

        mProfilePic = data.getString(indexProfilePic);
        mName = data.getString(indexName);
        mLocation = data.getString(indexLocation);
        mWeatherId = data.getInt(indexWeatherId);
        mWeatherDescription = data.getString(indexWeatherDescription);
        mCurrentTemp = data.getDouble(indexCurrentTemp);
        mPressure = data.getDouble(indexPressure);
        mHumidity = data.getInt(indexHumidity);
        mWindSpeed = data.getDouble(indexWindSpeed);


        //set location name
        if(mLocation.equals(getString(R.string.location_empty))) {
            //check empty location
            mLocationTextView.setText(getString(R.string.location_default));
        } else {
            mLocationTextView.setText(mLocation);
        }

        //set weather icon
        int icon = WeatherUtils.getColorWeatherIcon(mWeatherId); //handles empty cases
        mIconImageView.setImageResource(icon);

        //set weather description
        if(mWeatherDescription.equals(getString(R.string.description_empty))) {
            mDescriptionTextView.setText(getString(R.string.description_default));
        } else {
            //format and display description
            String formattedDescription = WeatherUtils.formatDescription(mWeatherDescription);
            mDescriptionTextView.setText(formattedDescription);
        }

        //set current temperature
        if(mCurrentTemp == -1) {
            //check empty temperature
            mCurrentTempTextView.setText("");
        } else {
            //format temperature and display
            String formattedCurrentTemp = WeatherUtils.formatTemperature(this, mCurrentTemp);
            mCurrentTempTextView.setText(formattedCurrentTemp);
        }

        //set pressure
        if(mPressure == -1) {
            //check empty pressure value
            mPressureTextView.setText("");
        } else {
            //format pressure and display
            String formattedPressure = WeatherUtils.formatPressure(this, mPressure);
            mPressureTextView.setText(formattedPressure);
        }

        //set humidity
        if(mHumidity == -1) {
            mHumidityTextView.setText("");
        } else {
            //format humidity and display
            String formattedHumidity = WeatherUtils.formatHumidity(this, mHumidity);
            mHumidityTextView.setText(formattedHumidity);
        }

        //set wind speed
        if (mWindSpeed == -1) {
            mWindSpeedTextView.setText("");
        } else {
            //format wind speed and display
            String formattedWindSpeed = WeatherUtils.formatWindSpeed(this, mWindSpeed);
            mWindSpeedTextView.setText(formattedWindSpeed);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing, since there are no external references to a cursor
    }
}
