package com.example.android.socialweather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.login.LoginManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountActivity extends AppCompatActivity {
    @BindView(R.id.account_profile_image) ImageView mProfileImageView;
    @BindView(R.id.account_id) TextView mIdTextView;
    @BindView(R.id.account_info_label) TextView mInfoLabelTextView;
    @BindView(R.id.account_info) TextView mInfoTextView;
    @BindView(R.id.account_location) TextView mLocationTextView;
    @BindView(R.id.account_logout_button) Button mLogoutButton;

    private AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //setup up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //binds views with butterknife
        ButterKnife.bind(this);

        //tracks changes to profile
        new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile != null) {
                    //displays the changed profile info
                    displayProfileInfo(currentProfile);
                }
            }
        };

        //facebook access token
        mAccessToken = AccessToken.getCurrentAccessToken();
        if(mAccessToken != null) {
            //if user logged in with facebook
            mInfoLabelTextView.setText(getString(R.string.account_name_label_text));

            //fetch and display user location info
            if(mAccessToken.getPermissions().contains("user_location")) {
                fetchLocation();
            }

            //get current profile
            Profile profile = Profile.getCurrentProfile();
            if(profile != null) {
                //display profile information
                displayProfileInfo(profile);
            } else {
                //fetch updated profile and triggers onCurrentProfileChanged
                Profile.fetchProfileForCurrentAccessToken();
            }
        } else {
            //if user logged in with account kit
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    //get account kit id
                    String accountKitId = account.getId();
                    mIdTextView.setText(accountKitId);

                    PhoneNumber phoneNumber = account.getPhoneNumber();
                    if(phoneNumber != null) {
                        //if user logged in with phone number
                        mInfoLabelTextView.setText(getString(R.string.account_phone_label_text));
                        String formattedPhoneNumber = formatPhoneNumber(phoneNumber.toString());
                        mInfoTextView.setText(formattedPhoneNumber);
                    } else {
                        //if user logged in with email
                        mInfoLabelTextView.setText(getString(R.string.account_email_label_text));
                        String emailString = account.getEmail();
                        mInfoTextView.setText(emailString);
                    }
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    //display error message
                    String toastMessage = accountKitError.getErrorType().getMessage();
                    Toast.makeText(AccountActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @OnClick(R.id.account_logout_button)
    public void onLogout() {
        //logout from current account
        AccountKit.logOut();
        LoginManager.getInstance().logOut();
        launchLoginActivity();
    }

    //launches LoginActivity and finishes current activity
    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //helper method that fetches user location and displays it
    private void fetchLocation() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "location");
        new GraphRequest(
                mAccessToken,
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(response.getError() != null) {
                            //display error message
                            String toastMessage = response.getError().getErrorMessage();
                            Toast.makeText(AccountActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                            return;
                        }

                        //extract location info
                        JSONObject jsonResponse = response.getJSONObject();
                        try {
                            JSONObject jsonLocation = jsonResponse.getJSONObject("location");
                            String locationId = jsonLocation.getString("id");
                            String locationString = jsonLocation.getString("name");

                            //check if location string is empty
                            if(!TextUtils.isEmpty(locationString)) {
                                mLocationTextView.setText(locationString);
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    //helper method that displays profile information
    private void displayProfileInfo(Profile profile) {
        //set profile id
        String profileId = profile.getId();
        mIdTextView.setText(profileId);

        //set user name
        String name = profile.getName();
        mInfoTextView.setText(name);

        //set user profile picture
        Uri profilePicUri = profile.getProfilePictureUri(180, 180);
        displayProfilePic(profilePicUri);
    }

    //helper method for displaying profile pictures
    private void displayProfilePic(Uri uri) {
        //transform profile picture in a circular frame
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(50)
                .oval(false)
                .build();

        //inserts profile picture to profile image view
        Picasso.with(AccountActivity.this)
                .load(uri)
                .transform(transformation)
                .into(mProfileImageView);
    }

    //helper method for formatting phone numbers
    private String formatPhoneNumber(String phoneNumber) {
        try {
            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber pn = pnu.parse(phoneNumber, Locale.getDefault().getCountry());
            phoneNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }
}
