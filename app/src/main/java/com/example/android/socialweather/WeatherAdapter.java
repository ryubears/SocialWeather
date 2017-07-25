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

    private Cursor mCursor;

    public WeatherAdapter() {}

    @Override
    public WeatherAdapter.WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.weather_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, attachToParentImmediately);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherAdapter.WeatherViewHolder holder, int position) {
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

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        if(mCursor != null && mCursor.getCount() != 0) {
            notifyDataSetChanged();
        }
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.weather_item_card_view) CardView mCardView;
        @BindView(R.id.weather_item_profile) ImageView mProfileImageView;
        @BindView(R.id.weather_item_name) TextView mNameTextView;
        @BindView(R.id.weather_item_location) TextView mLocationTextView;
        @BindView(R.id.weather_item_temperature) TextView mTemperatureTextView;

        private String mId;
        private String mName;
        private String mProfilePic;
        private String mLocation;

        public WeatherViewHolder(View view) {
            super(view);

            //binds views using butterknife
            ButterKnife.bind(this, view);
        }

        private void bind() {
            int indexFriendId = mCursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_ID);
            int indexFriendName = mCursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_NAME);
            int indexFriendProfilePic = mCursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_PROFILE);
            int indexFriendLocation = mCursor.getColumnIndex(WeatherEntry.COLUMN_PERSON_LOCATION);

            mId = mCursor.getString(indexFriendId);
            mName = mCursor.getString(indexFriendName);
            mProfilePic = mCursor.getString(indexFriendProfilePic);
            mLocation = mCursor.getString(indexFriendLocation);

            //set name
            mNameTextView.setText(mName);

            //set profile picture
            if(mProfilePic.equals(itemView.getContext().getString(R.string.picture_empty))) {
                //if user profile picture does not exist
                mProfileImageView.setImageResource(R.drawable.profile_default);
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

            //temporary
            mTemperatureTextView.setText("13°");
        }

        @OnClick(R.id.weather_item_card_view)
        public void onClick() {
            Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
            itemView.getContext().startActivity(intent);
        }
    }
}
