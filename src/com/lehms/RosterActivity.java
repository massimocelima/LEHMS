package com.lehms;

import java.util.Date;
import java.util.TimeZone;

import com.google.inject.Inject;
import com.lehms.adapters.JobAdapter;
import com.lehms.messages.LoginResponse;
import com.lehms.messages.dataContracts.JobDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.service.IRosterResource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView; 
import roboguice.inject.InjectExtra;; 

public class RosterActivity  extends RoboListActivity { //implements AsyncQueryListener 

	@InjectView(R.id.title_bar_title) TextView _titleBarTitle;
	@InjectExtra(value = "roster_date", optional = true) Date _currentDate;
	
	@Inject IRosterResource _rosterResource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roster);
		
		if( _currentDate == null )
			_currentDate = new Date();
						
		_titleBarTitle.setText("Roster"); //  + DateFormat.getDateFormat(this).format( _currentDate));
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		fillDataAsync();
		
        //listView.addHeaderView(buildHeader());		
        //listView.addFooterView(buildFooter(), null, false);

		listView.setOnItemClickListener(new OnItemClickListener() { 
			    public void onItemClick(AdapterView<?> parent, View view, 
			        int position, long id) { 
			      // When clicked, show a toast with the TextView text 
				      Toast.makeText(getApplicationContext(), "Item Clicked", 
				          Toast.LENGTH_SHORT).show(); 
			    } 
			  }); 
	}
	
	private void fillDataAsync()
	{
		LoadRosterTask task = new LoadRosterTask(this);
		task.execute();

		/*
		try
		{
			RosterDataContract response = _rosterResource.GetRosterFor(_currentDate);

			JobAdapter adapter = new JobAdapter(this, R.layout.job_item, response.Jobs);
			ListView listView = getListView();
			listView.setAdapter(adapter);
		} 
		catch (Exception ex) 
		{
			Log.e("LEHMS", ex.getMessage());
			AlertDialog.Builder dialog = new AlertDialog.Builder(this)
				.setMessage(ex.getMessage())
				.setTitle("Error retriving roster");
			dialog.show();
		}
		*/
	}

	
	public void onHomeClick(View view)
	{
		UIHelper.GoHome(this);
	}
	
	public void onRefreshClick(View view)
	{
	}
	
	
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

	
}
