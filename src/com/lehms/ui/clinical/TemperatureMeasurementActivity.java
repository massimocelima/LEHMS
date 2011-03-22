package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
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

public class TemperatureMeasurementActivity  extends ClinicalMeasurmentBaseActivity<TemperatureMeasurement> { 
	
	@InjectView(R.id.activity_measurment_temperature_edit) EditText _temperatureEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_temperature);
		init();
	}

	@Override
	protected IMeasurementDeviceProvider<TemperatureMeasurement> getDeviceProvider() {
		return new TemperatureMeasurementDeviceProvider();
	}

	@Override
	protected void openAuoMeasurementActivity(
			IMeasurementDevice<TemperatureMeasurement> device) {
		Intent intent = new Intent(this, TemperatureAutoMeasurementActivity.class);
		intent.putExtra(ClinicalMeasurmentAutoBaseActivity.EXTRA_DEVICE, device);
		startActivityForResult(intent, ClinicalMeasurmentBaseActivity.REQUEST_AUTO_MEASURMENT);
	}

	@Override
	protected Boolean validate() {
		if( _temperatureEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the temperature.");
			return false;
		}
		return true;
	}

	@Override
	protected TemperatureMeasurement getMeasurement() {
		TemperatureMeasurement result = new TemperatureMeasurement();
		result.Degrees = Double.parseDouble( _temperatureEdit.getText().toString() );
		return result;
	}

	@Override
	protected void loadMeasurement(TemperatureMeasurement measurement) {
		_temperatureEdit.setText(measurement.Degrees + "");
	}
	
}
