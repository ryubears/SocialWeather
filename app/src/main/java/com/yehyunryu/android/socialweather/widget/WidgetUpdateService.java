package com.yehyunryu.android.socialweather.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.yehyunryu.android.socialweather.R;
import com.yehyunryu.android.socialweather.data.WeatherContract;
import com.yehyunryu.android.socialweather.data.WeatherPreferences;

import java.util.ArrayList;

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

public class WidgetUpdateService extends IntentService {
    private static final String LOG_TAG = WidgetUpdateService.class.getSimpleName();
    public static final String ACTION_UPDATE_WIDGET = "com.example.android.socialweather.action_update";

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    public static void startActionUpdateWidget(Context context) {
        //start update service
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //check intent action
        if(intent != null) {
            final String action = intent.getAction();
            if(action.equals(ACTION_UPDATE_WIDGET)) {
                handleActionUpdateWidgets();
            }
        }
    }

    private void handleActionUpdateWidgets() {
        //query for data
        Cursor cursor;
        if(WeatherPreferences.getLoginType(getApplicationContext())) {
            cursor = getContentResolver().query(
                    WeatherContract.WeatherEntry.FACEBOOK_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        } else {
            cursor = getContentResolver().query(
                    WeatherContract.WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        int indexId = cursor.getColumnIndex(WeatherContract.WeatherEntry._ID);
        int indexLocationName = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOCATION_NAME);
        int indexFriendNames = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FRIEND_NAMES);
        int indexWeatherIds = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);

        ArrayList<Integer> rowIds = new ArrayList<>();
        ArrayList<String> locationNames = new ArrayList<>();
        ArrayList<String> friendNames = new ArrayList<>();
        ArrayList<String> friendProfiles = new ArrayList<>();
        ArrayList<String> weatherIds = new ArrayList<>();

        //convert location data to friend data
        for(int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);

            int cursorRowId = cursor.getInt(indexId);
            String cursorLocationName = cursor.getString(indexLocationName);
            String cursorFriendNames = cursor.getString(indexFriendNames);
            String cursorWeatherIds = cursor.getString(indexWeatherIds);

            String cursorFriendProfiles = "";
            if(WeatherPreferences.getLoginType(getApplicationContext())) {
                int indexFriendProfiles = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_FRIEND_PICTURES);
                cursorFriendProfiles = cursor.getString(indexFriendProfiles);
            }

            String[] friendNameArray = cursorFriendNames.split(getString(R.string.delimiter));
            String[] friendProfileArray = cursorFriendProfiles.split(getString(R.string.delimiter));
            String[] weatherIdProfile = cursorWeatherIds.split(getString(R.string.delimiter));
            for(int x = 0; x < friendNameArray.length; x++) {
                rowIds.add(cursorRowId);
                locationNames.add(cursorLocationName);
                friendNames.add(friendNameArray[x]);
                if(WeatherPreferences.getLoginType(getApplicationContext())) {
                    friendProfiles.add(friendProfileArray[x]);
                } else {
                    friendProfiles.add(null);
                }
                weatherIds.add(weatherIdProfile[0]);
            }
        }

        //get all app widget ids
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));

        //notify data change and update social weather widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
        WidgetProvider.updateWeatherWidgets(this, appWidgetManager, appWidgetIds, rowIds, friendProfiles, friendNames, locationNames, weatherIds);
    }
}
