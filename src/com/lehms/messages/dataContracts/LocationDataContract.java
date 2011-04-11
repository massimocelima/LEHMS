package com.lehms.messages.dataContracts;

import java.io.Serializable;
import java.util.Date;

public class LocationDataContract implements Serializable {
	
	public LocationDataContract()
	{
		Taken = new Date();
	}
	
     public float Accuracy;
     public double Latitude;
     public double Longitude;
     public Date Taken;
     
     public String Username;
     public String JobId;
}
