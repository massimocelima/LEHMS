package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.formDefinition.FormElementOption;
import com.lehms.ui.clinical.device.BloodPressureMeasurementDeviceProvider;
import com.lehms.ui.clinical.device.BloodSugerLevelMeasurementDeviceProvider;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.BloodSugerLevelInsulinType;
import com.lehms.ui.clinical.model.BloodSugerLevelMeasurement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class BloodSugerLevelMeasurementActivity extends ClinicalMeasurmentBaseActivity<BloodSugerLevelMeasurement> { 
	
	@InjectView(R.id.activity_measurment_blood_suger_level_edit) EditText _bslEdit;
	@InjectView(R.id.activity_measurment_blood_suger_level_insulin_edit) EditText _insulinEdit;
	@InjectView(R.id.activity_measurment_blood_suger_level_insulin_type_edit) Spinner _insulinType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_blood_suger_level);
		init();
	
        ArrayAdapter<BloodSugerLevelInsulinType> adapter = new ArrayAdapter<BloodSugerLevelInsulinType>( this, android.R.layout.simple_spinner_item, BloodSugerLevelInsulinType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _insulinType.setAdapter(adapter);
	}

	@Override
	protected IMeasurementDeviceProvider<BloodSugerLevelMeasurement> getDeviceProvider() {
		return new BloodSugerLevelMeasurementDeviceProvider();
	}

	@Override
	protected void openAuoMeasurementActivity(
			IMeasurementDevice<BloodSugerLevelMeasurement> device) {
		Intent intent = new Intent(this, BloodSugerLevelMeasurementAutoActivity.class);
		intent.putExtra(ClinicalMeasurmentAutoBaseActivity.EXTRA_DEVICE, device);
		startActivityForResult(intent, ClinicalMeasurmentBaseActivity.REQUEST_AUTO_MEASURMENT);
	}

	@Override
	protected Boolean validate() {
		if( _bslEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for bsl.");
			return false;
		}
		return true;
	}

	@Override
	protected BloodSugerLevelMeasurement getMeasurement() {
		BloodSugerLevelMeasurement measurement = new BloodSugerLevelMeasurement();
		measurement.Level = Integer.parseInt( _bslEdit.getText().toString() );
		if(!_insulinEdit.getText().toString().equals(""))
		{
			measurement.Insulin = Integer.parseInt( _insulinEdit.getText().toString() );
			measurement.InsulinType = (BloodSugerLevelInsulinType) _insulinType.getSelectedItem();
		}
		return measurement;
	}

	@Override
	protected void loadMeasurement(BloodSugerLevelMeasurement measurement) {
		_bslEdit.setText(measurement.Level + "");
		
		if(measurement.Insulin != 0)
			_insulinEdit.setText(measurement.Insulin + "");
	}
}
