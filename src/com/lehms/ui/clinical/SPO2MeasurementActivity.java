package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class SPO2MeasurementActivity  extends RoboActivity { 

	public static final String EXTRA_CLIENT = "client";
	
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;
	
	@InjectView(R.id.activity_title) TextView _title;
	@InjectView(R.id.activity_sub_title) TextView _subtitle;
	@InjectView(R.id.activity_sub_title2) TextView _subtitle2;

	@InjectView(R.id.activity_measurment_spo2_edit) EditText _oxegenEdit;
	@InjectView(R.id.activity_measurment_spo2_pulse_edit) EditText _pulseEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_spo2);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		_subtitle.setText(_client.FirstName + " " + _client.LastName);
		_subtitle2.setText(_client.ClientId);
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
		if( _oxegenEdit.getText().toString().equals("" ))
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for SPO2.");
		else
		{
			SPO2Measurement measurement = new SPO2Measurement();
			measurement.OxegenPercent = Double.parseDouble( _oxegenEdit.getText().toString() );
			measurement.Pulse = Integer.parseInt( _pulseEdit.getText().toString() );
			// Create an event and save the measurement
			finish();
		}
	}
}
