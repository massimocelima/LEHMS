package com.lehms;

import java.util.ArrayList;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.messages.GetFormDataListResponse;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.FormDataSummaryDataContract;
import com.lehms.messages.formDefinition.*;
import com.lehms.serviceInterface.IFormDataResource;
import com.lehms.util.AppLog;
import com.lehms.adapters.FormDataSummaryAdapter;

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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class FormDetailsListActivity extends RoboListActivity  { 

	public final static String EXTRA_CLIENT = "client";
	public final static String EXTRA_FORM_DEFINITION = "form_definition";
	
	private int _pageIndex = 0;
	private static final int PAGE_SIZE = 10;

	@InjectView(R.id.title_bar_title) TextView _bannerTitle; 
	@InjectView(R.id.activity_form_details_list_title) TextView _title; 
	@InjectView(R.id.activity_form_details_list_sub_title) TextView _subTitle; 

	@InjectExtra(EXTRA_CLIENT) protected ClientDataContract _client;
	@InjectExtra(EXTRA_FORM_DEFINITION) protected FormDefinition _formDefinition;
	
	private ImageView _refreshImageView;
	private TextView _refreshTextView;
	private TextView _noConnectionTextView;
	
	private FormDataSummaryAdapter _adapter = null;

	@Inject IFormDataResource _formDataResource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_details_list);
			
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null )
			_client = (ClientDataContract)savedInstanceState.get(EXTRA_CLIENT);

		if(savedInstanceState != null && savedInstanceState.get(EXTRA_FORM_DEFINITION) != null )
			_formDefinition = (FormDefinition)savedInstanceState.get(EXTRA_FORM_DEFINITION);

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
		outState.putSerializable(EXTRA_CLIENT, _client);
		outState.putSerializable(EXTRA_FORM_DEFINITION, _formDefinition);
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	public void onRefreshClick(View view)
	{
		fillDataAsync(true);
	}
	
	protected void initHeader()
	{
		_title.setText(_formDefinition.Name);
		_subTitle.setText(_client.FirstName + " " + _client.LastName);
	}
	
	protected View buildListHeader()
	{
        LayoutInflater inflater = getLayoutInflater(); 
        View header = inflater.inflate( R.layout.activity_form_details_list_header, null, false);
        return header;
	}
	
	protected View buildListFooter()
	{
        LayoutInflater inflater = getLayoutInflater(); 
        View header = inflater.inflate( R.layout.activity_form_details_list_footer, null, false);
        _refreshImageView = (ImageView)header.findViewById(R.id.activity_form_details_list_footer_ic_refresh);
        _refreshTextView = (TextView)header.findViewById(R.id.activity_form_details_list_footer_refresh);
        _noConnectionTextView = (TextView) header.findViewById(R.id.activity_form_details_list_footer_no_connection);
        return header;
	}
	
	
	private void fillDataAsync(Boolean refreshData)
	{
		if( ! UIHelper.IsOnline(this))
		{
			_adapter = new FormDataSummaryAdapter(this, R.layout.form_data_summary_item, new ArrayList<FormDataSummaryDataContract>());
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

			LoadFormDataListTask task = new LoadFormDataListTask(this, refreshData);
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
					NavigationHelper.createFormDetails(FormDetailsListActivity.this, _formDefinition, _client);
				}
				else
				{
					// On Footer Click
					_pageIndex += 1;
					fillDataAsync(false);
				}
			}
			else
			{
				FormDataSummaryDataContract formData = _adapter.getItem(index - 1);
				LoadFormDataTask task = new LoadFormDataTask( FormDetailsListActivity.this, formData.FormDataId);
				task.execute(null);
			}
		}
	}
	
	   private class LoadFormDataListTask extends AsyncTask<Void, Integer, GetFormDataListResponse>
	    {
	    	private Activity _context;
	    	private ProgressDialog _progressDialog;
	    	private Exception _exception;
	    	private Boolean _refreshData;

	    	public LoadFormDataListTask(Activity context, Boolean refreshData)
	    	{
	    		_context = context;
	    		
	            _progressDialog = new ProgressDialog(context);
	            _progressDialog.setMessage("Loading " + _formDefinition.Title + " for client...");
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
	        		_pageIndex = 0;
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

				if(_pageIndex >= 0)
					_pageIndex -= 1;
				
				if( _adapter == null )
				{
					_adapter = new FormDataSummaryAdapter(_context, R.layout.form_data_summary_item, new ArrayList<FormDataSummaryDataContract>());
					listView.setAdapter(_adapter);
				}
				listView.invalidate();
	    	}
	    	
			@Override
			protected GetFormDataListResponse doInBackground(Void... arg0) {
				
				GetFormDataListResponse result = null;
				
				try {
					result = _formDataResource.Get( Long.parseLong(_client.ClientId), _formDefinition.Id, _pageIndex * PAGE_SIZE, PAGE_SIZE);
				} catch (Exception e) {
					AppLog.error(e.getMessage());
					_exception = e;
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(GetFormDataListResponse result) {
				super.onPostExecute(result);
				
				if(isCancelled())
					return;
				
				if( result == null )
				{
					if( _exception != null )
						createDialog("Error", "Error retriving forms: " + _exception.getMessage());
					else
						createDialog("Error", "Error retriving forms");
				}
				else
				{
					ListView listView = getListView();
					
					if( _refreshData )
					{
						_adapter = new FormDataSummaryAdapter(_context, R.layout.form_data_summary_item, result.FormDataSummaries);
						listView.setAdapter(_adapter);
						_pageIndex = 0;
					}
					else {
						for(int i = 0; i < result.FormDataSummaries.size(); i++)
							_adapter.add(result.FormDataSummaries.get(i));
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
	   
	   private class LoadFormDataTask extends AsyncTask<Void, Integer, FormData>
	   {
	    	private Activity _context;
	    	private ProgressDialog _progressDialog;
	    	private Exception _exception;
	    	private UUID _formDataId;
	    	
	    	public LoadFormDataTask(Activity context, UUID formDataId)
	    	{
	    		_context = context;
	    		
	            _progressDialog = new ProgressDialog(context);
	            _progressDialog.setMessage("Loading " + _formDefinition.Title + " details...");
	            _progressDialog.setIndeterminate(true);
	            _progressDialog.setCancelable(true);
	            
	            _progressDialog.setOnCancelListener(new OnCancelListener() 
	            {             
	            	@Override             
	            	public void onCancel(DialogInterface dialog) {                 
	            		cancel(true);             
	            	}         
	            });
	            
	            _formDataId = formDataId;
	    	}
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		super.onPreExecute();
	            _progressDialog.show();
	    	}
	    	
	    	@Override
	    	protected void onCancelled() {
	    		super.onCancelled();
	    	}
	    	
			@Override
			protected FormData doInBackground(Void... arg0) {
				
				FormData result = null;
				
				try {
					result = _formDataResource.Get(_formDataId);
				} catch (Exception e) {
					AppLog.error(e.getMessage());
					_exception = e;
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(FormData result) {
				super.onPostExecute(result);
				
				if(isCancelled())
					return;
				
				if( result == null )
				{
					if( _exception != null )
						createDialog("Error", "Error retriving form data: " + _exception.getMessage());
					else
						createDialog("Error", "Error retriving form data");
				}
				else
				{
					NavigationHelper.viewFormDetails(FormDetailsListActivity.this, _formDefinition, result, _client);
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
