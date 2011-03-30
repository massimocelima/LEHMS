package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class TemperatureMeasurement extends Measurement implements Serializable {

	public TemperatureMeasurement()
	{
		
	}
	
	public double Degrees;

	@Override
	public String toString()
	{
		return "Temperature (c): " + Degrees;
	}
}
