package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class BloodSugerLevelMeasurement extends Measurement implements Serializable {

	public BloodSugerLevelMeasurement()
	{
		
	}
	
	public double Level;
	public int Insulin;
	public BloodSugerLevelInsulinType InsulinType;
	
}
