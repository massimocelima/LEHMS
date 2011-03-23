package com.lehms.ui.clinical.model;

import java.io.Serializable;
import java.util.Date;

public class Measurement implements Serializable {
	
	public Measurement()
	{
		CreatedDate = new Date();
	}
	
	public Date CreatedDate;
	public String CreatedBy;
}
