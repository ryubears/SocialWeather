package com.yehyunryu.android.socialweather.sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yehyunryu.android.socialweather.R;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

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

public class WeatherSyncUtils {
    private static final String LOG_TAG = WeatherSyncUtils.class.getSimpleName();

    //constants for sync interval and flex time
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_HOURS / 3;

    //job tag
    private static final String FACEBOOK_WEATHER_SYNC_TAG = "facebook_weather_sync";
    private static final String ACCOUNT_KIT_WEATHER_SYNC_TAG = "account_kit_weather_sync";

    //schedules facebook weather job
    private static void scheduleFacebookFirebaseJobDispatcherSync(final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //job
        Job syncWeatherJob = dispatcher.newJobBuilder()
                //service used to sync weather data
                .setService(FacebookWeatherFirebaseJobService.class)
                //unique tag to identify job
                .setTag(FACEBOOK_WEATHER_SYNC_TAG)
                //network constrains on which this job should run
                .setConstraints(Constraint.ON_ANY_NETWORK)
                //how long job should persist
                .setLifetime(Lifetime.FOREVER)
                //needs to be repeated to stay up to date
                .setRecurring(true)
                //synced every 3 to 4 hours
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                //replace existing job
                .setReplaceCurrent(true)
                .build();

        //schedule
        dispatcher.schedule(syncWeatherJob);
        Log.d(LOG_TAG, "Job Scheduled");
    }

    //schedules facebook weather job
    private static void scheduleAccountKitFirebaseJobDispatcherSync(final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //job
        Job syncWeatherJob = dispatcher.newJobBuilder()
                //service used to sync weather data
                .setService(AccountKitWeatherFirebaseJobService.class)
                //unique tag to identify job
                .setTag(ACCOUNT_KIT_WEATHER_SYNC_TAG)
                //network constrains on which this job should run
                .setConstraints(Constraint.ON_ANY_NETWORK)
                //how long job should persist
                .setLifetime(Lifetime.FOREVER)
                //needs to be repeated to stay up to date
                .setRecurring(true)
                //synced every 3 to 4 hours
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                //replace existing job
                .setReplaceCurrent(true)
                .build();

        //schedule
        dispatcher.schedule(syncWeatherJob);
        Log.d(LOG_TAG, "Job Scheduled");
    }

    synchronized public static void initialize(final Context context, boolean isFacebook) {
        Log.d(LOG_TAG, "Initialize Weather Data");

        //schedule and immediately sync weather data
        if(isFacebook) {
            scheduleFacebookFirebaseJobDispatcherSync(context);
        } else {
            scheduleAccountKitFirebaseJobDispatcherSync(context);
        }

        startImmediateSync(context, isFacebook);
    }

    //immediately calls intent service that makes weather network requests
    private static void startImmediateSync(final Context context, boolean isFacebook) {
        Intent intentSyncImmediately = new Intent(context, WeatherSyncIntentService.class);
        intentSyncImmediately.putExtra(context.getString(R.string.is_facebook_key), isFacebook);
        context.startService(intentSyncImmediately);
    }
}
