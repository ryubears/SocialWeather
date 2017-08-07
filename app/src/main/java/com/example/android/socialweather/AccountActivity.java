package com.example.android.socialweather;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.socialweather.data.WeatherContract.WeatherEntry;
import com.example.android.socialweather.sync.WeatherSyncUtils;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountActivity extends AppCompatActivity {
    @BindView(R.id.account_profile) ImageView mProfileImageView;
    @BindView(R.id.account_name_value) EditText mNameEditText;
    @BindView(R.id.account_location_value) EditText mLocationEditText;

    //friend position
    private int mPosition = -1;

    //friend attributes
    private String mProfileUrl;
    private String mOriginalName;
    private String mOriginalLocation;

    //boolean values to track state of activity
    private boolean mIsFacebook;
    private boolean mTextChanged;
    private boolean mIsAdd;

    //dialog builders and dialogs
    private AlertDialog.Builder mDiscardDialogBuilder;
    private AlertDialog.Builder mSaveDialogBuilder;
    private AlertDialog.Builder mDeleteDialogBuilder;
    private AlertDialog mDiscardDialog;
    private AlertDialog mSaveDialog;
    private AlertDialog mDeleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //bind views with butterknife
        ButterKnife.bind(this);

        //set up button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //text change listener for edit texts
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing
            }
        };

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(getString(R.string.account_position_key))) {
            mIsFacebook = intent.getBooleanExtra(getString(R.string.is_facebook_key), false); //saves how user logged in
            if(mIsFacebook) {
                //if user logged in with facebook

                //set title
                getSupportActionBar().setTitle(getString(R.string.account_view_title));

                //get profile url
                mProfileUrl = intent.getStringExtra(getString(R.string.account_profile_key));

                //transform profile picture in a circular frame
                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(100)
                        .oval(false)
                        .build();

                //load profile picture
                if(mProfileUrl != null && !mProfileUrl.equals(getString(R.string.picture_empty))) {
                    Picasso.with(this)
                            .load(mProfileUrl)
                            .transform(transformation)
                            .into(mProfileImageView);
                } else {
                    //load default profile icon
                    mProfileImageView.setImageResource(R.drawable.profile_white);
                }

                //disable editing
                mNameEditText.setFocusable(false);
                mLocationEditText.setFocusable(false);
            }

            //save that this activity was opened for editing
            mIsAdd = false;

            getSupportActionBar().setTitle(getString(R.string.account_edit_title));

            //set friend position
            mPosition = intent.getIntExtra(getString(R.string.account_position_key), -1);

            //save and display original friend name
            mOriginalName = intent.getStringExtra(getString(R.string.account_name_key));
            mNameEditText.setText(mOriginalName);

            //save and display original location
            mOriginalLocation = intent.getStringExtra(getString(R.string.account_location_key));
            if(mOriginalLocation.equals(getString(R.string.location_empty))) {
                mLocationEditText.setText(getString(R.string.location_default));
            } else {
                mLocationEditText.setText(mOriginalLocation);
            }
        } else {
            //save that this activity was opened for adding
            mIsAdd = true;
            getSupportActionBar().setTitle(getString(R.string.account_add_title));
        }

        //add text change listener
        mNameEditText.addTextChangedListener(textWatcher);
        mLocationEditText.addTextChangedListener(textWatcher);

        //setup all dialogs
        setDialogs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mIsFacebook) {
            //no menu for facebook logins
            return super.onCreateOptionsMenu(menu);
        } else {
            //inflate menus
            MenuInflater menuInflater = new MenuInflater(this);
            menuInflater.inflate(R.menu.account_menu, menu);
            if(mIsAdd) {
                //hide delete button
                menu.getItem(1).setVisible(false);
            }
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: //up button
                if(mTextChanged) {
                    //show discard dialog
                    mDiscardDialog.show();
                } else {
                    //return to previous activity
                    finish();
                }
                return true;
            case R.id.forecast_save_menu:
                if(!TextUtils.isEmpty(mNameEditText.getText()) && !TextUtils.isEmpty(mLocationEditText.getText())) {
                    //if there is text in both edit text view
                    mSaveDialog.show();
                } else {
                    //show toast message
                    Toast.makeText(this, getString(R.string.account_fill_fields_toast), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.forecast_delete_menu:
                //show delete dialog
                mDeleteDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(mTextChanged) {
            mDiscardDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void setDialogs() {
        //setup alert dialog builders
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDiscardDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            mSaveDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            mDeleteDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            mDiscardDialogBuilder = new AlertDialog.Builder(this);
            mSaveDialogBuilder = new AlertDialog.Builder(this);
            mDeleteDialogBuilder = new AlertDialog.Builder(this);
        }

        //set content in discard dialog
        mDiscardDialogBuilder
                .setTitle(getString(R.string.discard_title))
                .setMessage(getString(R.string.discard_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //return to previous activity
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });

        //set title, message for save dialog
        if(mIsAdd) {
            //for adding
            mSaveDialogBuilder
                    .setTitle(getString(R.string.add_title))
                    .setMessage(getString(R.string.add_message));
        } else {
            //for editing
            mSaveDialogBuilder
                    .setTitle(getString(R.string.edit_title))
                    .setMessage(getString(R.string.edit_message));
        }

        //set negative, positive button
        mSaveDialogBuilder
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get info from edit texts
                        String[] info = new String[] {mNameEditText.getText().toString(), mLocationEditText.getText().toString()};
                        if(mIsAdd) {
                            //check network connection
                            if(!isNetworkAvailable()) {
                                Toast.makeText(getApplicationContext(), getString(R.string.network_message), Toast.LENGTH_SHORT).show();
                            } else {
                                //add friend info
                                addFriend(info);
                                //display toast
                                Toast.makeText(getApplicationContext(), "Added Friend", Toast.LENGTH_SHORT).show();
                                //return to previous activity
                                finish();
                            }
                        } else {
                            if(!isNetworkAvailable()) {
                                Toast.makeText(getApplicationContext(), getString(R.string.network_message), Toast.LENGTH_SHORT).show();
                            } else {
                                //edit friend info
                                editFriend(info);
                                //display toast
                                Toast.makeText(getApplicationContext(), "Edit Successful", Toast.LENGTH_SHORT).show();
                                //return to main activity to prevent errors when location itself is deleted
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });

        //set content for delete dialog
        mDeleteDialogBuilder
                .setTitle(getString(R.string.delete_title))
                .setMessage(getString(R.string.delete_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!isNetworkAvailable()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.network_message), Toast.LENGTH_SHORT).show();
                        } else {
                            //delete friend
                            deleteFriend();
                            //display toast
                            Toast.makeText(getApplicationContext(), "Friend Deleted", Toast.LENGTH_SHORT).show();
                            //return to main activity to prevent errors when location itself is deleted
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });

        //create dialogs
        mDiscardDialog = mDiscardDialogBuilder.create();
        mSaveDialog = mSaveDialogBuilder.create();
        mDeleteDialog = mDeleteDialogBuilder.create();

        //remove top padding
        mDiscardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSaveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    //adds friend info
    private void addFriend(String[] info) {
        new AddFriendTask().execute(info);
    }

    //async task that handles adding friend info
    private class AddFriendTask extends AsyncTask<String[], Void, Void> {
        @Override
        protected Void doInBackground(String[]... infos) {
            //get name and location
            String friendName = infos[0][0];
            String friendLocation = infos[0][1];

            //query account kit table
            String[] projection = new String[] {WeatherEntry._ID, WeatherEntry.COLUMN_LOCATION_NAME, WeatherEntry.COLUMN_FRIEND_NAMES};
            Cursor cursor = getContentResolver().query(
                    WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );

            //get the row id to add friend
            int rowId = -1;
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                //location name for each row
                int indexLocationName = cursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
                String dataLocation = cursor.getString(indexLocationName);

                //try matching location
                if(friendLocation.equals(dataLocation)) {
                    //if current location equals one of the location in the table
                    int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                    rowId = cursor.getInt(indexId);
                    break;
                }
            }

            //content values to be used for inserting/updating
            ContentValues contentValues = new ContentValues();
            if(rowId == -1) {
                //when friend location did not match with any stored locations

                //put values into content provider
                contentValues.put(WeatherEntry.COLUMN_LAST_UPDATE_TIME, System.currentTimeMillis()); //add insert time
                contentValues.put(WeatherEntry.COLUMN_LOCATION_NAME, friendLocation);
                contentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, friendName);

                //insert, should notify change in database if there were no errors
                getContentResolver().insert(
                        WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                        contentValues
                );

                //add weather and place data
                WeatherSyncUtils.initialize(getApplicationContext(), mIsFacebook);
            } else {
                //when friend location matched with one of the stored locations

                //add friend name to friend names column
                int indexFriendNames = cursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
                String friendNames = cursor.getString(indexFriendNames);
                friendNames += getString(R.string.delimiter) + friendName;

                //put values into content provider
                contentValues.put(WeatherEntry.COLUMN_LAST_UPDATE_TIME, System.currentTimeMillis());
                contentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, friendNames);

                //update
                getContentResolver().update(
                        ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, rowId),
                        contentValues,
                        null,
                        null
                );
            }

            //close cursor to prevent memory leaks
            cursor.close();
            return null;
        }
    }

    //edits friend info
    private void editFriend(String[] info) {
        new EditFriendTask().execute(info);
    }

    //async task that edits friend info
    private class EditFriendTask extends AsyncTask<String[], Void, Void> {

        @Override
        protected Void doInBackground(String[]... infos) {
            //get current name and location
            String friendName = infos[0][0];
            String friendLocation  = infos[0][1];

            //return early if there were no changes to original value for name and location
            if(mOriginalName.equals(friendName) && mOriginalLocation.equals(friendLocation)) {
                return null;
            }

            //query account kit table
            String[] projection = new String[] {WeatherEntry._ID, WeatherEntry.COLUMN_LOCATION_NAME, WeatherEntry.COLUMN_FRIEND_NAMES};
            Cursor cursor = getContentResolver().query(
                    WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );

            if (!mOriginalLocation.equals(friendLocation)) {
                //when location changed

                //delete user name from original location
                //get find original location row id
                int rowId = -1; //rowId will never be -1 since original location is already stored
                for(int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    //location name for each row
                    int indexLocationName = cursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
                    String dataLocation = cursor.getString(indexLocationName);
                    //find and save row id
                    if(mOriginalLocation.equals(dataLocation)) {
                        int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                        rowId = cursor.getInt(indexId);
                        break;
                    }
                }

                //get original location friend names
                int indexFriendNames = cursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
                String[] friendArray = cursor.getString(indexFriendNames).split(getString(R.string.delimiter));
                if(friendArray.length == 1) {
                    //delete the entire row if current friend is the only person living there
                    getContentResolver().delete(
                            ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, rowId),
                            null,
                            null
                    );
                } else {
                    //delete friend name from original locations' friend names
                    String originalFriendNames = "";
                    for(int i = 0; i < friendArray.length; i++) {
                        //exclude current friend
                        if(i != mPosition) {
                            if(mPosition == friendArray.length - 1 && i == mPosition - 1) {
                                originalFriendNames += friendArray[i];
                            } else if(i != friendArray.length - 1) {
                                originalFriendNames += friendArray[i] + getString(R.string.delimiter);
                            } else {
                                originalFriendNames += friendArray[i];
                            }
                        }
                    }

                    //update(delete friend name)
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, originalFriendNames);
                    getContentResolver().update(
                            ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, rowId),
                            contentValues,
                            null,
                            null
                    );
                }

                //check if new location matches stored locations
                rowId = -1;
                for(int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    //location from each row
                    int indexLocationName = cursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
                    String dataLocation = cursor.getString(indexLocationName);
                    if(friendLocation.equals(dataLocation)) {
                        //save row id if location matches
                        int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                        rowId = cursor.getInt(indexId);
                        break;
                    }
                }

                //content values to be used to insert/update
                ContentValues contentValues = new ContentValues();
                if(rowId == -1) {
                    //if current location is new

                    //put values
                    contentValues = new ContentValues();
                    contentValues.put(WeatherEntry.COLUMN_LAST_UPDATE_TIME, System.currentTimeMillis());
                    contentValues.put(WeatherEntry.COLUMN_LOCATION_NAME, friendLocation);
                    contentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, friendName);

                    //insert new row
                    getContentResolver().insert(
                            WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                            contentValues
                    );

                    //get weather and place photo data
                    WeatherSyncUtils.initialize(getApplicationContext(), mIsFacebook);
                } else {
                    //if there is a matching location

                    //add current friend name to location's friend names
                    String currentFriendNames = "";
                    currentFriendNames = cursor.getString(indexFriendNames);
                    currentFriendNames += getString(R.string.delimiter) + friendName;

                    //put values
                    contentValues.put(WeatherEntry.COLUMN_LAST_UPDATE_TIME, System.currentTimeMillis());
                    contentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, currentFriendNames);

                    //update this location row
                    getContentResolver().update(
                            ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, rowId),
                            contentValues,
                            null,
                            null
                    );
                }
            } else {
                //if only name changed

                //find location row id
                int rowId = -1; //row id will never be -1 since location did not change
                for(int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    //location for each row
                    int indexLocationName = cursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
                    String dataLocation = cursor.getString(indexLocationName);
                    if(mOriginalLocation.equals(dataLocation)) {
                        //save row id
                        int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                        rowId = cursor.getInt(indexId);
                        break;
                    }
                }

                //find and edit friend name
                int indexFriendNames = cursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
                String[] friendArray = cursor.getString(indexFriendNames).split(getString(R.string.delimiter));
                friendArray[mPosition] = friendName;
                String friendNames = "";
                for(int i = 0; i < friendArray.length; i++) {
                    friendNames += friendArray[i] + getString(R.string.delimiter);
                }

                //update row
                ContentValues contentValues = new ContentValues();
                contentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, friendNames.substring(0, friendNames.length() - 3));
                getContentResolver().update(
                        ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, rowId),
                        contentValues,
                        null,
                        null
                );
            }

            //close cursor to prevent memory leaks
            cursor.close();
            return null;
        }
    }

    //deletes friend info
    private void deleteFriend() {
        new DeleteFriendTask().execute();
    }

    //async task that handles deleting friend info
    private class DeleteFriendTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //query account kit table
            String[] projection = new String[] {WeatherEntry._ID, WeatherEntry.COLUMN_LOCATION_NAME, WeatherEntry.COLUMN_FRIEND_NAMES};
            Cursor cursor = getContentResolver().query(
                    WeatherEntry.ACCOUNT_KIT_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );

            //find location row id
            int rowId = -1;
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int indexLocationName = cursor.getColumnIndex(WeatherEntry.COLUMN_LOCATION_NAME);
                String dataLocation = cursor.getString(indexLocationName);
                if(mOriginalLocation.equals(dataLocation)) {
                    int indexId = cursor.getColumnIndex(WeatherEntry._ID);
                    rowId = cursor.getInt(indexId);
                    break;
                }
            }

            //get friend names string
            int indexFriendNames = cursor.getColumnIndex(WeatherEntry.COLUMN_FRIEND_NAMES);
            String friendNames = cursor.getString(indexFriendNames);
            String[] friendArray = friendNames.split(getString(R.string.delimiter));

            if(friendArray.length == 1) {
                //delete entire row if friend is the only person living there
                getContentResolver().delete(
                        ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, rowId),
                        null,
                        null
                );
            } else {
                //delete friend name from current location
                friendNames = "";
                for(int i = 0; i < friendArray.length; i++) {
                    //exclude current friend name
                    if(i != mPosition) {
                        if(mPosition == friendArray.length - 1 && i == mPosition - 1) {
                            friendNames += friendArray[i];
                        } else if(i != friendArray.length - 1) {
                            friendNames += friendArray[i] + getString(R.string.delimiter);
                        } else {
                            friendNames += friendArray[i];
                        }
                    }
                }

                //update(delete friend name)
                ContentValues contentValues = new ContentValues();
                contentValues.put(WeatherEntry.COLUMN_FRIEND_NAMES, friendNames);
                getContentResolver().update(
                        ContentUris.withAppendedId(WeatherEntry.ACCOUNT_KIT_CONTENT_URI, rowId),
                        contentValues,
                        null,
                        null
                );
            }

            //close cursor to prevent memory leaks
            cursor.close();
            return null;
        }
    }

    //code snippet from stack overflow https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        //create connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //get active network info
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //return state of active network
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
