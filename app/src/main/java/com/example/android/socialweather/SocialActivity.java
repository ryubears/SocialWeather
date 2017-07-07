package com.example.android.socialweather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
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

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SocialActivity extends AppCompatActivity {
    @BindView(R.id.social_id_text_view) TextView mIdTextView;
    @BindView(R.id.social_info_text_view) TextView mInfoTextView;
    @BindView(R.id.social_logout_button) Button mLogoutButton;
    @BindView(R.id.social_profile_image_view) ImageView mProfileImageView;

    private ProfileTracker mProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        //bind views with butterknife
        ButterKnife.bind(this);

        //tracks changes to profile
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile != null) {
                    //displays the changed profile info
                    displayProfileInfo(currentProfile);
                }
            }
        };

        if(AccessToken.getCurrentAccessToken() != null) { //if user logged in through facebook
            //get current profile
            Profile profile = Profile.getCurrentProfile();
            if(profile != null) {
                //displays profile
                displayProfileInfo(profile);
            } else {
                //fetch profile info and calls onCurrentProfileChanged
                Profile.fetchProfileForCurrentAccessToken();
            }
        } else { //if user logged in through account kit
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    //get account kit id
                    String accountKitId = account.getId();
                    mIdTextView.setText(accountKitId);

                    PhoneNumber phoneNumber = account.getPhoneNumber();
                    if(phoneNumber != null) {
                        //if phone number is available, display it
                        String formattedPhoneNumber = formatPhoneNumber(phoneNumber.toString());
                        mInfoTextView.setText(formattedPhoneNumber);
                    } else {
                        //if email address is available, display it
                        String emailString = account.getEmail();
                        mInfoTextView.setText(emailString);
                    }
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    //display error message
                    String toastMessage = accountKitError.getErrorType().getMessage();
                    Toast.makeText(SocialActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @OnClick(R.id.social_logout_button)
    public void onLogout() {
        //logout from current account
        AccountKit.logOut();
        LoginManager.getInstance().logOut();
        launchLoginActivity();
    }

    private void displayProfileInfo(Profile profile) {
        //set profile id
        String profileId = profile.getId();
        mIdTextView.setText(profileId);

        //set user name
        String name = profile.getName();
        mInfoTextView.setText(name);

        //set user profile picture
        Uri profilePicUri = profile.getProfilePictureUri(100, 100);
        displayProfilePic(profilePicUri);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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

    //helper method for displaying profile pictures
    private void displayProfilePic(Uri uri) {
        //transform profile picture in a circular frame
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        //inserts profile picture to profile image view
        Picasso.with(SocialActivity.this)
                .load(uri)
                .transform(transformation)
                .into(mProfileImageView);
    }
}
