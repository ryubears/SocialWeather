package com.example.android.socialweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_friends_text_view) TextView mFriendsTextView;

    private AccessToken mAccessToken;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind views with butterknife
        ButterKnife.bind(this);


        //callback manager for login permissions
        mCallbackManager = CallbackManager.Factory.create();

        //facebook access token
        mAccessToken = AccessToken.getCurrentAccessToken();
        if(mAccessToken != null) {
            //if user logged in through facebook
            if(mAccessToken.getPermissions().contains("user_friends")) {
                //if user-friends permission is granted, fetch friends list
                fetchFriends();
            } else {
                //if user-friends permission is not granted, open permission dialog
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //when granted permission
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
                //permission granted
            } else {
                //if user did not grant user_location permission, open permission dialog
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //success
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
        } else {
            //if user logged in through account kit
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    //success
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

    //to handle login permissions results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //inflates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    //handles menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_account_info:
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //launches LoginActivity and finishes current activity
    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
}
