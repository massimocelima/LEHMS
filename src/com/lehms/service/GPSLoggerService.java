package com.lehms.service;

import java.util.Date;

import roboguice.service.RoboService;

import com.google.inject.Inject;
import com.lehms.JobDetailsActivity;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.LocationDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.persistence.IRosterRepository;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.ISerializer;
import com.lehms.serviceInterface.ITracker;
import com.lehms.serviceInterface.ITrackingSettings;
import com.lehms.util.AppLog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class GPSLoggerService extends RoboService implements LocationListener, GpsStatus.Listener {

	private static Boolean _isRunning = false;
	private LocationManager _gpsLocationManager;
    private Looper _serviceLooper;
    private ServiceHandler _serviceHandler;
    private NotificationManager _notificationManager;
    
    @Inject ISerializer _serializer;
    @Inject ITracker _tracker;
    
    @Inject IActiveJobProvider _activeJobProvider;
    @Inject IIdentityProvider _identityProvider;

    @Inject ITrackingSettings _trackingSettings;

    @Inject IRosterRepository _rosterRepository;

    @Inject IEventRepository _eventRepository;
    @Inject IEventFactory _eventFactory;

    //private final int PROXIMTY_TO_TARGET = 25; 
    
	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		
		public ServiceHandler(Looper looper) {
			super(looper);
		}
	  
		@Override
		public void handleMessage(Message msg) {
		}
	}
	
	public GPSLoggerService() {
        AppLog.info("GPSLoggerService.GPSLoggerService().");
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceStartArguments",HandlerThread.NORM_PRIORITY); 
        thread.start();
        
        _serviceLooper = thread.getLooper();
        _serviceHandler = new ServiceHandler(_serviceLooper);
        _notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
        AppLog.info("GPSLoggerService.onBind(...).");
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( ! _isRunning )
		{
		
			ShowNotifification("GPS Logger Service", "GPS logging service starting!");
			//Toast.makeText(this, "GPS logging service starting", Toast.LENGTH_SHORT).show();
			StartGpsManager();
			//Message msg = _serviceHandler.obtainMessage();
		    //msg.arg1 = startId;
		    //_serviceHandler.sendMessage(msg);
			_isRunning = true;
		}
		else
			ShowNotifification("GPS Logger Service", "GPS logging service already started!");
			//Toast.makeText(this, "GPS logging service already started!", Toast.LENGTH_SHORT).show();
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		ShowNotifification("GPS Logger Service", "GPS logging service done");
		//Toast.makeText(this, "GPS logging service done", Toast.LENGTH_SHORT).show();
		StopGpsManager();
		_isRunning = false;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		if( _identityProvider.isAuthenticated() )
		{
			try
			{
				LocationDataContract locationDC = new LocationDataContract();
				locationDC.Accuracy = location.getAccuracy();
				locationDC.Latitude = location.getLatitude();
				locationDC.Longitude = location.getLongitude();
				locationDC.Taken = new Date();
				
				if(_identityProvider.isAuthenticated())
					locationDC.Username = _identityProvider.getCurrent().Username;
				if(_activeJobProvider.get() != null)
					locationDC.JobId = _activeJobProvider.get().JobId;
				
				Event event = _eventFactory.create(locationDC, EventType.LocationTracking);
				_eventRepository.create(event);

				if(_tracker != null)
				{
					addDistance(location);
					_tracker.putLastLocation(locationDC);
				}
				
				startJobIfInProximity(location);
				
			}
			catch(Exception ex)
			{
				AppLog.error("Error saving location.", ex);
			}

		}
	}
	
	public void addDistance(Location location)
	{
		if(_tracker.isMeasuringDistance())
		{
			LocationDataContract lastLocation = _tracker.getLastLocation();
			Location jobLocation = new Location("");
			jobLocation.setLongitude(lastLocation.Longitude);
			jobLocation.setLatitude(lastLocation.Latitude);
			
			float distance = location.distanceTo(jobLocation);
			_tracker.addDistance(distance);
		}
	}
	
	public JobDetailsDataContract startJobIfInProximity(Location location)
	{
		JobDetailsDataContract result = null;

		if(_trackingSettings.getProximityEnabled())
		{
			try
			{
				RosterDataContract roster = _rosterRepository.fetchRosterFor(new Date());
				
				JobDetailsDataContract startedJob = roster.getStartedJob();
				
				for(JobDetailsDataContract job : roster.Jobs)
				{
					if( job != startedJob )
					{
						if( job.Client.Address != null && job.Client.Address.Location != null && 
								job.Client.Address.Location.Latitude != 0 && 
								job.Client.Address.Location.Longitude != 0 )
						{
							Location jobLocation = new Location("");
							jobLocation.setLongitude(job.Client.Address.Location.Longitude);
							jobLocation.setLatitude(job.Client.Address.Location.Latitude);
							
							float distance = location.distanceTo(jobLocation);
							if(distance <= _trackingSettings.getProximityDistance())
							{
								//job.Start(kilometersTravelled);
								//if(startedJob != null)
									//startedJob.stop
							}
						}
					}
				}
			}
			catch(Exception ex)
			{
				AppLog.error("Error getting roster.", ex);
			}
		}
		
		return result;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		StopGpsManager();
		StartGpsManager();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		StopGpsManager();
		StartGpsManager();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	@Override
	public void onGpsStatusChanged(int arg0) {
	}

	private void StartGpsManager()
	{
		_gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		_gpsLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 
				0,
				_trackingSettings.getTrackingDistance(), 
				this, 
				_serviceLooper );
		//_gpsLocationManager.addProximityAlert( latitude, longitude, radius, expiration, intent)
		_gpsLocationManager.addGpsStatusListener(this);
	}
    
	private void StopGpsManager()
	{
		_gpsLocationManager.removeUpdates(this);
		_gpsLocationManager.removeGpsStatusListener(this);
	}
	
	private void ShowNotifification(String title, String text)
	{
		/*
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GPSLoggerService.class), 0);
        
        Notification notification = new Notification();
        notification.tickerText = "text";
        notification.when = System.currentTimeMillis();

        notification.setLatestEventInfo(this, title, text, contentIntent);

        //notification.defaults = Notification.DEFAULT_SOUND;
        
        _notificationManager.notify(0, // we use a string id because it is a uniquenumber. we use it later to cancel the
                   notification);
                   
        // Throw an exception
		_notificationManager.notify();
		*/		
	}

}
