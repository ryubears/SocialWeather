package com.example.android.socialweather.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.socialweather.R;
import com.example.android.socialweather.data.WeatherContract;
import com.example.android.socialweather.utils.WeatherUtils;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Yehyun Ryu on 8/6/2017.
 */

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Integer> mIds;
    private ArrayList<String> mLocationNames;
    private ArrayList<String> mFriendNames;
    private ArrayList<String> mProfileUrls;
    private ArrayList<String> mWeatherIds;

    private Context mContext;
    private int mAppWidgetId;

    public ListProvider(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        //get data
        mIds = intent.getIntegerArrayListExtra(WeatherContract.WeatherEntry._ID);
        mLocationNames = intent.getStringArrayListExtra(WeatherContract.WeatherEntry.COLUMN_LOCATION_NAME);
        mFriendNames = intent.getStringArrayListExtra(WeatherContract.WeatherEntry.COLUMN_FRIEND_NAMES);
        mProfileUrls = intent.getStringArrayListExtra(WeatherContract.WeatherEntry.COLUMN_FRIEND_PICTURES);
        mWeatherIds = intent.getStringArrayListExtra(WeatherContract.WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);
    }

    @Override
    public void onCreate() {
        //nothing
    }

    @Override
    public void onDataSetChanged() {
        //nothing
    }

    @Override
    public void onDestroy() {
        //nothing
    }

    @Override
    public int getCount() {
        if(mIds == null) {
            return 0;
        }

        return mIds.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        //set profile picture
        if(mProfileUrls.get(position) != null) {
            //transform profile picture in a circular frame
            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(50)
                    .oval(false)
                    .build();

            //get bitmap using picasso
            try {
                Bitmap profileBitmap = Picasso.with(mContext).load(mProfileUrls.get(position)).transform(transformation).get();
                row.setImageViewBitmap(R.id.widget_profile, profileBitmap);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        //set name and location
        row.setTextViewText(R.id.widget_name, mFriendNames.get(position));
        row.setTextViewText(R.id.widget_location, mLocationNames.get(position));

        //set weather icon
        int weatherId = Integer.valueOf(mWeatherIds.get(position));
        int weatherIcon = WeatherUtils.getColorWeatherIcon(weatherId);
        row.setImageViewResource(R.id.widget_weather_icon, weatherIcon);

        //return remote view
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
