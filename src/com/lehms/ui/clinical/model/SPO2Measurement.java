package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class SPO2Measurement extends Measurement implements Serializable  {

	public SPO2Measurement()
	{
		
	}
	
	public double OxegenPercent;
	public int Pulse;
	
	@Override
	public String toString()
	{
		String result = "SPO2 (%): " + OxegenPercent;
		if( Pulse > 0 )
			result +=  "\r\n" + "Pulse (bpm): " + Pulse;
		return result;
	}
}
