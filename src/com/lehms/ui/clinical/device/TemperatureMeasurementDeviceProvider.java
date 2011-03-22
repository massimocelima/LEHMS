package com.lehms.ui.clinical.device;

import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class TemperatureMeasurementDeviceProvider extends BluetoothMeasurementDeviceProvider<TemperatureMeasurement> {
	
	public TemperatureMeasurementDeviceProvider() {
		super();
	}
	
	@Override
	public Boolean isMeasurmentDevice(BluetoothDevice device) {
		return device.getName().startsWith("TaiDoc-BTM");
	}

	@Override
	public IMeasurementDevice<TemperatureMeasurement> createDevice(BluetoothDevice device) {
		if( device.getName().startsWith("TaiDoc-BTM") )
			return new TemperatureTaiDocMeasurementDevice(device);
		return null;
	}

}
