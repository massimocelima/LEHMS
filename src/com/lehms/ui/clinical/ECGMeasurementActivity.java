package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.persistence.EventType;
import com.lehms.ui.clinical.device.ECGMeasurementDeviceProvider;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.ECGMeasurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class ECGMeasurementActivity  extends ClinicalMeasurmentBaseActivity<ECGMeasurement> { 

	@InjectView(R.id.activity_measurment_ecg_pulse_edit) EditText _pulseEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_ecg);
		init();
	}

	@Override
	protected IMeasurementDeviceProvider<ECGMeasurement> getDeviceProvider() {
		return new ECGMeasurementDeviceProvider();
	}

	@Override
	protected void openAuoMeasurementActivity(
			IMeasurementDevice<ECGMeasurement> device) {
		Intent intent = new Intent(this, ECGMeasurementAutoActivity.class);
		intent.putExtra(ClinicalMeasurmentAutoBaseActivity.EXTRA_DEVICE, device);
		startActivityForResult(intent, ClinicalMeasurmentBaseActivity.REQUEST_AUTO_MEASURMENT);
	}

	@Override
	protected Boolean validate() {
		if( _pulseEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for pulse.");
			return false;
		}
		return true;
	}

	@Override
	protected ECGMeasurement getMeasurement() {
		ECGMeasurement measurement = new ECGMeasurement();
		measurement.Pulse = Integer.parseInt( _pulseEdit.getText().toString() );
		measurement.ClientId = _client.ClientId;
		return measurement;
	}

	@Override
	protected void loadMeasurement(ECGMeasurement measurement) {
		_pulseEdit.setText(measurement.Pulse + "");
	}

	@Override
	protected EventType getEventType() {
		return EventType.ECGTaken;
	}

}
