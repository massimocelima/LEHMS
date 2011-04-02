package com.lehms;


import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.google.inject.Inject;
import com.lehms.controls.ActionItem;
import com.lehms.controls.QuickAction;
import com.lehms.messages.GetClientDetailsResponse;
import com.lehms.messages.LoginResponse;
import com.lehms.messages.RaiseAlarmRequest;
import com.lehms.messages.RaiseAlarmResponse;
import com.lehms.messages.dataContracts.AlarmType;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.LocationDataContract;
import com.lehms.serviceInterface.IAlarmResource;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IOfficeContactProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.serviceInterface.ITracker;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class RaiseAlarmActivity extends LehmsRoboActivity { 

	public static final String EXTRA_ALARM_TYPE = "alarm_type";
	
	@InjectExtra(optional=true, value=EXTRA_ALARM_TYPE) private AlarmType _alarmType = AlarmType.Emergency;
	
	private long _startTime;
	private Timer _timer;
	private Handler _handler = new Handler();
	private Boolean _quitTimer = false;
	
	private final int CONTDOWN_SECONDS = 10; // 10 seconds
	
	@InjectView(R.id.activity_raise_alarm_timer) private TextView _timerTextView;
	@InjectView(R.id.activity_title) private TextView _titleTextView;

	@Inject IAlarmResource _alarmResource;
	@Inject IIdentityProvider _identityProvider;
	@Inject IOfficeContactProvider _officeContactProvider;
	@Inject ITracker _tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_raise_alarm);
		
		_startTime = SystemClock.uptimeMillis();
		
		if(_alarmType == AlarmType.Test)
		{
			_titleTextView.setText("Test Alarm");
			onRaiseAlarmClick(null);
		}
		else
		{
			_handler.postDelayed(_updateTimeTask, 1000);
		}
	}

    @Override 
    protected void onSaveInstanceState(Bundle outState) { 
        super.onSaveInstanceState(outState); 
    }
    
    private void stopTimer()
    {
    	_quitTimer = true;
    	try { _handler.removeCallbacks(_updateTimeTask); } catch (Exception e) {}
    }
    
    public void onRaiseAlarmClick(View view)
    {
    	stopTimer();
		RaiseAlarmTask task = new RaiseAlarmTask();
		task.execute(null);
    }

    public void onCancelAlarmClick(View view)
    {
    	stopTimer();
    	this.finish();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	stopTimer();
    };

    private Runnable _updateTimeTask = new Runnable() {   
    	public void run() {
    		if(_quitTimer)
    			return;
    		
    		long millis = SystemClock.uptimeMillis() - _startTime;
    		int seconds = (int) (millis / 1000);
    		int reaminingSeconds = CONTDOWN_SECONDS - seconds;
    		if( reaminingSeconds <= 0 )
    		{
    			_timerTextView.setText("Sending Alarm...");
	    		RaiseAlarmTask task = new RaiseAlarmTask();
	    		task.execute(null);
    		}
    		else
    		{
    			if( reaminingSeconds == 1 )
        			_timerTextView.setText("The alarm will be sent in\n\r" + reaminingSeconds + " second");
    			else
    				_timerTextView.setText("The alarm will be sent in\n\r" + reaminingSeconds + " seconds");
    			_handler.postDelayed(_updateTimeTask, 1000);
    		}
    	}
   };
   
	private class RaiseAlarmTask extends AsyncTask<Void, Void, RaiseAlarmResponse>
	{
		public ProgressDialog _progressDialog;
	   	private Exception _exception;
	
	   	public RaiseAlarmTask()
	   	{
	   	}
	   	
	   	@Override
	   	protected void onPreExecute() {
	   		super.onPreExecute();
	   		
	           _progressDialog = new ProgressDialog(RaiseAlarmActivity.this);
	           if( _alarmType == AlarmType.Test )
	        	   _progressDialog.setMessage("Raising TEST alarm please wait...");
	           else
	        	   _progressDialog.setMessage("Raising alarm please wait...");
	        	   
	           _progressDialog.setIndeterminate(true);
	           _progressDialog.setCancelable(false);
	           _progressDialog.show();
	   	}
	   	
 		@Override
		protected RaiseAlarmResponse doInBackground(Void... arg0) {
			try {
				if( UIHelper.IsOnline(RaiseAlarmActivity.this))
				{
					RaiseAlarmRequest request = new RaiseAlarmRequest();
					request.AlarmSentOn = new Date();
					request.Type = _alarmType;
					request.Location = _tracker.getLastLocation();
					request.Description = "";

					return  _alarmResource.Raise(request);
				}
				else
					return SendViaSMS();
			} catch (Exception e) {
				
				try
				{
					return SendViaSMS();
				} catch (Exception ex) {
					AppLog.error(ex.getMessage());
					_exception = ex;
				}
			}
			return null;
		}
 		
 		private RaiseAlarmResponse SendViaSMS() throws Exception
 		{
 			String alramTypeString = "EMERGENCY";
 			Date date = new Date();
 			
 			DateFormat formatter = new DateFormat();
 			//TODO: gets the latest gps cordinates
 			LocationDataContract location = _tracker.getLastLocation();
 			
 			if( _alarmType == AlarmType.Test )
 				alramTypeString = "TEST";
 			
            String seperator = "%7C";
            String smsTextMessage = "";
           	smsTextMessage = ((((("LEHMS" + seperator) + "alarm" + seperator) + alramTypeString + seperator) + _identityProvider.getCurrent().Username + seperator) + seperator) + "MobileId" + seperator;
           	if(location != null)
 	            smsTextMessage = (((smsTextMessage + formatter.format("yyMMddHHmmss", date) + seperator) + location.Latitude + seperator) + location.Longitude + seperator) + location.Accuracy + seperator;
            else
	            smsTextMessage = (((smsTextMessage + formatter.format("yyMMddHHmmss", date) + seperator) + "0.000000" + seperator) + "0.000000" + seperator) + "0" + seperator;
           	
			SmsManager sm = SmsManager.getDefault();
			sm.sendTextMessage(_officeContactProvider.getAlarmSmsNumber(), null, smsTextMessage, null, null);
			return new RaiseAlarmResponse();
 		}
		
		@Override
		protected void onPostExecute(RaiseAlarmResponse result) {
			super.onPostExecute(result);
			
			_progressDialog.dismiss();
			if( result == null )
			{
				if(_exception != null )
					createDialog("Error", "Couldn't establish a connection: " + _exception.getMessage());
				else
					createDialog("Error", "Couldn't establish a connection");
			}
			else
			{
				if( _alarmType == AlarmType.Test )
					createDialog("Test Alarm has been raised", "The test alarm has been raised.");
				else
				{
					try {
						UIHelper.MakeCall(_officeContactProvider.getCallCentrePhoneNumber(), RaiseAlarmActivity.this);
						RaiseAlarmActivity.this.finalize();
					} catch (Throwable e) {
						createDialog("Error trying to make call to call centre", "Error trying to make call to call centre: " + e.getMessage());
						AppLog.error("Error trying to make call to call centre", e);
					}
					//createDialog("Alarm has been raised", "The alarm has been raised. If you you do not recieve a response within one minute please call 000.");
				}
			}
		}

	    private void createDialog(String title, String text) {
	        AlertDialog ad = new AlertDialog.Builder(RaiseAlarmActivity.this)
	        	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						RaiseAlarmActivity.this.finish();
					}
				})
	        	.setTitle(title)
	        	.setMessage(text)
	        	.create();
	        ad.show();
	    }
   }
	
}
