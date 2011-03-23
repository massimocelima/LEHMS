package com.lehms.ui.clinical.device;

import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class BloodPressureMeasurementDeviceProvider extends BluetoothMeasurementDeviceProvider<BloodPressureMeasurement> {
	
	public BloodPressureMeasurementDeviceProvider() {
		super();
	}
	
	@Override
	public Boolean isMeasurmentDevice(BluetoothDevice device) {
		return device.getName().startsWith("TaiDoc-BTM");
	}

	@Override
	public IMeasurementDevice<BloodPressureMeasurement> createDevice(BluetoothDevice device) {
		if( device.getName().startsWith("TaiDoc-BTM") )
			return new BloodPressureTaiDocMeasurementDevice(device);
		return null;
	}

}
