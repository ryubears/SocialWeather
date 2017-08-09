package com.example.android.socialweather.analytics;

import android.app.Application;

import com.example.android.socialweather.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

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

public class AnalyticsApplication extends Application {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        sAnalytics = GoogleAnalytics.getInstance(this);
    }

    synchronized public Tracker getDefaultTracker() {
        if(sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}
