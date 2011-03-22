package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.ui.clinical.device.SPO2MeasurementDeviceProvider;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class SPO2MeasurementActivity extends ClinicalMeasurmentBaseActivity { 

	public static final String EXTRA_CLIENT = "client";
	
	private static final int REQUEST_AUTO_MEASURMENT = 0;
	
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;
	
	@InjectView(R.id.activity_title) TextView _title;
	@InjectView(R.id.activity_sub_title) TextView _subtitle;
	//@InjectView(R.id.activity_sub_title2) TextView _subtitle2;

	@InjectView(R.id.activity_measurment_spo2_edit) EditText _oxegenEdit;
	@InjectView(R.id.activity_measurment_spo2_pulse_edit) EditText _pulseEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_spo2);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		_subtitle.setText(_client.FirstName + " " + _client.LastName);
		//_subtitle2.setText(_client.ClientId);
		
		if(isAutoEntryForm())
		{
			if( ! UIHelper.HasBluetoth() )
				UIHelper.ShowAlertDialog(this, "No bluetooth found on device", "No bluetooth found on device");
			else
				openAutoEntryForm();
		}
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
	
	public void onTakeAutoMeasurementClick(View view)
	{
		openAutoEntryForm();
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

	@Override
	protected IMeasurementDeviceProvider getDeviceProvider() {
		return new SPO2MeasurementDeviceProvider();
	}

	@Override
	protected void openAuoMeasurementActivity(IMeasurementDevice device) {
		Intent intent = new Intent(this, SPO2AutoMeasurementActivity.class);
		intent.putExtra(SPO2AutoMeasurementActivity.EXTRA_DEVICE, device);
		startActivityForResult(intent, REQUEST_AUTO_MEASURMENT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_AUTO_MEASURMENT)
		{
			if( resultCode == Activity.RESULT_OK)
			{
				SPO2Measurement measurement = (SPO2Measurement)data.getSerializableExtra(SPO2AutoMeasurementActivity.EXTRA_MEASUREMENT);
				_oxegenEdit.setText(measurement.OxegenPercent + "");
				_pulseEdit.setText(measurement.Pulse + "");
			}
		}
	}
}
