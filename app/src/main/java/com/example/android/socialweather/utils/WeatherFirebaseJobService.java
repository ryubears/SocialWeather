package com.example.android.socialweather.utils;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Yehyun Ryu on 7/24/2017.
 */

//job service for scheduling weather sync over a certain interval
public class WeatherFirebaseJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
