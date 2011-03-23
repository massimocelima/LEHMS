package com.lehms.ui.clinical.device;

import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.BloodSugerLevelMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class BloodSugerLevelMeasurementDeviceProvider extends BluetoothMeasurementDeviceProvider<BloodSugerLevelMeasurement> {
	
	public BloodSugerLevelMeasurementDeviceProvider() {
		super();
	}
	
	@Override
	public Boolean isMeasurmentDevice(BluetoothDevice device) {
		return device.getName().startsWith("TaiDoc-BTM");
	}

	@Override
	public IMeasurementDevice<BloodSugerLevelMeasurement> createDevice(BluetoothDevice device) {
		if( device.getName().startsWith("TaiDoc-BTM") )
			return new BloodSugerLevelTaiDocMeasurementDevice(device);
		return null;
	}

}
