package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class BloodPressureMeasurement extends Measurement implements Serializable  {

	public BloodPressureMeasurement()
	{
		
	}
	
	public int Systolic;
	public int Diastolic;
	public int Map;
	public int Pulse;
	
	@Override
	public String toString()
	{
		return "Systolic (mmHg): " + Systolic + "\r\n" + "Diastolic (mmHg): " + Diastolic;
	}	
}
