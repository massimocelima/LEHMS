package com.lehms;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.inject.Module;
import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.service.*;

import roboguice.application.RoboApplication;

public class LehmsApplication extends RoboApplication 
		implements IIdentityProvider, IProfileProvider 
{

	private UserDataContract _currentUser;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.application_settings, false);
	}
	
	@Override
	protected void addApplicationModules(List<Module> modules) {
		super.addApplicationModules(modules);
		modules.add(new ConfigurationModule(this));
	}

	public void Logout() 
	{
		_currentUser = null;
	}

	@Override
	public UserDataContract getCurrent() throws Exception {
		// TODO Auto-generated method stub
		return _currentUser;
	}

	@Override
	public void setCurrent(UserDataContract user) throws Exception {
		_currentUser = user;
	}

	@Override
	public void logout() throws Exception {
		_currentUser = null;
	}

	@Override
	public Boolean isAuthenticated() {
		return _currentUser != null;
	}
	
	public Profile getProfile()
	{
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPref.getString(ApplicationSettings.KEY_PROFILE_PREF, "Development");
		return new Profile( Enum.valueOf(ProfileEnvironment.class, profileName) );
	}

}
