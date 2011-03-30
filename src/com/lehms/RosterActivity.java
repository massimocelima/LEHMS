package com.lehms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.inject.Inject;
import com.lehms.adapters.JobAdapter;
import com.lehms.messages.LoginResponse;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.persistence.IRosterRepository;
import com.lehms.persistence.RosterRepository;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IRosterResource;
import com.lehms.util.AppLog;
import com.lehms.util.MathUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView; 
import roboguice.inject.InjectExtra;

public class RosterActivity  extends RoboListActivity 
{
	public final static String EXTRA_ROSTER_DATE = "roster_date"; 
	
	@InjectView(R.id.title_bar_title) TextView _titleBarTitle;
	@InjectExtra(value = EXTRA_ROSTER_DATE, optional = true) Date _rosterDate;
	@InjectView(R.id.activity_roster_title) TextView _title;
	@InjectView(R.id.activity_roster_sub_title) TextView _subtitle;
	@InjectView(R.id.activity_roster_last_updated) TextView _lastUpdated;
	
	@Inject IRosterResource _rosterResource;
	@Inject IIdentityProvider _identityProvider;
    @Inject private IRosterRepository _rosterRepository; 
	
	private GestureDetector _gestureDetector;
    private GuestureListener _gestureListener;
	
    private ArrayList<Date> _viewdRosters = new ArrayList<Date>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roster);

		if(savedInstanceState != null && savedInstanceState.get(EXTRA_ROSTER_DATE) != null )
			_rosterDate = (Date)savedInstanceState.get(EXTRA_ROSTER_DATE);
		
		_gestureDetector = new GestureDetector(this, new FlingGestureDetector());
		_gestureListener = new GuestureListener(_gestureDetector);
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		listView.setOnTouchListener(_gestureListener);

		listView.setOnTouchListener(_gestureListener);
		
		listView.setOnItemClickListener(new OnItemClickListener() { 
			    public void onItemClick(AdapterView<?> parent, View view, 
			        int position, long id) { 
			    	
					try
					{
				      // When clicked, show a toast with the TextView text 
				    	ShowJob(position, id);
					}
					catch(Exception ex)
					{
						UIHelper.ShowAlertDialog(RosterActivity.this, "Error", "Error: " + ex.getMessage());
					}
			    } 
			  }); 
		
		initHeader();
		fillDataAsync(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_ROSTER_DATE, _rosterDate);
	}
	
	private void ShowJob(int position, long id)
	{
		Intent intent = new Intent(this, JobDetailsActivity.class);
		intent.putExtra(JobDetailsActivity.EXTRA_JOB_ID, id);
		intent.putExtra(JobDetailsActivity.EXTRA_ROSTER_DATE, _rosterDate.getTime());
		startActivity(intent);
	}
	
	private String GetUserDetails()
	{
		String result = "";
		
		UserDataContract user = null;
		try
		{
			user = _identityProvider.getCurrent(); 	
		} catch(Exception e) {
			// This should never happen
			AppLog.error("Error getting identity from provider");
		}
		if( user != null )
		{
			result = user.getUserDetails();
		}
		return result;
	}
	
	private void fillDataAsync(Boolean reloadFromServer)
	{
		if( reloadFromServer && ! UIHelper.IsOnline(this) )
		{
			UIHelper.ShowAlertDialog(this, "Unable to connect to server", "An internet connection could not be established. Please connect this device to the internet and try again.");
		}
		else
		{
			if( _rosterDate == null )
				_rosterDate = new Date();

			JobAdapter adapter = (JobAdapter)getListView().getAdapter();
			if(adapter != null)
			{
				adapter.clear();
				adapter.notifyDataSetChanged();
			}
			
			LoadRosterTask task = new LoadRosterTask(this, reloadFromServer);
			task.execute();
		}
	}

	private void initHeader()
	{
		_titleBarTitle.setText("Roster");
		_title.setText(UIHelper.FormatLongDate(_rosterDate));
		_subtitle.setText(GetUserDetails());
		_lastUpdated.setText("");
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onRefreshClick(View view)
	{
		fillDataAsync(true);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	public void onSpecifyDateClick(View view)
	{
		DatePickerDialog dialog = new DatePickerDialog(this,
				_dateSetListener,
				_rosterDate.getYear() + 1900, 
				_rosterDate.getMonth(), 
				_rosterDate.getDate());
		dialog.show();
	}
	
    private DatePickerDialog.OnDateSetListener _dateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
            	
            	_rosterDate = new Date(year - 1900, monthOfYear, dayOfMonth);
            	initHeader();
            	fillDataAsync(false);
            }
        };
	
    private class LoadRosterTask extends AsyncTask<String, Integer, RosterDataContract>
    {
    	private Activity _context;
    	private ProgressDialog _progressDialog;
    	private Boolean _reloadFromServer;
    	private Exception _exception;

    	public LoadRosterTask(Activity context, Boolean reloadFromServer)
    	{
    		_context = context;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading roster please wait...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(false);
            
            _reloadFromServer = reloadFromServer;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
		@Override
		protected RosterDataContract doInBackground(String... arg0) {
			
			RosterDataContract roster = null;
			
			try {
				_rosterRepository.open();
				if( ! _reloadFromServer && _viewdRosters.contains(_rosterDate) )
					roster = _rosterRepository.fetchRosterFor(_rosterDate);
				if( roster == null )
				{
					if( ! UIHelper.IsOnline(RosterActivity.this) )
						throw new Exception("The roster for this date has not been cahced onto the device and a internet connection could not be found." + 
								" Please connect this device to the internet and try again");
					else
					{
						roster = _rosterResource.GetRosterFor(_rosterDate);
						_rosterRepository.saveRoster(roster);
						
						if(!_viewdRosters.contains(_rosterDate))
							_viewdRosters.add(_rosterDate);
					}
				}
				//_rosterRepository.close();
			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			} 
			finally 
			{
				_rosterRepository.close();
			}
			return roster;
		}
		
		@Override
		protected void onPostExecute(RosterDataContract result) {
			super.onPostExecute(result);
			if( result == null )
			{
				if( _exception != null )
					createDialog("Error", "Error retriving roster: " + _exception.getMessage());
				else
					createDialog("Error", "Error retriving roster");
			}
			else
			{
				JobAdapter adapter = new JobAdapter(_context, R.layout.job_item, result.Jobs);
				ListView listView = getListView();
				listView.setAdapter(adapter);
				listView.invalidate();
				_lastUpdated.setText( "Last Updated on " + UIHelper.FormatShortDateTime(result.LastUpdatedFromServer));

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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return _gestureDetector.onTouchEvent(event);
	}

	public class FlingGestureDetector extends SimpleOnGestureListener {
	 
		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float velocityX,
				float velocityY) {
	
			//DisplayMetrics dm = getResources().getDisplayMetrics(); 
			
			int minScaledFlingVelocity  = ViewConfiguration.get(getApplicationContext()).getScaledMinimumFlingVelocity() * 10; // 10 = fudge by experimentation
			
	        if (Math.abs(velocityX) > minScaledFlingVelocity  &&
	            Math.abs(velocityY) < minScaledFlingVelocity ) {
	
	        	Calendar calendar = Calendar.getInstance(); 
	        	calendar.setTime(_rosterDate); 
	        	
	            if( velocityX < 0 ) // Move to the next day
	            	calendar.add(Calendar.DAY_OF_YEAR, 1);
	            else  // Move to the previous day
	            	calendar.add(Calendar.DAY_OF_YEAR, -1);
	            
	            _rosterDate = calendar.getTime();
	            
	    		initHeader();
	    		fillDataAsync(false);
	    		
				return true;
	        }
			return false;
		}
	}

    // END OnGestureListener Implementation

	
}
