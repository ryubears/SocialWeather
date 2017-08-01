package com.example.android.socialweather;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.socialweather.data.WeatherContract;
import com.example.android.socialweather.data.WeatherContract.WeatherEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForecastActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.forecast_recycler_view) RecyclerView mForecastRecyclerView;

    private static final int FORECAST_LOADER_ID = 45;

    private ForecastAdapter mAdapter;

    private int mId;
    private String[] mWeatherTimes;
    private String[] mWeatherIds;
    private String[] mWeatherDescriptions;
    private String[] mWeatherMinTemps;
    private String[] mWeatherMaxTemps;
    private String[] mWeatherPressures;
    private String[] mWeatherHumidities;
    private String[] mWeatherWindSpeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        //bind views with butterknife
        ButterKnife.bind(this);

        //set the up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //set layout manager for recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mForecastRecyclerView.setLayoutManager(layoutManager);

        //set adapter to recycler view
        mAdapter = new ForecastAdapter(-1, null, null, null, null, null);
        mForecastRecyclerView.setAdapter(mAdapter);

        //set recycler view to fixed size for better performance
        mForecastRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        if(intent != null) {
            //get row id passed in from main activity
            mId = intent.getIntExtra(WeatherEntry._ID, -1);

            //initialize loader
            getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case FORECAST_LOADER_ID:
                return new CursorLoader(
                        this,
                        ContentUris.withAppendedId(WeatherEntry.CONTENT_URI, mId),
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
        //get data from cursor and attach it to adapter
        data.moveToFirst();

        int indexWeatherTimes = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES);
        int indexWeatherIds = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);
        int indexWeatherDescriptions = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS);
        int indexWeatherMinTemps = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS);
        int indexWeatherMaxTemps = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS);

        mWeatherTimes = data.getString(indexWeatherTimes).split(getString(R.string.delimiter));
        mWeatherIds = data.getString(indexWeatherIds).split(getString(R.string.delimiter));
        mWeatherDescriptions = data.getString(indexWeatherDescriptions).split(getString(R.string.delimiter));
        mWeatherMinTemps = data.getString(indexWeatherMinTemps).split(getString(R.string.delimiter));
        mWeatherMaxTemps = data.getString(indexWeatherMaxTemps).split(getString(R.string.delimiter));

        mAdapter.swapCursor(mId, mWeatherTimes, mWeatherIds, mWeatherDescriptions, mWeatherMinTemps, mWeatherMaxTemps);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing here since cursor is not passes onto any external class
    }

}
