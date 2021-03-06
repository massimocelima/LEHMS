package com.lehms;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.lehms.controls.*;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.lehms.adapters.ClientSummaryAdapter;
import com.lehms.adapters.FormDefinitionAdapter;
import com.lehms.adapters.JobAdapter;
import com.lehms.messages.GetFormDefinitionResponse;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.messages.formDefinition.FormDefinition;
import com.lehms.serviceInterface.CacheItem;
import com.lehms.serviceInterface.ICache;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.serviceInterface.IFormDefinitionResource;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
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
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class FormsActivity  extends LehmsRoboListActivity { //implements AsyncQueryListener 

	public final static String EXTRA_CLIENT = "client";

	@Inject protected IFormDefinitionResource _formDefinitionResource;
	@Inject protected ICache _cache;

	@InjectExtra(EXTRA_CLIENT) protected ClientDataContract _client;
	@InjectView(R.id.activity_forms_title) TextView _title; 
	@InjectView(R.id.activity_forms_sub_title) TextView _subTitle; 
	@InjectView(R.id.activity_forms_sub_title2) TextView _subTitle2; 
	
	private ListQuickAction _qa;
	
	private FormDefinition _selectedFormDefinition;
	private FormDefinitionAdapter _adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forms);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true); 
		
		_subTitle.setText(_client.FirstName + " " + _client.LastName);
		_subTitle2.setText(_client.ClientId);
		
		LoadForms(false);
				
		final ActionItem qaNewForm = new ActionItem();
		
		qaNewForm.setTitle("Complete Form");
		qaNewForm.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_complete_forms_new));
		qaNewForm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NavigationHelper.createFormDetails(FormsActivity.this, _selectedFormDefinition, _client);
				_qa.dismiss();
			}
		});
				
		final ActionItem qaViewForms = new ActionItem();
		
		qaViewForms.setTitle("View Forms");
		qaViewForms.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_complete_forms));
		qaViewForms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NavigationHelper.viewFormDetailsList(FormsActivity.this, _client, _selectedFormDefinition);
				_qa.dismiss();
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() { 
			    public void onItemClick(AdapterView<?> parent, View view, 
			        int position, long id) { 
			    	
			    	_selectedFormDefinition = _adapter.getItem(position);
			    	
					_qa = new ListQuickAction(view);
					
					_qa.addActionItem(qaNewForm);
					if( _selectedFormDefinition.ActivityReference == null ||  
							_selectedFormDefinition.ActivityReference.equals("") )
						_qa.addActionItem(qaViewForms);
					_qa.setAnimStyle(ListQuickAction.ANIM_AUTO);
					
					_qa.show();
			    }
			  }); 
	}

	@Override
		protected void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putSerializable(EXTRA_CLIENT, _client);
		}
	
	private void LoadForms(Boolean reload){
		LoadFormsTask task = new LoadFormsTask(this, reload);
		task.execute();
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onRefreshClick(View view)
	{
		LoadForms(true);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	private class LoadFormsTask extends AsyncTask<Void, Void, GetFormDefinitionResponse>
    {
    	private Activity _context;
    	private ProgressDialog _progressDialog;
    	private Exception _exception;
    	private Boolean _reaload;

    	public LoadFormsTask(Activity context, Boolean reaload)
    	{
    		_context = context;
    		_reaload = reaload;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading form definitions please wait...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(true);
            
            _progressDialog.setOnCancelListener(new OnCancelListener() 
            {             
            	@Override             
            	public void onCancel(DialogInterface dialog) {                 
            		cancel(true);             
            	}         
            });
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
		protected GetFormDefinitionResponse doInBackground(Void... arg0) {
			
			GetFormDefinitionResponse result = null;
						
			try 
			{
				if(_reaload)
				{
					result = _formDefinitionResource.Get();
					CacheHelper.put(_cache, result, "form_definitions", 60 * 24);
				}
				else
				{
					CacheItem cacheItem = _cache.get("form_definitions");
					if( cacheItem == null || 
							( UIHelper.IsOnline(_context) && cacheItem.hasExpired() ))
					{
						result = _formDefinitionResource.Get();
						CacheHelper.put(_cache, result, "form_definitions", 60 * 24);
					}
					else
						result = (GetFormDefinitionResponse)cacheItem.Item;
				}

			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			} 
			
			return result;
		}
		
		@Override
		protected void onPostExecute(GetFormDefinitionResponse result) {
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
				_adapter = new FormDefinitionAdapter(_context, R.layout.form_definition_item, result.FormDefinitions);
				ListView listView = getListView();
				listView.setAdapter(_adapter);
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
