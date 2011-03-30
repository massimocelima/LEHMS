package com.lehms.ui.clinical.model;

import java.io.Serializable;

import android.app.Dialog;

public class BloodSugerLevelMeasurement extends Measurement implements Serializable {

	public BloodSugerLevelMeasurement()
	{
		
	}
	
	public double Level;
	public int Insulin;
	public BloodSugerLevelInsulinType InsulinType;
	
	@Override
	public String toString()
	{
		String result = "BSL (mmol/l): " + Level;
		if( Insulin > 0 )
			result +=  "\r\n" + "Insulin (UI/mL): " + Insulin;
		return result;
	}

}
