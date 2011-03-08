package com.lehms;

import java.util.Collection;
import java.util.List;

import com.lehms.controls.*;

import com.google.inject.Inject;
import com.lehms.adapters.ClientSummaryAdapter;
import com.lehms.adapters.JobAdapter;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;

public class ClientsActivity  extends RoboListActivity { //implements AsyncQueryListener 

	@Inject protected IClientResource _clientResource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clients);
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true); 
		
		LoadClients();

		listView.setOnItemClickListener(new OnItemClickListener() { 
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
		    	NavigationHelper.openClient(ClientsActivity.this, id);
		    }}); 
		
		/*
		
		final ActionItem qaClientDetails = new ActionItem();
		
		qaClientDetails.setTitle("Client Details");
		qaClientDetails.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_client));
		qaClientDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientsActivity.this, "Client Details" , Toast.LENGTH_SHORT).show();
			}
		});
				
		final ActionItem qaProgressNote = new ActionItem();
		
		qaProgressNote.setTitle("Progress Notes");
		qaProgressNote.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_progress_notes));
		qaProgressNote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientsActivity.this, "Progress Notes selected", Toast.LENGTH_SHORT).show();
			}
		});

		final ActionItem qaProgressNote2 = new ActionItem();
		
		qaProgressNote2.setTitle("Progress Notes");
		qaProgressNote2.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_progress_notes));
		qaProgressNote2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientsActivity.this, "Progress Notes selected", Toast.LENGTH_SHORT).show();
			}
		});

		final ActionItem qaProgressNote3 = new ActionItem();
		
		qaProgressNote3.setTitle("Progress Notes");
		qaProgressNote3.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_progress_notes));
		qaProgressNote3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientsActivity.this, "Progress Notes selected", Toast.LENGTH_SHORT).show();
			}
		});

		
		listView.setOnItemClickListener(new OnItemClickListener() { 
			    public void onItemClick(AdapterView<?> parent, View view, 
			        int position, long id) { 
			    	
			    	//NavigationHelper.openClient(ClientsActivity.this, id);
			    	
					ListQuickAction qa = new ListQuickAction(view);
					
					qa.addActionItem(qaClientDetails);
					qa.addActionItem(qaProgressNote);
					qa.addActionItem(qaProgressNote2);
					qa.addActionItem(qaProgressNote3);
					qa.setAnimStyle(ListQuickAction.ANIM_AUTO);
					
					qa.show();
			    }
			  }); 
			  */
	}

	private void LoadClients(){
		LoadClientsTask task = new LoadClientsTask(this);
		task.execute();
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onRefreshClick(View view)
	{
		LoadClients();
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	private class LoadClientsTask extends AsyncTask<Void, Void, List<ClientSummaryDataContract>>
    {
    	private Activity _context;
    	private ProgressDialog _progressDialog;
    	private Exception _exception;

    	public LoadClientsTask(Activity context)
    	{
    		_context = context;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading clients please wait...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(false);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
		@Override
		protected List<ClientSummaryDataContract> doInBackground(Void... arg0) {
			
			List<ClientSummaryDataContract> clients = null;
			
			try {
				
				clients = _clientResource.GetClientSummaries();
			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			} 
			
			return clients;
		}
		
		@Override
		protected void onPostExecute(List<ClientSummaryDataContract> result) {
			super.onPostExecute(result);
			if( result == null )
			{
				if( _exception != null )
					createDialog("Error", "Error retriving clients: " + _exception.getMessage());
				else
					createDialog("Error", "Error retriving clients");
			}
			else
			{
				ClientSummaryAdapter adapter = new ClientSummaryAdapter(_context, R.layout.client_item, result);
				ListView listView = getListView();
				listView.setAdapter(adapter);
				listView.invalidate();
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
