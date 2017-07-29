package com.example.android.socialweather.data;

/**
 * Created by Yehyun Ryu on 7/29/2017.
 */

public class Friend {
    private String mId;
    private String mName;
    private String mProfilePic;
    private String mLocationId;
    private String mLocationName;

    public Friend(String id, String name, String profilePic, String locationId, String locationName) {
        mId = id;
        mName = name;
        mProfilePic = profilePic;
        mLocationId = locationId;
        mLocationName = locationName;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getProfilePic() {
        return mProfilePic;
    }

    public String getLocationId() {
        return mLocationId;
    }

    public String getLocationName() {
        return mLocationName;
    }
}
