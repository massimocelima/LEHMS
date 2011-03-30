package com.lehms;

import com.lehms.serviceInterface.Profile;
import com.lehms.serviceInterface.ProfileEnvironment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class ApplicationSettings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.application_settings);
        
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPref.getString(LehmsApplication.KEY_PROFILE_PREF, "");
        if(profileName.equals(""))
        	sharedPref.edit().putString(LehmsApplication.KEY_PROFILE_PREF, "Testing");
    }
}
