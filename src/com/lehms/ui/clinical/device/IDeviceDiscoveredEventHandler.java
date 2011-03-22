package com.lehms.ui.clinical.device;

public interface IDeviceDiscoveredEventHandler<T> {

	void deviceDiscovered(IMeasurementDevice<T> device);
	void discoveryFinished();
	void discoveryStarted();
}
