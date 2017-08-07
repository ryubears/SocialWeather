package com.example.android.socialweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.socialweather.ForecastActivity;
import com.example.android.socialweather.R;
import com.example.android.socialweather.data.WeatherContract;

import java.util.ArrayList;

/**
 * Created by Yehyun Ryu on 8/6/2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = WidgetProvider.class.getSimpleName();

    public static void updateWeatherWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, ArrayList<Integer> rowIds, ArrayList<String> profileUrls, ArrayList<String> friendNames, ArrayList<String> locationNames, ArrayList<String> weatherIds) {
        //update every social weather widget
        for(int appWidgetId : appWidgetIds) {
            updateWidgetListView(context, appWidgetManager, appWidgetId, rowIds, profileUrls, friendNames, locationNames, weatherIds);
        }
    }

    public static void updateWidgetListView(Context context, AppWidgetManager appWidgetManager, int appWidgetId, ArrayList<Integer> rowIds, ArrayList<String> profileUrls, ArrayList<String> friendNames, ArrayList<String> locationNames, ArrayList<String> weatherIds) {
        //create remote view and set service intent
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent widgetServiceIntent = new Intent(context, WidgetService.class);
        widgetServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        widgetServiceIntent.setData(Uri.parse(widgetServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        //attach intent with data
        widgetServiceIntent.putExtra(WeatherContract.WeatherEntry._ID, rowIds);
        widgetServiceIntent.putExtra(WeatherContract.WeatherEntry.COLUMN_LOCATION_NAME, locationNames);
        widgetServiceIntent.putExtra(WeatherContract.WeatherEntry.COLUMN_FRIEND_NAMES, friendNames);
        widgetServiceIntent.putExtra(WeatherContract.WeatherEntry.COLUMN_FRIEND_PICTURES, profileUrls);
        widgetServiceIntent.putExtra(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_IDS, weatherIds);

        //set click pending intent
        Intent clickIntent = new Intent(context, ForecastActivity.class);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_listview, clickPendingIntent);

        //set list view adapter and emptyview
        remoteViews.setRemoteAdapter(R.id.widget_listview, widgetServiceIntent);
        remoteViews.setEmptyView(R.id.widget_listview, R.id.widget_empty_view);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //called when widget is created and updated
        WidgetUpdateService.startActionUpdateWidget(context);
    }
}
