package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.formDefinition.FormElementOption;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.BloodSugerLevelInsulinType;
import com.lehms.ui.clinical.model.BloodSugerLevelMeasurement;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class BloodSugerLevelMeasurementActivity  extends RoboActivity { 

	public static final String EXTRA_CLIENT = "client";
	
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;
	
	@InjectView(R.id.activity_title) TextView _title;
	@InjectView(R.id.activity_sub_title) TextView _subtitle;
	@InjectView(R.id.activity_sub_title2) TextView _subtitle2;

	@InjectView(R.id.activity_measurment_blood_suger_level_edit) EditText _bslEdit;
	@InjectView(R.id.activity_measurment_blood_suger_level_insulin_edit) EditText _insulinEdit;
	@InjectView(R.id.activity_measurment_blood_suger_level_insulin_type_edit) Spinner _insulinType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_blood_suger_level);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		_subtitle.setText(_client.FirstName + " " + _client.LastName);
		_subtitle2.setText(_client.ClientId);
		
        ArrayAdapter<BloodSugerLevelInsulinType> adapter = new ArrayAdapter<BloodSugerLevelInsulinType>( this, android.R.layout.simple_spinner_item, BloodSugerLevelInsulinType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _insulinType.setAdapter(adapter);
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
		if( _bslEdit.getText().toString().equals("" ))
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for bsl.");
		else
		{
			BloodSugerLevelMeasurement measurement = new BloodSugerLevelMeasurement();
			measurement.Level = Integer.parseInt( _bslEdit.getText().toString() );
			if(!_insulinEdit.getText().toString().equals(""))
			{
				measurement.Insulin = Integer.parseInt( _insulinEdit.getText().toString() );
				measurement.InsulinType = (BloodSugerLevelInsulinType) _insulinType.getSelectedItem();
			}
			// Create an event and save the measurement
			finish();
		}
	}
}
