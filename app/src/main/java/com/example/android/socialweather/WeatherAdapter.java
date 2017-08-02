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
import android.widget.Toast;

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

    private boolean mClickable = false;

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

    public void setClickable(boolean clickable) {
        mClickable = clickable;
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.weather_item_card_view) CardView mCardView;
        @BindView(R.id.weather_item_background) ImageView mBackgroundImageView;
        @BindView(R.id.weather_item_location_name) TextView mLocationTextView;
        @BindView(R.id.weather_item_description) TextView mDescriptionTextView;
        @BindView(R.id.weather_item_icon) ImageView mIconImageView;
        @BindView(R.id.weather_item_friend1) ImageView mFriend1ImageView;
        @BindView(R.id.weather_item_friend2) ImageView mFriend2ImageView;
        @BindView(R.id.weather_item_lives) TextView mLivesTextView;

        private int mId = -1;
        private String mLocationName;
        private String mLocationPhoto;
        private String mProfilePics;
        private int mWeatherId;
        private String mWeatherDescription;

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
            int indexFriendPictures = mCursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_PICTURES);
            int indexWeatherId = mCursor.getColumnIndex(WeatherEntry.COLUMN_CURRENT_WEATHER_ID);
            int indexWeatherDescription = mCursor.getColumnIndex(WeatherEntry.COLUMN_CURRENT_WEATHER_DESCRIPTION);

            mId = mCursor.getInt(indexId);
            mLocationName = mCursor.getString(indexLocationName);
            mLocationPhoto = mCursor.getString(indexLocationPhoto);
            mProfilePics = mCursor.getString(indexFriendPictures);
            mWeatherId = mCursor.getInt(indexWeatherId);
            mWeatherDescription = mCursor.getString(indexWeatherDescription);

            //set location name
            mLocationTextView.setText(mLocationName);

            //set location photo
            if(mLocationPhoto.equals(itemView.getContext().getString(R.string.location_photo_empty))) {
                mBackgroundImageView.setImageResource(R.color.gray);
            } else {
                Picasso.with(itemView.getContext())
                        .load(mLocationPhoto)
                        .into(mBackgroundImageView);
            }

            //set weather icon
            int icon = WeatherUtils.getColorWeatherIcon(mWeatherId);
            mIconImageView.setImageResource(icon);

            //set weather description
            String weatherDescription = WeatherUtils.formatDescription(mWeatherDescription);
            mDescriptionTextView.setText(weatherDescription);

            //set friend pictures and number of friends living in the location
            String[] friendPics = mProfilePics.split(itemView.getContext().getString(R.string.delimiter));
            int numFriends = friendPics.length;
            mLivesTextView.setText(String.valueOf(numFriends));

            //transform profile picture in a circular frame
            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(50)
                    .oval(false)
                    .build();

            //set first friend picture
            if(friendPics[0].equals(itemView.getContext().getString(R.string.picture_empty))) {
                mFriend1ImageView.setImageResource(R.drawable.profile_color);
            } else {
                Picasso.with(itemView.getContext())
                        .load(friendPics[0])
                        .transform(transformation)
                        .into(mFriend1ImageView);
            }

            //set second friend picture
            if(friendPics.length >= 2) {
                mFriend2ImageView.setVisibility(View.VISIBLE);
                if(friendPics[1].equals(itemView.getContext().getString(R.string.picture_empty))) {
                    mFriend2ImageView.setImageResource(R.drawable.profile_color);
                } else {
                    Picasso.with(itemView.getContext())
                            .load(friendPics[1])
                            .transform(transformation)
                            .into(mFriend2ImageView);
                }
            } else {
                mFriend2ImageView.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.weather_item_card_view)
        public void onClick() {
            if(mClickable) {
                Intent intent = new Intent(itemView.getContext(), ForecastActivity.class);
                intent.putExtra(WeatherEntry._ID, mId);
                itemView.getContext().startActivity(intent);
            } else {
                String toastMessage = itemView.getContext().getString(R.string.loading_message);
                Toast.makeText(itemView.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
