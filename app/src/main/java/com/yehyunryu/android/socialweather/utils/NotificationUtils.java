package com.yehyunryu.android.socialweather.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.yehyunryu.android.socialweather.LoginActivity;
import com.yehyunryu.android.socialweather.R;
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

public class NotificationUtils {
    //notification id to uniquely identify notification
    private static final int UPDATE_NOTIFICATION_ID = 37;
    private static final int RAIN_NOTIFICATION_ID = 38;
    private static final int SNOW_NOTIFICATION_ID = 39;
    private static final int EXTREME_NOTIFICATION_ID = 40;

    //notifies weather updates
    public static void notifyUpdateWeather(Context context) {
        //title and text
        String notificationTitle = context.getString(R.string.app_name);
        String notificationText = context.getString(R.string.notification_update_text);

        //icon to be displayed at the top
        int smallIconId = R.mipmap.ic_launcher;

        //build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(smallIconId)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);

        //builds pending intent that opens LoginActivity once notification is clicked
        Intent intent = new Intent(context, LoginActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        //notify
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(UPDATE_NOTIFICATION_ID, notificationBuilder.build());

        //updates notification time
        String updateNotificationTimeKey = context.getString(R.string.pref_update_notification_time_key);
        WeatherPreferences.saveNotificationTime(context, System.currentTimeMillis(), updateNotificationTimeKey);
    }

    //notifies rain weather
    public static void notifyRainWeather(Context context, ArrayList<String> rainFriends) {
        //title and text
        String notificationTitle = context.getString(R.string.app_name);
        String notificationText = "";
        if(rainFriends.size() == 1) {
            notificationText = rainFriends.get(0) + context.getString(R.string.notification_rain_text_single);
        } else if(rainFriends.size() == 2){
            notificationText = rainFriends.get(0) + context.getString(R.string.notification_and) + rainFriends.get(1) + context.getString(R.string.notification_rain_text_two);
        } else {
            notificationText = rainFriends.get(0) + context.getString(R.string.notification_comma) + rainFriends.get(1) + context.getString(R.string.notification_and_comma) + (rainFriends.size() - 2) + context.getString(R.string.notification_rain_text_three);
        }

        //large icon to be displayed at the right
        Resources resources = context.getResources();
        int largeIconId = R.drawable.rain_color;
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, largeIconId);

        //small icon to be displayed at the top
        int smallIconId = R.mipmap.ic_launcher;

        //builds notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIconId)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);

        //builds pending intent to open LoginActivity once notification is clicked
        Intent intent = new Intent(context, LoginActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        //notify
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(RAIN_NOTIFICATION_ID, notificationBuilder.build());

        //updates notification time
        String rainNotificationTimeKey = context.getString(R.string.pref_rain_notification_time_key);
        WeatherPreferences.saveNotificationTime(context, System.currentTimeMillis(), rainNotificationTimeKey);
    }

    //notify snow weather
    public static void notifySnowWeather(Context context, ArrayList<String> snowFriends) {
        //title and text
        String notificationTitle = context.getString(R.string.app_name);
        String notificationText = "";
        if(snowFriends.size() == 1) {
            notificationText = snowFriends.get(0) + context.getString(R.string.notification_snow_text_single);
        } else if(snowFriends.size() == 2){
            notificationText = snowFriends.get(0) + context.getString(R.string.notification_and) + snowFriends.get(1) + context.getString(R.string.notification_snow_text_two);
        } else {
            notificationText = snowFriends.get(0) + context.getString(R.string.notification_comma) + snowFriends.get(1) + context.getString(R.string.notification_and_comma) + (snowFriends.size() - 2) + context.getString(R.string.notification_snow_text_three);
        }

        //large icon to be displayed at the right
        Resources resources = context.getResources();
        int largeIconId = R.drawable.snow_color;
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, largeIconId);

        //small icon to be displayed at the top
        int smallIconId = R.mipmap.ic_launcher;

        //build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIconId)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);

        //builds pending intent that opens LoginActivity once notification is clicked
        Intent intent = new Intent(context, LoginActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        //notify
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(SNOW_NOTIFICATION_ID, notificationBuilder.build());

        //updates notification time
        String snowNotificationTimeKey = context.getString(R.string.pref_snow_notification_time_key);
        WeatherPreferences.saveNotificationTime(context, System.currentTimeMillis(), snowNotificationTimeKey);
    }

    //notify extreme weather
    public static void notifyExtremeWeather(Context context, ArrayList<String> extremeFriends) {
        //title and text
        String notificationTitle = context.getString(R.string.app_name);
        String notificationText = "";
        if(extremeFriends.size() == 1) {
            notificationText = extremeFriends.get(0) + context.getString(R.string.notification_extreme_text_single);
        } else if(extremeFriends.size() == 2){
            notificationText = extremeFriends.get(0) + context.getString(R.string.notification_and) + extremeFriends.get(1) + context.getString(R.string.notification_extreme_text_two);
        } else {
            notificationText = extremeFriends.get(0) + context.getString(R.string.notification_comma) + extremeFriends.get(1) + context.getString(R.string.notification_and_comma) + (extremeFriends.size() - 2) + context.getString(R.string.notification_extreme_text_three);
        }

        //large icon to be displayed at the right
        Resources resources = context.getResources();
        int largeIconId = R.drawable.storm_color;
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, largeIconId);

        //small icon to be displayed at the top
        int smallIconId = R.mipmap.ic_launcher;

        //builds notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIconId)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);

        //pending intent that opens LoginActivity once notification is clicked
        Intent intent = new Intent(context, LoginActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        //notify
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(EXTREME_NOTIFICATION_ID, notificationBuilder.build());

        //updates notification time
        String extremeNotificationTimeKey = context.getString(R.string.pref_extreme_notification_time_key);
        WeatherPreferences.saveNotificationTime(context, System.currentTimeMillis(), extremeNotificationTimeKey);
    }
}
