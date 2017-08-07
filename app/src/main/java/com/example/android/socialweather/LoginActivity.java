package com.example.android.socialweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.socialweather.analytics.AnalyticsApplication;
import com.example.android.socialweather.data.WeatherPreferences;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_phone_button) Button mPhoneButton;
    @BindView(R.id.login_email_button) Button mEmailButton;
    @BindView(R.id.login_facebook_button) LoginButton mFacebookButton;

    public static int LOGIN_REQUEST_CODE = 1;

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private CallbackManager mCallbackManager;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //bind views with butterknife
        ButterKnife.bind(this);

        //obtain shared Tracker instance
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        //set facebook login permissions
        mFacebookButton.setReadPermissions("email");

        //register callback for facebook login
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //save login type
                WeatherPreferences.saveLoginType(getApplicationContext(), true);
                launchMainActivity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                //display error message
                String toastMessage = error.getMessage();
                Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });

        //check if an ClientKit/Facebook accessToken already exists
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        com.facebook.AccessToken loginToken = com.facebook.AccessToken.getCurrentAccessToken();
        if(accessToken != null || loginToken != null) {
            //if previously logged in, proceed to social activity
            launchMainActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String name = "LoginActivity";
        Log.d(LOG_TAG, "Setting screen name: " + name);
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //call facebook callback manager's onActivityResult for facebook login
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        //confirm that response matches request code
        if(requestCode == LOGIN_REQUEST_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(loginResult.getError() != null) {
                //display login error
                String toastMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            } else if(loginResult.getAccessToken() != null) {
                //save login type
                WeatherPreferences.saveLoginType(getApplicationContext(), false);
                //proceed to main activity if login was successful
                launchMainActivity();
            }
        }
    }

    private void onLogin(final LoginType loginType) {
        //intent for AccountKit activity
        final Intent intent = new Intent(this, AccountKitActivity.class);

        //configure login type and response type
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        loginType,
                        AccountKitActivity.ResponseType.TOKEN
                );
        final AccountKitConfiguration configuration = configurationBuilder.build();

        //launch the AccountKit activity
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        startActivityForResult(intent, LOGIN_REQUEST_CODE);
    }

    @OnClick(R.id.login_phone_button)
    public void onPhoneLogin() {
        //logs phone events
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("onPhoneLogin");

        onLogin(LoginType.PHONE);
    }

    @OnClick(R.id.login_email_button)
    public void onEmailLogin() {
        //logs email events
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("onEmailLogin");

        onLogin(LoginType.EMAIL);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
