package com.lehms;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.PhotoType;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class TakePhotoActivity  extends RoboActivity { //implements AsyncQueryListener 

    public static final int CAPTURE_PICTURE_INTENT = 1;

	public static final String EXTRA_CLIENT = "client";
	
	@InjectExtra( value=EXTRA_CLIENT, optional=true) ClientSummaryDataContract _clientSummary;

	@InjectView(R.id.activity_take_photo_clients) Spinner _clientsSpinner;
	@InjectView(R.id.activity_take_photo_clients_selected) TextView _clientSelectedTextView;
	@InjectView(R.id.activity_take_photo_type) Spinner _typeSpinner;
	@InjectView(R.id.activity_take_photo_take) Button _takePhotoButton;

	@Inject protected IClientResource _clientResource;

	private File _imageFile;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_clientSummary = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
        ArrayAdapter<PhotoType> adapter = new ArrayAdapter<PhotoType>( 
        		TakePhotoActivity.this, 
        		android.R.layout.simple_spinner_item, 
        		PhotoType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_typeSpinner.setAdapter(adapter);
		
		if(_clientSummary == null )
		{
			if( UIHelper.IsOnline(this))
				LoadClients();
			else
			{
				_takePhotoButton.setVisibility(View.GONE);
				UIHelper.ShowAlertDialog(this, "A data connection could not be established", "A data connection could not be established. Please connect to the internet and try again.");
			}
		}
		else
		{
			// Otherwise we have passed the client in
			_clientSelectedTextView.setVisibility(View.VISIBLE);
			_clientsSpinner.setVisibility(View.GONE);
			_clientSelectedTextView.setText(_clientSummary.toString());
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_CLIENT, _clientSummary);
	}

	private void LoadClients(){
		LoadClientsTask task = new LoadClientsTask(this);
		task.execute();
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	public void onCancelClick(View view)
	{
		finish();
	}
	
	public void onViewPhotosClick(View view)
	{
		//if( getSelectedPhotoType() == null )
			NavigationHelper.viewPictures(this, getSelectedClient());
		//else
		//	NavigationHelper.viewPictures(this, getSelectedClient(), getSelectedPhotoType());
	}

	private ClientSummaryDataContract getSelectedClient()
	{
		if( _clientSummary == null )
			return (ClientSummaryDataContract)_clientsSpinner.getSelectedItem();
		return _clientSummary;
	}

	private PhotoType getSelectedPhotoType()
	{
		return (PhotoType)_typeSpinner.getSelectedItem();
	}

	public void onTakeClick(View view)
	{
		String storageState = Environment.getExternalStorageState();         
		if( ! storageState.equals(Environment.MEDIA_MOUNTED)) {
			UIHelper.ShowAlertDialog(this, "External Storeage (SD Card) is required", "External Storeage (SD Card) is required.");
		}
		else
		{
			UUID photoId = UUID.randomUUID();
	        
			try {
				_imageFile = new File(UIHelper.GetClientPhotoPath(getSelectedClient().ClientId, photoId, getSelectedPhotoType(), "jpg"));
		        
		        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(_imageFile));
		        startActivityForResult(intent, CAPTURE_PICTURE_INTENT);

			} catch (Exception e) {
				UIHelper.ShowAlertDialog(this, "Could not create file", "Could not create file: "  + e);
				AppLog.error("Could not create file", e);
			}
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if( requestCode == CAPTURE_PICTURE_INTENT)
		{
			if( resultCode == Activity.RESULT_OK )
			{
				if( _imageFile.exists() )
				{
			        UIHelper.ShowToast(this.getApplicationContext(), "Photo saved to " + _imageFile.toString());
			        finish();
				}
				else
					UIHelper.ShowToast(this.getApplicationContext(), "Photo has not been saved");
			}
		}
        
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
		        ArrayAdapter<ClientSummaryDataContract> adapter = new ArrayAdapter<ClientSummaryDataContract>( 
		        		TakePhotoActivity.this, 
		        		android.R.layout.simple_spinner_item, 
		        		result);
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		        _clientsSpinner.setAdapter(adapter);
		        _clientsSpinner.invalidate();
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
