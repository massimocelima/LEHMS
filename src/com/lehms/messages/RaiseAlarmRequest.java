package com.lehms.messages;

import java.util.Date;

import com.lehms.messages.dataContracts.AlarmType;
import com.lehms.messages.dataContracts.LocationDataContract;

public class RaiseAlarmRequest {

	public LocationDataContract Location;
	public Date AlarmSentOn;
	public String Description;
	public AlarmType Type;
	
}