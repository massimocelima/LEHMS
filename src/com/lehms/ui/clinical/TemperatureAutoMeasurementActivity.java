package com.lehms.ui.clinical;

import com.lehms.LoginActivity;
import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class TemperatureAutoMeasurementActivity extends ClinicalMeasurmentAutoBaseActivity<TemperatureMeasurement> {

	@InjectView(R.id.activity_measurment_temperature_auto_edit) TextView _tempTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_measurement_temperature_auto);
		start();
	}

	@Override
	public void loadMeasurement(TemperatureMeasurement measurement) {
		_tempTextView.setText(measurement.Degrees + "");
	}
	
	
}
