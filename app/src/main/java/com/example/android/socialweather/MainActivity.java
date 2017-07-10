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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.facebook.login.LoginResult;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_id_text_view) TextView mIdTextView;
    @BindView(R.id.main_info_text_view) TextView mInfoTextView;
    @BindView(R.id.main_logout_button) Button mLogoutButton;
    @BindView(R.id.main_profile_image_view) ImageView mProfileImageView;
    @BindView(R.id.main_friends_text_view) TextView mFriendsTextView;
    @BindView(R.id.main_user_location_text_view) TextView mUserLocationTextView;

    private AccessToken mAccessToken;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind views with butterknife
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

        //callback manager for login permissions
        mCallbackManager = CallbackManager.Factory.create();

        //facebook access token
        mAccessToken = AccessToken.getCurrentAccessToken();
        if(mAccessToken != null) { //if user logged in through facebook
            if(mAccessToken.getPermissions().contains("user_friends")) {
                //if user-friends permission is granted, fetch friends list
                fetchFriends();
            } else {
                //if user-friends permission is not granted, open permission dialog
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //granted permission
                        fetchFriends();
                    }

                    @Override
                    public void onCancel() {
                        //canceled dialog, display that app couldn't access user friend's list
                        String permissionMessage = getResources().getString(R.string.friends_permission_message);
                        Toast.makeText(MainActivity.this, permissionMessage, Toast.LENGTH_LONG).show();

                        //(temporary) log out and return to login activity
                        //TODO: display cached information in future
                        AccountKit.logOut();
                        LoginManager.getInstance().logOut();
                        launchLoginActivity();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        //display error message
                        String toastMessage = error.getMessage();
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                });

                //login with the permission whether it is granted or not
                loginManager.logInWithReadPermissions(this, Arrays.asList("user_friends"));
            }

            if(mAccessToken.getPermissions().contains("user_location")) {
                //fetch user location and display it
                fetchLocation();
            } else {
                //if user did not grant user_location permission, open permission dialog
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //if user granted permission
                        fetchLocation();
                    }

                    @Override
                    public void onCancel() {
                        //canceled dialog, display that user's weather info would not be available to user's friends
                        String toastMessage = getResources().getString(R.string.location_permission_message);
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();

                        //(temporary) log out and return to login activity
                        AccountKit.logOut();
                        LoginManager.getInstance().logOut();
                        launchLoginActivity();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        //display error message
                        String toastMessage = error.getMessage();
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                });

                loginManager.logInWithReadPermissions(this, Arrays.asList("user_location"));
            }

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
                    Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                            String toastMessage = response.getError().getErrorMessage();
                            Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONObject jsonResponse = response.getJSONObject();
                        try {
                            JSONObject jsonLocation = jsonResponse.getJSONObject("location");
                            String locationId = jsonLocation.getString("id");
                            String locationString = jsonLocation.getString("name");

                            mUserLocationTextView.setText(locationId + " " + locationString);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }


    //method that makes the API call to fetch friends list
    private void fetchFriends() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture"); //fields to extract data from
        parameters.putInt("limit", 100); //limit number of friends in list to 100
        new GraphRequest(
                mAccessToken,
                "/me/friends",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(response.getError() != null) {
                            //display error message and end early
                            String toastMessage = response.getError().getErrorMessage();
                            Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                            return;
                        }

                        //data extracted from friends list
                        //TODO: attach to recycler view adapter
                        ArrayList<String> friendIds = new ArrayList<String>();
                        ArrayList<String> friendNames = new ArrayList<String>();
                        ArrayList<String> friendPics = new ArrayList<String>();

                        //extract data

                        JSONObject jsonResponse = response.getJSONObject();
                        try {
                            JSONArray jsonData = jsonResponse.getJSONArray("data");
                            for(int i = 0; i < jsonData.length(); i++) {
                                JSONObject jsonUser = jsonData.getJSONObject(i);
                                String id = jsonUser.getString("id");
                                String name = jsonUser.getString("name");
                                String image = jsonUser.getJSONObject("picture").getJSONObject("data").getString("url");


                                friendIds.add(id);
                                friendNames.add(name);
                                friendPics.add(image);
                            }

                            //temporary friends string to verify that data is being extracted correctly
                            String friendsString = "";
                            for(int x = 0; x < friendIds.size(); x++) {
                                friendsString += friendIds.get(x) + " " + friendNames.get(x) + " " + friendPics.get(x) + "\n";
                            }
                            mFriendsTextView.setText(friendsString);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

    @OnClick(R.id.main_logout_button)
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
        Picasso.with(MainActivity.this)
                .load(uri)
                .transform(transformation)
                .into(mProfileImageView);
    }
}
