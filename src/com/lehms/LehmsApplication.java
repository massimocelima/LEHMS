package com.lehms;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.inject.Module;
import com.lehms.IoC.Container;
import com.lehms.IoC.ContainerFactory;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.Permission;
import com.lehms.messages.dataContracts.RoleDataContract;
import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.serviceInterface.*;
import com.lehms.util.AppLog;

import roboguice.application.RoboApplication;

public class LehmsApplication extends RoboApplication 
		implements IIdentityProvider, 
			IProfileProvider , 
			IDeviceIdentifierProvider, 
			IDepartmentProvider, 
			IOfficeContactProvider, 
			IAuthorisationProvider,
			IActiveJobProvider,
			IDefualtDeviceAddressProvider
{
    public static final String KEY_PROFILE_PREF = "application_settings_profile_pref";
    public static final String KEY_DEVICE_ID_PREF = "application_settings_device_id_pref";
    public static final String KEY_DEPARTMENT_PREF = "application_settings_department_pref";
    public static final String KEY_OFFICE_PHONE_PREF = "application_settings_office_phone_pref";
    public static final String KEY_OFFICE_EMAIL_PREF = "application_settings_office_email_pref";

    public static final String KEY_USER = "application_data_user";
    public static final String KEY_JOB = "application_data_job";

    //public static final String KEY_USER_JOB = "current_user_pref";
    //public static final String KEY_CURRENT_JOB = "current_job_pref";

    private JobDetailsDataContract _job = null; 
	private UserDataContract _currentUser;
	private ISerializer _serializer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.application_settings, false);
		
		// Inits the container for the container factory
		ContainerFactory.SetContainer(new Container(this));
		_serializer  = ContainerFactory.Create().resolve(ISerializer.class);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	@Override
	protected void addApplicationModules(List<Module> modules) {
		super.addApplicationModules(modules);
		modules.add(new ConfigurationModule(this));
	}
	
	@Override
	public UserDataContract getCurrent() throws Exception {
/*		
		if( _currentUser == null )
		{
	        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			String userString = sharedPref.getString(KEY_USER, null);
			if( userString != null && ! userString.equals(""))
				_currentUser = _serializer.Deserializer(userString, UserDataContract.class);
		}
*/		
		return _currentUser;
	}

	@Override
	public void setCurrent(UserDataContract user) throws Exception {
		_currentUser = user;
/*        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if( user != null )
        	editor.putString(KEY_USER, _serializer.Serializer(user));
        else
        	editor.putString(KEY_USER, "");*/
	}

	@Override
	public void logout() throws Exception {
		setCurrent(null);
	}

	@Override
	public Boolean isAuthenticated() {
		return _currentUser != null;
	}
	
	public Profile getProfile()
	{
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPref.getString(KEY_PROFILE_PREF, "Development");
		return new Profile( Enum.valueOf(ProfileEnvironment.class, profileName) );
	}
	
	public String getDeviceId()
	{
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_DEVICE_ID_PREF, "DEV0001");
	}
	
	public String getDepartment()
	{
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_DEPARTMENT_PREF, "INS");
	}
	
	public String getOfficePhoneNumber()
	{
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_OFFICE_PHONE_PREF, "0410308497");
	}

	public String getOfficeEmail()
	{
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_OFFICE_EMAIL_PREF, "massimo_celima@tpg.com.au");
	}

	@Override
	public Boolean isAuthorised(Permission permission) {
		Boolean result = false;
		try {
			UserDataContract user = getCurrent();
			
			if( user != null )
			{
				RoleDataContract role;
				for(int i=0; i < user.Roles.size(); i++)
				{
					role = user.Roles.get(i);
					for(int j=0; j < role.Permissions.size(); j++)
					{
						if( role.Permissions.get(j).equals(permission) )
						{
							result = true;
							break;
						}
					}
					// We have found the permission, so exit the for loop
					if(result)
						break;
				}
			}
			
		} catch (Exception e) {
			result = false;
			AppLog.error(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public Boolean isInRole(String role) {
		Boolean result = false;
		try {
			UserDataContract user = getCurrent();
			if( user != null)
				result = user.IsInRole(role);
		} catch (Exception e) {
			result = false;
			AppLog.error(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public JobDetailsDataContract get() {
		/*
		if( _job == null )
		{
	        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			String jobString = sharedPref.getString(KEY_JOB, null);
			if( jobString != null && ! jobString.equals(""))
			{
				try {
					_job = _serializer.Deserializer(jobString, JobDetailsDataContract.class);
				} catch (Exception e) {
					_job = null;
				}
			}
		}
		*/
		return _job;
	}

	@Override
	public void set(JobDetailsDataContract job) {
		_job = job;
		/*
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if( job != null )
        {
			try {
				editor.putString(KEY_JOB, _serializer.Serializer(job));
			} catch (Exception e) {
	        	editor.putString(KEY_JOB, "");
			}
        }
		else
        	editor.putString(KEY_JOB, "");
        	*/
	}

	@Override
	public String getDeafultDeviceAddress(String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(key, "");
	}

	@Override
	public void setDeafultDeviceAddress(String key, String value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(key, value);
        editor.commit();
	}

}
