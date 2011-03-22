package com.lehms.ui.clinical;


import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.ui.clinical.model.SPO2Measurement;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;

public abstract class ClinicalMeasurmentBaseActivity extends RoboActivity {

	public static final String EXTRA_AUTO_ENTRY = "auto"; 

	public static final int REQUEST_ENABLE_BT = 1; 
	public static final int REQUEST_CONNECT_DEVICE = 2; 

	//private BluetoothAdapter _bluetoothAdapter;
	//private BluetoothDevice _device;

	protected abstract IMeasurementDeviceProvider getDeviceProvider();
	protected abstract void openAuoMeasurementActivity(IMeasurementDevice device);
	
	@InjectExtra( optional=true, value=EXTRA_AUTO_ENTRY) protected Boolean _isAuto;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	};
	
	public Boolean isAutoEntryForm()
	{
		return _isAuto != null && _isAuto; 
	}

	public void openAutoEntryForm()
	{
		if ( !getDeviceProvider().isEnabled()) { 
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); 
		}
		else
		{
			try {
				IMeasurementDevice device = getDeviceProvider().getDefaultDevice();;
	
				if(device == null)
				{
		            Intent deviceListIntent = new Intent(this, DeviceListActivity.class);
		            deviceListIntent.putExtra(DeviceListActivity.EXTRA_DEVICE_PROVIDER_CLASS, getDeviceProvider().getClass().getName());
		            startActivityForResult(deviceListIntent, REQUEST_CONNECT_DEVICE);
				}
				else
				{
					// start listening for measurments from this device
					//device.getMeasurement()
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	/*
	private void GetDataDromDevice(BluetoothDevice device)
	{
		try {
			
			//BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UIHelper.getApplicationUUID());
			
			Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});          
			BluetoothSocket socket = (BluetoothSocket) m.invoke(device, 1); 

			// In case this is running
	        _bluetoothAdapter.cancelDiscovery();

	        socket.connect(); 
	        
	        InputStream socketInputStream = socket.getInputStream(); 
	        byte[] buffer = new byte[1024];  
	        int bytes;
	        //while (true) { 
	            try { 
	                // Read from the InputStream 
	                bytes = socketInputStream.read(buffer); 
	                // Send the obtained bytes to the UI Activity 
	                //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer) 
	                        //.sendToTarget(); 
	            } catch (IOException e) { 
	                //break; 
	            } 
	        //} 
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	@Override 
	protected void onDestroy() {
		super.onDestroy();
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_ENABLE_BT )
		{
			if(resultCode == RESULT_OK)
			{
				openAutoEntryForm();
			}
		}
		else if(requestCode == REQUEST_CONNECT_DEVICE)
		{
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                IMeasurementDevice device = (IMeasurementDevice)data.getExtras().getSerializable(DeviceListActivity.EXTRA_SELECTED_DEVICE);
                openAuoMeasurementActivity(device);
            }
		}
	}
	
	


}
