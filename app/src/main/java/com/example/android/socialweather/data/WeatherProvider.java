package com.example.android.socialweather.data;

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
 * Created by Yehyun Ryu on 7/19/2017.
 */

public class WeatherProvider extends ContentProvider {

    //constants to match what type of Uri user is trying to use
    public static final int CODE_ALL_WEATHER = 100;
    public static final int CODE_SINGLE_WEATHER = 101;

    //UriMatcher match uris
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WeatherDbHelper mDbHelper;

    //method that returns appropirate constant for the type of uri
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WeatherContract.PATH_WEATHER, CODE_ALL_WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", CODE_SINGLE_WEATHER);

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
            case CODE_ALL_WEATHER:
                final SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.beginTransaction();
                int numRowsInserted = 0;
                try {
                    for (ContentValues value: values) {
                        long id = database.insert(
                                WeatherContract.WeatherEntry.TABLE_NAME,
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
    //for accountkit
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        switch(sUriMatcher.match(uri)) {
            case CODE_ALL_WEATHER:
                long id = mDbHelper.getWritableDatabase().insert(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        null,
                        values);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(WeatherContract.WeatherEntry.CONTENT_URI, id);
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
    //for details activity
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch(sUriMatcher.match(uri)) {
            case CODE_SINGLE_WEATHER: {
                String personId = uri.getLastPathSegment();
                selectionArgs = new String[] {personId};
                cursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherContract.WeatherEntry.COLUMN_PERSON_ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_ALL_WEATHER:
                cursor = mDbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
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
    //either for updating facebook friend list
    //or for deleting individual person data in accountkit
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        if(selection == null) {
            selection = "1";
        }

        switch(sUriMatcher.match(uri)) {
            case CODE_SINGLE_WEATHER:
                String personId = uri.getLastPathSegment();
                selectionArgs = new String[] {personId};
                numRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        WeatherContract.WeatherEntry.COLUMN_PERSON_ID + "=?",
                        selectionArgs);
                break;
            case CODE_ALL_WEATHER:
                numRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
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

    //updates individual person data
    //for account kit
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsUpdated;

        switch(sUriMatcher.match(uri)) {
            case CODE_SINGLE_WEATHER:
                String personId = uri.getLastPathSegment();
                selectionArgs = new String[] {personId};

                numRowsUpdated = mDbHelper.getWritableDatabase().update(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        values,
                        WeatherContract.WeatherEntry.COLUMN_PERSON_ID + "=?",
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
