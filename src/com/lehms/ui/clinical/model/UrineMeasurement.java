package com.lehms.ui.clinical.model;

import java.io.Serializable;

public class UrineMeasurement extends Measurement implements Serializable {

	public UrineMeasurement()
	{
		
	}
	
	public String Blood;
	public double Bilirubin;
	public double Urobilinogen;
	public double Ketones;
	public double Protein;
	public double Nitrite;
	public double Glucose;
	public double pH;
	public double SpecificGrav;
	public double Leucocytes;
	
	@Override
	public String toString()
	{
		String result = "Protein: " + Protein;
		if( Glucose > 0 )
			result +=  "\r\n" + "Glucose: " + Glucose;
		return result;
	}
}
