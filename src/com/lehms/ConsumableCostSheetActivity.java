package com.lehms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

import com.google.android.maps.ItemizedOverlay;
import com.google.inject.Inject;
import com.lehms.messages.AddConsumableCostItemRequest;
import com.lehms.messages.CreateProgressNoteRequest;
import com.lehms.messages.GetConsumableCostItemsResponse;
import com.lehms.messages.GetFormDefinitionResponse;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ConsumableCostItem;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.messages.formDefinition.FormElementOption;
import com.lehms.serviceInterface.CacheItem;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.ICache;
import com.lehms.serviceInterface.IConsumableCostItemResource;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.serviceInterface.IProgressNoteResource;
import com.lehms.util.AppLog;
import com.lehms.media.StreamingMediaPlayer;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import roboguice.activity.RoboActivity;
import roboguice.inject.*;;


public class ConsumableCostSheetActivity extends LehmsRoboActivity implements ISaveEventResultHandler {

	@InjectExtra(optional=true, value=FormDetailsActivity.EXTRA_CLIENT) ClientDataContract _client; 
	
	@Inject IConsumableCostItemResource _consumableCostItemResource;
	@Inject ICache _cache;

	@Inject IEventRepository _eventRepository;
	@Inject IEventFactory _eventEventFactory;
	@Inject IEventExecuter _eventExecuter;
	@Inject IActiveJobProvider _activeJobProvider;
	
	@InjectView(R.id.activity_title) TextView _titleTextView;

	@InjectView(R.id.activity_consumable_cost_sheet_item) Spinner _itemSpinner;
	@InjectView(R.id.activity_consumable_cost_sheet_cost) EditText _costEditText;
	@InjectView(R.id.activity_consumable_cost_sheet_quantity) EditText _quantityEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumable_cost_sheet);
        
		if(savedInstanceState != null && savedInstanceState.get(FormDetailsActivity.EXTRA_CLIENT) != null )
			_client = (ClientDataContract)savedInstanceState.get(FormDetailsActivity.EXTRA_CLIENT);

		LoadConsumableCostSheetItemsTask task = new LoadConsumableCostSheetItemsTask();
		task.execute(null);
		
		_titleTextView.setText(_client.FirstName + " " + _client.LastName);
		
		_itemSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				ConsumableCostSheetActivity.this.onItemClick(arg0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				ConsumableCostSheetActivity.this.onItemClick(arg0);
			}});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(FormDetailsActivity.EXTRA_CLIENT, _client);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void onSaveClick(View view)
	{
		_eventRepository.open();

		try
		{
			if( _quantityEditText.getText().toString().equals("" ))
			{
				UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the quantity.");
				return;
			}
			if( _itemSpinner.getSelectedItemPosition() < 0 )
			{
				UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the item.");
				return;
			}
			
			AddConsumableCostItemRequest request = new AddConsumableCostItemRequest();
			request.Item = (ConsumableCostItem) _itemSpinner.getSelectedItem();
			request.Quantity = Integer.parseInt(_quantityEditText.getText().toString());
			request.ClientId = _client.ClientId;
			
			if(_activeJobProvider.get() != null)
				request.JobId = _activeJobProvider.get().JobId;
			
			Event event = _eventEventFactory.create(request, EventType.AddConsumableCost);
			UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, "Consumable Cost Item");
			
		}
		catch(Exception ex)
		{
			AppLog.error(ex.getMessage(), ex);
			UIHelper.ShowAlertDialog(this, "Error saving consumable cost", "An error has occured while saving the consumable cost: " + ex.getMessage());
		}
		finally
		{
			_eventRepository.close();
		}
	}

	@Override
	public void onSuccess(Object data) {
		// Returns the progress note to the parent activity
		UIHelper.ShowToast(this, "Consumable cost sheet saved."); 
		
		this.finish();
	}

	@Override
	public void onError(Exception e) {
		UIHelper.ShowToast(this, "Error saving consumable cost sheet."); 
	}
	
	public void onCancelClick(View view)
	{
		this.finish();
	}
	
	public void onItemClick(View view)
	{
		if(_itemSpinner.getSelectedItem() != null)
			_costEditText.setText( "$" + ((ConsumableCostItem)_itemSpinner.getSelectedItem()).Cost);
		else
			_costEditText.setText("");
	}
	
   private class LoadConsumableCostSheetItemsTask extends AsyncTask<Void, Void, GetConsumableCostItemsResponse>
   {
    	private ProgressDialog _progressDialog;
    	private Exception _exception;

    	public LoadConsumableCostSheetItemsTask()
    	{
            _progressDialog = new ProgressDialog(ConsumableCostSheetActivity.this);
            _progressDialog.setMessage("Loading items...");
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
			ConsumableCostSheetActivity.this.finish();
		}
		
		@Override
		protected GetConsumableCostItemsResponse doInBackground(Void... arg0) {
			
			GetConsumableCostItemsResponse result = null;
			
			try {
				
				CacheItem cacheItem = _cache.get("consumable_cost_items");
				if( cacheItem == null || 
						( UIHelper.IsOnline(ConsumableCostSheetActivity.this) && cacheItem.hasExpired() ))
				{
					result = _consumableCostItemResource.Get();
					CacheHelper.put(_cache, result, "consumable_cost_items", 60 * 24);
				}
				else
					result = (GetConsumableCostItemsResponse)cacheItem.Item;
				
			} catch (Exception e) {
				_exception = e;
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(GetConsumableCostItemsResponse result) {
			super.onPostExecute(result);
			
			if(isCancelled())
				return;
			
			if( result == null )
			{
				UIHelper.handleException(ConsumableCostSheetActivity.this, _exception, "Error retriving items");
				}
				else
				{
			        ArrayAdapter<ConsumableCostItem> adapter = new ArrayAdapter<ConsumableCostItem>(ConsumableCostSheetActivity.this, 
			        		android.R.layout.simple_spinner_item, 
			        		result.Items);
			        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
			        _itemSpinner.setAdapter(adapter);
			        onItemClick(null);
				}
				if( _progressDialog.isShowing() )
					_progressDialog.dismiss();
			}
		}
	
}
