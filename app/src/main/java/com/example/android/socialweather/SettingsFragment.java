package com.example.android.socialweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //add preferences defined in pref_settings.xml
        addPreferencesFromResource(R.xml.pref_settings);

        //sets summary for list preferences
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for(int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if(p instanceof ListPreference) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
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
            //change temperature unit to appropriate unit
        }

        //set appropriate summary
        Preference preference = findPreference(key);
        if(preference != null) {
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
