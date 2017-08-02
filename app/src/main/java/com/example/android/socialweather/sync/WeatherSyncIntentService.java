package com.example.android.socialweather.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.socialweather.R;

/**
 * Created by Yehyun Ryu on 7/26/2017.
 */

//a subclass handling asynchronous task for immediately syncing weather data
public class WeatherSyncIntentService extends IntentService {

    public WeatherSyncIntentService() {
        super("WeatherSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean isFacebook = intent.getBooleanExtra(getString(R.string.is_facebook_key), false);
        WeatherSyncTask.syncWeather(this, isFacebook);
    }
}
