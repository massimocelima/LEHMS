package com.lehms;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.inject.Inject;
import com.lehms.adapters.JobAdapter;
import com.lehms.messages.LoginResponse;
import com.lehms.messages.dataContracts.JobDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.service.IIdentityProvider;
import com.lehms.service.IRosterResource;
import com.lehms.util.MathUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
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
import roboguice.inject.InjectExtra;; 

public class RosterActivity  extends RoboListActivity 
{ 
	@InjectView(R.id.title_bar_title) TextView _titleBarTitle;
	@InjectExtra(value = "roster_date", optional = true) Date _currentDate;
	@InjectView(R.id.activity_roster_title) TextView _title;
	@InjectView(R.id.activity_roster_sub_title) TextView _subtitle;
	
	@Inject IRosterResource _rosterResource;
	@Inject IIdentityProvider _identityProvider;
	
	private GestureDetector _gestureDetector;
    private View.OnTouchListener _gestureListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roster);
		
		_gestureDetector = new GestureDetector(this, new FlingGestureDetector());
		_gestureListener = new View.OnTouchListener() {             
			public boolean onTouch(View v, MotionEvent event) {                 
				if (_gestureDetector.onTouchEvent(event)) {                     
					return true;                 }                 
				return false;             
				}         
			}; 
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		listView.setOnTouchListener(_gestureListener);
		
        //listView.addHeaderView(buildHeader());		
        //listView.addFooterView(buildFooter(), null, false);

		listView.setOnTouchListener(_gestureListener);
		
		listView.setOnItemClickListener(new OnItemClickListener() { 
			    public void onItemClick(AdapterView<?> parent, View view, 
			        int position, long id) { 
			      // When clicked, show a toast with the TextView text 
			    	ShowJob(position, id);
			    } 
			  }); 
		
		initHeader();
		fillDataAsync();

	}
	
	private void ShowJob(int position, long id)
	{
		Intent intent = new Intent(this, JobDetailsActivity.class);
		intent.putExtra(JobDetailsActivity.JOB_ID, id);
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
			Log.e("LEHMS", "Error getting identity from provider");
		}
		if( user != null )
		{
			result = user.getUserDetails();
		}
		return result;
	}
	
	private void fillDataAsync()
	{
		if( _currentDate == null )
			_currentDate = new Date();

		JobAdapter adapter = (JobAdapter)getListView().getAdapter();
		if(adapter != null)
		{
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
		
		LoadRosterTask task = new LoadRosterTask(this);
		task.execute();
	}

	private void initHeader()
	{
		_titleBarTitle.setText("Roster");
		_title.setText(DateFormat.format( "EEEE, MMMM dd, yyyy", _currentDate));
		_subtitle.setText(GetUserDetails());
	}
	
	public void onHomeClick(View view)
	{
		UIHelper.GoHome(this);
	}
	
	public void onRefreshClick(View view)
	{
		fillDataAsync();
	}
	
	public void onSpecifyDateClick(View view)
	{
		DatePickerDialog dialog = new DatePickerDialog(this,
				_dateSetListener,
                _currentDate.getYear() + 1900, 
                _currentDate.getMonth(), 
                _currentDate.getDate());
		dialog.show();
	}
	
    private DatePickerDialog.OnDateSetListener _dateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
            	
            	_currentDate = new Date(year - 1900, monthOfYear, dayOfMonth);
            	initHeader();
            	fillDataAsync();
            }
        };
	
    private class LoadRosterTask extends AsyncTask<String, Integer, RosterDataContract>
    {
    	private Activity _context;
    	private ProgressDialog _progressDialog;

    	public LoadRosterTask(Activity context)
    	{
    		_context = context;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading roster please wait...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(false);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
		@Override
		protected RosterDataContract doInBackground(String... arg0) {
			try {
				return _rosterResource.GetRosterFor(_currentDate);
			} catch (Exception e) {
				Log.e("LEHMS", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(RosterDataContract result) {
			super.onPostExecute(result);
			if( result == null )
			{
				createDialog("Error", "Error retriving roster");
			}
			else
			{
				JobAdapter adapter = new JobAdapter(_context, R.layout.job_item, result.Jobs);
				ListView listView = getListView();
				listView.setAdapter(adapter);
				listView.invalidate();
			}
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
	        	calendar.setTime(_currentDate); 
	        	
	            if( velocityX < 0 ) // Move to the next day
	            	calendar.add(Calendar.DAY_OF_YEAR, 1);
	            else  // Move to the previous day
	            	calendar.add(Calendar.DAY_OF_YEAR, -1);
	            
	            _currentDate = calendar.getTime();
	            
	    		initHeader();
	    		fillDataAsync();
	    		
				return true;
	        }
			return false;
		}
	}

    // END OnGestureListener Implementation

	
}
