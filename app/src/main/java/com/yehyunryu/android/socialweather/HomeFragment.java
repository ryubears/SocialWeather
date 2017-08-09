package com.yehyunryu.android.socialweather;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.yehyunryu.android.socialweather.data.Friend;
import com.yehyunryu.android.socialweather.data.WeatherContract;
import com.yehyunryu.android.socialweather.sync.WeatherSyncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

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

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.home_swipe_refresh_layout) SwipeRefreshLayout mHomeSwipeRefreshLayout;
    @BindView(R.id.home_recycler_view) RecyclerView mHomeRecyclerView;
    @BindView(R.id.home_empty_view) LinearLayout mHomeEmptyView;
    @BindView(R.id.home_empty_text_view) TextView mHomeEmptyTextView;

    private static final String INITIALIZE_KEY = "initialize"; //string key for storing whether friend data has been initialized
    private static final int WEATHER_LOADER_ID = 33; //loader id for weather data

    private boolean mFriendInitialized; //stores state of friend initialization

    private WeatherAdapter mAdapter;  //adapter for recycler view in this fragment
    private AccessToken mAccessToken; //facebook access token
    private boolean mIsFacebook; //stores how user logged in

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate layout for this fragment and bind views using butterknife
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);

        //sets layout manager to recycler view
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mHomeRecyclerView.setLayoutManager(layoutManager);
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            mHomeRecyclerView.setLayoutManager(layoutManager);
        }
        mHomeRecyclerView.setHasFixedSize(true);

        //set swipe refresh listener
        mHomeSwipeRefreshLayout.setOnRefreshListener(this);

        //get state of friend initialization
        mAccessToken = AccessToken.getCurrentAccessToken();

        //hide or show add menu depending on how user logged in
        if(mAccessToken != null && mAccessToken.getPermissions().contains("user_friends")
                && mAccessToken.getPermissions().contains("user_location")) {
            mIsFacebook = true;
            setHasOptionsMenu(false);
        } else {
            mIsFacebook = false;
            setHasOptionsMenu(true);
        }

        //sets adapter to recycler view
        mAdapter = new WeatherAdapter(mIsFacebook);
        mHomeRecyclerView.setAdapter(mAdapter);

        //initialize loader
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(WEATHER_LOADER_ID, null, this);

        //avoid checking data if friend data has already been initialized
        //for cases like screen rotation
        if(!mFriendInitialized) {
            mFriendInitialized = true;
            //check if data is empty
            checkData();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    //refresh data
    @Override
    public void onRefresh() {
        //check network connection
        if(!isNetworkAvailable()) {
            //show network message
            Toast.makeText(getContext(), getString(R.string.network_message), Toast.LENGTH_SHORT).show();
            mHomeSwipeRefreshLayout.setRefreshing(false); //hide refresh indicator
            return;
        }

        //set clicking on weather items to false to prevent errors
        mAdapter.setClickable(false);
        if(mIsFacebook) {
            //refresh friend data and query weather and photo info
            syncFriends();
        } else {
            //refresh weather and photo info
            WeatherSyncUtils.initialize(getContext(), mIsFacebook);
        }
    }

    //method that calls async task that checks if weather data is empty or outdated
    private void checkData() {
        new DataCheckQuery().execute();
    }

    private class DataCheckQuery extends AsyncTask<Void, Void, Integer> {
        private static final int DATA_EMPTY = 0;
        private static final int DATA_OUTDATED = 1;
        private static final int DATA_PRESENT = 2;

        @Override
        protected Integer doInBackground(Void... voids) {
            //query weather data to check if it is empty or outdated
            Cursor cursor;
            if(mIsFacebook) {
                cursor = getContext().getContentResolver().query(
                        WeatherContract.WeatherEntry.FACEBOOK_CONTENT_URI,
                        new String[] {WeatherContract.WeatherEntry.COLUMN_LAST_UPDATE_TIME}, //just one column since this is just to check if data is empty or outdated
                        null,
                        null,
                        WeatherContract.WeatherEntry.COLUMN_LAST_UPDATE_TIME + " ASC" //in ascending order to get the oldest(smallest) update time
                );
            } else {
                cursor = getContext().getContentResolver().query(
                        WeatherContract.WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                        new String[] {WeatherContract.WeatherEntry.COLUMN_LAST_UPDATE_TIME}, //just one column since this is just to check if data is empty or outdated
                        null,
                        null,
                        WeatherContract.WeatherEntry.COLUMN_LAST_UPDATE_TIME + " ASC" //in ascending order to get the oldest(smallest) update time
                );
            }

            int dataState;
            if(cursor == null || cursor.getCount() == 0) {
                dataState = DATA_EMPTY;
            } else {
                //check if data is outdated
                //data is outdated if they are older than four hours
                cursor.moveToFirst();
                int indexLastUpdateTime = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LAST_UPDATE_TIME);
                long lastUpdateTime = cursor.getLong(indexLastUpdateTime);
                long currentTime = System.currentTimeMillis();
                long maxHour = 3;
                if((currentTime - lastUpdateTime) >= TimeUnit.HOURS.toMillis(maxHour)) {
                    dataState = DATA_OUTDATED;
                } else {
                    dataState = DATA_PRESENT;
                }
            }

            //close cursor to prevent memory leaks
            if(cursor != null) {
                cursor.close();
            }

            return dataState;
        }

        @Override
        protected void onPostExecute(Integer dataState) {
            super.onPostExecute(dataState);

            if(dataState == DATA_EMPTY) {
                if(mIsFacebook) {
                    //check network connection
                    if(!isNetworkAvailable()) {
                        Toast.makeText(getContext(), getString(R.string.network_message), Toast.LENGTH_SHORT).show();
                    } else {
                        //sync with friend data if data is empty
                        syncFriends();
                    }
                }
            } else if(dataState == DATA_OUTDATED) {
                //check network connection
                if(!isNetworkAvailable()) {
                    Toast.makeText(getContext(), getString(R.string.network_message), Toast.LENGTH_SHORT).show();
                } else {
                    //update facebook weather data if data is outdated
                    WeatherSyncUtils.initialize(getContext(), mIsFacebook);
                }
            }
        }
    }

    //method that makes the API call to fetch friends list
    public void syncFriends() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture.height(200).width(200),location"); //fields to extract data from
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
                            Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
                            return;
                        }

                        int numLocations = 0;
                        ArrayList<Friend> friends = new ArrayList<Friend>();
                        ArrayList<String> friendLocationIds = new ArrayList<String>();

                        //extract data
                        JSONObject jsonResponse = response.getJSONObject();
                        try {
                            JSONArray jsonData = jsonResponse.getJSONArray("data");
                            for(int i = 0; i < jsonData.length(); i++) {
                                JSONObject jsonUser = jsonData.getJSONObject(i);
                                String id = jsonUser.getString("id");
                                String name = jsonUser.getString("name");

                                //check if profile picture is empty
                                String image = "";
                                if(jsonUser.isNull("picture")) {
                                    image = getString(R.string.picture_empty);
                                } else {
                                    image = jsonUser.getJSONObject("picture").getJSONObject("data").getString("url");
                                }

                                //check if location data is empty
                                String locationId = "";
                                String locationName = "";
                                if(jsonUser.isNull("location")) {
                                    locationName = getString(R.string.location_empty);
                                    locationId = "-1";
                                } else {
                                    JSONObject locationJson = jsonUser.getJSONObject("location");
                                    locationId = locationJson.getString("id");
                                    locationName = locationJson.getString("name");
                                }

                                if(!friendLocationIds.contains(locationId)) {
                                    friendLocationIds.add(locationId);
                                }

                                friends.add(new Friend(id, name, image, locationId, locationName));
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        //put data inside ContentValues[]
                        ContentValues[] contentValues = new ContentValues[friendLocationIds.size()];
                        for(int i = 0; i < friendLocationIds.size(); i++) {
                            String locationId = friendLocationIds.get(i);

                            String locationName = "";
                            String friendIds = "";
                            String friendNames = "";
                            String friendPictures = "";

                            for(int x = 0; x < friends.size(); x++) {
                                Friend friend = friends.get(x);
                                if(friend.getLocationId().equals(locationId)) {
                                    locationName = friend.getLocationName();
                                    friendIds += friend.getId() + getString(R.string.delimiter);
                                    friendNames += friend.getName() + getString(R.string.delimiter);
                                    friendPictures += friend.getProfilePic() + getString(R.string.delimiter);
                                }
                            }

                            ContentValues values = new ContentValues();
                            values.put(WeatherContract.WeatherEntry.COLUMN_LAST_UPDATE_TIME, System.currentTimeMillis());
                            values.put(WeatherContract.WeatherEntry.COLUMN_LOCATION_ID, locationId);
                            values.put(WeatherContract.WeatherEntry.COLUMN_LOCATION_NAME, locationName);
                            values.put(WeatherContract.WeatherEntry.COLUMN_FRIEND_IDS, friendIds.substring(0, friendIds.length() - 3));
                            values.put(WeatherContract.WeatherEntry.COLUMN_FRIEND_NAMES, friendNames.substring(0, friendNames.length() - 3));
                            values.put(WeatherContract.WeatherEntry.COLUMN_FRIEND_PICTURES, friendPictures.substring(0, friendPictures.length() - 3));

                            contentValues[i] = values;
                        }

                        //deletes existing weather data
                        getContext().getContentResolver().delete(
                                WeatherContract.WeatherEntry.FACEBOOK_CONTENT_URI,
                                null,
                                null
                        );

                        //inserts fresh friend list data
                        int rowsInserted = getContext().getContentResolver().bulkInsert(
                                WeatherContract.WeatherEntry.FACEBOOK_CONTENT_URI,
                                contentValues
                        );

                        //initialize weather data if there are friends, :( sad life
                        if(rowsInserted != 0) {
                            WeatherSyncUtils.initialize(getContext(), mIsFacebook);
                        }
                    }
                }
        ).executeAsync();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case WEATHER_LOADER_ID:
                //query entire weather data
                if(mIsFacebook) {
                    return new CursorLoader(
                            getContext(),
                            WeatherContract.WeatherEntry.FACEBOOK_CONTENT_URI,
                            null,
                            null,
                            null,
                            null
                    );
                } else {
                    return new CursorLoader(
                            getContext(),
                            WeatherContract.WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                            null,
                            null,
                            null,
                            null
                    );
                }
            default:
                throw new RuntimeException("This loader is not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.cancelLoadingToast(); //cancel loading toast if exist
        mAdapter.setClickable(true); //make clicking on weather items possible
        mHomeSwipeRefreshLayout.setRefreshing(false); //hide refreshing animation
        //change data in weather adapter
        mAdapter.swapCursor(data);
        if(data == null || data.getCount() == 0) {
            showEmptyView();
        } else {
            showFriends();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void showFriends() {
        mHomeRecyclerView.setVisibility(View.VISIBLE);
        mHomeEmptyView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        mHomeRecyclerView.setVisibility(View.GONE);
        mHomeEmptyView.setVisibility(View.VISIBLE);

        //set appropriate empty text
        if(mIsFacebook) {
            mHomeEmptyTextView.setText(getString(R.string.home_empty_view_facebook));
        } else {
            mHomeEmptyTextView.setText(getString(R.string.home_empty_view_account_kit));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save friend initialization state
        outState.putBoolean(INITIALIZE_KEY, mFriendInitialized);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home_add:
                //handles add menu
                Intent intent = new Intent(getContext(), AccountActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //code snippet from stack overflow https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        //create connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //get active network info
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //return state of active network
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
