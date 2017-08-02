package com.example.android.socialweather.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Yehyun Ryu on 8/2/2017.
 */

//firebase job service that schedules weather update on facebook weather table
public class FacebookWeatherFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchWeatherTask;

    //this method is run on the applications's main thread, so need to use async task to make network call
    @Override
    public boolean onStartJob(final JobParameters job) {

        mFetchWeatherTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                WeatherSyncTask.syncWeather(context, true);
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mFetchWeatherTask.execute();
        return true;
    }

    //called when scheduling was interrupted most likely due to runtime constraints
    @Override
    public boolean onStopJob(JobParameters job) {
        if(mFetchWeatherTask != null) {
            mFetchWeatherTask.cancel(true);
        }
        return true;
    }
}
