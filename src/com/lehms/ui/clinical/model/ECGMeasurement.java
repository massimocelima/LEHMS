package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class ECGMeasurement extends Measurement implements Serializable {

	public ECGMeasurement()
	{
		
	}
	
	public int Pulse;
	
	@Override
	public String toString()
	{
		return "Pulse (bpm): " + Pulse;
	}
}
