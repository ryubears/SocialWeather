package com.example.android.socialweather;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.socialweather.data.WeatherContract;
import com.example.android.socialweather.utils.WeatherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yehyun Ryu on 7/31/2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    //whether user logged in with facebook
    private boolean mIsFacebook;

    //attributes of forecast
    private int mId;
    private String[] mWeatherTimes;
    private String[] mWeatherIds;
    private String[] mWeatherDescriptions;
    private String[] mWeatherMinTemps;
    private String[] mWeatherMaxTemps;

    public ForecastAdapter(boolean isFacebook, int id, String[] weatherTimes, String[] weatherIds, String[] weatherDescriptions, String[] minTemps, String[] maxTemps) {
        mIsFacebook = isFacebook;
        mId = id;
        mWeatherTimes = weatherTimes;
        mWeatherIds = weatherIds;
        mWeatherDescriptions = weatherDescriptions;
        mWeatherMinTemps = minTemps;
        mWeatherMaxTemps = maxTemps;
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

    public void swapData(int id, String[] weatherTimes, String[] weatherIds, String[] weatherDescriptions, String[] minTemps, String[] maxTemps) {
        mId = id;
        mWeatherTimes = weatherTimes;
        mWeatherIds = weatherIds;
        mWeatherDescriptions = weatherDescriptions;
        mWeatherMinTemps = minTemps;
        mWeatherMaxTemps = maxTemps;

        if(mWeatherTimes != null && mWeatherTimes.length != 0) {
            notifyDataSetChanged();
        }
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.forecast_item) ConstraintLayout mForecastItem;
        @BindView(R.id.forecast_item_icon) ImageView mIconImageView;
        @BindView(R.id.forecast_item_date) TextView mDateTextView;
        @BindView(R.id.forecast_item_description) TextView mDescriptionTextView;
        @BindView(R.id.forecast_item_min_temp) TextView mMinTempTextView;
        @BindView(R.id.forecast_item_max_temp) TextView mMaxTempTextView;

        private Context mContext;
        private int mPosition;

        public ForecastViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bind(int position) {
            mPosition = position;

            //set weather icon
            int weatherId = Integer.valueOf(mWeatherIds[position]);
            int icon = WeatherUtils.getColorWeatherIcon(weatherId);
            mIconImageView.setImageResource(icon);

            //set weather date
            long weatherTime = Long.valueOf(mWeatherTimes[position]);
            String timeString = WeatherUtils.formatDate(mContext, weatherTime);
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

        @OnClick(R.id.forecast_item)
        public void onClick() {
            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra(WeatherContract.WeatherEntry._ID, mId);
            intent.putExtra(mContext.getString(R.string.forecast_position_key), mPosition);
            intent.putExtra(mContext.getString(R.string.is_facebook_key), mIsFacebook);
            itemView.getContext().startActivity(intent);
        }
    }
}
