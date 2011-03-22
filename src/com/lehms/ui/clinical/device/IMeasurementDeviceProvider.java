package com.lehms.ui.clinical.device;

import java.io.Serializable;
import java.util.List;

import android.content.Context;

public interface IMeasurementDeviceProvider<T> extends Serializable {

	List<IMeasurementDevice<T>> getAvailableDevices() throws Exception;
	void beginDiscovery(IDeviceDiscoveredEventHandler<T> handler, Context context) throws Exception;
	void cancelDiscovery(Context context);

	IMeasurementDevice<T> getDefaultDevice();
	void setDefaultDevice(IMeasurementDevice<T> device);
	
	Boolean isEnabled();

}
