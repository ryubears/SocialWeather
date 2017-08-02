package com.example.android.socialweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.android.socialweather.data.WeatherContract;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //add preferences defined in pref_settings.xml
        addPreferencesFromResource(R.xml.pref_settings);

        //sets summary for list preference
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        Preference preference = findPreference(getString(R.string.pref_units_key));
        setPreferenceSummary(preference, sharedPreferences.getString(getString(R.string.pref_units_key), ""));
    }

    //helper method that sets the summary of changed preferences
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        //ListPreference
        if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_units_key))) {
            //notify change in data to display correct temperature units
            getActivity().getContentResolver().notifyChange(WeatherContract.WeatherEntry.FACEBOOK_CONTENT_URI, null);
            getActivity().getContentResolver().notifyChange(WeatherContract.WeatherEntry.ACCOUNT_KIT_CONTENT_URI, null);
        }

        //set appropriate summary
        Preference preference = findPreference(key);
        if(preference instanceof ListPreference) {
            setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //unregister preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //register preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
}
