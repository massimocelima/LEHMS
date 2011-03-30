package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class INRMeasurement extends Measurement implements Serializable  {

	public INRMeasurement()
	{
		
	}
	
	public double Value;
	public double Sec;
	
	@Override
	public String toString()
	{
		return "INR: " + Value;
	}
}
