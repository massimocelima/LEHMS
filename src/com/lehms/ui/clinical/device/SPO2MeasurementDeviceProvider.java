package com.lehms.ui.clinical.device;

import com.lehms.ui.clinical.model.SPO2Measurement;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class SPO2MeasurementDeviceProvider extends BluetoothMeasurementDeviceProvider<SPO2Measurement> {
	
	public SPO2MeasurementDeviceProvider() {
		super();
	}
	
	@Override
	public Boolean isMeasurmentDevice(BluetoothDevice device) {
		return device.getName().startsWith("Nonin");
	}

	@Override
	public IMeasurementDevice<SPO2Measurement> createDevice(BluetoothDevice device) {
		if( device.getName().startsWith("Nonin") )
			return new SPO2NoninMeasurementDevice(device);
		return null;
	}

}
