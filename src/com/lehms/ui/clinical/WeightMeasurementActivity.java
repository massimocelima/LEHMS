package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.ui.clinical.model.WeightMeasurement;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class WeightMeasurementActivity  extends RoboActivity { 

	public static final String EXTRA_CLIENT = "client";
	
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;
	
	@InjectView(R.id.activity_title) TextView _title;
	@InjectView(R.id.activity_sub_title) TextView _subtitle;
	@InjectView(R.id.activity_sub_title2) TextView _subtitle2;

	@InjectView(R.id.activity_measurment_weight_label) TextView _weightLabel;
	@InjectView(R.id.activity_measurment_weight_edit) EditText _weightEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_weight);
		
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
		if( _weightEdit.getText().toString().equals("" ))
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the weight.");
		else
		{
			WeightMeasurement measurement = new WeightMeasurement();
			measurement.Weight = Double.parseDouble( _weightEdit.getText().toString() );
			// Create an event and save the measurement
			finish();
		}
	}
}