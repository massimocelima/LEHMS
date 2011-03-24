package com.lehms.ui.clinical;

import java.io.IOException;

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
import com.lehms.ui.clinical.model.WeightMeasurement;
import com.lehms.util.AppLog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class WeightMeasurementActivity  extends RoboActivity implements ISaveEventResultHandler  { 

	public static final String EXTRA_CLIENT = "client";
	
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;
	
	@InjectView(R.id.activity_title) TextView _title;
	@InjectView(R.id.activity_sub_title) TextView _subtitle;
	//@InjectView(R.id.activity_sub_title2) TextView _subtitle2;

	@InjectView(R.id.activity_measurment_weight_label) TextView _weightLabel;
	@InjectView(R.id.activity_measurment_weight_edit) EditText _weightEdit;

	@Inject IEventRepository _eventRepository;
	@Inject IEventExecuter _eventExecuter;
	@Inject IEventFactory _eventEventFactory;
	
	private AcceptThread _acceptThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_weight);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		_subtitle.setText(_client.FirstName + " " + _client.LastName);
		
		_acceptThread = new AcceptThread();
		_acceptThread.start();
	}
	
	private class AcceptThread extends Thread {
		
		private final BluetoothServerSocket mmServerSocket;
		
		private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		public AcceptThread() {        
			// Use a temporary object that is later assigned to mmServerSocket,        
			// because mmServerSocket is final        
			BluetoothServerSocket tmp = null;        
			try {            
				// MY_UUID is the app's UUID string, also used by the client code            
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Lehms", UIHelper.getApplicationUUID());        
			} catch (IOException e) { }        
			mmServerSocket = tmp;    
		}    
		
		public void run() {        
			BluetoothSocket socket = null;        
			// Keep listening until exception occurs or a socket is returned        
			while (true) {            
				try {                
					socket = mmServerSocket.accept();            
				} catch (IOException e) {                
					break;            
				}            
				// If a connection was accepted            
				if (socket != null) {                
					// Do work to manage the connection (in a separate thread)
					try {                
						//manageConnectedSocket(socket);
						socket.close();
						mmServerSocket.close();
					} catch (Exception e) {}
					break;
				}
			}
		}
		
		/** Will cancel the listening socket, and cause the thread to finish */    
		public void cancel() {        
			try {            
				mmServerSocket.close();        
			} catch (IOException e) { }    
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_acceptThread.cancel();
	};
	
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
		if( _weightEdit.getText().toString().equals("" ))
			UIHelper.ShowAlertDialog(this, "Validation error", "Please enter a value for the weight.");
		else
		{
			WeightMeasurement measurement = new WeightMeasurement();
			measurement.Weight = Double.parseDouble( _weightEdit.getText().toString() );
			measurement.ClientId = _client.ClientId;
			
			Event event = _eventEventFactory.create(measurement, EventType.WeightTaken);
			try {
				UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, this.getTitle().toString());
				UIHelper.ShowToast(this, "Weight Measurement Saved");

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
