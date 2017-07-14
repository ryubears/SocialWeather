package com.example.android.socialweather;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListFragment extends Fragment implements WeatherAdapter.WeatherItemClickListener {
    @BindView(R.id.weather_recycler_view) RecyclerView mWeatherRecyclerView;

    //onClickListener to pass list item clicks that was received to MainActivity
    private ListItemClickListener mCallback;
    private WeatherAdapter mAdapter;

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

        long[] ids = {
                100000000000000L,
                100000000000001L,
                100000000000002L,
                100000000000003L,
                100000000000004L,
                100000000000005L,
                100000000000006L,
                100000000000007L,
                100000000000008L,
                100000000000009L
        };

        String[] profileUrls = {
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087",
                "https://scontent.xx.fbcdn.net/v/t1.0-1/c1.0.480.480/p480x480/16299556_776268709186751_2918404937696595424_n.jpg?oh=60cf38250a8aa7388741b958bb409468&oe=5A0C4087"
        };

        String[] names = {
                "Yehyun Ryu",
                "Yehyun Ryu",
                "Yehyun Ryu",
                "Yehyun Ryu",
                "Yehyun Ryu",
                "Yehyun Ryu",
                "Yehyun Ryu",
                "Yehyun Ryu",
                "Yehyun Ryu",
                "Yehyun Ryu"
        };

        String[] locations = {
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota",
                "Minneapolis, Minnesota"
        };

        //sets adapter to recycler view
        mAdapter = new WeatherAdapter(this, ids, profileUrls, names, locations);
        mWeatherRecyclerView.setAdapter(mAdapter);

        //sets layout manager to recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mWeatherRecyclerView.setLayoutManager(layoutManager);

        mWeatherRecyclerView.setHasFixedSize(true);

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
}
