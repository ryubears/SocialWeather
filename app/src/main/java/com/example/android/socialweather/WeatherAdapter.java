package com.example.android.socialweather;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.socialweather.utils.WeatherUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.android.socialweather.data.WeatherContract.WeatherEntry;

/**
 * Created by Yehyun Ryu on 7/14/2017.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private static String LOG_TAG = WeatherAdapter.class.getSimpleName();

    //cursor from home fragment loader
    private Cursor mCursor;

    public WeatherAdapter() {}

    @Override
    public WeatherAdapter.WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.weather_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParentImmediately = false;

        //inflate view using layout inflater
        View view = inflater.inflate(layoutId, parent, attachToParentImmediately);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherAdapter.WeatherViewHolder holder, int position) {
        //move cursor to current position and bind data with views
        mCursor.moveToPosition(position);
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if(mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    //method loader will use to update recycler view
    public void swapCursor(Cursor newCursor) {
        //swap with new cursor
        mCursor = newCursor;
        if(mCursor != null && mCursor.getCount() != 0) {
            //notify data change to make refresh adapter
            notifyDataSetChanged();
        }
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.weather_item_card_view) CardView mCardView;
        @BindView(R.id.weather_item_background) ImageView mBackgroundImageView;
        @BindView(R.id.weather_item_location_name) TextView mLocationTextView;
        @BindView(R.id.weather_item_temperature) TextView mTemperatureTextView;

        private int mId;
        private String mLocationName;
        private String mLocationPhoto;
        private String mNames;
        private String mProfilePics;
        private int mWeatherId;
        private double mTemperature;

        public WeatherViewHolder(View view) {
            super(view);

            //binds views using butterknife
            ButterKnife.bind(this, view);
        }

        private void bind() {
            //extract data from cursor
            int indexId = mCursor.getColumnIndex(WeatherEntry._ID);
            int indexLocationName = mCursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
            int indexLocationPhoto = mCursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_PHOTO);
            int indexFriendNames = mCursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
            int indexFriendPictures = mCursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_PICTURES);
            int indexWeatherId = mCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
            int indexTemperature = mCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_CURRENT_TEMP);

            mId = mCursor.getInt(indexId);
            mLocationName = mCursor.getString(indexLocationName);
            mLocationPhoto = mCursor.getString(indexLocationPhoto);

            mNames = mCursor.getString(indexFriendNames);
            mProfilePics = mCursor.getString(indexFriendPictures);
            mWeatherId = mCursor.getInt(indexWeatherId);
            mTemperature = mCursor.getDouble(indexTemperature);

            //set location name
            mLocationTextView.setText(mLocationName);

            //set weather background
            //int background = WeatherUtils.getWeatherBackground(mWeatherId); //handles empty weather id(-1)
            //mBackgroundImageView.setImageResource(background);

            //set location photo
            if(mLocationPhoto.equals(itemView.getContext().getString(R.string.location_photo_empty))) {
                mBackgroundImageView.setImageResource(R.color.gray);
            } else {
                Picasso.with(itemView.getContext())
                        .load(mLocationPhoto)
                        .into(mBackgroundImageView);
            }

            //set current temperature
            if(mTemperature == -1) {
                //display null character if temperature is empty(-1)
                mTemperatureTextView.setText("");
            } else {
                //format and display temperature
                String formattedTemp = WeatherUtils.formatTemperature(itemView.getContext(), mTemperature);
                mTemperatureTextView.setText(formattedTemp);
            }
        }

        @OnClick(R.id.weather_item_card_view)
        public void onClick() {
            Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
            intent.putExtra(WeatherEntry._ID, mId);
            itemView.getContext().startActivity(intent);
        }
    }
}
