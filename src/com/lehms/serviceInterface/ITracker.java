package com.lehms.serviceInterface;

import com.lehms.messages.dataContracts.LocationDataContract;

public interface ITracker {
	
	LocationDataContract getLastLocation();
	void putLastLocation(LocationDataContract location);
	void startTracking();
	void stopTracking();
	
	Boolean isMeasuringDistance();
	void beginMeasuringDistance();
	void addDistance(float meters);
	float endMeasuringDistance();
}
