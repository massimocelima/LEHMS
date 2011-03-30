package com.lehms.ui.clinical.model;

import java.io.Serializable;


public class MeasurementType implements Serializable {

	public String Name;
	public String Description;
	public MeasurementTypeEnum Type; 
	public int ImageDrawableId;
	
	public String PreviousMeasurmentData;
	
}
