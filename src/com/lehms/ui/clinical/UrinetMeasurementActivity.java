package com.lehms.ui.clinical;

import com.google.inject.Inject;
import com.lehms.ISaveEventResultHandler;
import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.ui.clinical.model.INRMeasurement;
import com.lehms.ui.clinical.model.UrineMeasurement;
import com.lehms.ui.clinical.model.WeightMeasurement;
import com.lehms.util.AppLog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class UrinetMeasurementActivity  extends RoboActivity implements ISaveEventResultHandler  { 

	public static final String EXTRA_CLIENT = "client";
	
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;
	
	@InjectView(R.id.activity_title) TextView _title;
	@InjectView(R.id.activity_sub_title) TextView _subtitle;
	//@InjectView(R.id.activity_sub_title2) TextView _subtitle2;

	@InjectView(R.id.activity_container) View _container;

	@InjectView(R.id.activity_measurment_urine_blood_edit) EditText _bloodEdit;
	@InjectView(R.id.activity_measurment_urine_bilirubin_edit) EditText _bilirubinEdit;
	@InjectView(R.id.activity_measurment_urine_glucose_edit) EditText _glucoseEdit;
	@InjectView(R.id.activity_measurment_urine_leucocytes_edit) EditText _leucocytesEdit;
	@InjectView(R.id.activity_measurment_urine_nitrite_edit) EditText _nitriteEdit;
	@InjectView(R.id.activity_measurment_urine_ph_edit) EditText _phEdit;
	@InjectView(R.id.activity_measurment_urine_protein_edit) EditText _proteinEdit;
	@InjectView(R.id.activity_measurment_urine_specific_grav_edit) EditText _specificGravEdit;
	@InjectView(R.id.activity_measurment_urine_urobilinogen_edit) EditText _urobilinogenEdit;
	@InjectView(R.id.activity_measurment_urine_ketones_edit) EditText _ketonesEdit;

	@Inject IEventRepository _eventRepository;
	@Inject IEventExecuter _eventExecuter;
	@Inject IEventFactory _eventEventFactory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_urine);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		_subtitle.setText(_client.FirstName + " " + _client.LastName);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_CLIENT, _client);
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
		
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	public void onCancelClick(View view)
	{
		finish();
	}
		
	public void onSaveClick(View view)
	{
		if( _bilirubinEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Bilirubin.");
			_bilirubinEdit.requestFocus();
		}
		else if( _urobilinogenEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Urobilinogen.");
			_urobilinogenEdit.requestFocus();
		}
		else if( _ketonesEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Ketones.");
			_ketonesEdit.requestFocus();
		}
		else if( _proteinEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Protein.");
			_proteinEdit.requestFocus();
		}
		else if( _nitriteEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Nitrite.");
			_nitriteEdit.requestFocus();
		}
		else if( _glucoseEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Glucose.");
			_glucoseEdit.requestFocus();
		}
		else if( _phEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the pH.");
			_phEdit.requestFocus();
		}
		else if( _specificGravEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the SpecificGrav.");
			_specificGravEdit.requestFocus();
		}
		else if( _leucocytesEdit.getText().toString().equals("" ))
		{
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the Leucocytes.");
			_leucocytesEdit.requestFocus();
		}
		else
		{
			UrineMeasurement measurement = new UrineMeasurement();
			measurement.Bilirubin = Double.parseDouble( _bilirubinEdit.getText().toString() );
			measurement.Blood = _bloodEdit.getText().toString();
			measurement.Glucose = Double.parseDouble( _glucoseEdit.getText().toString() );
			measurement.Ketones = Double.parseDouble( _ketonesEdit.getText().toString() );
			measurement.Leucocytes = Double.parseDouble( _leucocytesEdit.getText().toString() );
			measurement.Nitrite = Double.parseDouble( _nitriteEdit.getText().toString() );
			measurement.pH = Double.parseDouble( _phEdit.getText().toString() );
			measurement.Protein = Double.parseDouble( _proteinEdit.getText().toString() );
			measurement.SpecificGrav = Double.parseDouble( _specificGravEdit.getText().toString() );
			measurement.Urobilinogen = Double.parseDouble( _urobilinogenEdit.getText().toString() );
			measurement.ClientId = _client.ClientId;
			
			Event event = _eventEventFactory.create(measurement, EventType.UrineTaken);
			try {
				UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, this.getTitle().toString());
				UIHelper.ShowToast(this, "INR Measurement Saved");

				finish();
			} catch (Exception e) {
				onError(e);
			}
		}
		
		
	}
	
	//ISaveEventResultHandler Implementation
	@Override
	public void onSuccess(Object data) {
		this.finish();
	}

	@Override
	public void onError(Exception e) {
		UIHelper.ShowAlertDialog(this, "Error saving measurment", "Error ssving measurment: " + e.getMessage());
		AppLog.error("Error ssving measurment", e);
	}
	//ISaveEventResultHandler Implementation
}
