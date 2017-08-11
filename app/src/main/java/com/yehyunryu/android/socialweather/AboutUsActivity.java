package com.yehyunryu.android.socialweather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

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

public class AboutUsActivity extends AppCompatActivity {
    @BindView(R.id.email_view) Button mEmailView;
    @BindView(R.id.facebook_view) Button mFacebookView;
    @BindView(R.id.github_view) Button mGithubView;
    @BindView(R.id.playstore_view) Button mPlaystoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        //set up button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //bind views
        ButterKnife.bind(this);
    }

    @OnClick(R.id.email_view)
    public void onEmailClick() {
        //email intent
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"ryubearsdev@gmail.com"});
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.facebook_view)
    public void onFacebookClick() {
        //facebook browser intent
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/josephyehyun.ryu"));
        startActivity(intent);
    }

    @OnClick(R.id.github_view)
    public void onGithubClick() {
        //github browser intent
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ryubears/SocialWeather"));
        startActivity(intent);
    }

    @OnClick(R.id.playstore_view)
    public void onPlaystoreClick(){
        //play store intent
        String appPackageName = getPackageName();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            startActivity(intent);
        } catch(android.content.ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            startActivity(intent);
        }
    }
}
