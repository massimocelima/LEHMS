package com.lehms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.inject.Module;
import com.lehms.IoC.Container;
import com.lehms.IoC.ContainerFactory;
import com.lehms.activerecord.ActiveRecordBase;
import com.lehms.activerecord.DatabaseManager;
import com.lehms.activerecord.IApplicationContext;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.LocationDataContract;
import com.lehms.messages.dataContracts.Permission;
import com.lehms.messages.dataContracts.RoleDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
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
import com.lehms.ui.clinical.model.RespiratoryRateMeasurement;
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
			IDutyManager,
			ICache,
			IApplicationContext,
			ITrackingSettings,
			IInboxSettings
{
    public static final String KEY_PROFILE_PREF = "application_settings_profile_pref";
    public static final String KEY_DEVICE_ID_PREF = "application_settings_device_id_pref";
    public static final String KEY_DEPARTMENT_PREF = "application_settings_department_pref";
    public static final String KEY_OFFICE_PHONE_PREF = "application_settings_office_phone_pref";
    public static final String KEY_OFFICE_EMAIL_PREF = "application_settings_office_email_pref";
    public static final String KEY_OFFICE_FAX_PREF = "application_settings_office_fax_pref";

    public static final String KEY_TRACKING_DISTANCE_PREF = "application_settings_tracking_distance";
    public static final String KEY_PROXIMITY_ENABLED_PREF = "application_settings_tracking_proximity_enabled";
    public static final String KEY_PROXIMITY_PREF = "application_settings_tracking_proximity";

    public static final String KEY_DISTANCE_PREF = "application_settings_tracking_distance";
    public static final String KEY_DISTANCE_ACTIVE_PREF = "application_settings_tracking_distance_active";
    public static final String KEY_DISTANCE_LAST_LOCATION_PREF = "application_settings_tracking_distance_last_loc";
    
    public static final String KEY_ALARM_PHONE_PREF = "application_settings_alarm_phone_pref";
    public static final String KEY_ALARM_SMS_PREF = "application_settings_alarm_sms_number_pref";
    public static final String KEY_SERVCIDE_PHONE_PREF = "application_settings_service_phone_pref";
    public static final String KEY_CALL_CENTRE_PHONE_PREF = "application_settings_call_centre_phone_pref";
    
    public static final String KEY_INBOX_NAMESPACE_PREF = "application_settings_inbox_namespace";
    public static final String KEY_INBOX_INTENT_PREF = "application_settings_inbox_intent";
    
    public static final String KEY_USER = "application_data_user";
    public static final String KEY_JOB = "application_data_job";

    public static final String KEY_ON_DUTY = "application_on_duty_pref";

	private ISerializer _serializer;
	
	private UserDataContract _user = null;
	private JobDetailsDataContract _job = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.application_settings, false);

		initiliseDatabase();

		// Inits the container for the container factory
		ContainerFactory.SetContainer(new Container(this));
		_serializer  = ContainerFactory.Create().resolve(ISerializer.class);
		
		// Starts the tracking and anything else to do with being on duty
		if(IsOnDuty())
			OnDuty();
		
	}
	
	@Override
	public void onTerminate() {
		terminateDatabase();
		super.onTerminate();
	}
	
	@Override
	protected void addApplicationModules(List<Module> modules) {
		super.addApplicationModules(modules);
		modules.add(new ConfigurationModule(this));
	}
	
	@Override
	public UserDataContract getCurrent() throws Exception {
		if(_user == null)
			_user = getSerializable( "current_user", UserDataContract.class);
		return _user;
	}

	@Override
	public void setCurrent(UserDataContract user) throws Exception {
		_user = null;
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
		if(_job == null)
			_job = getSerializable("current_job", JobDetailsDataContract.class);
		return _job;
	}

	@Override
	public void set(JobDetailsDataContract job) {
		_job = null;
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
		LocationDataContract result = getSerializable(LocationDataContract.class);
		return result;
	}

	@Override
	public void putLastLocation(LocationDataContract location) {
		putSerializable(location);
	}
	
	@Override
	public Measurement getPreviousMeasurment(String clientId, MeasurementTypeEnum measurementType) throws Exception {
	    Class measurmentClass = getClassFromType(measurementType);
	    Measurement result = getSerializable(clientId + "_" + measurmentClass.getName(), Measurement.class);
	    return result;
	}

	@Override
	public void putPreviousMeasurment(String clientId, Measurement measurement) throws Exception  {
		putSerializable(clientId + "_" + measurement.getClass().getName(), measurement);
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
		case RespiratoryRate:
			return RespiratoryRateMeasurement.class;
		//default:
		//	throw new Exception("Measurment Type " + measurementType.toString() + " undefind.");
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
        return prefs.getBoolean(KEY_ON_DUTY, false);
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

	@Override
	public CacheItem get(String name) throws Exception {
		
		try
		{
			FileInputStream fis = openFileInput(name + ".dat");
			ObjectInputStream in = new ObjectInputStream(fis);
			CacheItem result = (CacheItem)in.readObject();
			in.close();
			return result;		
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public void put(String name, CacheItem item) throws Exception {

		try { deleteFile(name + ".dat"); } catch (Exception e) {}
		FileOutputStream fos = openFileOutput(name + ".dat", Context.MODE_PRIVATE);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(item);
		out.close();
	}
	
	private <T> void putSerializable(String name, T serializable)
	{
		try
		{
			try { deleteFile(name + ".dat"); } catch (Exception e) {}
			FileOutputStream fos = openFileOutput(name + ".dat", Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(serializable);
			out.close();
		} catch(Exception ex) {}
	}

	private <T> T getSerializable(String name, Class<T> resultType)
	{
		try
		{
			FileInputStream fis = openFileInput(name + ".dat");
			ObjectInputStream in = new ObjectInputStream(fis);
			T result = (T)in.readObject();
			in.close();
			return result;		
		}
		catch(Exception e)
		{
			return null;
		}
	}

	private <T> void putSerializable(T serializable)
	{
		putSerializable(serializable.getClass().getName(), serializable);
	}
	
	private <T> T getSerializable(Class<T> resultType)
	{
		return getSerializable(resultType.getName(), resultType);
	}

	
	
	private DatabaseManager mDatabaseManager;
	private Set<ActiveRecordBase<?>> mEntities;

	private void initiliseDatabase()
	{
	    this.mDatabaseManager = new DatabaseManager(this);
	    this.mEntities = new HashSet();
	}

	private void terminateDatabase()
	{
	    if (this.mDatabaseManager != null) {
	        this.mDatabaseManager.closeDB();
	    }
	}
	
	@Override
	public DatabaseManager getDatabaseManager() {
	    return this.mDatabaseManager;
	}

	@Override
	public void addEntity(ActiveRecordBase<?> entity) {
	    this.mEntities.add(entity);
	}

	@Override
	public void addEntities(Set<ActiveRecordBase<?>> entities) {
	    this.mEntities.addAll(entities);
	}

	@Override
	public void removeEntity(ActiveRecordBase<?> entity) {
	    this.mEntities.remove(entity);
	}

	@Override
	public ActiveRecordBase<?> getEntity(Class<? extends ActiveRecordBase<?>> entityType, long id) {
	    for (ActiveRecordBase entity : this.mEntities) {
	        if ((entity.getClass() == entityType) && (entity.getId().longValue() == id)) {
	        	return entity;
	        }
	    }
	
	    return null;
	}

	@Override
	public int getTrackingDistance() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Double result = new Double( Double.parseDouble(prefs.getString(KEY_TRACKING_DISTANCE_PREF, "10")) );
        return result == 0 ? 10 : result.intValue();
	}

	@Override
	public int getProximityDistance() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt( prefs.getString(KEY_PROXIMITY_PREF, "25") );
	}

	@Override
	public Boolean getProximityEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(KEY_PROXIMITY_ENABLED_PREF, false);
	}

	@Override
	public Boolean isMeasuringDistance() {
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_DISTANCE_ACTIVE_PREF, false);
	}
	
	@Override
	public void beginMeasuringDistance() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(KEY_DISTANCE_PREF, "0");
        editor.putBoolean(KEY_DISTANCE_ACTIVE_PREF, true);
        editor.commit();
	}

	@Override
	public void addDistance(float meters) {
		float distance = Float.parseFloat( PreferenceManager.getDefaultSharedPreferences(this)
			.getString(KEY_DISTANCE_PREF, "0") );
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(KEY_DISTANCE_PREF, new Float(distance + meters).toString());
        editor.commit();
	}

	@Override
	public float endMeasuringDistance() {
		float distance = Float.parseFloat( PreferenceManager.getDefaultSharedPreferences(this)
			.getString(KEY_DISTANCE_PREF, "0") );
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(KEY_DISTANCE_PREF, "0");
        editor.putBoolean(KEY_DISTANCE_ACTIVE_PREF, false);
        editor.commit();
		return distance;
	}

	@Override
	public String getNamespace() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(KEY_INBOX_NAMESPACE_PREF, "com.android.email");
 	}

	@Override
	public String getIntentName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(KEY_INBOX_INTENT_PREF, "com.android.email.activity.Welcome");
	}

}
