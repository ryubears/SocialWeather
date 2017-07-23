package com.example.android.socialweather;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yehyun Ryu on 7/14/2017.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private ArrayList<String> mIds;
    private ArrayList<String> mProfiles;
    private ArrayList<String> mNames;
    private ArrayList<String> mLocations;

    public WeatherAdapter(ArrayList<String> ids, ArrayList<String> profiles, ArrayList<String> names, ArrayList<String> locations) {
        mIds = ids;
        mProfiles = profiles;
        mNames = names;
        mLocations = locations;
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
        return mIds.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.weather_item_card_view) CardView mCardView;
        @BindView(R.id.weather_item_profile) ImageView mProfileImageView;
        @BindView(R.id.weather_item_name) TextView mNameTextView;
        @BindView(R.id.weather_item_location) TextView mLocationTextView;
        @BindView(R.id.weather_item_temperature) TextView mTemperatureTextView;

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
                    .load(mProfiles.get(position))
                    .transform(transformation)
                    .into(mProfileImageView);

            //set name and location info
            mNameTextView.setText(mNames.get(position));
            mLocationTextView.setText(mLocations.get(position));

            //temporary
            mTemperatureTextView.setText("13°");

            //temporary testing for clicks
            mCardView.setTag(R.string.weather_item_position_tag, position);
        }

        @OnClick(R.id.weather_item_card_view)
        public void onClick() {
            //temporary testing
            Toast.makeText(itemView.getContext(), "Selected: " + mCardView.getTag(R.string.weather_item_position_tag), Toast.LENGTH_SHORT).show();
        }
    }
}
