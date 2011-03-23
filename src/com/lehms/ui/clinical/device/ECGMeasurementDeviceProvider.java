package com.lehms.ui.clinical.device;

import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.ECGMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class ECGMeasurementDeviceProvider extends BluetoothMeasurementDeviceProvider<ECGMeasurement> {
	
	public ECGMeasurementDeviceProvider() {
		super();
	}
	
	@Override
	public Boolean isMeasurmentDevice(BluetoothDevice device) {
		return device.getName().startsWith("AATOS");
	}

	@Override
	public IMeasurementDevice<ECGMeasurement> createDevice(BluetoothDevice device) {
		if( device.getName().startsWith("AATOS") )
			return new ECGAATOSMeasurementDevice(device);
		return null;
	}

}
