package com.example.android.socialweather;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.socialweather.data.WeatherContract;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.home_recycler_view) RecyclerView mHomeRecyclerView;
    @BindView(R.id.home_empty_view) TextView mHomeEmptyView;
    @BindView(R.id.home_progress_bar) ProgressBar mHomeProgressBar;


    private static final String LOG_TAG = HomeFragment.class.getSimpleName(); //log tag for debugging
    private static final String INITIALIZE_KEY = "initialize"; //string key for storing whether friend data has been initialized
    private static final int WEATHER_LOADER_ID = 33; //loader id for weather data

    private boolean mFriendInitialized; //stores state of friend initialization

    private WeatherAdapter mAdapter;  //adapter for recycler view in this fragment
    private AccessToken mAccessToken; //facebook access token

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate layout for this fragment and bind views using butterknife
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);

        //sets adapter to recycler view
        mAdapter = new WeatherAdapter();
        mHomeRecyclerView.setAdapter(mAdapter);

        //sets layout manager to recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mHomeRecyclerView.setLayoutManager(layoutManager);
        mHomeRecyclerView.setHasFixedSize(true);

        //initialize loader
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(WEATHER_LOADER_ID, null, this);

        //get state of friend initialization
        if(savedInstanceState != null) {
            mFriendInitialized = savedInstanceState.getBoolean(INITIALIZE_KEY);
        }

        mAccessToken = AccessToken.getCurrentAccessToken();
        if(mAccessToken != null && mAccessToken.getPermissions().contains("user_friends")
                && mAccessToken.getPermissions().contains("user_location")) {
            //when user logged in with facebook with all permissions

            //show progress bar
            mHomeProgressBar.setVisibility(View.VISIBLE);

            //when friend data has not been initialized
            if(!mFriendInitialized) {
                mFriendInitialized = true;
                //check if data is empty
                checkEmpty();
            }
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    //method that calls async task that checks if weather data is empty
    private void checkEmpty() {
        new DataEmptyQuery().execute();
    }

    private class DataEmptyQuery extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            //query weather data to check if it is empty
            Cursor cursor = getContext().getContentResolver().query(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    new String[] {WeatherContract.WeatherEntry._ID}, //just one column since this is just to check if data is empty
                    null,
                    null,
                    null
            );

            boolean isEmpty;
            if(cursor == null || cursor.getCount() == 0) {
                isEmpty = true;
            } else {
                isEmpty = false;
            }

            //close cursor to prevent memory leaks
            if(cursor != null) {
                cursor.close();
            }

            return isEmpty;
        }

        @Override
        protected void onPostExecute(Boolean isEmpty) {
            super.onPostExecute(isEmpty);

            if(isEmpty) {
                syncFriends();
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

                        ArrayList<String> friendIds = new ArrayList<String>();
                        ArrayList<String> friendNames = new ArrayList<String>();
                        ArrayList<String> friendProfilePics = new ArrayList<String>();
                        ArrayList<String> friendLocations = new ArrayList<String>();

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
                                String location = "";
                                if(jsonUser.isNull("location")) {
                                    location = getString(R.string.location_empty);
                                } else {
                                    location = jsonUser.getJSONObject("location").getString("name");
                                }

                                friendIds.add(id);
                                friendNames.add(name);
                                friendProfilePics.add(image);
                                friendLocations.add(location);
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        //put data inside ContentValues[]
                        ContentValues[] contentValues = new ContentValues[friendIds.size()];
                        for(int i = 0; i < friendIds.size(); i++) {
                            ContentValues values = new ContentValues();
                            values.put(WeatherContract.WeatherEntry.COLUMN_PERSON_ID, friendIds.get(i));
                            values.put(WeatherContract.WeatherEntry.COLUMN_PERSON_NAME, friendNames.get(i));
                            values.put(WeatherContract.WeatherEntry.COLUMN_PERSON_PROFILE, friendProfilePics.get(i));
                            values.put(WeatherContract.WeatherEntry.COLUMN_PERSON_LOCATION, friendLocations.get(i));

                            contentValues[i] = values;
                        }

                        //deletes existing weather data
                        getContext().getContentResolver().delete(
                                WeatherContract.WeatherEntry.CONTENT_URI,
                                null,
                                null
                        );

                        //inserts fresh friend list data
                        getContext().getContentResolver().bulkInsert(
                                WeatherContract.WeatherEntry.CONTENT_URI,
                                contentValues
                        );
                    }
                }
        ).executeAsync();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case WEATHER_LOADER_ID:
                //query entire weather data
                return new CursorLoader(
                        getContext(),
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            default:
                throw new RuntimeException("This loader is not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
        mHomeProgressBar.setVisibility(View.GONE);
        mHomeRecyclerView.setVisibility(View.VISIBLE);
        mHomeEmptyView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        mHomeProgressBar.setVisibility(View.GONE);
        mHomeRecyclerView.setVisibility(View.GONE);
        mHomeEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save friend initialization state
        outState.putBoolean(INITIALIZE_KEY, mFriendInitialized);
    }
}
