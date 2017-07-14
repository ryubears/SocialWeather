package com.example.android.socialweather;

import android.content.Context;
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

/**
 * Created by Yehyun Ryu on 7/14/2017.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private long[] mIds;
    private String[] mProfiles;
    private String[] mNames;
    private String[] mLocations;

    //onClickListener to pass on click event to fragment
    final private WeatherItemClickListener mOnClickListener;

    public WeatherAdapter(WeatherItemClickListener listener, long[] ids, String[] profiles, String[] names, String[] locations) {
        mOnClickListener = listener;
        mIds = ids;
        mProfiles = profiles;
        mNames = names;
        mLocations = locations;
    }

    //onClickListener interface
    public interface WeatherItemClickListener {
        void onWeatherItemClick(int position);
    }

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
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mIds == null) {
            return 0;
        }
        return mIds.length;
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.weather_item_profile) ImageView mProfileImageView;
        @BindView(R.id.weather_item_name) TextView mNameTextView;
        @BindView(R.id.weather_item_location) TextView mLocationTextView;
        @BindView(R.id.weather_item_icon) ImageView mIconImageView;

        private int mPosition;

        public WeatherViewHolder(View view) {
            super(view);

            //binds views using butterknife
            ButterKnife.bind(this, view);
        }

        private void bind(int position) {
            mPosition = position;

            //transform profile picture in a circular frame
            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(100)
                    .oval(false)
                    .build();

            //load profile image
            Picasso.with(itemView.getContext())
                    .load(mProfiles[position])
                    .transform(transformation)
                    .into(mProfileImageView);

            //set name and location info
            mNameTextView.setText(mNames[position]);
            mLocationTextView.setText(mLocations[position]);

            //temporary
            mIconImageView.setImageResource(R.drawable.rain_logo);
        }


        @Override
        public void onClick(View view) {
            mOnClickListener.onWeatherItemClick(mPosition);
        }
    }
}
