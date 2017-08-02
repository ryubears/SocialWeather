package com.example.android.socialweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountActivity extends AppCompatActivity {
    @BindView(R.id.account_profile) ImageView mProfileImageView;
    @BindView(R.id.account_id_value) TextView mIdTextView;
    @BindView(R.id.account_info_label) TextView mInfoLabelTextView;
    @BindView(R.id.account_info_value) TextView mInfoValueTextView;
    @BindView(R.id.account_location_value) TextView mLocationTextView;

    private String mProfileUrl;
    private String mId;
    private String mInfo;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ButterKnife.bind(this);

        Intent intent = getIntent();

    }
}
