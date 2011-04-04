package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class RespiratoryRateMeasurement extends Measurement implements Serializable  {

	public RespiratoryRateMeasurement()
	{
		
	}
	
	public double Rate;
	
	@Override
	public String toString()
	{
		return "Rate: " + Rate;
	}

}
