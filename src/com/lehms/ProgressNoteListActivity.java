package com.lehms;

import java.util.Date;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.adapters.JobAdapter;
import com.lehms.adapters.ProgressNoteAdapter;
import com.lehms.messages.GetProgressNotesResponse;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IProgressNoteResource;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;;

public class ProgressNoteListActivity extends RoboListActivity {

	public static final String EXTRA_CLIENT_ID = "client_id"; 
	
	@InjectExtra(EXTRA_CLIENT_ID) public Long _clientId;

	@InjectView(R.id.activity_progress_notes_list_title) TextView _titleTextView;
	@InjectView(R.id.activity_progress_notes_list_sub_title) TextView _subTitleTextView;
	
	@Inject IProgressNoteResource _progressNoteResource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_notes_list);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT_ID) != null )
			_clientId = savedInstanceState.getLong(EXTRA_CLIENT_ID);
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
        listView.addHeaderView(buildListHeader(), null, true);
        listView.addFooterView(buildListFooter(), null, true);

		listView.setOnItemClickListener(new ListViewOnItemClickListener());
		
		fillDataAsync();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(EXTRA_CLIENT_ID, _clientId);
	}
	
	protected void initHeader(ClientSummaryDataContract client)
	{
		_titleTextView.setText(client.FirstName + " " + client.LastName);
		_subTitleTextView.setText(client.ClientId);
	}
	
	protected View buildListHeader()
	{
        LayoutInflater inflater = getLayoutInflater(); 
        View header = inflater.inflate( R.layout.activity_progress_notes_list_header, null, false);
        return header;
	}
	
	protected View buildListFooter()
	{
        LayoutInflater inflater = getLayoutInflater(); 
        View header = inflater.inflate( R.layout.activity_progress_notes_list_footer, null, false);
        return header;
	}
	
	public void onRefreshClick(View view)
	{
		fillDataAsync();
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onFooterClick(View view)
	{
		UIHelper.ShowUnderConstructionMessage(ProgressNoteListActivity.this);
	}

	public void onHeaderClick(View view)
	{
		UIHelper.ShowUnderConstructionMessage(ProgressNoteListActivity.this);
	}

	private void fillDataAsync()
	{
		if( ! UIHelper.IsOnline(this))
			// show empty list of progress notes
			UIHelper.ShowAlertDialog(this, "Unable to connect to server", "An internet connection could not be established. Please connect this device to the internet and try again.");
		else
		{
			LoadProgressNotesTask task = new LoadProgressNotesTask(this);
			task.execute();
		}
	}
	
	private class ListViewOnItemClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index,
				long id) {
			
			//TODO Show Progress Note
			UIHelper.ShowUnderConstructionMessage(ProgressNoteListActivity.this);
		}
	}
	
	   private class LoadProgressNotesTask extends AsyncTask<Void, Integer, GetProgressNotesResponse>
	    {
	    	private Activity _context;
	    	private ProgressDialog _progressDialog;
	    	private Exception _exception;

	    	public LoadProgressNotesTask(Activity context)
	    	{
	    		_context = context;
	    		
	            _progressDialog = new ProgressDialog(context);
	            _progressDialog.setMessage("Loading progress notes for client...");
	            _progressDialog.setIndeterminate(true);
	            _progressDialog.setCancelable(false);
	    	}
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		super.onPreExecute();
	            _progressDialog.show();
	    	}
	    	
			@Override
			protected GetProgressNotesResponse doInBackground(Void... arg0) {
				
				GetProgressNotesResponse result = null;
				
				try {
					
					result = _progressNoteResource.Get(_clientId.longValue(), 0, 10);
					
				} catch (Exception e) {
					AppLog.error(e.getMessage());
					_exception = e;
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(GetProgressNotesResponse result) {
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
					ProgressNoteAdapter adapter = new ProgressNoteAdapter(_context, R.layout.progress_note_item, result.ProgressNotes);
					ListView listView = getListView();
					listView.setAdapter(adapter);
					listView.invalidate();
					
					initHeader(result.Client);
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
