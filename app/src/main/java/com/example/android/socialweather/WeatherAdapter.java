package com.example.android.socialweather;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

    //whether user logged in with facebook
    private boolean mIsFacebook;

    //whether item clickable or not
    private boolean mClickable = false;

    //toast to be shown when item is not clickable
    private Toast mToast;

    public WeatherAdapter(boolean isFacebook) {
        mIsFacebook = isFacebook;
    }

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

    public void cancelLoadingToast() {
        if(mToast != null) {
            mToast.cancel();
        }
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.weather_item_card_view) CardView mCardView;
        @BindView(R.id.weather_item_delete) ImageButton mDeleteButton;
        @BindView(R.id.weather_item_background) ImageView mBackgroundImageView;
        @BindView(R.id.weather_item_location_name) TextView mLocationTextView;
        @BindView(R.id.weather_item_description) TextView mDescriptionTextView;
        @BindView(R.id.weather_item_icon) ImageView mIconImageView;
        @BindView(R.id.weather_item_friend1) ImageView mFriend1ImageView;
        @BindView(R.id.weather_item_friend2) ImageView mFriend2ImageView;
        @BindView(R.id.weather_item_lives) TextView mLivesTextView;

        private Context mContext;
        private int mId = -1;
        private String mLocationName;
        private String mLocationPhoto;
        private String mNames;
        private String mProfilePics;
        private String[] mWeatherIds;
        private String[] mWeatherDescriptions;

        private boolean mIsValid;

        public WeatherViewHolder(View view) {
            super(view);

            //binds views using butterknife
            ButterKnife.bind(this, view);
            mContext = view.getContext();
        }

        private void bind() {
            //reset values
            mIsValid = true;

            //extract data from cursor
            int indexId = mCursor.getColumnIndex(WeatherEntry._ID);
            int indexLocationName = mCursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
            int indexLocationPhoto = mCursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_PHOTO);
            int indexFriendNames = mCursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
            int indexWeatherId = mCursor.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_IDS);
            int indexWeatherDescription = mCursor.getColumnIndex(WeatherEntry.COLUMN_FORECAST_WEATHER_DESCRIPTIONS);

            mId = mCursor.getInt(indexId);
            mLocationName = mCursor.getString(indexLocationName);
            mLocationPhoto = mCursor.getString(indexLocationPhoto);
            mNames = mCursor.getString(indexFriendNames);
            mWeatherIds = mCursor.getString(indexWeatherId).split(itemView.getContext().getString(R.string.delimiter));
            mWeatherDescriptions = mCursor.getString(indexWeatherDescription).split(itemView.getContext().getString(R.string.delimiter));

            //set default profile
            mFriend1ImageView.setImageResource(R.drawable.profile_color);

            if(mIsFacebook) {
                mDeleteButton.setVisibility(View.GONE);
                //set friend pictures and number of friends living in the location
                String[] friendNames = mNames.split(mContext.getString(R.string.delimiter));
                int numFriends = friendNames.length;
                mLivesTextView.setText(String.valueOf(numFriends));
                //get friend profile pictures
                int indexFriendPictures = mCursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_PICTURES);
                mProfilePics = mCursor.getString(indexFriendPictures);
                String[] friendPics = mProfilePics.split(mContext.getString(R.string.delimiter));

                //transform profile picture in a circular frame
                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(50)
                        .oval(false)
                        .build();

                //set first friend picture
                if(friendPics[0].equals(mContext.getString(R.string.picture_empty))) {
                    mFriend1ImageView.setImageResource(R.drawable.profile_color);
                } else {
                    Picasso.with(mContext)
                            .load(friendPics[0])
                            .transform(transformation)
                            .into(mFriend1ImageView);
                }

                //set second friend picture
                if(numFriends >= 2) {
                    mFriend2ImageView.setVisibility(View.VISIBLE);
                    if(friendPics[1].equals(mContext.getString(R.string.picture_empty))) {
                        mFriend2ImageView.setImageResource(R.drawable.profile_color);
                    } else {
                        Picasso.with(mContext)
                                .load(friendPics[1])
                                .transform(transformation)
                                .into(mFriend2ImageView);
                    }
                } else {
                    mFriend2ImageView.setVisibility(View.GONE);
                }
            } else {
                mDeleteButton.setVisibility(View.VISIBLE);
            }

            //if location is invalid or empty
            if(mLocationName.equals(mContext.getString(R.string.location_empty))) {
                //set default values
                mBackgroundImageView.setImageResource(R.drawable.no_weather_background);
                mLocationTextView.setText(mContext.getString(R.string.location_default));
                mDescriptionTextView.setText(mContext.getString(R.string.description_default));
                mIconImageView.setImageResource(R.drawable.no_weather_color);
                int numFriends = mNames.split(mContext.getString(R.string.delimiter)).length;
                mLivesTextView.setText(String.valueOf(numFriends));
                mIsValid = false;
                return;
            }

            //check if weather item have been initialized
            if(mLocationPhoto.equals(mContext.getString(R.string.location_photo_empty))) {
                //set background to gray to indicate that item is not fully loaded
                mBackgroundImageView.setImageResource(R.color.gray);
                //prevent clicking and return early
                mClickable = false;
                return;
            }

            //set location name
            mLocationTextView.setText(mLocationName);

            //set location photo
            Picasso.with(mContext)
                    .load(mLocationPhoto)
                    .into(mBackgroundImageView);

            //set weather icon
            if(!mWeatherIds[0].equals(mContext.getString(R.string.forecast_ids_empty))) {
                int currentWeatherId = Integer.valueOf(mWeatherIds[0]);
                int icon = WeatherUtils.getColorWeatherIcon(currentWeatherId);
                mIconImageView.setImageResource(icon);
            }

            //set weather description
            if(!mWeatherDescriptions[0].equals(mContext.getString(R.string.forecast_descriptions_empty))) {
                String currentWeatherDescription = mWeatherDescriptions[0];
                String weatherDescription = WeatherUtils.formatDescription(currentWeatherDescription);
                mDescriptionTextView.setText(weatherDescription);
            }

            //set friend pictures and number of friends living in the location
            String[] friendNames = mNames.split(mContext.getString(R.string.delimiter));
            int numFriends = friendNames.length;
            mLivesTextView.setText(String.valueOf(numFriends));
        }

        @OnClick(R.id.weather_item_delete)
        public void onDelete() {
            if(mClickable) {
                deleteItem();
                if(mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(mContext, "Deleted Item", Toast.LENGTH_SHORT);
                mToast.show();
            }
        }

        @OnClick(R.id.weather_item_card_view)
        public void onClick() {
            if(mClickable && mIsValid) {
                Intent intent = new Intent(itemView.getContext(), ForecastActivity.class);
                intent.putExtra(WeatherEntry._ID, mId);
                intent.putExtra(mContext.getString(R.string.is_facebook_key), mIsFacebook);
                mContext.startActivity(intent);
            } else {
                if(!mIsValid) {
                    Intent intent = new Intent(mContext, ForecastActivity.class);
                    intent.putExtra(WeatherEntry._ID, mId);
                    intent.putExtra(mContext.getString(R.string.is_facebook_key), mIsFacebook);
                    intent.putExtra(mContext.getString(R.string.is_invalid_key), mIsValid);
                    mContext.startActivity(intent);
                } else {
                    if(mToast != null) {
                        mToast.cancel();
                    }
                    String toastMessage = mContext.getString(R.string.loading_message);
                    mToast = Toast.makeText(mContext, toastMessage, Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        }

        private void deleteItem() {
            new DeleteItemTask().execute();
        }

        private class DeleteItemTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                mContext.getContentResolver().delete(
                        ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, mId),
                        null,
                        null
                );
                return null;
            }
        }
    }
}
