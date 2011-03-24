package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.persistence.EventType;
import com.lehms.ui.clinical.device.BloodPressureMeasurementDeviceProvider;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.ui.clinical.device.TemperatureMeasurementDeviceProvider;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.ECGMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class BloodPressureMeasurementActivity  extends ClinicalMeasurmentBaseActivity<BloodPressureMeasurement> { 
	
	@InjectView(R.id.activity_measurment_blood_pressure_systolic_edit) TextView _systolicTextView;
	@InjectView(R.id.activity_measurment_blood_pressure_diastolic_edit) TextView _diastlicTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_blood_pressure);
		init();
	}

	@Override
	protected IMeasurementDeviceProvider<BloodPressureMeasurement> getDeviceProvider() {
		return new BloodPressureMeasurementDeviceProvider();
	}

	@Override
	protected void openAuoMeasurementActivity(
			IMeasurementDevice<BloodPressureMeasurement> device) {
		Intent intent = new Intent(this, BloodPressureMeasurementAutoActivity.class);
		intent.putExtra(ClinicalMeasurmentAutoBaseActivity.EXTRA_DEVICE, device);
		startActivityForResult(intent, ClinicalMeasurmentBaseActivity.REQUEST_AUTO_MEASURMENT);
	}

	@Override
	protected Boolean validate() {
		if( _systolicTextView.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Systolic.");
			return false;
		}
		if( _diastlicTextView.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Diastolic.");
			return false;
		}
		return true;
	}

	@Override
	protected BloodPressureMeasurement getMeasurement() {
		BloodPressureMeasurement result = new BloodPressureMeasurement();
		result.Systolic = Integer.parseInt( _systolicTextView.getText().toString() );
		result.Diastolic = Integer.parseInt( _diastlicTextView.getText().toString() );
		result.ClientId = _client.ClientId;
		return result;
	}

	@Override
	protected void loadMeasurement(BloodPressureMeasurement measurement) {
		_systolicTextView.setText(measurement.Systolic + "");
		_diastlicTextView.setText(measurement.Diastolic + "");
	}

	@Override
	protected EventType getEventType() {
		return EventType.BloodPressureTaken;
	}
	
}
