package com.example.android.socialweather;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class ListFragment extends Fragment implements WeatherAdapter.WeatherItemClickListener {
    @BindView(R.id.weather_recycler_view) RecyclerView mWeatherRecyclerView;

    //onClickListener to pass list item clicks that was received to MainActivity
    private ListItemClickListener mCallback;
    private WeatherAdapter mAdapter;
    private AccessToken mAccessToken;

    private ArrayList<String> mFriendIds = new ArrayList<>();
    private ArrayList<String> mFriendNames = new ArrayList<>();
    private ArrayList<String> mFriendProfiles = new ArrayList<>();
    private ArrayList<String> mFriendLocations = new ArrayList<>();

    @Override
    public void onWeatherItemClick(int position) {
        mCallback.onListItemItemClick(position);
    }

    //onClickListener interface
    public interface ListItemClickListener {
        void onListItemItemClick(int position);
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate layout for this fragment and bind views using butterknife
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, rootView);

        //sets adapter to recycler view
        mAdapter = new WeatherAdapter(this, mFriendIds, mFriendProfiles, mFriendNames, mFriendLocations);
        mWeatherRecyclerView.setAdapter(mAdapter);

        //sets layout manager to recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mWeatherRecyclerView.setLayoutManager(layoutManager);
        mWeatherRecyclerView.setHasFixedSize(true);

        mAccessToken = AccessToken.getCurrentAccessToken();
        if(mAccessToken != null && mAccessToken.getPermissions().contains("user_friends")
                && mAccessToken.getPermissions().contains("user_location")) {
            //when user logged in with facebook with all permissions
            fetchFriends();
        } else {

        }

        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //checks if MainActivity implemented ListItemClickListener interface
        try {
            mCallback = (ListItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
              " must implement ListItemClickListener");
        }
    }

    //method that makes the API call to fetch friends list
    private void fetchFriends() {
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

                        //extract data
                        JSONObject jsonResponse = response.getJSONObject();
                        try {
                            JSONArray jsonData = jsonResponse.getJSONArray("data");
                            for(int i = 0; i < jsonData.length(); i++) {
                                JSONObject jsonUser = jsonData.getJSONObject(i);
                                String id = jsonUser.getString("id");
                                String name = jsonUser.getString("name");
                                String image = jsonUser.getJSONObject("picture").getJSONObject("data").getString("url");


                                mFriendIds.add(id);
                                mFriendNames.add(name);
                                mFriendProfiles.add(image);

                                String location = "";
                                if(jsonUser.isNull("location")) {
                                    location = "Unknown";
                                } else {
                                    location = jsonUser.getJSONObject("location").getString("name");
                                }
                                mFriendLocations.add(location);
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}
