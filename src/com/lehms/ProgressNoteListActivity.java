package com.lehms;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.adapters.JobAdapter;
import com.lehms.adapters.ProgressNoteAdapter;
import com.lehms.messages.GetProgressNotesResponse;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IProgressNoteResource;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;;

public class ProgressNoteListActivity extends RoboListActivity {

	public static final String EXTRA_CLIENT_ID = "client_id"; 
	public static final String EXTRA_CLIENT_NAME = "client_name"; 
	
	private int _currentPageIndex = 0;
	private static final int PAGE_SIZE = 10;
	
	@InjectExtra(EXTRA_CLIENT_ID) public Long _clientId;
	@InjectExtra(EXTRA_CLIENT_NAME) public String _clientName;

	@InjectView(R.id.activity_progress_notes_list_title) TextView _titleTextView;
	@InjectView(R.id.activity_progress_notes_list_sub_title) TextView _subTitleTextView;

	private ImageView _refreshImageView;
	private TextView _refreshTextView;
	private TextView _noConnectionTextView;
	private ProgressNoteAdapter _adapter = null;

	@Inject IProgressNoteResource _progressNoteResource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_notes_list);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT_ID) != null )
			_clientId = savedInstanceState.getLong(EXTRA_CLIENT_ID);

		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT_NAME) != null )
			_clientName = savedInstanceState.getString(EXTRA_CLIENT_NAME);

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
        listView.addHeaderView(buildListHeader(), null, true);
        listView.addFooterView(buildListFooter(), null, true);
        
		listView.setOnItemClickListener(new ListViewOnItemClickListener());
		
		initHeader();

		fillDataAsync(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(EXTRA_CLIENT_ID, _clientId);
		outState.putString(EXTRA_CLIENT_NAME, _clientName);
	}
	
	protected void initHeader()
	{
		_titleTextView.setText(_clientName);
		_subTitleTextView.setText(_clientId + "");
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
        _refreshImageView = (ImageView)header.findViewById(R.id.activity_prgress_notes_list_footer_ic_refresh);
        _refreshTextView = (TextView)header.findViewById(R.id.activity_prgress_notes_list_footer_refresh);
        _noConnectionTextView = (TextView) header.findViewById(R.id.activity_prgress_notes_list_footer_no_connection);
        return header;
	}
	
	public void onRefreshClick(View view)
	{
		fillDataAsync(true);
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	private void fillDataAsync(Boolean refreshData)
	{
		if( ! UIHelper.IsOnline(this))
		{
			_adapter = new ProgressNoteAdapter(this, R.layout.progress_note_item, new ArrayList<ProgressNoteDataContract>());
			getListView().setAdapter(_adapter);

			_refreshImageView.setVisibility(View.GONE);
			_refreshTextView.setVisibility(View.GONE);
			_noConnectionTextView.setVisibility(View.VISIBLE);
		}
		else
		{
			_refreshImageView.setVisibility(View.VISIBLE);
			_refreshTextView.setVisibility(View.VISIBLE);
			_noConnectionTextView.setVisibility(View.GONE);

			LoadProgressNotesTask task = new LoadProgressNotesTask(this, refreshData);
			task.execute();
		}
	}
	
	private class ListViewOnItemClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index,
				long id) {
			
			if( id == -1)
			{
				if( index == 0 )
				{
					NavigationHelper.createProgressNote(ProgressNoteListActivity.this, _clientId, _clientName );
				}
				else
				{
					// On Footer Click
					_currentPageIndex += 1;
					fillDataAsync(false);
				}
			}
			else
			{
				ProgressNoteDataContract note = _adapter.getItem(index - 1);
				NavigationHelper.viewProgressNote(ProgressNoteListActivity.this, note.Id);
			}
		}
	}
	
	   private class LoadProgressNotesTask extends AsyncTask<Void, Integer, GetProgressNotesResponse>
	    {
	    	private Activity _context;
	    	private ProgressDialog _progressDialog;
	    	private Exception _exception;
	    	private Boolean _refreshData;

	    	public LoadProgressNotesTask(Activity context, Boolean refreshData)
	    	{
	    		_context = context;
	    		
	            _progressDialog = new ProgressDialog(context);
	            _progressDialog.setMessage("Loading progress notes for client...");
	            _progressDialog.setIndeterminate(true);
	            _progressDialog.setCancelable(true);
	            
	            _progressDialog.setOnCancelListener(new OnCancelListener() 
	            {             
	            	@Override             
	            	public void onCancel(DialogInterface dialog) {                 
	            		cancel(true);             
	            	}         
	            });
	            
	            _refreshData = refreshData;
	            if(_refreshData)
	        		_currentPageIndex = 0;
	    	}
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		super.onPreExecute();
	            _progressDialog.show();
	    	}
	    	
	    	@Override
	    	protected void onCancelled() {
	    		super.onCancelled();
				ListView listView = getListView();

				if(_currentPageIndex >= 0)
					_currentPageIndex -= 1;
				
				if( _adapter == null )
				{
					_adapter = new ProgressNoteAdapter(_context, R.layout.progress_note_item, new ArrayList<ProgressNoteDataContract>());
					listView.setAdapter(_adapter);
				}
				listView.invalidate();
	    	}
	    	
			@Override
			protected GetProgressNotesResponse doInBackground(Void... arg0) {
				
				GetProgressNotesResponse result = null;
				
				try {
					result = _progressNoteResource.Get(_clientId.longValue(), _currentPageIndex * PAGE_SIZE, PAGE_SIZE);
					
				} catch (Exception e) {
					AppLog.error(e.getMessage());
					_exception = e;
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(GetProgressNotesResponse result) {
				super.onPostExecute(result);
				
				if(isCancelled())
					return;
				
				if( result == null )
				{
					if( _exception != null )
						createDialog("Error", "Error retriving roster: " + _exception.getMessage());
					else
						createDialog("Error", "Error retriving roster");
				}
				else
				{
					ListView listView = getListView();
					
					if( _refreshData )
					{
						_adapter = new ProgressNoteAdapter(_context, R.layout.progress_note_item, result.ProgressNotes);
						listView.setAdapter(_adapter);
						_currentPageIndex = 0;
					}
					else {
						for(int i = 0; i < result.ProgressNotes.size(); i++)
							_adapter.add(result.ProgressNotes.get(i));
					}
					
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
