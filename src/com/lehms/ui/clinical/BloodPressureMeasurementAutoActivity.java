package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class BloodPressureMeasurementAutoActivity extends ClinicalMeasurmentAutoBaseActivity<BloodPressureMeasurement> {

	@InjectView(R.id.activity_measurment_blood_pressure_systolic_auto) TextView _systolicTextView;
	@InjectView(R.id.activity_measurment_blood_pressure_diastolic_auto) TextView _diastlicTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_measurement_blood_pressure_auto);
		start();
	}

	@Override
	public void loadMeasurement(BloodPressureMeasurement measurement) {
		_systolicTextView.setText(measurement.Systolic + "");
		_diastlicTextView.setText(measurement.Diastolic + "");
	}
	
}
