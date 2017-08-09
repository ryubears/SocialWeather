package com.yehyunryu.android.socialweather.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.yehyunryu.android.socialweather.R;

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
