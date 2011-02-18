package com.lehms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;


public class ApplicationSettings extends PreferenceActivity {

    public static final String KEY_PROFILE_PREF = "application_settings_profile_pref";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.application_settings);
    }

}
