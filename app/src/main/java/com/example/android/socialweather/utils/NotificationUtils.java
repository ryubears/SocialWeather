package com.example.android.socialweather.utils;

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

import com.example.android.socialweather.LoginActivity;
import com.example.android.socialweather.R;
import com.example.android.socialweather.data.WeatherPreferences;

import java.util.ArrayList;

/**
 * Created by Yehyun Ryu on 7/27/2017.
 */

public class NotificationUtils {
    //notification id to uniquely identify notification
    private static final int UPDATE_NOTIFICATION_ID = 37;
    private static final int RAIN_NOTIFICATION_ID = 38;
    private static final int SNOW_NOTIFICATION_ID = 39;
    private static final int EXTREME_NOTIFICATION_ID = 40;

    //TODO: Group all notify methods into one
    //notifies weather updates
    public static void notifyUpdateWeather(Context context) {
        //title and text
        String notificationTitle = context.getString(R.string.app_name);
        String notificationText = "New Weather Available!";

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
            notificationText = rainFriends.get(0) + " is experiencing rain.";
        } else if(rainFriends.size() == 2){
            notificationText = rainFriends.get(0) + " and " + rainFriends.get(1) + " are experiencing rain.";
        } else {
            notificationText = rainFriends.get(0) + ", " + rainFriends.get(1) + ", and " + (rainFriends.size() - 2) + " others are experiencing rain.";
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
            notificationText = snowFriends.get(0) + " is experiencing snow.";
        } else if(snowFriends.size() == 2){
            notificationText = snowFriends.get(0) + " and " + snowFriends.get(1) + " are experiencing snow.";
        } else {
            notificationText = snowFriends.get(0) + ", " + snowFriends.get(1) + ", and " + (snowFriends.size() - 2) + " others are experiencing snow.";
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
            notificationText = extremeFriends.get(0) + " is experiencing extreme weather.";
        } else if(extremeFriends.size() == 2){
            notificationText = extremeFriends.get(0) + " and " + extremeFriends.get(1) + " are experiencing extreme weather.";
        } else {
            notificationText = extremeFriends.get(0) + ", " + extremeFriends.get(1) + ", and " + (extremeFriends.size() - 2) + " others are experiencing extreme weather.";
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
