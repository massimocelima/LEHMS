package com.lehms;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.inject.Module;
import com.lehms.IoC.Container;
import com.lehms.IoC.ContainerFactory;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.LocationDataContract;
import com.lehms.messages.dataContracts.Permission;
import com.lehms.messages.dataContracts.RoleDataContract;
import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.service.GPSLoggerService;
import com.lehms.serviceInterface.*;
import com.lehms.ui.clinical.device.ECGAATOSMeasurementDevice;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.BloodSugerLevelMeasurement;
import com.lehms.ui.clinical.model.ECGMeasurement;
import com.lehms.ui.clinical.model.INRMeasurement;
import com.lehms.ui.clinical.model.Measurement;
import com.lehms.ui.clinical.model.MeasurementSummary;
import com.lehms.ui.clinical.model.MeasurementTypeEnum;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;
import com.lehms.ui.clinical.model.UrineMeasurement;
import com.lehms.ui.clinical.model.WeightMeasurement;
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
			IDefualtDeviceAddressProvider,
			ITracker,
			IPreviousMeasurmentProvider,
			IDutyManager
{
    public static final String KEY_PROFILE_PREF = "application_settings_profile_pref";
    public static final String KEY_DEVICE_ID_PREF = "application_settings_device_id_pref";
    public static final String KEY_DEPARTMENT_PREF = "application_settings_department_pref";
    public static final String KEY_OFFICE_PHONE_PREF = "application_settings_office_phone_pref";
    public static final String KEY_OFFICE_EMAIL_PREF = "application_settings_office_email_pref";
    public static final String KEY_OFFICE_FAX_PREF = "application_settings_office_fax_pref";

    public static final String KEY_ALARM_PHONE_PREF = "application_settings_alarm_phone_pref";
    public static final String KEY_ALARM_SMS_PREF = "application_settings_alarm_sms_number_pref";
    public static final String KEY_SERVCIDE_PHONE_PREF = "application_settings_service_phone_pref";
    public static final String KEY_CALL_CENTRE_PHONE_PREF = "application_settings_call_centre_phone_pref";
    
    public static final String KEY_USER = "application_data_user";
    public static final String KEY_JOB = "application_data_job";

    public static final String KEY_ON_DUTY = "application_on_duty_pref";

	private ISerializer _serializer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.application_settings, false);
		
		// Inits the container for the container factory
		ContainerFactory.SetContainer(new Container(this));
		_serializer  = ContainerFactory.Create().resolve(ISerializer.class);
		
		// Starts the tracking and anything else to do with being on duty
		if(IsOnDuty())
			OnDuty();
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
		return getSerializable( "current_user", UserDataContract.class);
	}

	@Override
	public void setCurrent(UserDataContract user) throws Exception {
		putSerializable("current_user", user);
	}

	@Override
	public void logout() throws Exception {
		setCurrent(null);
	}

	@Override
	public Boolean isAuthenticated() {
		try {
			return getCurrent() != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Profile getProfile()
	{
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String profileName = sharedPref.getString(KEY_PROFILE_PREF, "Testing");
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

	public String getOfficeFax()
	{
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_OFFICE_FAX_PREF, "0410308497");
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
		return getSerializable("current_job", JobDetailsDataContract.class);
	}

	@Override
	public void set(JobDetailsDataContract job) {
		putSerializable("current_job", job);
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

	@Override
	public String getAlarmPhoneNumber() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_ALARM_PHONE_PREF, "0410308497");
	}

	@Override
	public String getAlarmSmsNumber() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_ALARM_SMS_PREF, "6410308497");
	}

	@Override
	public String getServicePhoneNumber() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_SERVCIDE_PHONE_PREF, "0410308497");
	}

	@Override
	public String getCallCentrePhoneNumber() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(KEY_CALL_CENTRE_PHONE_PREF, "0410308497");
	}

	@Override
	public LocationDataContract getLastLocation() {
		LocationDataContract result = null;
		try {
		    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		    String locationSerilised = sharedPref.getString("current_location", null);
		    if(locationSerilised != null)
		    	result = _serializer.Deserializer(locationSerilised, LocationDataContract.class);
		} catch (Exception e) {
			AppLog.error("Error saving current location: " + e.getMessage());
		}
		return result;
	}

	@Override
	public void putLastLocation(LocationDataContract location) {
		try {
			String locationSerilised = _serializer.Serializer(location);
		    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		    Editor editor = sharedPref.edit();
		    editor.putString("current_location", locationSerilised);
		    editor.commit();
		} catch (Exception e) {
			AppLog.error("Error saving current location: " + e.getMessage());
		}		
	}

	
	@Override
	public Measurement getPreviousMeasurment(String clientId, MeasurementTypeEnum measurementType) throws Exception {
		Measurement result = null;
	    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    Class measurmentClass = getClassFromType(measurementType);
	    String serilised = sharedPref.getString(clientId + "_" + measurmentClass.getName(), null);
	    if(serilised != null)
	    	result = _serializer.Deserializer(serilised, measurmentClass);
		return result;
	}

	@Override
	public void putPreviousMeasurment(String clientId, Measurement measurement) throws Exception  {
		String serilised = _serializer.Serializer(measurement);
	    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	    Editor editor = sharedPref.edit();
	    editor.putString(clientId + "_" + measurement.getClass().getName(), serilised);
	    editor.commit();
	}
	
	private Class getClassFromType(MeasurementTypeEnum measurementType)
	{
		switch(measurementType)
		{
		case BP:
			return BloodPressureMeasurement.class;
		case BSL:
			return BloodSugerLevelMeasurement.class;
		case ECG:
			return ECGMeasurement.class;
		case INR:
			return INRMeasurement.class;
		case SPO2:
			return SPO2Measurement.class;
		case Temp:
			return TemperatureMeasurement.class;
		case Urine:
			return UrineMeasurement.class;
		case Weight:
			return WeightMeasurement.class;
		}
		return null;
	}

	@Override
	public void OnDuty() {
		startTracking();
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(KEY_ON_DUTY, true);
        editor.commit();
	}

	@Override
	public void OffDuty() {
		stopTracking();
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(KEY_ON_DUTY, false);
        editor.commit();
	}
	
	@Override
	public Boolean IsOnDuty() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(KEY_ON_DUTY, true);
	}

	@Override
	public void startTracking() {
		// Starts the GPS logging
        Intent serviceIntent = new Intent(this, GPSLoggerService.class);
        this.startService(serviceIntent);
	}

	@Override
	public void stopTracking() {
        Intent serviceIntent = new Intent(this, GPSLoggerService.class);
        this.stopService(serviceIntent);
	}

	
	
	
	private <T> void putSerializable(String name, T serializable)
	{
		try {
			String serilisedData = _serializer.Serializer(serializable);
		    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		    Editor editor = sharedPref.edit();
		    editor.putString(name, serilisedData);
		    editor.commit();
		} catch (Exception e) {
			AppLog.error("Error saving " + name + ": " + e.getMessage());
		}
	}

	private <T> T getSerializable(String name, Class<T> resultType)
	{
		T result = null;
		try {
		    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		    String serilisedData = sharedPref.getString(name, null);
		    if(serilisedData != null)
		    	result = _serializer.Deserializer(serilisedData, resultType);
		} catch (Exception e) {
			AppLog.error("Error getting + " + name + ": " + e.getMessage());
		}
		return result;
	}

	private <T> void putSerializable(T serializable)
	{
		putSerializable(serializable.getClass().getName(), serializable);
	}
	
	private <T> T getSerializable(Class<T> resultType)
	{
		return getSerializable(resultType.getName(), resultType);
	}
}
