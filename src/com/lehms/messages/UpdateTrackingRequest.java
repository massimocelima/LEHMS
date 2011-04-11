package com.lehms.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lehms.messages.dataContracts.LocationDataContract;

public class UpdateTrackingRequest implements Serializable {
	
	public UpdateTrackingRequest()
	{
		Locations = new ArrayList<LocationDataContract>();
	}
	
	public List<LocationDataContract> Locations;

}
