package com.lehms.serviceInterface;

import com.lehms.messages.dataContracts.LocationDataContract;

public interface ITracker {
	LocationDataContract getLastLocation();
	void putLastLocation(LocationDataContract location);
}
