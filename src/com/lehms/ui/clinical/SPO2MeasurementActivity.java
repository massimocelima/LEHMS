package com.lehms.ui.clinical;

import com.google.inject.Inject;
import com.lehms.JobDetailsActivity;
import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.controls.ActionItem;
import com.lehms.controls.QuickAction;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.ui.clinical.device.SPO2MeasurementDeviceProvider;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class SPO2MeasurementActivity extends ClinicalMeasurmentBaseActivity<SPO2Measurement> { 

	@InjectView(R.id.activity_measurment_spo2_edit) EditText _oxegenEdit;
	@InjectView(R.id.activity_measurment_spo2_pulse_edit) EditText _pulseEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_spo2);
		init();
	}
	
	@Override protected Boolean validate() 
	{
		if( _oxegenEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for SPO2.");
			return false;
		}
		return true;
	};
	
	@Override
	protected IMeasurementDeviceProvider<SPO2Measurement> getDeviceProvider() {
		return new SPO2MeasurementDeviceProvider();
	}
	
	@Override
	protected void openAuoMeasurementActivity(IMeasurementDevice<SPO2Measurement> device) {
		Intent intent = new Intent(this, SPO2AutoMeasurementActivity.class);
		intent.putExtra(ClinicalMeasurmentAutoBaseActivity.EXTRA_DEVICE, device);
		startActivityForResult(intent, ClinicalMeasurmentBaseActivity.REQUEST_AUTO_MEASURMENT);
	}

	@Override
	protected SPO2Measurement getMeasurement() {
		SPO2Measurement measurement = new SPO2Measurement();
		measurement.OxegenPercent = Double.parseDouble( _oxegenEdit.getText().toString() );
		measurement.Pulse = Integer.parseInt( _pulseEdit.getText().toString() );
		measurement.ClientId = _client.ClientId;
		return measurement;
	}

	@Override
	protected void loadMeasurement(SPO2Measurement measurement) {
		_oxegenEdit.setText(measurement.OxegenPercent + "");
		_pulseEdit.setText(measurement.Pulse + "");
	}

	@Override
	protected EventType getEventType() {
		return EventType.SPO2Taken;
	}
	
}
