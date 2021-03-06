package com.yehyunryu.android.socialweather.data;

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
