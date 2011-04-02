package com.lehms.messages;

import java.io.Serializable;
import java.util.Date;

public class JobEndActionRequest implements Serializable  {

	public Date ActionDate;
	public float KilometersTravelled;
    public String JobId;
}
