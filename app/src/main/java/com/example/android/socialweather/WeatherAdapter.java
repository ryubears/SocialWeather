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
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
        @BindView(R.id.weather_item_profile) ImageView mProfileImageView;
        @BindView(R.id.weather_item_name) TextView mNameTextView;
        @BindView(R.id.weather_item_location) TextView mLocationTextView;
        @BindView(R.id.weather_item_temperature) TextView mTemperatureTextView;

        private int mId;
        private String mName;
        private String mProfilePic;
        private String mLocation;
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
            int indexFriendName = mCursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_NAME);
            int indexFriendProfilePic = mCursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_PROFILE);
            int indexFriendLocation = mCursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_LOCATION);
            int indexWeatherId = mCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
            int indexTemperature = mCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_CURRENT_TEMP);

            mId = mCursor.getInt(indexId);
            mName = mCursor.getString(indexFriendName);
            mProfilePic = mCursor.getString(indexFriendProfilePic);
            mLocation = mCursor.getString(indexFriendLocation);
            mWeatherId = mCursor.getInt(indexWeatherId);
            mTemperature = mCursor.getDouble(indexTemperature);

            //set name
            mNameTextView.setText(mName);

            //set profile picture
            if(mProfilePic.equals(itemView.getContext().getString(R.string.picture_empty))) {
                //if user profile picture does not exist
                mProfileImageView.setImageResource(R.drawable.profile_color);
            } else {
                //transform profile picture in a circular frame
                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(100)
                        .oval(false)
                        .build();

                //load profile image
                Picasso.with(itemView.getContext())
                        .load(mProfilePic)
                        .transform(transformation)
                        .into(mProfileImageView);
            }

            //set location
            if(mLocation.equals(itemView.getContext().getString(R.string.location_empty))) {
                //when location data does not exist
                mLocationTextView.setText(itemView.getContext().getString(R.string.location_default));
            } else {
                mLocationTextView.setText(mLocation);
            }

            int background = WeatherUtils.getWeatherBackground(mWeatherId);
            mBackgroundImageView.setImageResource(background);

            //set current temperature
            mTemperatureTextView.setText(String.valueOf(mTemperature));
        }

        @OnClick(R.id.weather_item_card_view)
        public void onClick() {
            Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
            intent.putExtra(WeatherEntry._ID, mId);
            itemView.getContext().startActivity(intent);
        }
    }
}
