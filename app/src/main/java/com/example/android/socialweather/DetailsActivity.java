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
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.details_profile) ImageView mProfileImageView;
    @BindView(R.id.details_name) TextView mNameTextView;
    @BindView(R.id.details_location) TextView mLocationTextView;
    @BindView(R.id.details_weather_icon) ImageView mIconImageView;
    @BindView(R.id.details_current_temperature) TextView mCurrentTempTextView;
    @BindView(R.id.details_minmax_temperature) TextView mMinMaxTempTextView;
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
        int indexProfilePic = data.getColumnIndex(WeatherEntry.COLUMN_PERSON_PROFILE);
        int indexName = data.getColumnIndex(WeatherEntry.COLUMN_PERSON_NAME);
        int indexLocation = data.getColumnIndex(WeatherEntry.COLUMN_PERSON_LOCATION);
        int indexWeatherId = data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
        int indexCurrentTemp = data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_CURRENT_TEMP);
        int indexMinTemp = data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_MIN_TEMP);
        int indexMaxTemp = data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_MAX_TEMP);
        int indexPressure = data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_PRESSURE);
        int indexHumidity = data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_HUMIDITY);
        int indexWindSpeed = data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_WIND_SPEED);

        mProfilePic = data.getString(indexProfilePic);
        mName = data.getString(indexName);
        mLocation = data.getString(indexLocation);
        mWeatherId = data.getInt(indexWeatherId);
        mCurrentTemp = data.getDouble(indexCurrentTemp);
        mMinTemp = data.getDouble(indexMinTemp);
        mMaxTemp = data.getDouble(indexMaxTemp);
        mPressure = data.getDouble(indexPressure);
        mHumidity = data.getInt(indexHumidity);
        mWindSpeed = data.getDouble(indexWindSpeed);

        //attach data to views
        if(mProfilePic.equals(getString(R.string.picture_empty))) {
            mProfileImageView.setImageResource(R.drawable.profile_color);
        } else {
            //transform profile picture in a circular frame
            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(100)
                    .oval(false)
                    .build();

            //load profile image
            Picasso.with(this)
                    .load(mProfilePic)
                    .transform(transformation)
                    .into(mProfileImageView);
        }

        mNameTextView.setText(mName);

        if(mLocation.equals(getString(R.string.location_empty))) {
            mLocationTextView.setText(getString(R.string.location_default));
        } else {
            mLocationTextView.setText(mLocation);
        }

        int icon = WeatherUtils.getColorWeatherIcon(mWeatherId);
        mIconImageView.setImageResource(icon);

        //TODO: handle empty cases
        mCurrentTempTextView.setText(String.valueOf(mCurrentTemp));
        String minMaxString = String.valueOf(mMinTemp) + " / " + String.valueOf(mMaxTemp);
        mMinMaxTempTextView.setText(minMaxString);
        mPressureTextView.setText(String.valueOf(mPressure));
        mHumidityTextView.setText(String.valueOf(mHumidity));
        mWindSpeedTextView.setText(String.valueOf(mWindSpeed));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing, since there are no external references to a cursor
    }
}
