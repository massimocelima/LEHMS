package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.ECGMeasurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class ECGMeasurementAutoActivity extends ClinicalMeasurmentAutoBaseActivity<ECGMeasurement> {

	@InjectView(R.id.activity_measurment_ecg_pulse_auto) TextView _ecgTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_measurement_ecg_auto);
		start();
	}

	@Override
	public void loadMeasurement(ECGMeasurement measurement) {
		_ecgTextView.setText(measurement.Pulse + "");
	}
	
}
