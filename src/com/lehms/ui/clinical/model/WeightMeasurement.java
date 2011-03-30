package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class WeightMeasurement extends Measurement implements Serializable {

	public WeightMeasurement()
	{
		
	}
	
	public double Weight;
	
	@Override
	public String toString()
	{
		return "Weight (kg): " + Weight;
	}
}
