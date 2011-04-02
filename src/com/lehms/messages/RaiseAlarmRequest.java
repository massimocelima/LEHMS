package com.lehms.messages;

import java.io.Serializable;
import java.util.Date;

import com.lehms.messages.dataContracts.AlarmType;
import com.lehms.messages.dataContracts.LocationDataContract;

public class RaiseAlarmRequest  implements Serializable {

	public LocationDataContract Location;
	public Date AlarmSentOn;
	public String Description;
	public AlarmType Type;
	
}