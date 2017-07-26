package com.example.android.socialweather.sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Yehyun Ryu on 7/26/2017.
 */

public class WeatherSyncUtils {
    private static final String LOG_TAG = WeatherSyncUtils.class.getSimpleName();

    //constants for sync interval and flex time
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_HOURS / 3;

    //job tag
    private static final String WEATHER_SYNC_TAG = "weather_sync";

    //schedules job
    private static void scheduleFirebaseJobDispatcherSync(final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //job
        Job syncWeatherJob = dispatcher.newJobBuilder()
                //service used to sync weather data
                .setService(WeatherFirebaseJobService.class)
                //unique tag to identify job
                .setTag(WEATHER_SYNC_TAG)
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

    synchronized public static void initialize(final Context context) {
        Log.d(LOG_TAG, "Initialize Weather Data");
        //schedule and immediately sync weather data
        scheduleFirebaseJobDispatcherSync(context);
        startImmediateSync(context);
    }

    //immediately calls intent service that makes weather network requests
    private static void startImmediateSync(final Context context) {
        Intent intentSyncImmediately = new Intent(context, WeatherSyncIntentService.class);
        context.startService(intentSyncImmediately);
    }
}
