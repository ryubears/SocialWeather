package com.example.android.socialweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.socialweather.utils.WeatherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yehyun Ryu on 7/31/2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    //attributes of forecast
    private String[] mWeatherTimes;
    private String[] mWeatherIds;
    private String[] mWeatherDescriptions;
    private String[] mWeatherMinTemps;
    private String[] mWeatherMaxTemps;
    private String[] mWeatherPressures;
    private String[] mWeatherHumidities;
    private String[] mWeatherWindSpeeds;

    public ForecastAdapter(String[] weatherTimes, String[] weatherIds, String[] weatherDescriptions, String[] minTemps, String[] maxTemps, String[] pressures, String[] humidities, String[] windspeeds) {
        mWeatherTimes = weatherTimes;
        mWeatherIds = weatherIds;
        mWeatherDescriptions = weatherDescriptions;
        mWeatherMinTemps = minTemps;
        mWeatherMaxTemps = maxTemps;
        mWeatherPressures = pressures;
        mWeatherHumidities = humidities;
        mWeatherWindSpeeds = windspeeds;
    }

    @Override
    public ForecastAdapter.ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        int layoutId = R.layout.forecast_item;
        boolean attachToParentImmediately = false;

        //inflate view and create view holder
        View view = layoutInflater.inflate(layoutId, parent, attachToParentImmediately);
        ForecastViewHolder viewHolder = new ForecastViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ForecastAdapter.ForecastViewHolder holder, int position) {
        //bind data
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mWeatherTimes == null) {
            return 0;
        }

        return mWeatherTimes.length;
    }

    public void swapCursor(String[] weatherTimes, String[] weatherIds, String[] weatherDescriptions, String[] minTemps, String[] maxTemps, String[] pressures, String[] humidities, String[] windspeeds) {
        mWeatherTimes = weatherTimes;
        mWeatherIds = weatherIds;
        mWeatherDescriptions = weatherDescriptions;
        mWeatherMinTemps = minTemps;
        mWeatherMaxTemps = maxTemps;
        mWeatherPressures = pressures;
        mWeatherHumidities = humidities;
        mWeatherWindSpeeds = windspeeds;

        if(mWeatherTimes != null && mWeatherTimes.length != 0) {
            notifyDataSetChanged();
        }
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.forecast_item_icon) ImageView mIconImageView;
        @BindView(R.id.forecast_item_date) TextView mDateTextView;
        @BindView(R.id.forecast_item_description) TextView mDescriptionTextView;
        @BindView(R.id.forecast_item_min_temp) TextView mMinTempTextView;
        @BindView(R.id.forecast_item_max_temp) TextView mMaxTempTextView;

        public ForecastViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            //set weather icon
            int weatherId = Integer.valueOf(mWeatherIds[position]);
            int icon = WeatherUtils.getColorWeatherIcon(weatherId);
            mIconImageView.setImageResource(icon);

            //set weather date
            long weatherTime = Long.valueOf(mWeatherTimes[position]);
            String timeString = WeatherUtils.formatDate(weatherTime);
            mDateTextView.setText(timeString);

            //set weather description
            String description = mWeatherDescriptions[position];
            String weatherDescription = WeatherUtils.formatDescription(description);
            mDescriptionTextView.setText(weatherDescription);

            //set min temp
            double minTemp = Double.valueOf(mWeatherMinTemps[position]);
            String minTempString = WeatherUtils.formatTemperature(itemView.getContext(), minTemp);
            mMinTempTextView.setText(minTempString);

            //set max temp
            double maxTemp = Double.valueOf(mWeatherMaxTemps[position]);
            String maxTempString = WeatherUtils.formatTemperature(itemView.getContext(), maxTemp);
            mMaxTempTextView.setText(maxTempString);
        }
    }
}
