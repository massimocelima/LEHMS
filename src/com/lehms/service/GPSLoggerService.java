package com.lehms.service;

import roboguice.service.RoboService;

import com.google.inject.Inject;
import com.lehms.messages.dataContracts.LocationDataContract;
import com.lehms.serviceInterface.ISerializer;
import com.lehms.serviceInterface.ITracker;
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
		
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		double x = longitude + latitude;
		// Store this data
		
		
		LocationDataContract locationDC = new LocationDataContract();
		locationDC.Accuracy = location.getAccuracy();
		locationDC.Latitude = latitude;
		locationDC.Longitude = longitude;
		if(_tracker != null)
			_tracker.putLastLocation(locationDC);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
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
				10, 
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
