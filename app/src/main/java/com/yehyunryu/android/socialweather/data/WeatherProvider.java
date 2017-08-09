package com.yehyunryu.android.socialweather.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

public class WeatherProvider extends ContentProvider {

    //constants to match what type of Uri user is trying to use
    public static final int FACEBOOK_ALL_WEATHER = 100;
    public static final int FACEBOOK_SINGLE_WEATHER = 101;
    public static final int ACCOUNT_KIT_ALL_WEATHER = 200;
    public static final int ACCOUNT_KIT_SINGLE_WEATHER = 201;

    //UriMatcher match uris
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WeatherDbHelper mDbHelper;

    //method that returns appropirate constant for the type of uri
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WeatherContract.PATH_FACEBOOK, FACEBOOK_ALL_WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_FACEBOOK + "/#", FACEBOOK_SINGLE_WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_ACCOUNT_KIT, ACCOUNT_KIT_ALL_WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_ACCOUNT_KIT + "/#", ACCOUNT_KIT_SINGLE_WEATHER);

        return matcher;
    }

    //initialize content provider
    @Override
    public boolean onCreate() {
        mDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    //inserts multiple rows of data at once
    //for facebook friend list
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch(sUriMatcher.match(uri)) {
            case FACEBOOK_ALL_WEATHER:
                final SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.beginTransaction();
                int numRowsInserted = 0;
                try {
                    for (ContentValues value: values) {
                        long id = database.insert(
                                WeatherContract.WeatherEntry.FACEBOOK_TABLE_NAME,
                                null,
                                value);
                        if(id != -1) {
                            numRowsInserted++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }

                if(numRowsInserted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return numRowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    //inserts one person
    //for account kit table
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        switch(sUriMatcher.match(uri)) {
            case ACCOUNT_KIT_ALL_WEATHER:
                long id = mDbHelper.getWritableDatabase().insert(
                        WeatherContract.WeatherEntry.ACCOUNT_KIT_TABLE_NAME,
                        null,
                        values);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(WeatherContract.WeatherEntry.ACCOUNT_KIT_CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row. Return id: " + id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    //queries data for specific person
    //for both facebook and account kit
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch(sUriMatcher.match(uri)) {
            case FACEBOOK_SINGLE_WEATHER: {
                String id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                cursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.FACEBOOK_TABLE_NAME,
                        projection,
                        WeatherContract.WeatherEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FACEBOOK_ALL_WEATHER:
                cursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.FACEBOOK_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ACCOUNT_KIT_SINGLE_WEATHER:
                String id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                cursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.ACCOUNT_KIT_TABLE_NAME,
                        projection,
                        WeatherContract.WeatherEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ACCOUNT_KIT_ALL_WEATHER:
                cursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.ACCOUNT_KIT_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }

        if(cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    //deletes entire weather table or specific person data
    //for deleting facebook table before a sync
    //or for deleting individual person data in account kit
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        if(selection == null) {
            selection = "1";
        }

        switch(sUriMatcher.match(uri)) {
            case FACEBOOK_ALL_WEATHER:
                numRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.FACEBOOK_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ACCOUNT_KIT_SINGLE_WEATHER:
                String id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                numRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.ACCOUNT_KIT_TABLE_NAME,
                        WeatherContract.WeatherEntry._ID + "=?",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        if(numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    //for mass updates in either table
    //or for individual update in account kit
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsUpdated;

        switch(sUriMatcher.match(uri)) {
            case FACEBOOK_SINGLE_WEATHER:
                String id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};

                numRowsUpdated = mDbHelper.getWritableDatabase().update(
                        WeatherContract.WeatherEntry.FACEBOOK_TABLE_NAME,
                        values,
                        WeatherContract.WeatherEntry._ID + "=?",
                        selectionArgs);
                break;
            case ACCOUNT_KIT_SINGLE_WEATHER:
                String rowId = uri.getLastPathSegment();
                selectionArgs = new String[] {rowId};

                numRowsUpdated = mDbHelper.getWritableDatabase().update(
                        WeatherContract.WeatherEntry.ACCOUNT_KIT_TABLE_NAME,
                        values,
                        WeatherContract.WeatherEntry._ID + "=?",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        if(numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in SocialWeather");
    }
}
