package com.example.android.socialweather.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Yehyun Ryu on 8/6/2017.
 */

public class WidgetService extends RemoteViewsService {
    //defining which adapter to attach to widget listview
    //adapter here refers to ListProvider (RemoteViewsFactory)
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListProvider(getApplicationContext(), intent);
    }
}
