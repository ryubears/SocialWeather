package com.example.android.socialweather;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.socialweather.data.WeatherContract;
import com.example.android.socialweather.data.WeatherContract.WeatherEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForecastActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.forecast_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.friend_recycler_view) RecyclerView mFriendRecyclerView;
    @BindView(R.id.forecast_recycler_view) RecyclerView mForecastRecyclerView;

    private static final int FORECAST_LOADER_ID = 45;

    private ActionBarDrawerToggle mDrawerToggle;

    private FriendAdapter mFriendAdapter;
    private ForecastAdapter mForecastAdapter;

    private boolean mIsFacebook;

    private int mId = -1;
    private String mLocationName;
    private String[] mFriendNames;
    private String[] mFriendProfiles;
    private String[] mWeatherTimes;
    private String[] mWeatherIds;
    private String[] mWeatherDescriptions;
    private String[] mWeatherMinTemps;
    private String[] mWeatherMaxTemps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        //bind views with butterknife
        ButterKnife.bind(this);

        //set the up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //set drawer layout toggle
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.friend_drawer_open, R.string.friend_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //get data from previous activity
        Intent intent = getIntent();
        if(intent != null) {
            //get row id passed in from main activity
            mId = intent.getIntExtra(WeatherEntry._ID, -1);

            //get how user logged in
            mIsFacebook = intent.getBooleanExtra(getString(R.string.is_facebook_key), false);
        }

        //set layout manager for friend recycler view
        LinearLayoutManager friendLayoutManager = new LinearLayoutManager(this);
        mFriendRecyclerView.setLayoutManager(friendLayoutManager);

        //set adapter to friend recycler view
        mFriendAdapter = new FriendAdapter(mIsFacebook, null, null, null);
        mFriendRecyclerView.setAdapter(mFriendAdapter);

        //set layout manager for forecast recycler view
        LinearLayoutManager forecastLayoutManager = new LinearLayoutManager(this);
        mForecastRecyclerView.setLayoutManager(forecastLayoutManager);

        //set adapter to forecast recycler view
        mForecastAdapter = new ForecastAdapter(mIsFacebook, -1, null, null, null, null, null);
        mForecastRecyclerView.setAdapter(mForecastAdapter);

        //set recycler view to fixed size for better performance
        mForecastRecyclerView.setHasFixedSize(true);

        if(intent != null) {
            //initialize loader
            getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case FORECAST_LOADER_ID:
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
        //get data from cursor and attach it to adapter
        data.moveToFirst();

        int indexLocationName = data.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
        int indexFriendNames = data.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
        int indexFriendProfiles = -1;
        if(mIsFacebook) {
            indexFriendProfiles = data.getColumnIndex(WeatherEntry.COLUMN_FRIEND_PICTURES);
        }
        int indexWeatherTimes = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_TIMES);
        int indexWeatherIds = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);
        int indexWeatherDescriptions = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS);
        int indexWeatherMinTemps = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_MIN_TEMPS);
        int indexWeatherMaxTemps = data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_MAX_TEMPS);

        mLocationName = data.getString(indexLocationName);
        mFriendNames = data.getString(indexFriendNames).split(getString(R.string.delimiter));
        if(indexFriendProfiles != -1) {
            mFriendProfiles = data.getString(indexFriendProfiles).split(getString(R.string.delimiter));
        }
        mWeatherTimes = data.getString(indexWeatherTimes).split(getString(R.string.delimiter));
        mWeatherIds = data.getString(indexWeatherIds).split(getString(R.string.delimiter));
        mWeatherDescriptions = data.getString(indexWeatherDescriptions).split(getString(R.string.delimiter));
        mWeatherMinTemps = data.getString(indexWeatherMinTemps).split(getString(R.string.delimiter));
        mWeatherMaxTemps = data.getString(indexWeatherMaxTemps).split(getString(R.string.delimiter));

        mFriendAdapter.swapData(mLocationName, mFriendNames, mFriendProfiles);
        mForecastAdapter.swapData(mId, mWeatherTimes, mWeatherIds, mWeatherDescriptions, mWeatherMinTemps, mWeatherMaxTemps);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing here since cursor is not passes onto any external class
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.forecast_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.forecast_friends_menu:
                if(!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
