package com.lehms.ui.clinical;

import com.google.inject.Inject;
import com.lehms.ISaveEventResultHandler;
import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.ECGMeasurement;
import com.lehms.ui.clinical.model.INRMeasurement;
import com.lehms.util.AppLog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class INRMeasurementActivity  extends RoboActivity implements ISaveEventResultHandler { 

	public static final String EXTRA_CLIENT = "client";
	
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;
	
	@InjectView(R.id.activity_title) TextView _title;
	@InjectView(R.id.activity_sub_title) TextView _subtitle;
	//@InjectView(R.id.activity_sub_title2) TextView _subtitle2;

	@InjectView(R.id.activity_measurment_inr_edit) EditText _inrEdit;

	@Inject IEventRepository _eventRepository;
	@Inject IEventExecuter _eventExecuter;
	@Inject IEventFactory _eventEventFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_inr);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		_subtitle.setText(_client.FirstName + " " + _client.LastName);
		//_subtitle2.setText(_client.ClientId);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_CLIENT, _client);
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
		
	public void onSaveClick(View view)
	{
		if( _inrEdit.getText().toString().equals("" ))
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for INR.");
		else
		{
			
			INRMeasurement measurement = new INRMeasurement();
			measurement.Value = Double.parseDouble(_inrEdit.getText().toString() );
			measurement.ClientId = _client.ClientId;

			Event event = _eventEventFactory.create(measurement, EventType.INRTaken);
			try {
				UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, this.getTitle().toString());
				UIHelper.ShowToast(this, "INR Measurement Saved");

				finish();
			} catch (Exception e) {
				onError(e);
			}
		}
	}
	
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

}
