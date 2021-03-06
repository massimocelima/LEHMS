package com.lehms.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import roboguice.service.RoboService;

import com.google.inject.Inject;
import com.lehms.UIHelper;
import com.lehms.messages.CreateProgressNoteRequest;
import com.lehms.messages.UploadProgressNoteRecordingResponse;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventStatus;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IProgressNoteResource;
import com.lehms.util.AppLog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.widget.Toast;


public class DataSyncService extends RoboService {

	private final int MAX_ATTEMPTS = 4;
	
	@Inject IEventRepository _eventRepository;
	@Inject IProgressNoteResource _progressNoteResource;
	@Inject IEventExecuter _eventExecuter;
	
    public DataSyncService() {
        AppLog.info("DataSyncService.DataSyncService().");
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
        AppLog.info("DataSyncService.onBind(...).");
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
    	if( UIHelper.IsOnline(this))
    	{
    		Runnable syncDataRunner = new Runnable() {
				
				@Override
				public void run() {
					RunSynch();
				}
			};
			new Thread(syncDataRunner).start();
			//Message msg = Message.obtain();
			//_serviceHandler.dispatchMessage(message);
    	}
		
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
        AppLog.info("DataSyncService.onDestroy().");
	}
	
	private void ShowNotifification(String title, String text)
	{
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	    //Runnable notifier = new Runnable() {
	        //public void run() {
	            PendingIntent contentIntent = PendingIntent.getActivity(DataSyncService.this, 0,
	                    new Intent(DataSyncService.this, DataSyncService.class), 0);
	            
	            
	            Notification notification = new Notification();
	            notification.tickerText = "text";
	            notification.when = System.currentTimeMillis();

	            notification.setLatestEventInfo(DataSyncService.this, title, text, contentIntent);
	            notification.defaults = Notification.DEFAULT_SOUND;
	            
	            notificationManager.notify(0, // we use a string id because it is a uniquenumber. we use it later to cancel the
	                       notification);
	                       
	            notificationManager.notify();
	        //}
	    //};
	    //_serviceHandler.post(notifier);
	}
	
	synchronized public void RunSynch()
	{
		try
		{
			_eventRepository.open();
			
			List<Event> eventList = _eventRepository.fetchPending();
			for(int i = 0; i < eventList.size(); i++)
			{
				Event e = eventList.get(i);
				e.Attempts += 1;
				try
				{
					_eventExecuter.ExecuteEvent(e);
					_eventRepository.delete(e);
				}
				catch(Exception exception)
				{
					e.ErrorMessage = exception.getMessage();
					if(e.Attempts >= MAX_ATTEMPTS)
						e.Status = EventStatus.Error;
					
					//_eventRepository.update(e);
				}
			}
		}
		catch(Exception ex)
		{
			AppLog.error("Error in DataSynchService", ex);
		}
		finally
		{
			_eventRepository.close();
		}
	}

}
