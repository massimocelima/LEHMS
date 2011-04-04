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
import com.lehms.ui.clinical.model.RespiratoryRateMeasurement;
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

public class  RespiratoryRateMeasurementActivity  extends ClinicalMeasurmentBaseActivity<RespiratoryRateMeasurement> { 
	
	@InjectView(R.id.activity_measurement_respiratory_rate_edit) TextView _rateTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_respiratory_rate);
		init();
	}

	@Override
	protected IMeasurementDeviceProvider<RespiratoryRateMeasurement> getDeviceProvider() {
		return null;
	}

	@Override
	protected void openAuoMeasurementActivity(
			IMeasurementDevice<RespiratoryRateMeasurement> device) {
		
	}

	@Override
	protected Boolean validate() {
		if( _rateTextView.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the rate.");
			return false;
		}
		return true;
	}

	@Override
	protected RespiratoryRateMeasurement getMeasurement() {
		RespiratoryRateMeasurement result = new RespiratoryRateMeasurement();
		result.Rate = Double.parseDouble( _rateTextView.getText().toString() );
		result.ClientId = _client.ClientId;
		return result;
	}

	@Override
	protected void loadMeasurement(RespiratoryRateMeasurement measurement) {
		_rateTextView.setText(measurement.Rate + "");
	}

	@Override
	protected EventType getEventType() {
		return EventType.RespiratoryRateTaken;
	}
	
}
