package com.example.android.socialweather.utils;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Yehyun Ryu on 7/24/2017.
 */

public class WeatherSyncIntentService extends IntentService {
    private static String LOG_TAG = WeatherSyncIntentService.class.getSimpleName();

    public WeatherSyncIntentService() {
        super("WeatherSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
