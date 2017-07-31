package com.example.android.socialweather;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.socialweather.data.WeatherContract;
import com.example.android.socialweather.data.WeatherPreferences;
import com.example.android.socialweather.utils.NetworkUtils;
import com.example.android.socialweather.utils.WeatherUtils;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

//App Launcher Icons made by "https://www.flaticon.com/authors/gregor-cresnar" "Gregor Cresnar"
//Weather and Profile Icons made by "https://www.flaticon.com/authors/madebyoliver" "Madebyoliver"

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.main_toolbar) Toolbar mToolbar;
    @BindView(R.id.main_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.main_nav_view) NavigationView mNavigationView;

    //views in navigation header
    private View mHeader;
    private ImageView mHeaderBackground;
    private ImageView mHeaderProfile;
    private TextView mHeaderInfo;
    private TextView mHeaderLocation;

    //index to identify current nav menu item
    public static int navItemIndex = 0;

    //fragment tags
    private static final String TAG_HOME = "home";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    //toolbar titles respected to selected nav menu item
    private String[] mActivityTitles;

    //flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    //facebook access token and a callback manager to handle permission dialogs
    private AccessToken mAccessToken;
    private CallbackManager mCallbackManager;

    //instance of Google API Client
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind views with butterknife
        ButterKnife.bind(this);

        //set action bar to use customized toolbar
        setSupportActionBar(mToolbar);

        //initialize handler
        mHandler = new Handler();

        //initialize header views
        mHeader = mNavigationView.getHeaderView(0);
        mHeaderInfo = (TextView) mHeader.findViewById(R.id.nav_header_info);
        mHeaderLocation = (TextView) mHeader.findViewById(R.id.nav_header_location);
        mHeaderBackground = (ImageView) mHeader.findViewById(R.id.nav_header_background);
        mHeaderProfile = (ImageView) mHeader.findViewById(R.id.nav_header_profile);

        //get activity titles
        mActivityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        //callback manager for login permissions
        mCallbackManager = CallbackManager.Factory.create();

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
            //if user logged in through facebook

            //get current profile and display profile info in navigation header
            Profile profile = Profile.getCurrentProfile();
            if(profile != null) {
                //display profile information
                displayProfileInfo(profile);
            } else {
                //fetch updated profile and triggers onCurrentProfileChanged
                Profile.fetchProfileForCurrentAccessToken();
            }

            //check user-friends permission
            if(!mAccessToken.getPermissions().contains("user_friends")) {
                //if user-friends permission is not granted, open permission dialog
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //when granted permission
                    }

                    @Override
                    public void onCancel() {
                        //canceled dialog, display that app couldn't access user friend's list
                        String permissionMessage = getResources().getString(R.string.friends_permission_message);
                        Toast.makeText(MainActivity.this, permissionMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        //display error message
                        String toastMessage = error.getMessage();
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();

                        //automatically logout
                        AccountKit.logOut();
                        LoginManager.getInstance().logOut();
                        launchLoginActivity();
                    }
                });

                //login with the permission whether granted or not
                loginManager.logInWithReadPermissions(this, Arrays.asList("user_friends"));
            }

            //check user-location permission
            if(mAccessToken.getPermissions().contains("user_location")) {
                //permission granted
                fetchLocation();
            } else {
                //if user did not grant user_location permission, open permission dialog
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //success
                        fetchLocation();
                    }

                    @Override
                    public void onCancel() {
                        //canceled dialog, display that user's weather info would not be available to user's friends
                        String toastMessage = getResources().getString(R.string.location_permission_message);
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        //display error message
                        String toastMessage = error.getMessage();
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();

                        //automatically logout
                        AccountKit.logOut();
                        LoginManager.getInstance().logOut();
                        launchLoginActivity();
                    }
                });

                //login with the permission whether granted or not
                loginManager.logInWithReadPermissions(this, Arrays.asList("user_location"));
            }
        } else {
            //if user logged in through account kit
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    PhoneNumber phoneNumber = account.getPhoneNumber();
                    if(phoneNumber != null) {
                        //when logged in with phone number
                        String formattedPhoneNumber = formatPhoneNumber(phoneNumber.toString());
                        mHeaderInfo.setText(formattedPhoneNumber);
                    } else {
                        //when logged in with email
                        String emailString = account.getEmail();
                        mHeaderInfo.setText(emailString);
                    }
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    //display error message
                    String toastMessage = accountKitError.getErrorType().getMessage();
                    Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();

                    //automatically logout
                    AccountKit.logOut();
                    LoginManager.getInstance().logOut();
                    launchLoginActivity();
                }
            });
        }

        //set up navigation item selection events
        setUpNavigationView();

        //display home fragment if activity is freshly opened
        if(savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadFragment();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_logout:
                        //logout from current account
                        AccountKit.logOut();
                        LoginManager.getInstance().logOut();
                        launchLoginActivity();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //load current fragment
                loadFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                //triggered when drawer closes
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //triggered when drawer opens
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        //necessary for hamburger icon to show up
        actionBarDrawerToggle.syncState();

        //set logout item text color to red
        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.nav_logout);
        SpannableString logoutTitle = new SpannableString(menuItem.getTitle());
        logoutTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.nav_logout_text)), 0 , logoutTitle.length(), 0);
        menuItem.setTitle(logoutTitle);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadFragment() {
        //selecting appropriate nav menu item
        selectNavMenu();

        //set appropriate toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if(getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            mDrawerLayout.closeDrawers();
            return;
        }

        //sometimes, when fragment has huge data, screen seems hanging
        //when switching between navigation menus
        //so using runnable, the fragment is loaded with cross fade effect
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_fragment, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        //if mPendingRunnable is not null, then add to the message queue
        if(mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //closing drawer on item click
        mDrawerLayout.closeDrawers();

        //refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getFragment() {
        switch(navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    //assign appropriate title for toolbar
    private void setToolbarTitle() {
        getSupportActionBar().setTitle(mActivityTitles[navItemIndex]);
    }

    //set selected menu item as checked
    private void selectNavMenu() {
        mNavigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        //This code loads home fragment when back key is pressed
        //when user is in other fragment than home
        if(shouldLoadHomeFragOnBackPress) {
            //checking if user is on other navigation menu
            //rather than home
            if(navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    //handles login permissions results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                            Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                            return;
                        }

                        //extract location info
                        JSONObject jsonResponse = response.getJSONObject();
                        try {
                            if(!jsonResponse.isNull("location")) {
                                JSONObject jsonLocation = jsonResponse.getJSONObject("location");
                                String locationString = jsonLocation.getString("name");
                                mHeaderLocation.setText(locationString);

                                long currentTime = System.currentTimeMillis();
                                long lastFetchTime = WeatherPreferences.getProfileWeatherFetchTime(getApplicationContext());
                                int maxHours = 3;
                                if(currentTime - lastFetchTime >= TimeUnit.HOURS.toMillis(maxHours)) {
                                    fetchWeather(locationString);
                                } else {
                                    int weatherId = WeatherPreferences.getUserWeatherId(getApplicationContext());
                                    int background = WeatherUtils.getWeatherBackground(weatherId);
                                    mHeaderBackground.setImageResource(background);
                                }
                            } else {
                                mHeaderLocation.setText(getString(R.string.location_default));
                                //set background to random weather background if location is empty

                                //temporary for testing
                                long currentTime = System.currentTimeMillis();
                                long lastFetchTime = WeatherPreferences.getProfileWeatherFetchTime(getApplicationContext());
                                int maxHours = 3;
                                if(currentTime - lastFetchTime >= TimeUnit.HOURS.toMillis(maxHours)) {
                                    fetchWeather("Seattle, Washington");
                                } else {
                                    int weatherId = WeatherPreferences.getUserWeatherId(getApplicationContext());
                                    int background = WeatherUtils.getWeatherBackground(weatherId);
                                    mHeaderBackground.setImageResource(background);
                                }
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

        //set user name
        String name = profile.getName();
        mHeaderInfo.setText(name);

        //set user profile picture
        Uri profilePicUri = profile.getProfilePictureUri(100, 100);
        displayProfilePic(profilePicUri);
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
                .into(mHeaderProfile);
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

    //handles connection failures for GoogleApiClient
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String toastMessage = connectionResult.getErrorMessage();
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    private void fetchWeather(String location) {
        new fetchWeatherTask().execute(location);
    }

    private class fetchWeatherTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            WeatherPreferences.saveProfileWeatherFetchTime(getApplicationContext(), System.currentTimeMillis());
            ContentValues contentValues = NetworkUtils.fetchWeather(strings[0]);
            int weatherId = contentValues.getAsInteger(WeatherContract.WeatherEntry.COLUMN_CURRENT_WEATHER_ID);
            WeatherPreferences.saveUserWeatherId(getApplicationContext(), weatherId);
            return weatherId;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            int background = WeatherUtils.getWeatherBackground(integer);
            mHeaderBackground.setImageResource(background);
        }
    }
}
