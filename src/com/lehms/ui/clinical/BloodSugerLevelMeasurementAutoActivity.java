package com.lehms.ui.clinical;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.BloodSugerLevelMeasurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class BloodSugerLevelMeasurementAutoActivity extends ClinicalMeasurmentAutoBaseActivity<BloodSugerLevelMeasurement> {

	@InjectView(R.id.activity_measurment_blood_suger_level_auto) TextView _bslTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_measurement_blood_suger_level_auto);
		start();
	}

	@Override
	public void loadMeasurement(BloodSugerLevelMeasurement measurement) {
		_bslTextView.setText(measurement.Level + "");
	}
	
}
