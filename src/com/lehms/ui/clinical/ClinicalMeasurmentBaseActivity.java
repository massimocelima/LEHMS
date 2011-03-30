package com.lehms.ui.clinical;


import com.google.inject.Inject;
import com.lehms.ISaveEventResultHandler;
import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.controls.ActionItem;
import com.lehms.controls.QuickAction;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IPreviousMeasurmentProvider;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.ui.clinical.model.Measurement;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public abstract class ClinicalMeasurmentBaseActivity<T> extends RoboActivity implements ISaveEventResultHandler {
	
	public static final String EXTRA_CLIENT = "client";
	public static final String EXTRA_AUTO_ENTRY = "auto"; 

	public static final int REQUEST_ENABLE_BT = 1; 
	public static final int REQUEST_CONNECT_DEVICE = 2; 

	public static final int REQUEST_AUTO_MEASURMENT = 0;

	
	@InjectView (R.id.activity_measurment_take_measurement) Button _takeMeasurmentButton;
	@InjectView(R.id.activity_title) TextView _title;
	@InjectView(R.id.activity_sub_title) TextView _subtitle;
	
	private QuickAction _qaTakeMeasurment;

	@InjectExtra( optional=true, value=EXTRA_AUTO_ENTRY) protected Boolean _isAuto;
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;

	@Inject IEventRepository _eventRepository;
	@Inject IEventExecuter _eventExecuter;
	@Inject IEventFactory _eventEventFactory;
	@Inject IActiveJobProvider _activeJobProvider;
	@Inject IPreviousMeasurmentProvider _previousMeasurmentProvider;

	protected abstract IMeasurementDeviceProvider<T> getDeviceProvider();
	protected abstract void openAuoMeasurementActivity(IMeasurementDevice<T> device);
	protected abstract Boolean validate();
	protected abstract T getMeasurement();
	protected abstract void loadMeasurement(T measurement);	
	protected abstract EventType getEventType();
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);

		if(_takeMeasurmentButton != null)
			CreateQuickActions();
	};

	protected void init()
	{
		_subtitle.setText(_client.FirstName + " " + _client.LastName);
		
		if(isAutoEntryForm())
		{
			if( ! UIHelper.HasBluetoth() )
				UIHelper.ShowAlertDialog(this, "No bluetooth found on device", "No bluetooth found on device");
			else
				openAutoEntryForm();
		}
		
		CreateQuickActions();
	}
	
	@Override 
	protected void onSaveInstanceState(android.os.Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_CLIENT, _client);
	};
	
	//ISaveEventResultHandler Implementation
	@Override
	public void onSuccess(Object data) {
		this.finish();
	}

	@Override
	public void onError(Exception e) {
		UIHelper.ShowAlertDialog(this, "Error saving measurment", "Error ssving measurment: " + e.getMessage());
		AppLog.error("Error ssving measurment", e);
	}
	//ISaveEventResultHandler Implementation

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
	
	public void onTakeAutoMeasurementClick(View view)
	{
		openAutoEntryForm();
	}
	
	public void onSaveClick(View view)
	{
		if( validate() )
		{
			T measurement = getMeasurement();
			
			if(_activeJobProvider.get() != null)
				((com.lehms.ui.clinical.model.Measurement)measurement).JobId = _activeJobProvider.get().JobId;

			Event event = _eventEventFactory.create(measurement, getEventType());
			try {
				_previousMeasurmentProvider.putPreviousMeasurment(_client.ClientId, (Measurement)measurement);
				UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, this.getTitle().toString());
				UIHelper.ShowToast(this, this.getTitle() + " Saved");
			} catch (Exception e) {
				onError(e);
			}
		}
	}
		
	public Boolean isAutoEntryForm()
	{
		return _isAuto != null && _isAuto; 
	}

	public void openAutoEntryForm()
	{
		if ( !getDeviceProvider().isEnabled()) { 
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); 
		}
		else
		{
			try {
				IMeasurementDevice device = getDeviceProvider().getDefaultDevice();;
	
				if(device == null)
				{
		            Intent deviceListIntent = new Intent(this, DeviceListActivity.class);
		            deviceListIntent.putExtra(DeviceListActivity.EXTRA_DEVICE_PROVIDER_CLASS, getDeviceProvider().getClass().getName());
		            startActivityForResult(deviceListIntent, REQUEST_CONNECT_DEVICE);
				}
				else
				{
					openAuoMeasurementActivity(device);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override 
	protected void onDestroy() {
		super.onDestroy();
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_ENABLE_BT )
		{
			if(resultCode == RESULT_OK)
			{
				openAutoEntryForm();
			}
		}
		else if(requestCode == REQUEST_CONNECT_DEVICE)
		{
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                IMeasurementDevice device = (IMeasurementDevice)data.getExtras().getSerializable(DeviceListActivity.EXTRA_SELECTED_DEVICE);
                getDeviceProvider().setDefaultDevice(device);
                openAuoMeasurementActivity(device);
            }
		}
		else if( requestCode == REQUEST_AUTO_MEASURMENT)
		{
			if( resultCode == Activity.RESULT_OK)
			{
				T measurement = (T)data.getSerializableExtra(ClinicalMeasurmentAutoBaseActivity.EXTRA_MEASUREMENT);
				loadMeasurement(measurement);
				onSaveClick(null);
			}
		}
		
	}
	
	public void CreateQuickActions()
	{
		final ActionItem qaSelectDevice = new ActionItem();
		
		qaSelectDevice.setTitle("Select Device");
		qaSelectDevice.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_bluetooth));
		qaSelectDevice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	            Intent deviceListIntent = new Intent(ClinicalMeasurmentBaseActivity.this, DeviceListActivity.class);
	            deviceListIntent.putExtra(DeviceListActivity.EXTRA_DEVICE_PROVIDER_CLASS, getDeviceProvider().getClass().getName());
	            startActivityForResult(deviceListIntent, REQUEST_CONNECT_DEVICE);
	            _qaTakeMeasurment.dismiss();
			}
		});

		final ActionItem qaGetMeasurment = new ActionItem();
		
		qaGetMeasurment.setTitle("Get Measurement");
		qaGetMeasurment.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_bluetooth));
		qaGetMeasurment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openAutoEntryForm();
				_qaTakeMeasurment.dismiss();
			}
		});

		_takeMeasurmentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				_qaTakeMeasurment = new QuickAction(_takeMeasurmentButton);
				_qaTakeMeasurment.addActionItem(qaSelectDevice);
				_qaTakeMeasurment.addActionItem(qaGetMeasurment);
				_qaTakeMeasurment.show();

			}
		});
	}


}
