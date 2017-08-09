package com.yehyunryu.android.socialweather;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
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

/**
 * Copyright 2017 Yehyun Ryu

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private boolean mIsFacebook;
    private String mLocationName;
    private String[] mFriendNames;
    private String[] mFriendProfiles;

    public FriendAdapter(boolean isFacebook, String locationName, String[] friendNames, String[] friendProfiles) {
        mIsFacebook = isFacebook;
        mLocationName = locationName;
        mFriendNames = friendNames;
        mFriendProfiles = friendProfiles;
    }

    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate view and create view holder
        int layoutId = R.layout.friend_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        boolean attachToParentImmediately = false;

        View view = layoutInflater.inflate(layoutId, parent, attachToParentImmediately);
        FriendViewHolder viewHolder = new FriendViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendAdapter.FriendViewHolder holder, int position) {
        //bind views with data
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mFriendNames == null) {
            return 0;
        }
        return mFriendNames.length;
    }

    public void swapData(String locationName, String[] friendNames, String[] friendProfiles) {
        //change data
        mLocationName = locationName;
        mFriendNames = friendNames;
        mFriendProfiles = friendProfiles;

        if(mFriendNames != null && mFriendNames.length != 0) {
            notifyDataSetChanged();
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friend_item) ConstraintLayout mFriendItem;
        @BindView(R.id.friend_item_profile) ImageView mProfileImageView;
        @BindView(R.id.friend_item_name) TextView mNameTextView;

        private Context mContext;
        private int mPosition;
        private String mProfile;
        private String mName;

        public FriendViewHolder(View itemView) {
            super(itemView);

            //bind views with butterknife
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
        }

        public void bind(int position) {
            mPosition = position;
            if(mIsFacebook) mProfile = mFriendProfiles[position];
            mName = mFriendNames[position];

            if(mIsFacebook) {
                //transform profile picture in a circular frame
                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(50)
                        .oval(false)
                        .build();

                //load friend profile picture
                Picasso.with(mContext)
                        .load(mProfile)
                        .transform(transformation)
                        .into(mProfileImageView);
            } else {
                mProfileImageView.setImageResource(R.drawable.profile_color);
            }

            //set friend name
            mNameTextView.setText(mName);
        }

        @OnClick(R.id.friend_item)
        public void onClick() {
            Intent intent = new Intent(mContext, AccountActivity.class);
            intent.putExtra(mContext.getString(R.string.is_facebook_key), mIsFacebook);
            intent.putExtra(mContext.getString(R.string.account_position_key), mPosition);
            intent.putExtra(mContext.getString(R.string.account_profile_key), mProfile);
            intent.putExtra(mContext.getString(R.string.account_name_key), mName);
            intent.putExtra(mContext.getString(R.string.account_location_key), mLocationName);
            mContext.startActivity(intent);
        }
    }
}
