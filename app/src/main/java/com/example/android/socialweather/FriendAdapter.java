package com.example.android.socialweather;

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
 * Created by Yehyun Ryu on 8/2/2017.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private String[] mFriendNames;
    private String[] mFriendProfiles;

    public FriendAdapter(String[] friendNames, String[] friendProfiles) {
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

    public void swapData(String[] friendNames, String[] friendProfiles) {
        //change data
        mFriendNames = friendNames;
        mFriendProfiles = friendProfiles;

        if(mFriendNames != null && mFriendNames.length != 0) {
            notifyDataSetChanged();
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friend_item_profile) ImageView mProfileImageView;
        @BindView(R.id.friend_item_name) TextView mNameTextView;

        public FriendViewHolder(View itemView) {
            super(itemView);

            //bind views with butterknife
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            //transform profile picture in a circular frame
            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(50)
                    .oval(false)
                    .build();

            if(mFriendProfiles != null) {
                //load friend profile picture
                Picasso.with(itemView.getContext())
                        .load(mFriendProfiles[position])
                        .transform(transformation)
                        .into(mProfileImageView);
            }

            //set friend name
            mNameTextView.setText(mFriendNames[position]);
        }
    }
}
