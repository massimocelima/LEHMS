package com.lehms;

import java.util.Date;

import com.google.inject.Inject;
import com.lehms.messages.GetClientDetailsResponse;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.Permission;
import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.receivers.AlarmReceiver;
import com.lehms.service.GPSLoggerService;
import com.lehms.serviceInterface.IAuthorisationProvider;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.serviceInterface.IDutyManager;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IOfficeContactProvider;
import com.lehms.serviceInterface.ITracker;
import com.lehms.util.AppLog;
import com.lehms.util.MathUtils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import roboguice.activity.RoboActivity;  
import roboguice.inject.InjectView;  

public class Dashboard extends LehmsRoboActivity //implements OnGestureListener
{
	private GetClientDetailsResponse _clientResponse;
	@Inject protected IClientResource _clientResource;

	@Inject protected IIdentityProvider _identityProvider;
	@Inject protected IOfficeContactProvider _officeContactProvider;
	@Inject protected IAuthorisationProvider _authorisationProvider;
	@Inject protected IDutyManager _dutyManager;
	@Inject protected ITracker _tracker;
	
	@InjectView(R.id.footer_text) protected TextView _footerText;

	//@InjectView(R.id.dashboard_tab_host) protected TabHost _tabHost;
	//@InjectView(android.R.id.tabs) protected TabWidget _tabWidget;
	@InjectView(R.id.footer_container) protected LinearLayout _footerContainer;

    Animation _rightInAnimation;
    Animation _rightOutAnimation;
    Animation _leftInAnimation;
    Animation _leftOutAnimation;

	//private GestureDetector _gestureDetector;
    //private GuestureListener _gestureListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		//InitAnimations();
	
		//_gestureDetector = new GestureDetector(this);
		//_gestureListener = new GuestureListener(_gestureDetector); 

		//_tabHost.setup();
		//_tabHost.setOnTouchListener(_gestureListener);
		
		if( isAuthrosied(Permission.RosterView) )
		{
			//_tabHost.addTab(
	        //		_tabHost.newTabSpec("tabCarePractice")
	    	//			.setIndicator(buildIndicator("Care Practice")) //, getResources().getDrawable(R.drawable.ic_tab_options))
	    	//			.setContent(R.id.dashboard_care_practice_content)); // new Intent(this, Notepadv3.class)));
			
			if( ! isAuthrosied(Permission.ClientsView) )
				findViewById(R.id.dashboard_btn_clients).setVisibility(View.GONE);

			if( ! isAuthrosied(Permission.TakePhoto) )
				findViewById(R.id.dashboard_btn_camera).setVisibility(View.GONE);
			
			if( ! isAuthrosied(Permission.Contact) )
				findViewById(R.id.dashboard_btn_call_call_centre).setVisibility(View.GONE);
			
			if( ! isAuthrosied(Permission.Help) )
				findViewById(R.id.dashboard_btn_emergency).setVisibility(View.GONE);

			findViewById(R.id.dashboard_care_practice_content).setVisibility(View.VISIBLE);
			findViewById(R.id.dashboard_personal_response_content).setVisibility(View.GONE);
			
			// Only track the distance if we are a clinical worker
			_tracker.beginMeasuringDistance();
			
		}
		else
		{
			//_tabHost.addTab(
	        //		_tabHost.newTabSpec("tabPersonalResponse")
	    	//			.setIndicator(buildIndicator("Personal Response")) //, getResources().getDrawable(R.drawable.ic_tab_options))
	    	//			.setContent(R.id.dashboard_personal_response_content)); // new Intent(this, Notepadv3.class)));
			
			// They can self care
			if( ! isAuthrosied(Permission.OwnedClientDetailsView) )
				findViewById(R.id.dashboard_btn_clinical_details).setVisibility(View.GONE);
			else
				LoadClient();
			
			if( ! isAuthrosied(Permission.Help) )
				findViewById(R.id.dashboard_btn_test_emergency).setVisibility(View.GONE);
			if( ! isAuthrosied(Permission.Contact) )
				findViewById(R.id.dashboard_btn_call_call_centre).setVisibility(View.GONE);
			if( ! isAuthrosied(Permission.Help) )
				findViewById(R.id.dashboard_btn_emergency_self_care).setVisibility(View.GONE);

			findViewById(R.id.dashboard_care_practice_content).setVisibility(View.GONE);
			findViewById(R.id.dashboard_personal_response_content).setVisibility(View.VISIBLE);

		}
		
		
		//SetTouchEventOnChildViews(_tabHost);

