package com.lehms.messages;

import java.io.Serializable;
import java.util.Date;

public class JobStartActionRequest implements Serializable  {
	
	public Date ActionDate;
	public float KilometersTravelled;
    public float KilometersTravelledFromGps;
    public String JobId;
    
}