		try
		{
			_footerText.setText(
					"Welcome " + 
					_identityProvider.getCurrent().FirstName + " " + _identityProvider.getCurrent().LastName);
		}
		catch(Exception ex)
		{
			UIHelper.handleException(this, ex, "Failed to set footer in dashboard");
		}
		
		_dutyManager.OnDuty();
		
		StartDataSynchService();
	}
/*
	private void SetTouchEventOnChildViews(ViewGroup viewGroup)
	{
		for(int i = 0; i < viewGroup.getChildCount(); i++)
		{
			View view = viewGroup.getChildAt(i);
			view.setOnTouchListener(_gestureListener);
			if(  view instanceof ViewGroup ){
				SetTouchEventOnChildViews((ViewGroup)view);
			}
		}
	}
	
	private void InitAnimations()
	{
        _rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        _rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        _leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        _leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
	}
*/
	public void onMyRosterClick(View view)
	{
		NavigationHelper.openRoster(this, new Date());
	}
	
	public void onContactClick(View view)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
	        .setTitle("Please select how you would like to contact the office")
            .setItems(R.array.dashboard_contact_office_selection, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	switch(which)
                	{
                	case 0:
                		UIHelper.MakeCall( _officeContactProvider.getOfficePhoneNumber(), Dashboard.this);
                		break;
                	case 1:
                		NavigationHelper.sendEmail(Dashboard.this, _officeContactProvider.getOfficeEmail(), "", "");
                		break;
                	case 2:
                		UIHelper.OpenCall(Dashboard.this);
                		break;
                	case 3:
                		NavigationHelper.sendEmail(Dashboard.this, null, "", "");
                		break;
                	}
                	
                	//_officeContactProvider.getOfficePhoneNumber();
               }
            })
            .setCancelable(true)
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            })
            .create();
		
		dialog.show();
	}
	
	public void onClientsClick(View view)
	{
		NavigationHelper.openClients(this);
	}
	
	public void onMapClick(View view)
	{
        // This example shows how to add a custom layout to an AlertDialog
        LayoutInflater factory = LayoutInflater.from(this);
        final View navigationLocationDialogView = factory.inflate(R.layout.navigation_location_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Enter the location you are traveling to:")
            .setView(navigationLocationDialogView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	
                	EditText location = (EditText)navigationLocationDialogView.findViewById(R.id.navigation_location_dialog_location);
                	if( ! location.getText().equals(""))
	            		UIHelper.LaunchNavigation(location.getText().toString(), Dashboard.this);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            })
            .create();
		
        dialog.show();
	}
	
	
	public void onCameraClick(View view)
	{
		NavigationHelper.goTakePicture(this);
	}
	
	public void onEmergencyTestClick(View view)
	{
		NavigationHelper.goTestEmergency(this);
	}
	
	public void onCallCallCentreClick(View view)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("Please select how you would like to contact the office")
        .setItems(R.array.dashboard_contact_call_centre_selection, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	switch(which)
            	{
            	case 0:
            		UIHelper.MakeCall( _officeContactProvider.getCallCentrePhoneNumber(), Dashboard.this);
            		break;
            	case 1:
            		UIHelper.MakeCall( _officeContactProvider.getServicePhoneNumber(), Dashboard.this);
            		break;
            	case 2:
            		UIHelper.OpenCall(Dashboard.this);
            		break;
            	}
           }
        })
        .setCancelable(true)
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        })
        .create();
	
	dialog.show();
	}
	
	public void onClinicalDetailsClick(View view)
	{
		try
		{
			UserDataContract user = _identityProvider.getCurrent();
			if(user.ClientId != null && ! user.ClientId.equals(""))
				NavigationHelper.goCliniclaDetails(this, _clientResponse.Client);
			else
				throw new Exception("Client id not found.");
		} 
		catch (Exception e)
		{
			AppLog.error("Error opening client: " + e.getMessage());
			UIHelper.ShowAlertDialog(this, "Error opening client", "Error opening client: " + e.getMessage());
		}
	}
	/*
    private View buildIndicator(String string) {
    	
        final TextView indicator = (TextView) getLayoutInflater().inflate(R.layout.tab_indicator,
        		_tabWidget, false);
        indicator.setText(string);
        return indicator;
    }

	public boolean onTouchEvent(MotionEvent event) {  
		return _gestureDetector.onTouchEvent(event);
	} 
*/
	/*

    // Begin OnGestureListener Implementation
    
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		
		int minScaledFlingVelocity  = ViewConfiguration.get(this).getScaledMinimumFlingVelocity() * 10; // 10 = fudge by experimentation
		
        int tabCount = _tabWidget.getTabCount();
        int currentTab = _tabHost.getCurrentTab();
        if (Math.abs(velocityX) > minScaledFlingVelocity  &&
            Math.abs(velocityY) < minScaledFlingVelocity ) {

            int newTab = currentTab;
            if( velocityX < 0 )
            	newTab = MathUtils.constrain(currentTab + 1, 0, tabCount - 1);
            else 
            	newTab = MathUtils.constrain(currentTab - 1, 0, tabCount - 1);
            
            if (newTab != currentTab) {
                View currentView = _tabHost.getCurrentView();
                _tabHost.setCurrentTab(newTab);
                View newView = _tabHost.getCurrentView();
                newView.startAnimation(velocityX < 0 ? _rightInAnimation : _leftInAnimation);
                currentView.startAnimation(velocityX < 0 ? _rightOutAnimation : _leftOutAnimation);
            }
        }
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,float arg3) { return false; }

	@Override
	public void onShowPress(MotionEvent arg0) {	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) { return false; }
	
    // End OnGestureListener Implementation
*/

	@Override
	protected void onDestroy() {
		super.onDestroy();

		_dutyManager.OffDuty();
		
		AlarmManager manager = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
		PendingIntent loggerIntent = PendingIntent.getBroadcast(this, 0,new Intent(this,AlarmReceiver.class), 0);
		manager.cancel(loggerIntent);
		
		try {
			//_identityProvider.logout();
		} catch (Exception e) {}
	}
	
	@Override
	public void onBackPressed() {
		
		AlertDialog dialog = new AlertDialog.Builder(this)
	        .setTitle("Are uou sure you want to quit the application.")
	        .setCancelable(true)
	        .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	Dashboard.this.finish();
	            }
	        })
	        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        })
	        .create();
		
		dialog.show();
	}
		
	private void StartDataSynchService()
	{
		AlarmManager manager = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
		PendingIntent loggerIntent = PendingIntent.getBroadcast(this, 0,new Intent(this, AlarmReceiver.class), 0);
		manager.cancel(loggerIntent);
			
		long duration = 1000 * 60;
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(), duration, loggerIntent);
	}
	
	private void LoadClient(){
		LoadClientTask task = new LoadClientTask(this);
		task.execute();
	}
	
	private class LoadClientTask extends AsyncTask<Void, Void, GetClientDetailsResponse>
    {
    	private Activity _context;
    	private ProgressDialog _progressDialog;
    	private Exception _exception;

    	public LoadClientTask(Activity context)
    	{
    		_context = context;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading client details please wait...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(false);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
		@Override
		protected GetClientDetailsResponse doInBackground(Void... arg0) {
			
			try {
				_clientResponse = _clientResource.GetClientDetails(Long.parseLong( _identityProvider.getCurrent().ClientId ));
			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			} 
			
			return _clientResponse;
		}
		
		@Override
		protected void onPostExecute(GetClientDetailsResponse result) {
			super.onPostExecute(result);
			
			if( result == null )
			{
				if( _exception != null )
					createDialog("Error", "Error opening client: " + _exception.getMessage());
				else
					createDialog("Error", "Error opening client");
			}
			
			if( _progressDialog.isShowing() )
				_progressDialog.dismiss();
		}
		
	    private void createDialog(String title, String text) {
	        AlertDialog ad = new AlertDialog.Builder(_context)
	        	.setPositiveButton("Ok", null)
	        	.setTitle(title)
	        	.setMessage(text)
	        	.create();
	        ad.show();
	    }
		
    }
	
}
